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
name|jsoup
operator|.
name|Jsoup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|nodes
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|nodes
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|select
operator|.
name|Elements
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Scrapes HTML tables from URLs using Jsoup.  */
end_comment

begin_class
specifier|public
class|class
name|FileReader
implements|implements
name|Iterable
argument_list|<
name|Elements
argument_list|>
block|{
specifier|private
specifier|final
name|Source
name|source
decl_stmt|;
specifier|private
specifier|final
name|String
name|selector
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|index
decl_stmt|;
specifier|private
specifier|final
name|Charset
name|charset
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
specifier|private
name|Element
name|tableElement
decl_stmt|;
specifier|private
name|Elements
name|headings
decl_stmt|;
specifier|public
name|FileReader
parameter_list|(
name|Source
name|source
parameter_list|,
name|String
name|selector
parameter_list|,
name|Integer
name|index
parameter_list|)
throws|throws
name|FileReaderException
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileReaderException
argument_list|(
literal|"URL must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
specifier|public
name|FileReader
parameter_list|(
name|Source
name|source
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|FileReaderException
block|{
name|this
argument_list|(
name|source
argument_list|,
name|selector
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileReader
parameter_list|(
name|Source
name|source
parameter_list|)
throws|throws
name|FileReaderException
block|{
name|this
argument_list|(
name|source
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|getTable
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Document
name|doc
decl_stmt|;
try|try
block|{
name|String
name|proto
init|=
name|source
operator|.
name|protocol
argument_list|()
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|doc
operator|=
name|Jsoup
operator|.
name|parse
argument_list|(
name|source
operator|.
name|file
argument_list|()
argument_list|,
name|this
operator|.
name|charset
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|Jsoup
operator|.
name|parse
argument_list|(
name|source
operator|.
name|url
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
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
name|FileReaderException
argument_list|(
literal|"Cannot read "
operator|+
name|source
operator|.
name|path
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|tableElement
operator|=
operator|(
name|this
operator|.
name|selector
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|selector
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
condition|?
name|getSelectedTable
argument_list|(
name|doc
argument_list|,
name|this
operator|.
name|selector
argument_list|)
else|:
name|getBestTable
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Element
name|getSelectedTable
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|FileReaderException
block|{
comment|// get selected elements
name|Elements
name|list
init|=
name|doc
operator|.
name|select
argument_list|(
name|selector
argument_list|)
decl_stmt|;
comment|// get the element
name|Element
name|el
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|index
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|FileReaderException
argument_list|(
literal|""
operator|+
name|list
operator|.
name|size
argument_list|()
operator|+
literal|" HTML element(s) selected"
argument_list|)
throw|;
block|}
name|el
operator|=
name|list
operator|.
name|first
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|el
operator|=
name|list
operator|.
name|get
argument_list|(
name|this
operator|.
name|index
argument_list|)
expr_stmt|;
block|}
comment|// verify element is a table
if|if
condition|(
name|el
operator|.
name|tag
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"table"
argument_list|)
condition|)
block|{
return|return
name|el
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileReaderException
argument_list|(
literal|"selected ("
operator|+
name|selector
operator|+
literal|") element is a "
operator|+
name|el
operator|.
name|tag
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", not a table"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Element
name|getBestTable
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|FileReaderException
block|{
name|Element
name|bestTable
init|=
literal|null
decl_stmt|;
name|int
name|bestScore
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Element
name|t
range|:
name|doc
operator|.
name|select
argument_list|(
literal|"table"
argument_list|)
control|)
block|{
name|int
name|rows
init|=
name|t
operator|.
name|select
argument_list|(
literal|"tr"
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|Element
name|firstRow
init|=
name|t
operator|.
name|select
argument_list|(
literal|"tr"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|cols
init|=
name|firstRow
operator|.
name|select
argument_list|(
literal|"th,td"
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|thisScore
init|=
name|rows
operator|*
name|cols
decl_stmt|;
if|if
condition|(
name|thisScore
operator|>
name|bestScore
condition|)
block|{
name|bestTable
operator|=
name|t
expr_stmt|;
name|bestScore
operator|=
name|thisScore
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bestTable
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileReaderException
argument_list|(
literal|"no tables found"
argument_list|)
throw|;
block|}
return|return
name|bestTable
return|;
block|}
name|void
name|refresh
parameter_list|()
throws|throws
name|FileReaderException
block|{
name|this
operator|.
name|headings
operator|=
literal|null
expr_stmt|;
name|getTable
argument_list|()
expr_stmt|;
block|}
name|Elements
name|getHeadings
parameter_list|()
throws|throws
name|FileReaderException
block|{
if|if
condition|(
name|this
operator|.
name|headings
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|headings
return|;
block|}
specifier|private
name|String
name|tableKey
parameter_list|()
block|{
return|return
literal|"Table: {url: "
operator|+
name|this
operator|.
name|source
operator|+
literal|", selector: "
operator|+
name|this
operator|.
name|selector
operator|+
literal|"}"
return|;
block|}
specifier|public
name|FileReaderIterator
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|tableElement
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|getTable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
decl||
name|Error
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|FileReaderIterator
name|iterator
init|=
operator|new
name|FileReaderIterator
argument_list|(
name|this
operator|.
name|tableElement
operator|.
name|select
argument_list|(
literal|"tr"
argument_list|)
argument_list|)
decl_stmt|;
comment|// if we haven't cached the headings, get them
comment|// TODO: this needs to be reworked to properly cache the headings
comment|//if (this.headings == null) {
if|if
condition|(
literal|true
condition|)
block|{
comment|// first row must contain headings
name|Elements
name|headings
init|=
name|iterator
operator|.
name|next
argument_list|(
literal|"th"
argument_list|)
decl_stmt|;
comment|// if not, generate some default column names
if|if
condition|(
name|headings
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// rewind and peek at the first row of data
name|iterator
operator|=
operator|new
name|FileReaderIterator
argument_list|(
name|this
operator|.
name|tableElement
operator|.
name|select
argument_list|(
literal|"tr"
argument_list|)
argument_list|)
expr_stmt|;
name|Elements
name|firstRow
init|=
name|iterator
operator|.
name|next
argument_list|(
literal|"td"
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|headings
operator|=
operator|new
name|Elements
argument_list|()
expr_stmt|;
for|for
control|(
name|Element
name|td
range|:
name|firstRow
control|)
block|{
name|Element
name|th
init|=
name|td
operator|.
name|clone
argument_list|()
decl_stmt|;
name|th
operator|.
name|tagName
argument_list|(
literal|"th"
argument_list|)
expr_stmt|;
name|th
operator|.
name|html
argument_list|(
literal|"col"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
name|headings
operator|.
name|add
argument_list|(
name|th
argument_list|)
expr_stmt|;
block|}
comment|// rewind, so queries see the first row
name|iterator
operator|=
operator|new
name|FileReaderIterator
argument_list|(
name|this
operator|.
name|tableElement
operator|.
name|select
argument_list|(
literal|"tr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|headings
operator|=
name|headings
expr_stmt|;
block|}
return|return
name|iterator
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
comment|/** Iterates over HTML tables, returning an Elements per row. */
specifier|private
specifier|static
class|class
name|FileReaderIterator
implements|implements
name|Iterator
argument_list|<
name|Elements
argument_list|>
block|{
specifier|final
name|Iterator
argument_list|<
name|Element
argument_list|>
name|rowIterator
decl_stmt|;
name|FileReaderIterator
parameter_list|(
name|Elements
name|rows
parameter_list|)
block|{
name|this
operator|.
name|rowIterator
operator|=
name|rows
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|this
operator|.
name|rowIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
name|Elements
name|next
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|Element
name|row
init|=
name|this
operator|.
name|rowIterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|row
operator|.
name|select
argument_list|(
name|selector
argument_list|)
return|;
block|}
comment|// return th and td elements by default
specifier|public
name|Elements
name|next
parameter_list|()
block|{
return|return
name|next
argument_list|(
literal|"th,td"
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"NFW - can't remove!"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FileReader.java
end_comment

end_unit

