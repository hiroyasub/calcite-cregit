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
name|adapter
operator|.
name|csv
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|linq4j
operator|.
name|Enumerator
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
name|type
operator|.
name|SqlTypeName
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
name|Pair
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
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|time
operator|.
name|FastDateFormat
import|;
end_import

begin_import
import|import
name|au
operator|.
name|com
operator|.
name|bytecode
operator|.
name|opencsv
operator|.
name|CSVReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|text
operator|.
name|ParseException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/** Enumerator that reads from a CSV file.  *  * @param<E> Row type  */
end_comment

begin_class
class|class
name|CsvEnumerator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|CSVReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|filterValues
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|cancelFlag
decl_stmt|;
specifier|private
specifier|final
name|RowConverter
argument_list|<
name|E
argument_list|>
name|rowConverter
decl_stmt|;
specifier|private
name|E
name|current
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FastDateFormat
name|TIME_FORMAT_DATE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FastDateFormat
name|TIME_FORMAT_TIME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FastDateFormat
name|TIME_FORMAT_TIMESTAMP
decl_stmt|;
static|static
block|{
specifier|final
name|TimeZone
name|gmt
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
name|TIME_FORMAT_DATE
operator|=
name|FastDateFormat
operator|.
name|getInstance
argument_list|(
literal|"yyyy-MM-dd"
argument_list|,
name|gmt
argument_list|)
expr_stmt|;
name|TIME_FORMAT_TIME
operator|=
name|FastDateFormat
operator|.
name|getInstance
argument_list|(
literal|"HH:mm:ss"
argument_list|,
name|gmt
argument_list|)
expr_stmt|;
name|TIME_FORMAT_TIMESTAMP
operator|=
name|FastDateFormat
operator|.
name|getInstance
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|,
name|gmt
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CsvEnumerator
parameter_list|(
name|Source
name|source
parameter_list|,
name|AtomicBoolean
name|cancelFlag
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|)
block|{
name|this
argument_list|(
name|source
argument_list|,
name|cancelFlag
argument_list|,
name|fieldTypes
argument_list|,
name|identityList
argument_list|(
name|fieldTypes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CsvEnumerator
parameter_list|(
name|Source
name|source
parameter_list|,
name|AtomicBoolean
name|cancelFlag
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
comment|//noinspection unchecked
name|this
argument_list|(
name|source
argument_list|,
name|cancelFlag
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
operator|(
name|RowConverter
argument_list|<
name|E
argument_list|>
operator|)
name|converter
argument_list|(
name|fieldTypes
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CsvEnumerator
parameter_list|(
name|Source
name|source
parameter_list|,
name|AtomicBoolean
name|cancelFlag
parameter_list|,
name|boolean
name|stream
parameter_list|,
name|String
index|[]
name|filterValues
parameter_list|,
name|RowConverter
argument_list|<
name|E
argument_list|>
name|rowConverter
parameter_list|)
block|{
name|this
operator|.
name|cancelFlag
operator|=
name|cancelFlag
expr_stmt|;
name|this
operator|.
name|rowConverter
operator|=
name|rowConverter
expr_stmt|;
name|this
operator|.
name|filterValues
operator|=
name|filterValues
expr_stmt|;
try|try
block|{
if|if
condition|(
name|stream
condition|)
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|CsvStreamReader
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|reader
operator|=
name|openCsv
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reader
operator|.
name|readNext
argument_list|()
expr_stmt|;
comment|// skip header row
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|RowConverter
argument_list|<
name|?
argument_list|>
name|converter
parameter_list|(
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|field
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
return|return
operator|new
name|SingleColumnRowConverter
argument_list|(
name|fieldTypes
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|,
name|field
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ArrayRowConverter
argument_list|(
name|fieldTypes
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
comment|/** Deduces the names and types of a table's columns by reading the first line    * of a CSV file. */
specifier|static
name|RelDataType
name|deduceRowType
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Source
name|source
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|)
block|{
return|return
name|deduceRowType
argument_list|(
name|typeFactory
argument_list|,
name|source
argument_list|,
name|fieldTypes
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Deduces the names and types of a table's columns by reading the first line   * of a CSV file. */
specifier|static
name|RelDataType
name|deduceRowType
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Source
name|source
parameter_list|,
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|,
name|Boolean
name|stream
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|CSVReader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stream
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|CsvSchemaFactory
operator|.
name|ROWTIME_COLUMN_NAME
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|=
name|openCsv
argument_list|(
name|source
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|strings
init|=
name|reader
operator|.
name|readNext
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|CsvFieldType
name|fieldType
decl_stmt|;
specifier|final
name|int
name|colon
init|=
name|string
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|>=
literal|0
condition|)
block|{
name|name
operator|=
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|String
name|typeString
init|=
name|string
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
decl_stmt|;
name|fieldType
operator|=
name|CsvFieldType
operator|.
name|of
argument_list|(
name|typeString
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: Found unknown type: "
operator|+
name|typeString
operator|+
literal|" in file: "
operator|+
name|source
operator|.
name|path
argument_list|()
operator|+
literal|" for column: "
operator|+
name|name
operator|+
literal|". Will assume the type of column is string"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|name
operator|=
name|string
expr_stmt|;
name|fieldType
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|type
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|fieldType
operator|.
name|toType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldTypes
operator|!=
literal|null
condition|)
block|{
name|fieldTypes
operator|.
name|add
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
literal|"line"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Pair
operator|.
name|zip
argument_list|(
name|names
argument_list|,
name|types
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CSVReader
name|openCsv
parameter_list|(
name|Source
name|source
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Reader
name|fileReader
init|=
name|source
operator|.
name|reader
argument_list|()
decl_stmt|;
return|return
operator|new
name|CSVReader
argument_list|(
name|fileReader
argument_list|)
return|;
block|}
specifier|public
name|E
name|current
parameter_list|()
block|{
return|return
name|current
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
name|outer
label|:
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|cancelFlag
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|String
index|[]
name|strings
init|=
name|reader
operator|.
name|readNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|strings
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|reader
operator|instanceof
name|CsvStreamReader
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|CsvStreamReader
operator|.
name|DEFAULT_MONITOR_DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
continue|continue;
block|}
name|current
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|filterValues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|filterValue
init|=
name|filterValues
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|filterValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|filterValue
operator|.
name|equals
argument_list|(
name|strings
index|[
name|i
index|]
argument_list|)
condition|)
block|{
continue|continue
name|outer
continue|;
block|}
block|}
block|}
block|}
name|current
operator|=
name|rowConverter
operator|.
name|convertRow
argument_list|(
name|strings
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error closing CSV reader"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Returns an array of integers {0, ..., n - 1}. */
specifier|static
name|int
index|[]
name|identityList
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
index|[]
name|integers
init|=
operator|new
name|int
index|[
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|integers
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|integers
return|;
block|}
comment|/** Row converter. */
specifier|abstract
specifier|static
class|class
name|RowConverter
parameter_list|<
name|E
parameter_list|>
block|{
specifier|abstract
name|E
name|convertRow
parameter_list|(
name|String
index|[]
name|rows
parameter_list|)
function_decl|;
specifier|protected
name|Object
name|convert
parameter_list|(
name|CsvFieldType
name|fieldType
parameter_list|,
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
return|return
name|string
return|;
block|}
switch|switch
condition|(
name|fieldType
condition|)
block|{
case|case
name|BOOLEAN
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|string
argument_list|)
return|;
case|case
name|BYTE
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|string
argument_list|)
return|;
case|case
name|SHORT
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|string
argument_list|)
return|;
case|case
name|INT
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|string
argument_list|)
return|;
case|case
name|LONG
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|string
argument_list|)
return|;
case|case
name|FLOAT
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|string
argument_list|)
return|;
case|case
name|DOUBLE
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|string
argument_list|)
return|;
case|case
name|DATE
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|Date
name|date
init|=
name|TIME_FORMAT_DATE
operator|.
name|parse
argument_list|(
name|string
argument_list|)
decl_stmt|;
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|TIME
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|Date
name|date
init|=
name|TIME_FORMAT_TIME
operator|.
name|parse
argument_list|(
name|string
argument_list|)
decl_stmt|;
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Time
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|TIMESTAMP
case|:
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|Date
name|date
init|=
name|TIME_FORMAT_TIMESTAMP
operator|.
name|parse
argument_list|(
name|string
argument_list|)
decl_stmt|;
return|return
operator|new
name|java
operator|.
name|sql
operator|.
name|Timestamp
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
case|case
name|STRING
case|:
default|default:
return|return
name|string
return|;
block|}
block|}
block|}
comment|/** Array row converter. */
specifier|static
class|class
name|ArrayRowConverter
extends|extends
name|RowConverter
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|CsvFieldType
index|[]
name|fieldTypes
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|fields
decl_stmt|;
comment|//whether the row to convert is from a stream
specifier|private
specifier|final
name|boolean
name|stream
decl_stmt|;
name|ArrayRowConverter
parameter_list|(
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
operator|.
name|toArray
argument_list|(
operator|new
name|CsvFieldType
index|[
name|fieldTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|stream
operator|=
literal|false
expr_stmt|;
block|}
name|ArrayRowConverter
parameter_list|(
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
parameter_list|,
name|int
index|[]
name|fields
parameter_list|,
name|boolean
name|stream
parameter_list|)
block|{
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
operator|.
name|toArray
argument_list|(
operator|new
name|CsvFieldType
index|[
name|fieldTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
specifier|public
name|Object
index|[]
name|convertRow
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
if|if
condition|(
name|stream
condition|)
block|{
return|return
name|convertStreamRow
argument_list|(
name|strings
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|convertNormalRow
argument_list|(
name|strings
argument_list|)
return|;
block|}
block|}
specifier|public
name|Object
index|[]
name|convertNormalRow
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|fields
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|objects
index|[
name|i
index|]
operator|=
name|convert
argument_list|(
name|fieldTypes
index|[
name|field
index|]
argument_list|,
name|strings
index|[
name|field
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
specifier|public
name|Object
index|[]
name|convertStreamRow
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|fields
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|objects
index|[
literal|0
index|]
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|objects
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|convert
argument_list|(
name|fieldTypes
index|[
name|field
index|]
argument_list|,
name|strings
index|[
name|field
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
block|}
comment|/** Single column row converter. */
specifier|private
specifier|static
class|class
name|SingleColumnRowConverter
extends|extends
name|RowConverter
block|{
specifier|private
specifier|final
name|CsvFieldType
name|fieldType
decl_stmt|;
specifier|private
specifier|final
name|int
name|fieldIndex
decl_stmt|;
specifier|private
name|SingleColumnRowConverter
parameter_list|(
name|CsvFieldType
name|fieldType
parameter_list|,
name|int
name|fieldIndex
parameter_list|)
block|{
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
name|this
operator|.
name|fieldIndex
operator|=
name|fieldIndex
expr_stmt|;
block|}
specifier|public
name|Object
name|convertRow
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
return|return
name|convert
argument_list|(
name|fieldType
argument_list|,
name|strings
index|[
name|fieldIndex
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CsvEnumerator.java
end_comment

end_unit

