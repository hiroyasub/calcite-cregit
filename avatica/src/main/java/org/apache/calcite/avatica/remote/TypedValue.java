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
name|avatica
operator|.
name|remote
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
name|ColumnMetaData
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
name|avatica
operator|.
name|proto
operator|.
name|Common
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
name|avatica
operator|.
name|util
operator|.
name|ByteString
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
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonCreator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Value and type.  *  *<p>There are 3 representations:  *<ul>  *<li>JDBC - the representation used by JDBC get and set methods  *<li>Serial - suitable for serializing using JSON  *<li>Local - used by Calcite for efficient computation  *</ul>  *  *<p>The following table shows the Java type(s) that may represent each SQL  * type in each representation.  *  *<table>  *<caption>SQL types and their representations</caption>  *<tr>  *<th>Type</th><th>JDBC</th><th>Serial</th><th>Local</th>  *</tr>  *<tr>  *<td>BOOLEAN</td><td>boolean</td><td>boolean</td><td>boolean</td>  *</tr>  *<tr>  *<td>BINARY, VARBINARY</td><td>byte[]</td>  *<td>String (base64)</td><td>{@link ByteString}</td>  *</tr>  *<tr>  *<td>DATE</td><td>{@link java.sql.Date}</td>  *<td>int</td><td>int</td>  *</tr>  *<tr>  *<td>TIME</td><td>{@link java.sql.Time}</td>  *<td>int</td><td>int</td>  *</tr>  *<tr>  *<td>DATE</td><td>{@link java.sql.Timestamp}</td>  *<td>long</td><td>long</td>  *</tr>  *<tr>  *<td>CHAR, VARCHAR</td>  *<td>String</td><td>String</td><td>String</td>  *</tr>  *<tr>  *<td>TINYINT</td><td>byte</td><td>Number</td><td>byte</td>  *</tr>  *<tr>  *<td>SMALLINT</td><td>short</td><td>Number</td><td>short</td>  *</tr>  *<tr>  *<td>INTEGER</td><td>int</td><td>Number</td><td>int</td>  *</tr>  *<tr>  *<td>BIGINT</td><td>long</td><td>Number</td><td>long</td>  *</tr>  *<tr>  *<td>REAL</td><td>float</td><td>Number</td><td>float</td>  *</tr>  *<tr>  *<td>FLOAT, DOUBLE</td>  *<td>double</td><td>Number</td><td>double</td>  *</tr>  *<tr>  *<td>DECIMAL</td>  *<td>BigDecimal</td><td>Number</td><td>BigDecimal</td>  *</tr>  *</table>  *  * Note:  *<ul>  *<li>The various numeric types (TINYINT, SMALLINT, INTEGER, BIGINT, REAL,  *   FLOAT, DOUBLE) are represented by {@link Number} in serial format because  *   JSON numbers are not strongly typed. A {@code float} value {@code 3.0} is  *   transmitted as {@code 3}, and is therefore decoded as an {@code int}.  *  *<li>The date-time types (DATE, TIME, TIMESTAMP) are represented in JDBC as  *   {@link java.sql.Date}, {@link java.sql.Time}, {@link java.sql.Timestamp},  *   all sub-classes of {@link java.util.Date}. When they are passed to and  *   from the server, they are interpreted in terms of a time zone, by default  *   the current connection's time zone. Their serial and local representations  *   as {@code int} (days since 1970-01-01 for DATE, milliseconds since  *   00:00:00.000 for TIME), and long (milliseconds since 1970-01-01  *   00:00:00.000 for TIMESTAMP) are easier to work with, because it is clear  *   that time zone is not involved.  *  *<li>BINARY and VARBINARY values are represented as base64-encoded strings  *   for serialization over JSON.  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|TypedValue
block|{
specifier|public
specifier|static
specifier|final
name|TypedValue
name|NULL
init|=
operator|new
name|TypedValue
argument_list|(
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|OBJECT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/** Type of the value. */
specifier|public
specifier|final
name|ColumnMetaData
operator|.
name|Rep
name|type
decl_stmt|;
comment|/** Value.    *    *<p>Always in a form that can be serialized to JSON by Jackson.    * For example, byte arrays are represented as String. */
specifier|public
specifier|final
name|Object
name|value
decl_stmt|;
specifier|private
name|TypedValue
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|rep
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
assert|assert
name|isSerial
argument_list|(
name|rep
argument_list|,
name|value
argument_list|)
operator|:
literal|"rep: "
operator|+
name|rep
operator|+
literal|", value: "
operator|+
name|value
assert|;
block|}
specifier|private
name|boolean
name|isSerial
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
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
name|rep
condition|)
block|{
case|case
name|BYTE_STRING
case|:
return|return
name|value
operator|instanceof
name|String
return|;
case|case
name|JAVA_SQL_DATE
case|:
case|case
name|JAVA_SQL_TIME
case|:
return|return
name|value
operator|instanceof
name|Integer
return|;
case|case
name|JAVA_SQL_TIMESTAMP
case|:
case|case
name|JAVA_UTIL_DATE
case|:
return|return
name|value
operator|instanceof
name|Long
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|JsonCreator
specifier|public
specifier|static
name|TypedValue
name|create
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"type"
argument_list|)
name|String
name|type
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"value"
argument_list|)
name|Object
name|value
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
name|NULL
return|;
block|}
name|ColumnMetaData
operator|.
name|Rep
name|rep
init|=
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|valueOf
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|ofLocal
argument_list|(
name|rep
argument_list|,
name|serialToLocal
argument_list|(
name|rep
argument_list|,
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a TypedValue from a value in local representation. */
specifier|public
specifier|static
name|TypedValue
name|ofLocal
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TypedValue
argument_list|(
name|rep
argument_list|,
name|localToSerial
argument_list|(
name|rep
argument_list|,
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a TypedValue from a value in serial representation. */
specifier|public
specifier|static
name|TypedValue
name|ofSerial
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TypedValue
argument_list|(
name|rep
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** Creates a TypedValue from a value in JDBC representation. */
specifier|public
specifier|static
name|TypedValue
name|ofJdbc
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|,
name|Calendar
name|calendar
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
name|NULL
return|;
block|}
return|return
operator|new
name|TypedValue
argument_list|(
name|rep
argument_list|,
name|jdbcToSerial
argument_list|(
name|rep
argument_list|,
name|value
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a TypedValue from a value in JDBC representation,    * deducing its type. */
specifier|public
specifier|static
name|TypedValue
name|ofJdbc
parameter_list|(
name|Object
name|value
parameter_list|,
name|Calendar
name|calendar
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
name|NULL
return|;
block|}
specifier|final
name|ColumnMetaData
operator|.
name|Rep
name|rep
init|=
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|of
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|TypedValue
argument_list|(
name|rep
argument_list|,
name|jdbcToSerial
argument_list|(
name|rep
argument_list|,
name|value
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts the value into the local representation.    *    *<p>For example, a byte string is represented as a {@link ByteString};    * a long is represented as a {@link Long} (not just some {@link Number}).    */
specifier|public
name|Object
name|toLocal
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
return|return
name|serialToLocal
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** Converts a value to the exact type required for the given    * representation. */
specifier|private
specifier|static
name|Object
name|serialToLocal
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|!=
literal|null
assert|;
if|if
condition|(
name|value
operator|.
name|getClass
argument_list|()
operator|==
name|rep
operator|.
name|clazz
condition|)
block|{
return|return
name|value
return|;
block|}
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|BYTE
case|:
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|byteValue
argument_list|()
return|;
case|case
name|SHORT
case|:
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|shortValue
argument_list|()
return|;
case|case
name|INTEGER
case|:
case|case
name|JAVA_SQL_DATE
case|:
case|case
name|JAVA_SQL_TIME
case|:
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
case|case
name|LONG
case|:
case|case
name|JAVA_UTIL_DATE
case|:
case|case
name|JAVA_SQL_TIMESTAMP
case|:
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
return|;
case|case
name|FLOAT
case|:
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
return|;
case|case
name|DOUBLE
case|:
return|return
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
case|case
name|NUMBER
case|:
return|return
name|value
operator|instanceof
name|BigDecimal
condition|?
name|value
else|:
name|value
operator|instanceof
name|BigInteger
condition|?
operator|new
name|BigDecimal
argument_list|(
operator|(
name|BigInteger
operator|)
name|value
argument_list|)
else|:
name|value
operator|instanceof
name|Double
condition|?
operator|new
name|BigDecimal
argument_list|(
operator|(
name|Double
operator|)
name|value
argument_list|)
else|:
name|value
operator|instanceof
name|Float
condition|?
operator|new
name|BigDecimal
argument_list|(
operator|(
name|Float
operator|)
name|value
argument_list|)
else|:
operator|new
name|BigDecimal
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
case|case
name|BYTE_STRING
case|:
return|return
name|ByteString
operator|.
name|ofBase64
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot convert "
operator|+
name|value
operator|+
literal|" ("
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|+
literal|") to "
operator|+
name|rep
argument_list|)
throw|;
block|}
block|}
comment|/** Converts the value into the JDBC representation.    *    *<p>For example, a byte string is represented as a {@link ByteString};    * a long is represented as a {@link Long} (not just some {@link Number}).    */
specifier|public
name|Object
name|toJdbc
parameter_list|(
name|Calendar
name|calendar
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
literal|null
return|;
block|}
return|return
name|serialToJdbc
argument_list|(
name|type
argument_list|,
name|value
argument_list|,
name|calendar
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Object
name|serialToJdbc
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|type
parameter_list|,
name|Object
name|value
parameter_list|,
name|Calendar
name|calendar
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTE_STRING
case|:
return|return
name|ByteString
operator|.
name|ofBase64
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
operator|.
name|getBytes
argument_list|()
return|;
case|case
name|JAVA_UTIL_DATE
case|:
return|return
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|(
name|adjust
argument_list|(
operator|(
name|Number
operator|)
name|value
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
case|case
name|JAVA_SQL_DATE
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
name|adjust
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
operator|*
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
case|case
name|JAVA_SQL_TIME
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Time
argument_list|(
name|adjust
argument_list|(
operator|(
name|Number
operator|)
name|value
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
case|case
name|JAVA_SQL_TIMESTAMP
case|:
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Timestamp
argument_list|(
name|adjust
argument_list|(
operator|(
name|Number
operator|)
name|value
argument_list|,
name|calendar
argument_list|)
argument_list|)
return|;
default|default:
return|return
name|serialToLocal
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|long
name|adjust
parameter_list|(
name|Number
name|number
parameter_list|,
name|Calendar
name|calendar
parameter_list|)
block|{
name|long
name|t
init|=
name|number
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|calendar
operator|!=
literal|null
condition|)
block|{
name|t
operator|-=
name|calendar
operator|.
name|getTimeZone
argument_list|()
operator|.
name|getOffset
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
comment|/** Converts a value from JDBC format to a type that can be serialized as    * JSON. */
specifier|private
specifier|static
name|Object
name|jdbcToSerial
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|,
name|Calendar
name|calendar
parameter_list|)
block|{
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|BYTE_STRING
case|:
return|return
operator|new
name|ByteString
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
operator|.
name|toBase64String
argument_list|()
return|;
case|case
name|JAVA_UTIL_DATE
case|:
case|case
name|JAVA_SQL_TIMESTAMP
case|:
case|case
name|JAVA_SQL_DATE
case|:
case|case
name|JAVA_SQL_TIME
case|:
name|long
name|t
init|=
operator|(
operator|(
name|Date
operator|)
name|value
operator|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|calendar
operator|!=
literal|null
condition|)
block|{
name|t
operator|+=
name|calendar
operator|.
name|getTimeZone
argument_list|()
operator|.
name|getOffset
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|JAVA_SQL_DATE
case|:
return|return
operator|(
name|int
operator|)
name|DateTimeUtils
operator|.
name|floorDiv
argument_list|(
name|t
argument_list|,
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|)
return|;
case|case
name|JAVA_SQL_TIME
case|:
return|return
operator|(
name|int
operator|)
name|DateTimeUtils
operator|.
name|floorMod
argument_list|(
name|t
argument_list|,
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|)
return|;
default|default:
return|return
name|t
return|;
block|}
default|default:
return|return
name|value
return|;
block|}
block|}
comment|/** Converts a value from internal format to a type that can be serialized    * as JSON. */
specifier|private
specifier|static
name|Object
name|localToSerial
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|BYTE_STRING
case|:
return|return
operator|(
operator|(
name|ByteString
operator|)
name|value
operator|)
operator|.
name|toBase64String
argument_list|()
return|;
default|default:
return|return
name|value
return|;
block|}
block|}
comment|/** Converts a list of {@code TypedValue} to a list of values. */
specifier|public
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|(
name|List
argument_list|<
name|TypedValue
argument_list|>
name|typedValues
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TypedValue
name|typedValue
range|:
name|typedValues
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|typedValue
operator|.
name|toLocal
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|public
name|Common
operator|.
name|TypedValue
name|toProto
parameter_list|()
block|{
specifier|final
name|Common
operator|.
name|TypedValue
operator|.
name|Builder
name|builder
init|=
name|Common
operator|.
name|TypedValue
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|Common
operator|.
name|Rep
name|protoRep
init|=
name|type
operator|.
name|toProto
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setType
argument_list|(
name|protoRep
argument_list|)
expr_stmt|;
comment|// Serialize the type into the protobuf
switch|switch
condition|(
name|protoRep
condition|)
block|{
case|case
name|BOOLEAN
case|:
case|case
name|PRIMITIVE_BOOLEAN
case|:
name|builder
operator|.
name|setBoolValue
argument_list|(
operator|(
name|boolean
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE_STRING
case|:
case|case
name|STRING
case|:
name|builder
operator|.
name|setStringValue
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|PRIMITIVE_CHAR
case|:
case|case
name|CHARACTER
case|:
name|builder
operator|.
name|setStringValue
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE
case|:
case|case
name|PRIMITIVE_BYTE
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
name|Byte
operator|.
name|valueOf
argument_list|(
operator|(
name|byte
operator|)
name|value
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
case|case
name|PRIMITIVE_DOUBLE
case|:
name|builder
operator|.
name|setDoubleValue
argument_list|(
operator|(
name|double
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
case|case
name|PRIMITIVE_FLOAT
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|(
name|float
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
case|case
name|PRIMITIVE_INT
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PRIMITIVE_SHORT
case|:
case|case
name|SHORT
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
name|Short
operator|.
name|valueOf
argument_list|(
operator|(
name|short
operator|)
name|value
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
case|case
name|PRIMITIVE_LONG
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|JAVA_SQL_DATE
case|:
case|case
name|JAVA_SQL_TIME
case|:
comment|// Persisted as integers
name|builder
operator|.
name|setNumberValue
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|JAVA_SQL_TIMESTAMP
case|:
case|case
name|JAVA_UTIL_DATE
case|:
comment|// Persisted as longs
name|builder
operator|.
name|setNumberValue
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|BIG_INTEGER
case|:
name|byte
index|[]
name|bytes
init|=
operator|(
operator|(
name|BigInteger
operator|)
name|value
operator|)
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setBytesValues
argument_list|(
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BIG_DECIMAL
case|:
specifier|final
name|BigDecimal
name|bigDecimal
init|=
operator|(
name|BigDecimal
operator|)
name|value
decl_stmt|;
specifier|final
name|int
name|scale
init|=
name|bigDecimal
operator|.
name|scale
argument_list|()
decl_stmt|;
specifier|final
name|BigInteger
name|bigInt
init|=
name|bigDecimal
operator|.
name|toBigInteger
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setBytesValues
argument_list|(
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|bigInt
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setNumberValue
argument_list|(
name|scale
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMBER
case|:
name|builder
operator|.
name|setNumberValue
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|OBJECT
case|:
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
comment|// We can persist a null value through easily
name|builder
operator|.
name|setNull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Intentional fall-through to RTE because we can't serialize something we have no type
comment|// insight into.
case|case
name|UNRECOGNIZED
case|:
comment|// Fail?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unhandled value: "
operator|+
name|protoRep
operator|+
literal|" "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
default|default:
comment|// Fail?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown serialized type: "
operator|+
name|protoRep
argument_list|)
throw|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|TypedValue
name|fromProto
parameter_list|(
name|Common
operator|.
name|TypedValue
name|proto
parameter_list|)
block|{
name|ColumnMetaData
operator|.
name|Rep
name|rep
init|=
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|fromProto
argument_list|(
name|proto
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
literal|null
decl_stmt|;
comment|// Deserialize the value again
switch|switch
condition|(
name|proto
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
case|case
name|PRIMITIVE_BOOLEAN
case|:
name|value
operator|=
name|proto
operator|.
name|getBoolValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|BYTE_STRING
case|:
case|case
name|STRING
case|:
name|value
operator|=
name|proto
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|PRIMITIVE_CHAR
case|:
case|case
name|CHARACTER
case|:
name|value
operator|=
name|proto
operator|.
name|getStringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE
case|:
case|case
name|PRIMITIVE_BYTE
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
operator|.
name|byteValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
case|case
name|PRIMITIVE_DOUBLE
case|:
name|value
operator|=
name|proto
operator|.
name|getDoubleValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
case|case
name|PRIMITIVE_FLOAT
case|:
name|value
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
case|case
name|PRIMITIVE_INT
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|PRIMITIVE_SHORT
case|:
case|case
name|SHORT
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
operator|.
name|shortValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|LONG
case|:
case|case
name|PRIMITIVE_LONG
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|JAVA_SQL_DATE
case|:
case|case
name|JAVA_SQL_TIME
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|JAVA_SQL_TIMESTAMP
case|:
case|case
name|JAVA_UTIL_DATE
case|:
name|value
operator|=
name|proto
operator|.
name|getNumberValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|BIG_INTEGER
case|:
name|value
operator|=
operator|new
name|BigInteger
argument_list|(
name|proto
operator|.
name|getBytesValues
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|BIG_DECIMAL
case|:
name|BigInteger
name|bigInt
init|=
operator|new
name|BigInteger
argument_list|(
name|proto
operator|.
name|getBytesValues
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|=
operator|new
name|BigDecimal
argument_list|(
name|bigInt
argument_list|,
operator|(
name|int
operator|)
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMBER
case|:
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|proto
operator|.
name|getNumberValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|OBJECT
case|:
if|if
condition|(
name|proto
operator|.
name|getNull
argument_list|()
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
break|break;
block|}
comment|// Intentional fall through to RTE. If we sent an object over the wire, it could only
comment|// possibly be null (at this point). Anything else has to be an error.
case|case
name|UNRECOGNIZED
case|:
comment|// Fail?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unhandled type: "
operator|+
name|proto
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
default|default:
comment|// Fail?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown type: "
operator|+
name|proto
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|TypedValue
argument_list|(
name|rep
argument_list|,
name|value
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|type
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|type
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|value
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
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
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|TypedValue
condition|)
block|{
name|TypedValue
name|other
init|=
operator|(
name|TypedValue
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|other
operator|.
name|type
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|other
operator|.
name|value
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|value
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End TypedValue.java
end_comment

end_unit

