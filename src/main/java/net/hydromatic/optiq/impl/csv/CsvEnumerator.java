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
name|impl
operator|.
name|csv
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
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
name|Date
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
comment|/** Enumerator that reads from a CSV file. */
end_comment

begin_class
class|class
name|CsvEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|CSVReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|RowConverter
name|rowConverter
decl_stmt|;
specifier|private
name|Object
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
literal|"hh:mm:ss"
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
literal|"yyyy-MM-dd hh:mm:ss"
argument_list|,
name|gmt
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CsvEnumerator
parameter_list|(
name|File
name|file
parameter_list|,
name|CsvFieldType
index|[]
name|fieldTypes
parameter_list|)
block|{
name|this
argument_list|(
name|file
argument_list|,
name|fieldTypes
argument_list|,
name|identityList
argument_list|(
name|fieldTypes
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CsvEnumerator
parameter_list|(
name|File
name|file
parameter_list|,
name|CsvFieldType
index|[]
name|fieldTypes
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|rowConverter
operator|=
name|fields
operator|.
name|length
operator|==
literal|1
condition|?
operator|new
name|SingleColumnRowConverter
argument_list|(
name|fieldTypes
index|[
name|fields
index|[
literal|0
index|]
index|]
argument_list|,
name|fields
index|[
literal|0
index|]
argument_list|)
else|:
operator|new
name|ArrayRowConverter
argument_list|(
name|fieldTypes
argument_list|,
name|fields
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|CSVReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
name|Object
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
specifier|private
specifier|abstract
specifier|static
class|class
name|RowConverter
block|{
specifier|abstract
name|Object
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
default|default:
case|case
name|STRING
case|:
return|return
name|string
return|;
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
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|ArrayRowConverter
extends|extends
name|RowConverter
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
specifier|private
name|ArrayRowConverter
parameter_list|(
name|CsvFieldType
index|[]
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
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
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
block|}
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

