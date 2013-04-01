begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|RoundingMode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

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
name|sql
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
name|*
import|;
end_import

begin_comment
comment|/**  * Base class for implementing a cursor.  *  *<p>Derived class needs to provide {@link Getter} and can override  * {@link Accessor} implementations if it wishes.</p>  *  * @author jhyde  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MILLIS_PER_DAY
init|=
literal|86400000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Calendar
name|LOCAL_CALENDAR
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|/**      * Slot into which each accessor should write whether the      * value returned was null.      */
specifier|protected
specifier|final
name|boolean
index|[]
name|wasNull
init|=
block|{
literal|false
block|}
decl_stmt|;
specifier|protected
name|AbstractCursor
parameter_list|()
block|{
block|}
specifier|public
name|List
argument_list|<
name|Accessor
argument_list|>
name|createAccessors
parameter_list|(
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|types
parameter_list|)
block|{
name|List
argument_list|<
name|Accessor
argument_list|>
name|accessors
init|=
operator|new
name|ArrayList
argument_list|<
name|Accessor
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnMetaData
name|type
range|:
name|types
control|)
block|{
name|accessors
operator|.
name|add
argument_list|(
name|createAccessor
argument_list|(
name|type
argument_list|,
name|accessors
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|accessors
return|;
block|}
specifier|protected
name|Accessor
name|createAccessor
parameter_list|(
name|ColumnMetaData
name|type
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
comment|// Create an accessor appropriate to the underlying type; the accessor
comment|// can convert to any type in the same family.
name|Getter
name|getter
init|=
name|createGetter
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
operator|.
name|type
condition|)
block|{
case|case
name|Types
operator|.
name|TINYINT
case|:
return|return
operator|new
name|ByteAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|SMALLINT
case|:
return|return
operator|new
name|ShortAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|INTEGER
case|:
return|return
operator|new
name|IntAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|BIGINT
case|:
return|return
operator|new
name|LongAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
return|return
operator|new
name|BooleanAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|FLOAT
case|:
case|case
name|Types
operator|.
name|REAL
case|:
return|return
operator|new
name|FloatAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoubleAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|BigDecimalAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|CHAR
case|:
return|return
operator|new
name|FixedStringAccessor
argument_list|(
name|getter
argument_list|,
name|type
operator|.
name|displaySize
argument_list|)
return|;
case|case
name|Types
operator|.
name|VARCHAR
case|:
return|return
operator|new
name|StringAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|BINARY
case|:
case|case
name|Types
operator|.
name|VARBINARY
case|:
return|return
operator|new
name|BinaryAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|DATE
case|:
return|return
operator|new
name|DateAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|TIME
case|:
return|return
operator|new
name|TimeAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
return|return
operator|new
name|TimestampAccessor
argument_list|(
name|getter
argument_list|)
return|;
case|case
name|Types
operator|.
name|JAVA_OBJECT
case|:
case|case
name|Types
operator|.
name|ARRAY
case|:
case|case
name|Types
operator|.
name|OTHER
case|:
comment|// e.g. map
return|return
operator|new
name|ObjectAccessor
argument_list|(
name|getter
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown type "
operator|+
name|type
operator|.
name|type
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|abstract
name|Getter
name|createGetter
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
function_decl|;
specifier|static
class|class
name|AccessorImpl
implements|implements
name|Accessor
block|{
specifier|protected
specifier|final
name|Getter
name|getter
decl_stmt|;
specifier|public
name|AccessorImpl
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|this
operator|.
name|getter
operator|=
name|getter
expr_stmt|;
block|}
specifier|public
name|String
name|getString
parameter_list|()
block|{
specifier|final
name|Object
name|o
init|=
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"boolean"
argument_list|)
throw|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"byte"
argument_list|)
throw|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"short"
argument_list|)
throw|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"int"
argument_list|)
throw|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"long"
argument_list|)
throw|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"float"
argument_list|)
throw|;
block|}
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"double"
argument_list|)
throw|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"BigDecimal"
argument_list|)
throw|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|(
name|int
name|scale
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"BigDecimal with scale"
argument_list|)
throw|;
block|}
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"byte[]"
argument_list|)
throw|;
block|}
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
return|return
name|getDate
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Time
name|getTime
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Timestamp
name|getTimestamp
parameter_list|()
block|{
return|return
name|getTimestamp
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getAsciiStream
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"InputStream (ascii)"
argument_list|)
throw|;
block|}
specifier|public
name|InputStream
name|getUnicodeStream
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"InputStream (unicode)"
argument_list|)
throw|;
block|}
specifier|public
name|InputStream
name|getBinaryStream
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"InputStream (binary)"
argument_list|)
throw|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
return|return
name|getter
operator|.
name|getObject
argument_list|()
return|;
block|}
specifier|public
name|Reader
name|getCharacterStream
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Reader"
argument_list|)
throw|;
block|}
specifier|private
name|RuntimeException
name|cannotConvert
parameter_list|(
name|String
name|targetType
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"cannot convert to "
operator|+
name|targetType
argument_list|)
return|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Object (with map)"
argument_list|)
throw|;
block|}
specifier|public
name|Ref
name|getRef
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Ref"
argument_list|)
throw|;
block|}
specifier|public
name|Blob
name|getBlob
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Blob"
argument_list|)
throw|;
block|}
specifier|public
name|Clob
name|getClob
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Clob"
argument_list|)
throw|;
block|}
specifier|public
name|Array
name|getArray
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Array"
argument_list|)
throw|;
block|}
specifier|public
name|Date
name|getDate
parameter_list|(
name|Calendar
name|cal
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Date"
argument_list|)
throw|;
block|}
specifier|public
name|Time
name|getTime
parameter_list|(
name|Calendar
name|cal
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Time"
argument_list|)
throw|;
block|}
specifier|public
name|Timestamp
name|getTimestamp
parameter_list|(
name|Calendar
name|cal
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Timestamp"
argument_list|)
throw|;
block|}
specifier|public
name|URL
name|getURL
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"URL"
argument_list|)
throw|;
block|}
specifier|public
name|NClob
name|getNClob
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"NClob"
argument_list|)
throw|;
block|}
specifier|public
name|SQLXML
name|getSQLXML
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"SQLXML"
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getNString
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"NString"
argument_list|)
throw|;
block|}
specifier|public
name|Reader
name|getNCharacterStream
parameter_list|()
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"NCharacterStream"
argument_list|)
throw|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getObject
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
throw|throw
name|cannotConvert
argument_list|(
literal|"Object (with type)"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Accessor of exact numeric values. The subclass must implement the      * {@link #getLong()} method.      */
specifier|private
specifier|static
specifier|abstract
class|class
name|ExactNumericAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|ExactNumericAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|(
name|int
name|scale
parameter_list|)
block|{
specifier|final
name|long
name|v
init|=
name|getLong
argument_list|()
decl_stmt|;
return|return
name|v
operator|==
literal|0
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|DOWN
argument_list|)
return|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|()
block|{
specifier|final
name|long
name|val
init|=
name|getLong
argument_list|()
decl_stmt|;
return|return
name|val
operator|==
literal|0
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|getLong
argument_list|()
return|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
return|return
name|getLong
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
name|long
name|getLong
parameter_list|()
function_decl|;
specifier|public
name|int
name|getInt
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|getLong
argument_list|()
return|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|getLong
argument_list|()
return|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
return|return
operator|(
name|byte
operator|)
name|getLong
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|getLong
argument_list|()
operator|!=
literal|0d
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Boolean};      * corresponds to {@link java.sql.Types#BOOLEAN}.      */
specifier|private
specifier|static
class|class
name|BooleanAccessor
extends|extends
name|ExactNumericAccessor
block|{
specifier|public
name|BooleanAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
name|Boolean
name|o
init|=
operator|(
name|Boolean
operator|)
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|!=
literal|null
operator|&&
name|o
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|()
condition|?
literal|1
else|:
literal|0
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Byte};      * corresponds to {@link java.sql.Types#TINYINT}.      */
specifier|private
specifier|static
class|class
name|ByteAccessor
extends|extends
name|ExactNumericAccessor
block|{
specifier|public
name|ByteAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
name|Byte
name|o
init|=
operator|(
name|Byte
operator|)
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0
else|:
name|o
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|getByte
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Short};      * corresponds to {@link java.sql.Types#SMALLINT}.      */
specifier|private
specifier|static
class|class
name|ShortAccessor
extends|extends
name|ExactNumericAccessor
block|{
specifier|public
name|ShortAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
name|Short
name|o
init|=
operator|(
name|Short
operator|)
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0
else|:
name|o
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|getShort
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is an {@link Integer};      * corresponds to {@link java.sql.Types#INTEGER}.      */
specifier|private
specifier|static
class|class
name|IntAccessor
extends|extends
name|ExactNumericAccessor
block|{
specifier|public
name|IntAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
name|Integer
name|o
init|=
operator|(
name|Integer
operator|)
name|super
operator|.
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0
else|:
name|o
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|getInt
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Long};      * corresponds to {@link java.sql.Types#BIGINT}.      */
specifier|private
specifier|static
class|class
name|LongAccessor
extends|extends
name|ExactNumericAccessor
block|{
specifier|public
name|LongAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
name|Long
name|o
init|=
operator|(
name|Long
operator|)
name|super
operator|.
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0
else|:
name|o
return|;
block|}
block|}
comment|/**      * Accessor of values that are {@link Double} or null.      */
specifier|private
specifier|static
specifier|abstract
class|class
name|ApproximateNumericAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|ApproximateNumericAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|(
name|int
name|scale
parameter_list|)
block|{
specifier|final
name|double
name|v
init|=
name|getDouble
argument_list|()
decl_stmt|;
return|return
name|v
operator|==
literal|0d
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|DOWN
argument_list|)
return|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|()
block|{
specifier|final
name|double
name|v
init|=
name|getDouble
argument_list|()
decl_stmt|;
return|return
name|v
operator|==
literal|0
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|double
name|getDouble
parameter_list|()
function_decl|;
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
return|return
operator|(
name|float
operator|)
name|getDouble
argument_list|()
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
operator|(
name|long
operator|)
name|getDouble
argument_list|()
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|getDouble
argument_list|()
return|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|getDouble
argument_list|()
return|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
return|return
operator|(
name|byte
operator|)
name|getDouble
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|getDouble
argument_list|()
operator|!=
literal|0
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Float};      * corresponds to {@link java.sql.Types#FLOAT}.      */
specifier|private
specifier|static
class|class
name|FloatAccessor
extends|extends
name|ApproximateNumericAccessor
block|{
specifier|public
name|FloatAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
name|Float
name|o
init|=
operator|(
name|Float
operator|)
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0f
else|:
name|o
return|;
block|}
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|getFloat
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link Float};      * corresponds to {@link java.sql.Types#FLOAT}.      */
specifier|private
specifier|static
class|class
name|DoubleAccessor
extends|extends
name|ApproximateNumericAccessor
block|{
specifier|public
name|DoubleAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
name|Double
name|o
init|=
operator|(
name|Double
operator|)
name|getObject
argument_list|()
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|0d
else|:
name|o
return|;
block|}
block|}
comment|/**      * Accessor of exact numeric values. The subclass must implement the      * {@link #getLong()} method.      */
specifier|private
specifier|static
specifier|abstract
class|class
name|BigNumberAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|BigNumberAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|Number
name|getNumber
parameter_list|()
function_decl|;
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0d
else|:
name|number
operator|.
name|doubleValue
argument_list|()
return|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0f
else|:
name|number
operator|.
name|floatValue
argument_list|()
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0l
else|:
name|number
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0
else|:
name|number
operator|.
name|intValue
argument_list|()
return|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0
else|:
name|number
operator|.
name|shortValue
argument_list|()
return|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|==
literal|null
condition|?
literal|0
else|:
name|number
operator|.
name|byteValue
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
name|Number
name|number
init|=
name|getNumber
argument_list|()
decl_stmt|;
return|return
name|number
operator|!=
literal|null
operator|&&
name|number
operator|.
name|doubleValue
argument_list|()
operator|!=
literal|0
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link BigDecimal};      * corresponds to {@link java.sql.Types#DECIMAL}.      */
specifier|private
specifier|static
class|class
name|BigDecimalAccessor
extends|extends
name|BigNumberAccessor
block|{
specifier|public
name|BigDecimalAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Number
name|getNumber
parameter_list|()
block|{
return|return
operator|(
name|Number
operator|)
name|getObject
argument_list|()
return|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|(
name|int
name|scale
parameter_list|)
block|{
return|return
operator|(
name|BigDecimal
operator|)
name|getObject
argument_list|()
return|;
block|}
specifier|public
name|BigDecimal
name|getBigDecimal
parameter_list|()
block|{
return|return
operator|(
name|BigDecimal
operator|)
name|getObject
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link String};      * corresponds to {@link java.sql.Types#CHAR} and {@link java.sql.Types#VARCHAR}.      */
specifier|private
specifier|static
class|class
name|StringAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|StringAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|getObject
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a {@link String};      * corresponds to {@link java.sql.Types#CHAR}.      */
specifier|private
specifier|static
class|class
name|FixedStringAccessor
extends|extends
name|StringAccessor
block|{
specifier|private
specifier|final
name|Spacer
name|spacer
decl_stmt|;
specifier|public
name|FixedStringAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
name|this
operator|.
name|spacer
operator|=
operator|new
name|Spacer
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getString
parameter_list|()
block|{
name|String
name|s
init|=
name|super
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|spacer
operator|.
name|padRight
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is an array of      * {@code byte} values;      * corresponds to {@link java.sql.Types#BINARY} and {@link java.sql.Types#VARBINARY}.      */
specifier|private
specifier|static
class|class
name|BinaryAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|BinaryAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
operator|(
name|byte
index|[]
operator|)
name|getObject
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
name|byte
index|[]
name|bytes
init|=
name|getBytes
argument_list|()
decl_stmt|;
return|return
name|bytes
operator|==
literal|null
condition|?
literal|null
else|:
name|ByteString
operator|.
name|toString
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a DATE;      * corresponds to {@link java.sql.Types#DATE}.      */
specifier|private
specifier|static
class|class
name|DateAccessor
extends|extends
name|IntAccessor
block|{
specifier|public
name|DateAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
return|return
name|getDate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|getDate
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
block|{
name|int
name|vv
init|=
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|vv
operator|==
literal|0
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|v
init|=
operator|(
name|long
operator|)
name|vv
operator|*
name|MILLIS_PER_DAY
decl_stmt|;
if|if
condition|(
name|calendar
operator|!=
literal|null
condition|)
block|{
name|v
operator|-=
name|calendar
operator|.
name|getTimeZone
argument_list|()
operator|.
name|getOffset
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
name|v
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|getDate
argument_list|(
name|LOCAL_CALENDAR
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a Time;      * corresponds to {@link java.sql.Types#TIME}.      */
specifier|private
specifier|static
class|class
name|TimeAccessor
extends|extends
name|IntAccessor
block|{
specifier|public
name|TimeAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
return|return
name|getTime
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Time
name|getTime
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
block|{
name|int
name|v
init|=
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|calendar
operator|!=
literal|null
condition|)
block|{
name|v
operator|-=
name|calendar
operator|.
name|getTimeZone
argument_list|()
operator|.
name|getOffset
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Time
argument_list|(
name|v
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
name|LOCAL_CALENDAR
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is a TIMESTAMP;      * corresponds to {@link java.sql.Types#TIMESTAMP}.      */
specifier|private
specifier|static
class|class
name|TimestampAccessor
extends|extends
name|LongAccessor
block|{
specifier|public
name|TimestampAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
return|return
name|getTimestamp
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Timestamp
name|getTimestamp
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
block|{
name|long
name|v
init|=
name|getLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0L
operator|&&
name|getter
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|calendar
operator|!=
literal|null
condition|)
block|{
name|v
operator|-=
name|calendar
operator|.
name|getTimeZone
argument_list|()
operator|.
name|getOffset
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Timestamp
argument_list|(
name|v
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|getTimestamp
argument_list|(
name|LOCAL_CALENDAR
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Accessor that assumes that the underlying value is an OBJECT;      * corresponds to {@link java.sql.Types#JAVA_OBJECT}.      */
specifier|private
specifier|static
class|class
name|ObjectAccessor
extends|extends
name|AccessorImpl
block|{
specifier|public
name|ObjectAccessor
parameter_list|(
name|Getter
name|getter
parameter_list|)
block|{
name|super
argument_list|(
name|getter
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
interface|interface
name|Getter
block|{
name|Object
name|getObject
parameter_list|()
function_decl|;
name|boolean
name|wasNull
parameter_list|()
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractCursor.java
end_comment

end_unit

