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
name|file
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
name|io
operator|.
name|input
operator|.
name|Tailer
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
name|io
operator|.
name|input
operator|.
name|TailerListener
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
name|io
operator|.
name|input
operator|.
name|TailerListenerAdapter
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
name|CSVParser
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
name|Closeable
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_comment
comment|/**  * Extension to {@link CSVReader} that can read newly appended file content.  */
end_comment

begin_class
class|class
name|CsvStreamReader
extends|extends
name|CSVReader
implements|implements
name|Closeable
block|{
specifier|protected
name|CSVParser
name|parser
decl_stmt|;
specifier|protected
name|int
name|skipLines
decl_stmt|;
specifier|protected
name|Tailer
name|tailer
decl_stmt|;
specifier|protected
name|Queue
argument_list|<
name|String
argument_list|>
name|contentQueue
decl_stmt|;
comment|/**    * The default line to start reading.    */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP_LINES
init|=
literal|0
decl_stmt|;
comment|/**    * The default file monitor delay.    */
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MONITOR_DELAY
init|=
literal|2000
decl_stmt|;
name|CsvStreamReader
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
name|this
argument_list|(
name|source
argument_list|,
name|CSVParser
operator|.
name|DEFAULT_SEPARATOR
argument_list|,
name|CSVParser
operator|.
name|DEFAULT_QUOTE_CHARACTER
argument_list|,
name|CSVParser
operator|.
name|DEFAULT_ESCAPE_CHARACTER
argument_list|,
name|DEFAULT_SKIP_LINES
argument_list|,
name|CSVParser
operator|.
name|DEFAULT_STRICT_QUOTES
argument_list|,
name|CSVParser
operator|.
name|DEFAULT_IGNORE_LEADING_WHITESPACE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a CsvStreamReader with supplied separator and quote char.    *    * @param source The file to an underlying CSV source    * @param separator The delimiter to use for separating entries    * @param quoteChar The character to use for quoted elements    * @param escape The character to use for escaping a separator or quote    * @param line The line number to skip for start reading    * @param strictQuotes Sets if characters outside the quotes are ignored    * @param ignoreLeadingWhiteSpace If true, parser should ignore    *  white space before a quote in a field    */
specifier|private
name|CsvStreamReader
parameter_list|(
name|Source
name|source
parameter_list|,
name|char
name|separator
parameter_list|,
name|char
name|quoteChar
parameter_list|,
name|char
name|escape
parameter_list|,
name|int
name|line
parameter_list|,
name|boolean
name|strictQuotes
parameter_list|,
name|boolean
name|ignoreLeadingWhiteSpace
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// dummy call to base constructor
name|contentQueue
operator|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
expr_stmt|;
name|TailerListener
name|listener
init|=
operator|new
name|CsvContentListener
argument_list|(
name|contentQueue
argument_list|)
decl_stmt|;
name|tailer
operator|=
name|Tailer
operator|.
name|create
argument_list|(
name|source
operator|.
name|file
argument_list|()
argument_list|,
name|listener
argument_list|,
name|DEFAULT_MONITOR_DELAY
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|CSVParser
argument_list|(
name|separator
argument_list|,
name|quoteChar
argument_list|,
name|escape
argument_list|,
name|strictQuotes
argument_list|,
name|ignoreLeadingWhiteSpace
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipLines
operator|=
name|line
expr_stmt|;
try|try
block|{
comment|// wait for tailer to capture data
name|Thread
operator|.
name|sleep
argument_list|(
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
block|}
comment|/**    * Reads the next line from the buffer and converts to a string array.    *    * @return a string array with each comma-separated element as a separate entry.    *    * @throws IOException if bad things happen during the read    */
annotation|@
name|Override
specifier|public
name|String
index|[]
name|readNext
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|result
init|=
literal|null
decl_stmt|;
do|do
block|{
name|String
name|nextLine
init|=
name|getNextLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextLine
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|r
init|=
name|parser
operator|.
name|parseLineMulti
argument_list|(
name|nextLine
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|r
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|t
init|=
operator|new
name|String
index|[
name|result
operator|.
name|length
operator|+
name|r
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|r
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
name|result
operator|.
name|length
argument_list|,
name|r
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|parser
operator|.
name|isPending
argument_list|()
condition|)
do|;
return|return
name|result
return|;
block|}
comment|/**    * Reads the next line from the file.    *    * @return the next line from the file without trailing newline    *    * @throws IOException if bad things happen during the read    */
specifier|private
name|String
name|getNextLine
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|contentQueue
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**    * Closes the underlying reader.    *    * @throws IOException if the close fails    */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
block|}
comment|/** Watches for content being appended to a CSV file. */
specifier|private
specifier|static
class|class
name|CsvContentListener
extends|extends
name|TailerListenerAdapter
block|{
specifier|final
name|Queue
argument_list|<
name|String
argument_list|>
name|contentQueue
decl_stmt|;
name|CsvContentListener
parameter_list|(
name|Queue
argument_list|<
name|String
argument_list|>
name|contentQueue
parameter_list|)
block|{
name|this
operator|.
name|contentQueue
operator|=
name|contentQueue
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|this
operator|.
name|contentQueue
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

