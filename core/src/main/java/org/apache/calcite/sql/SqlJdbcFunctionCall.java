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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|fun
operator|.
name|SqlTrimFunction
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
name|OperandTypes
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
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorScope
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * A<code>SqlJdbcFunctionCall</code> is a node of a parse tree which represents  * a JDBC function call. A JDBC call is of the form<code>{fn NAME(arg0, arg1,  * ...)}</code>.  *  *<p>See<a href="http://java.sun.com/products/jdbc/driverdevs.html">Sun's  * documentation for writers of JDBC drivers</a>.*  *  *<table>  *<caption>Supported JDBC functions</caption>  *<tr>  *<th>Function Name</th>  *<th>Function Returns</th>  *</tr>  *<tr>  *<td colspan="2"><br>  *  *<h3>NUMERIC FUNCTIONS</h3>  *</td>  *</tr>  *<tr>  *<td>ABS(number)</td>  *<td>Absolute value of number</td>  *</tr>  *<tr>  *<td>ACOS(float)</td>  *<td>Arccosine, in radians, of float</td>  *</tr>  *<tr>  *<td>ASIN(float)</td>  *<td>Arcsine, in radians, of float</td>  *</tr>  *<tr>  *<td>ATAN(float)</td>  *<td>Arctangent, in radians, of float</td>  *</tr>  *<tr>  *<td>ATAN2(float1, float2)</td>  *<td>Arctangent, in radians, of float2 / float1</td>  *</tr>  *<tr>  *<td>CEILING(number)</td>  *<td>Smallest integer&gt;= number</td>  *</tr>  *<tr>  *<td>COS(float)</td>  *<td>Cosine of float radians</td>  *</tr>  *<tr>  *<td>COT(float)</td>  *<td>Cotangent of float radians</td>  *</tr>  *<tr>  *<td>DEGREES(number)</td>  *<td>Degrees in number radians</td>  *</tr>  *<tr>  *<td>EXP(float)</td>  *<td>Exponential function of float</td>  *</tr>  *<tr>  *<td>FLOOR(number)</td>  *<td>Largest integer&lt;= number</td>  *</tr>  *<tr>  *<td>LOG(float)</td>  *<td>Base e logarithm of float</td>  *</tr>  *<tr>  *<td>LOG10(float)</td>  *<td>Base 10 logarithm of float</td>  *</tr>  *<tr>  *<td>MOD(integer1, integer2)</td>  *<td>Rh3ainder for integer1 / integer2</td>  *</tr>  *<tr>  *<td>PI()</td>  *<td>The constant pi</td>  *</tr>  *<tr>  *<td>POWER(number, power)</td>  *<td>number raised to (integer) power</td>  *</tr>  *<tr>  *<td>RADIANS(number)</td>  *<td>Radians in number degrees</td>  *</tr>  *<tr>  *<td>RAND(integer)</td>  *<td>Random floating point for seed integer</td>  *</tr>  *<tr>  *<td>ROUND(number, places)</td>  *<td>number rounded to places places</td>  *</tr>  *<tr>  *<td>SIGN(number)</td>  *<td>-1 to indicate number is&lt; 0; 0 to indicate number is = 0; 1 to  * indicate number is&gt; 0</td>  *</tr>  *<tr>  *<td>SIN(float)</td>  *<td>Sine of float radians</td>  *</tr>  *<tr>  *<td>SQRT(float)</td>  *<td>Square root of float</td>  *</tr>  *<tr>  *<td>TAN(float)</td>  *<td>Tangent of float radians</td>  *</tr>  *<tr>  *<td>TRUNCATE(number, places)</td>  *<td>number truncated to places places</td>  *</tr>  *<tr>  *<td colspan="2"><br>  *  *<h3>STRING FUNCTIONS</h3>  *</td>  *</tr>  *<tr>  *<td>ASCII(string)</td>  *<td>Integer representing the ASCII code value of the leftmost character in  * string</td>  *</tr>  *<tr>  *<td>CHAR(code)</td>  *<td>Character with ASCII code value code, where code is between 0 and  * 255</td>  *</tr>  *<tr>  *<td>CONCAT(string1, string2)</td>  *<td>Character string formed by appending string2 to string1; if a string is  * null, the result is DBMS-dependent</td>  *</tr>  *<tr>  *<td>DIFFERENCE(string1, string2)</td>  *<td>Integer indicating the difference between the values returned by the  * function SOUNDEX for string1 and string2</td>  *</tr>  *<tr>  *<td>INSERT(string1, start, length, string2)</td>  *<td>A character string formed by deleting length characters from string1  * beginning at start, and inserting string2 into string1 at start</td>  *</tr>  *<tr>  *<td>LCASE(string)</td>  *<td>Converts all uppercase characters in string to lowercase</td>  *</tr>  *<tr>  *<td>LEFT(string, count)</td>  *<td>The count leftmost characters from string</td>  *</tr>  *<tr>  *<td>LENGTH(string)</td>  *<td>Number of characters in string, excluding trailing blanks</td>  *</tr>  *<tr>  *<td>LOCATE(string1, string2[, start])</td>  *<td>Position in string2 of the first occurrence of string1, searching from  * the beginning of string2; if start is specified, the search begins from  * position start. 0 is returned if string2 does not contain string1. Position 1  * is the first character in string2.</td>  *</tr>  *<tr>  *<td>LTRIM(string)</td>  *<td>Characters of string with leading blank spaces rh3oved</td>  *</tr>  *<tr>  *<td>REPEAT(string, count)</td>  *<td>A character string formed by repeating string count times</td>  *</tr>  *<tr>  *<td>REPLACE(string1, string2, string3)</td>  *<td>Replaces all occurrences of string2 in string1 with string3</td>  *</tr>  *<tr>  *<td>RIGHT(string, count)</td>  *<td>The count rightmost characters in string</td>  *</tr>  *<tr>  *<td>RTRIM(string)</td>  *<td>The characters of string with no trailing blanks</td>  *</tr>  *<tr>  *<td>SOUNDEX(string)</td>  *<td>A character string, which is data source-dependent, representing the  * sound of the words in string; this could be a four-digit SOUNDEX code, a  * phonetic representation of each word, etc.</td>  *</tr>  *<tr>  *<td>SPACE(count)</td>  *<td>A character string consisting of count spaces</td>  *</tr>  *<tr>  *<td>SUBSTRING(string, start, length)</td>  *<td>A character string formed by extracting length characters from string  * beginning at start</td>  *</tr>  *<tr>  *<td>UCASE(string)</td>  *<td>Converts all lowercase characters in string to uppercase</td>  *</tr>  *<tr>  *<td colspan="2"><br>  *  *<h3>TIME and DATE FUNCTIONS</h3>  *</td>  *</tr>  *<tr>  *<td>CURDATE()</td>  *<td>The current date as a date value</td>  *</tr>  *<tr>  *<td>CURTIME()</td>  *<td>The current local time as a time value</td>  *</tr>  *<tr>  *<td>DAYNAME(date)</td>  *<td>A character string representing the day component of date; the name for  * the day is specific to the data source</td>  *</tr>  *<tr>  *<td>DAYOFMONTH(date)</td>  *<td>An integer from 1 to 31 representing the day of the month in date</td>  *</tr>  *<tr>  *<td>DAYOFWEEK(date)</td>  *<td>An integer from 1 to 7 representing the day of the week in date; 1  * represents Sunday</td>  *</tr>  *<tr>  *<td>DAYOFYEAR(date)</td>  *<td>An integer from 1 to 366 representing the day of the year in date</td>  *</tr>  *<tr>  *<td>HOUR(time)</td>  *<td>An integer from 0 to 23 representing the hour component of time</td>  *</tr>  *<tr>  *<td>MINUTE(time)</td>  *<td>An integer from 0 to 59 representing the minute component of time</td>  *</tr>  *<tr>  *<td>MONTH(date)</td>  *<td>An integer from 1 to 12 representing the month component of date</td>  *</tr>  *<tr>  *<td>MONTHNAME(date)</td>  *<td>A character string representing the month component of date; the name for  * the month is specific to the data source</td>  *</tr>  *<tr>  *<td>NOW()</td>  *<td>A timestamp value representing the current date and time</td>  *</tr>  *<tr>  *<td>QUARTER(date)</td>  *<td>An integer from 1 to 4 representing the quarter in date; 1 represents  * January 1 through March 31</td>  *</tr>  *<tr>  *<td>SECOND(time)</td>  *<td>An integer from 0 to 59 representing the second component of time</td>  *</tr>  *<tr>  *<td>TIMESTAMPADD(interval,count, timestamp)</td>  *<td>A timestamp calculated by adding count number of interval(s) to  * timestamp; interval may be one of the following: SQL_TSI_FRAC_SECOND,  * SQL_TSI_SECOND, SQL_TSI_MINUTE, SQL_TSI_HOUR, SQL_TSI_DAY, SQL_TSI_WEEK,  * SQL_TSI_MONTH, SQL_TSI_QUARTER, or SQL_TSI_YEAR</td>  *</tr>  *<tr>  *<td>TIMESTAMPDIFF(interval,timestamp1, timestamp2)</td>  *<td>An integer representing the number of interval(s) by which timestamp2 is  * greater than timestamp1; interval may be one of the following:  * SQL_TSI_FRAC_SECOND, SQL_TSI_SECOND, SQL_TSI_MINUTE, SQL_TSI_HOUR,  * SQL_TSI_DAY, SQL_TSI_WEEK, SQL_TSI_MONTH, SQL_TSI_QUARTER, or  * SQL_TSI_YEAR</td>  *</tr>  *<tr>  *<td>WEEK(date)</td>  *<td>An integer from 1 to 53 representing the week of the year in date</td>  *</tr>  *<tr>  *<td>YEAR(date)</td>  *<td>An integer representing the year component of date</td>  *</tr>  *<tr>  *<td colspan="2"><br>  *  *<h3>SYSTEM FUNCTIONS</h3>  *</td>  *</tr>  *<tr>  *<td>DATABASE()</td>  *<td>Name of the database</td>  *</tr>  *<tr>  *<td>IFNULL(expression, value)</td>  *<td>value if expression is null; expression if expression is not null</td>  *</tr>  *<tr>  *<td>USER()</td>  *<td>User name in the DBMS  *  *<tr>  *<td colspan="2"><br>  *  *<h3>CONVERSION FUNCTIONS</h3>  *</td>  *</tr>  *<tr>  *<td>CONVERT(value, SQLtype)</td>  *<td>value converted to SQLtype where SQLtype may be one of the following SQL  * types: BIGINT, BINARY, BIT, CHAR, DATE, DECIMAL, DOUBLE, FLOAT, INTEGER,  * LONGVARBINARY, LONGVARCHAR, REAL, SMALLINT, TIME, TIMESTAMP, TINYINT,  * VARBINARY, or VARCHAR</td>  *</tr>  *</table>  */
end_comment

begin_class
specifier|public
class|class
name|SqlJdbcFunctionCall
extends|extends
name|SqlFunction
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** List of all numeric function names defined by JDBC. */
specifier|private
specifier|static
specifier|final
name|String
name|NUMERIC_FUNCTIONS
init|=
name|constructFuncList
argument_list|(
literal|"ABS"
argument_list|,
literal|"ACOS"
argument_list|,
literal|"ASIN"
argument_list|,
literal|"ATAN"
argument_list|,
literal|"ATAN2"
argument_list|,
literal|"CEILING"
argument_list|,
literal|"COS"
argument_list|,
literal|"COT"
argument_list|,
literal|"DEGREES"
argument_list|,
literal|"EXP"
argument_list|,
literal|"FLOOR"
argument_list|,
literal|"LOG"
argument_list|,
literal|"LOG10"
argument_list|,
literal|"MOD"
argument_list|,
literal|"PI"
argument_list|,
literal|"POWER"
argument_list|,
literal|"RADIANS"
argument_list|,
literal|"RAND"
argument_list|,
literal|"ROUND"
argument_list|,
literal|"SIGN"
argument_list|,
literal|"SIN"
argument_list|,
literal|"SQRT"
argument_list|,
literal|"TAN"
argument_list|,
literal|"TRUNCATE"
argument_list|)
decl_stmt|;
comment|/** List of all string function names defined by JDBC. */
specifier|private
specifier|static
specifier|final
name|String
name|STRING_FUNCTIONS
init|=
name|constructFuncList
argument_list|(
literal|"ASCII"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"CONCAT"
argument_list|,
literal|"DIFFERENCE"
argument_list|,
literal|"INSERT"
argument_list|,
literal|"LCASE"
argument_list|,
literal|"LEFT"
argument_list|,
literal|"LENGTH"
argument_list|,
literal|"LOCATE"
argument_list|,
literal|"LTRIM"
argument_list|,
literal|"REPEAT"
argument_list|,
literal|"REPLACE"
argument_list|,
literal|"RIGHT"
argument_list|,
literal|"RTRIM"
argument_list|,
literal|"SOUNDEX"
argument_list|,
literal|"SPACE"
argument_list|,
literal|"SUBSTRING"
argument_list|,
literal|"UCASE"
argument_list|)
decl_stmt|;
comment|// "ASCII", "CHAR", "DIFFERENCE", "LOWER",
comment|// "LEFT", "TRIM", "REPEAT", "REPLACE",
comment|// "RIGHT", "SPACE", "SUBSTRING", "UPPER", "INITCAP", "OVERLAY"
comment|/** List of all time/date function names defined by JDBC. */
specifier|private
specifier|static
specifier|final
name|String
name|TIME_DATE_FUNCTIONS
init|=
name|constructFuncList
argument_list|(
literal|"CURDATE"
argument_list|,
literal|"CURTIME"
argument_list|,
literal|"DAYNAME"
argument_list|,
literal|"DAYOFMONTH"
argument_list|,
literal|"DAYOFWEEK"
argument_list|,
literal|"DAYOFYEAR"
argument_list|,
literal|"HOUR"
argument_list|,
literal|"MINUTE"
argument_list|,
literal|"MONTH"
argument_list|,
literal|"MONTHNAME"
argument_list|,
literal|"NOW"
argument_list|,
literal|"QUARTER"
argument_list|,
literal|"SECOND"
argument_list|,
literal|"TIMESTAMPADD"
argument_list|,
literal|"TIMESTAMPDIFF"
argument_list|,
literal|"WEEK"
argument_list|,
literal|"YEAR"
argument_list|)
decl_stmt|;
comment|/** List of all system function names defined by JDBC. */
specifier|private
specifier|static
specifier|final
name|String
name|SYSTEM_FUNCTIONS
init|=
name|constructFuncList
argument_list|(
literal|"DATABASE"
argument_list|,
literal|"IFNULL"
argument_list|,
literal|"USER"
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|jdbcName
decl_stmt|;
specifier|private
specifier|final
name|MakeCall
name|lookupMakeCallObj
decl_stmt|;
specifier|private
name|SqlCall
name|lookupCall
decl_stmt|;
specifier|private
name|SqlNode
index|[]
name|thisOperands
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlJdbcFunctionCall
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
literal|"{fn "
operator|+
name|name
operator|+
literal|"}"
argument_list|,
name|SqlKind
operator|.
name|JDBC_FN
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
name|jdbcName
operator|=
name|name
expr_stmt|;
name|lookupMakeCallObj
operator|=
name|JdbcToInternalLookupTable
operator|.
name|INSTANCE
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|lookupCall
operator|=
literal|null
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
specifier|static
name|String
name|constructFuncList
parameter_list|(
name|String
modifier|...
name|functionNames
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|funcName
range|:
name|functionNames
control|)
block|{
if|if
condition|(
name|JdbcToInternalLookupTable
operator|.
name|INSTANCE
operator|.
name|lookup
argument_list|(
name|funcName
argument_list|)
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|n
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|funcName
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
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
name|thisOperands
operator|=
name|operands
expr_stmt|;
return|return
name|super
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|operands
argument_list|)
return|;
block|}
specifier|public
name|SqlCall
name|getLookupCall
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|lookupCall
condition|)
block|{
name|lookupCall
operator|=
name|lookupMakeCallObj
operator|.
name|createCall
argument_list|(
name|thisOperands
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
expr_stmt|;
block|}
return|return
name|lookupCall
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|lookupMakeCallObj
operator|.
name|operator
operator|.
name|getAllowedSignatures
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Override SqlFunction.deriveType, because function-resolution is
comment|// not relevant to a JDBC function call.
comment|// REVIEW: jhyde, 2006/4/18: Should SqlJdbcFunctionCall even be a
comment|// subclass of SqlFunction?
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|RelDataType
name|nodeType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operand
argument_list|)
decl_stmt|;
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|operand
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
block|}
return|return
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
comment|// only expected to come here if validator called this method
name|SqlCallBinding
name|callBinding
init|=
operator|(
name|SqlCallBinding
operator|)
name|opBinding
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|lookupMakeCallObj
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|functionUndefined
argument_list|(
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|lookupMakeCallObj
operator|.
name|checkNumberOfArg
argument_list|(
name|opBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|wrongNumberOfParam
argument_list|(
name|getName
argument_list|()
argument_list|,
name|thisOperands
operator|.
name|length
argument_list|,
name|getArgCountMismatchMsg
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|lookupMakeCallObj
operator|.
name|operator
operator|.
name|checkOperandTypes
argument_list|(
operator|new
name|SqlCallBinding
argument_list|(
name|callBinding
operator|.
name|getValidator
argument_list|()
argument_list|,
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|getLookupCall
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
name|lookupMakeCallObj
operator|.
name|operator
operator|.
name|validateOperands
argument_list|(
name|callBinding
operator|.
name|getValidator
argument_list|()
argument_list|,
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|getLookupCall
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getArgCountMismatchMsg
parameter_list|()
block|{
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
index|[]
name|possible
init|=
name|lookupMakeCallObj
operator|.
name|getPossibleArgCounts
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|possible
operator|.
name|length
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
name|ret
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
name|possible
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
literal|" parameter(s)"
argument_list|)
expr_stmt|;
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"{fn "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|jdbcName
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|operand
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see java.sql.DatabaseMetaData#getNumericFunctions    */
specifier|public
specifier|static
name|String
name|getNumericFunctions
parameter_list|()
block|{
return|return
name|NUMERIC_FUNCTIONS
return|;
block|}
comment|/**    * @see java.sql.DatabaseMetaData#getStringFunctions    */
specifier|public
specifier|static
name|String
name|getStringFunctions
parameter_list|()
block|{
return|return
name|STRING_FUNCTIONS
return|;
block|}
comment|/**    * @see java.sql.DatabaseMetaData#getTimeDateFunctions    */
specifier|public
specifier|static
name|String
name|getTimeDateFunctions
parameter_list|()
block|{
return|return
name|TIME_DATE_FUNCTIONS
return|;
block|}
comment|/**    * @see java.sql.DatabaseMetaData#getSystemFunctions    */
specifier|public
specifier|static
name|String
name|getSystemFunctions
parameter_list|()
block|{
return|return
name|SYSTEM_FUNCTIONS
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Represent a Strategy Object to create a {@link SqlCall} by providing the    * feature of reording, adding/dropping operands.    */
specifier|private
specifier|static
class|class
name|MakeCall
block|{
specifier|final
name|SqlOperator
name|operator
decl_stmt|;
specifier|final
name|int
index|[]
name|order
decl_stmt|;
comment|/**      * List of the possible numbers of operands this function can take.      */
specifier|final
name|int
index|[]
name|argCounts
decl_stmt|;
specifier|private
name|MakeCall
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|int
name|argCount
parameter_list|)
block|{
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|order
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|argCounts
operator|=
operator|new
name|int
index|[]
block|{
name|argCount
block|}
expr_stmt|;
block|}
comment|/**      * Creates a MakeCall strategy object with reordering of operands.      *      *<p>The reordering is specified by an int array where the value of      * element at position<code>i</code> indicates to which element in a      * new SqlNode[] array the operand goes.      *      * @param operator Operator      * @param order    Order      * @pre order != null      * @pre order[i]< order.length      * @pre order.length> 0      * @pre argCounts == order.length      */
name|MakeCall
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|int
name|argCount
parameter_list|,
name|int
index|[]
name|order
parameter_list|)
block|{
assert|assert
name|order
operator|!=
literal|null
operator|&&
name|order
operator|.
name|length
operator|>
literal|0
assert|;
comment|// Currently operation overloading when reordering is necessary is
comment|// NOT implemented
name|Util
operator|.
name|pre
argument_list|(
name|argCount
operator|==
name|order
operator|.
name|length
argument_list|,
literal|"argCounts==order.length"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|argCounts
operator|=
operator|new
name|int
index|[]
block|{
name|order
operator|.
name|length
block|}
expr_stmt|;
comment|// sanity checking ...
for|for
control|(
name|int
name|anOrder
range|:
name|order
control|)
block|{
assert|assert
name|anOrder
operator|<
name|order
operator|.
name|length
assert|;
block|}
block|}
specifier|final
name|int
index|[]
name|getPossibleArgCounts
parameter_list|()
block|{
return|return
name|this
operator|.
name|argCounts
return|;
block|}
comment|/**      * Uses the data in {@link #order} to reorder a SqlNode[] array.      *      * @param operands Operands      */
specifier|protected
name|SqlNode
index|[]
name|reorder
parameter_list|(
name|SqlNode
index|[]
name|operands
parameter_list|)
block|{
assert|assert
name|operands
operator|.
name|length
operator|==
name|order
operator|.
name|length
assert|;
name|SqlNode
index|[]
name|newOrder
init|=
operator|new
name|SqlNode
index|[
name|operands
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|operands
index|[
name|i
index|]
operator|!=
literal|null
assert|;
name|int
name|joyDivision
init|=
name|order
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|newOrder
index|[
name|joyDivision
index|]
operator|==
literal|null
operator|:
literal|"mapping is not 1:1"
assert|;
name|newOrder
index|[
name|joyDivision
index|]
operator|=
name|operands
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|newOrder
return|;
block|}
comment|/**      * Creates and return a {@link SqlCall}. If the MakeCall strategy object      * was created with a reording specified the call will be created with      * the operands reordered, otherwise no change of ordering is applied      *      * @param operands Operands      */
name|SqlCall
name|createCall
parameter_list|(
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|order
condition|)
block|{
return|return
name|operator
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|operands
argument_list|)
return|;
block|}
return|return
name|operator
operator|.
name|createCall
argument_list|(
name|pos
argument_list|,
name|reorder
argument_list|(
name|operands
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns false if number of arguments are unexpected, otherwise true.      * This function is supposed to be called with an {@link SqlNode} array      * of operands direct from the oven, e.g no reording or adding/dropping      * of operands...else it would make much sense to have this methods      */
name|boolean
name|checkNumberOfArg
parameter_list|(
name|int
name|length
parameter_list|)
block|{
for|for
control|(
name|int
name|argCount
range|:
name|argCounts
control|)
block|{
if|if
condition|(
name|argCount
operator|==
name|length
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
block|}
comment|/**    * Lookup table between JDBC functions and internal representation    */
specifier|private
specifier|static
class|class
name|JdbcToInternalLookupTable
block|{
comment|/**      * The {@link org.apache.calcite.util.Glossary#SINGLETON_PATTERN singleton}      * instance.      */
specifier|static
specifier|final
name|JdbcToInternalLookupTable
name|INSTANCE
init|=
operator|new
name|JdbcToInternalLookupTable
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MakeCall
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MakeCall
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|JdbcToInternalLookupTable
parameter_list|()
block|{
comment|// A table of all functions can be found at
comment|// http://java.sun.com/products/jdbc/driverdevs.html
comment|// which is also provided in the javadoc for this class.
comment|// See also SqlOperatorTests.testJdbcFn, which contains the list.
name|map
operator|.
name|put
argument_list|(
literal|"ABS"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"EXP"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXP
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LOG"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LN
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LOG10"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOG10
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"MOD"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MOD
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"POWER"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POWER
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"CONCAT"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"INSERT"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVERLAY
argument_list|,
literal|4
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|,
literal|1
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LCASE"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOWER
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LENGTH"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHARACTER_LENGTH
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LOCATE"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POSITION
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"LTRIM"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
name|SqlCall
name|createCall
parameter_list|(
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
assert|assert
literal|1
operator|==
name|operands
operator|.
name|length
assert|;
return|return
name|super
operator|.
name|createCall
argument_list|(
operator|new
name|SqlNode
index|[]
block|{
name|SqlTrimFunction
operator|.
name|Flag
operator|.
name|LEADING
operator|.
name|symbol
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
block|,
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
literal|" "
argument_list|,
literal|null
argument_list|)
block|,
name|operands
index|[
literal|0
index|]
block|}
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"RTRIM"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
name|SqlCall
name|createCall
parameter_list|(
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
assert|assert
literal|1
operator|==
name|operands
operator|.
name|length
assert|;
return|return
name|super
operator|.
name|createCall
argument_list|(
operator|new
name|SqlNode
index|[]
block|{
name|SqlTrimFunction
operator|.
name|Flag
operator|.
name|TRAILING
operator|.
name|symbol
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
block|,
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
literal|" "
argument_list|,
literal|null
argument_list|)
block|,
name|operands
index|[
literal|0
index|]
block|}
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"SUBSTRING"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"UCASE"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UPPER
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"CURDATE"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_DATE
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"CURTIME"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOCALTIME
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"NOW"
argument_list|,
operator|new
name|MakeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_TIMESTAMP
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tries to lookup a given function name JDBC to an internal      * representation. Returns null if no function defined.      */
specifier|public
name|MakeCall
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlJdbcFunctionCall.java
end_comment

end_unit

