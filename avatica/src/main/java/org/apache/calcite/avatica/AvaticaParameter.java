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
name|ByteString
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
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
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
name|sql
operator|.
name|NClob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Ref
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|RowId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLXML
import|;
end_import

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
name|sql
operator|.
name|Timestamp
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
comment|/**  * Metadata for a parameter.  */
end_comment

begin_class
specifier|public
class|class
name|AvaticaParameter
block|{
specifier|public
specifier|final
name|boolean
name|signed
decl_stmt|;
specifier|public
specifier|final
name|int
name|precision
decl_stmt|;
specifier|public
specifier|final
name|int
name|scale
decl_stmt|;
specifier|public
specifier|final
name|int
name|parameterType
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|public
specifier|final
name|String
name|className
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Value that means the parameter has been set to null.    * If value is null, parameter has not been set. */
specifier|public
specifier|static
specifier|final
name|Object
name|DUMMY_VALUE
init|=
name|Dummy
operator|.
name|INSTANCE
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|AvaticaParameter
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"signed"
argument_list|)
name|boolean
name|signed
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"precision"
argument_list|)
name|int
name|precision
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"scale"
argument_list|)
name|int
name|scale
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"parameterType"
argument_list|)
name|int
name|parameterType
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"typeName"
argument_list|)
name|String
name|typeName
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"className"
argument_list|)
name|String
name|className
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"name"
argument_list|)
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|signed
operator|=
name|signed
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|scale
operator|=
name|scale
expr_stmt|;
name|this
operator|.
name|parameterType
operator|=
name|parameterType
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setByte
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|byte
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|void
name|setChar
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|char
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|void
name|setShort
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|short
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|void
name|setInt
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|void
name|setLong
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|long
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|void
name|setBoolean
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|boolean
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|private
specifier|static
name|Object
name|wrap
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
name|DUMMY_VALUE
return|;
block|}
return|return
name|o
return|;
block|}
specifier|public
name|boolean
name|isSet
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|slots
index|[
name|index
index|]
operator|!=
literal|null
return|;
block|}
specifier|public
name|void
name|setRowId
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|RowId
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNString
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|String
name|o
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|value
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|NClob
name|value
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setSQLXML
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|SQLXML
name|xmlObject
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|xmlObject
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|value
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setUnicodeStream
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|InputStream
name|x
parameter_list|,
name|int
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Timestamp
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Time
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFloat
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|float
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDouble
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|double
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBigDecimal
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|BigDecimal
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setString
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|String
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBytes
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|byte
index|[]
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|x
operator|==
literal|null
condition|?
name|DUMMY_VALUE
else|:
operator|new
name|ByteString
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Date
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Date
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Object
name|x
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|wrap
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|sqlType
parameter_list|)
block|{
name|slots
index|[
name|index
index|]
operator|=
name|DUMMY_VALUE
expr_stmt|;
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Time
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setRef
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Ref
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Blob
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Clob
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setArray
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Array
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Timestamp
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|sqlType
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setURL
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|URL
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
index|[]
name|slots
parameter_list|,
name|int
name|index
parameter_list|,
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|,
name|int
name|scaleOrLength
parameter_list|)
block|{
block|}
comment|/** Singleton value to denote parameters that have been set to null (as    * opposed to not set).    *    *<p>Not a valid value for a parameter.    *    *<p>As an enum, it is serializable by Jackson. */
specifier|private
enum|enum
name|Dummy
block|{
name|INSTANCE
block|}
block|}
end_class

begin_comment
comment|// End AvaticaParameter.java
end_comment

end_unit

