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
name|java
operator|.
name|io
operator|.
name|*
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
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|CSVReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|CsvFieldType
index|[]
name|fieldTypes
decl_stmt|;
specifier|private
name|Object
index|[]
name|current
decl_stmt|;
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
operator|.
name|fieldTypes
operator|=
name|fieldTypes
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
index|[]
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
specifier|private
name|Object
index|[]
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
name|fieldTypes
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
name|fieldTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|objects
index|[
name|i
index|]
operator|=
name|convert
argument_list|(
name|fieldTypes
index|[
name|i
index|]
argument_list|,
name|strings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
specifier|private
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
block|}
end_class

end_unit

