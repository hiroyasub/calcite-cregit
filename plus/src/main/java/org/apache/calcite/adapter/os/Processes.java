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
name|os
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|Enumerable
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
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Utilities regarding operating system processes.  *  *<p>WARNING: Spawning processes is not secure.  * Use this class with caution.  * This class is in the "plus" module because "plus" is not used by default.  * Do not move this class to the "core" module.  */
end_comment

begin_class
specifier|public
class|class
name|Processes
block|{
specifier|private
name|Processes
parameter_list|()
block|{
block|}
comment|/** Executes a command and returns its result as an enumerable of lines. */
specifier|static
name|Enumerable
argument_list|<
name|String
argument_list|>
name|processLines
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|processLines
argument_list|(
literal|' '
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/** Executes a command and returns its result as an enumerable of lines. */
specifier|static
name|Enumerable
argument_list|<
name|String
argument_list|>
name|processLines
parameter_list|(
name|char
name|sep
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|processLines
argument_list|(
name|sep
argument_list|,
name|processSupplier
argument_list|(
name|args
argument_list|)
argument_list|)
return|;
block|}
comment|/** Executes a command and returns its result as an enumerable of lines.    *    * @param sep Separator character    * @param processSupplier Command and its arguments    */
specifier|private
specifier|static
name|Enumerable
argument_list|<
name|String
argument_list|>
name|processLines
parameter_list|(
name|char
name|sep
parameter_list|,
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
parameter_list|)
block|{
if|if
condition|(
name|sep
operator|!=
literal|' '
condition|)
block|{
return|return
operator|new
name|SeparatedLinesEnumerable
argument_list|(
name|processSupplier
argument_list|,
name|sep
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ProcessLinesEnumerator
argument_list|(
name|processSupplier
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
parameter_list|(
specifier|final
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
operator|new
name|ProcessFactory
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/** Enumerator that executes a process and returns each line as an element. */
specifier|private
specifier|static
class|class
name|ProcessLinesEnumerator
extends|extends
name|AbstractEnumerable
argument_list|<
name|String
argument_list|>
block|{
specifier|private
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
decl_stmt|;
name|ProcessLinesEnumerator
parameter_list|(
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
parameter_list|)
block|{
name|this
operator|.
name|processSupplier
operator|=
name|processSupplier
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|String
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Process
name|process
init|=
name|processSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
specifier|final
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|bis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
name|String
name|line
decl_stmt|;
specifier|public
name|String
name|current
parameter_list|()
block|{
return|return
name|line
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
return|return
name|line
operator|!=
literal|null
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
name|br
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
literal|"while running "
operator|+
name|processSupplier
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/** Enumerator that executes a process and returns each line as an element. */
specifier|private
specifier|static
class|class
name|SeparatedLinesEnumerable
extends|extends
name|AbstractEnumerable
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
decl_stmt|;
specifier|private
specifier|final
name|int
name|sep
decl_stmt|;
name|SeparatedLinesEnumerable
parameter_list|(
name|Supplier
argument_list|<
name|Process
argument_list|>
name|processSupplier
parameter_list|,
name|char
name|sep
parameter_list|)
block|{
name|this
operator|.
name|processSupplier
operator|=
name|processSupplier
expr_stmt|;
name|this
operator|.
name|sep
operator|=
name|sep
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|String
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Process
name|process
init|=
name|processSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
specifier|final
name|BufferedInputStream
name|bis
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|bis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|String
name|line
decl_stmt|;
specifier|public
name|String
name|current
parameter_list|()
block|{
return|return
name|line
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|c
init|=
name|br
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|c
operator|==
name|sep
condition|)
block|{
name|line
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|b
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
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
name|br
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
literal|"while running "
operator|+
name|processSupplier
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/** Creates processes. */
specifier|private
specifier|static
class|class
name|ProcessFactory
implements|implements
name|Supplier
argument_list|<
name|Process
argument_list|>
block|{
specifier|private
specifier|final
name|String
index|[]
name|args
decl_stmt|;
name|ProcessFactory
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
specifier|public
name|Process
name|get
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|ProcessBuilder
argument_list|()
operator|.
name|command
argument_list|(
name|args
argument_list|)
operator|.
name|start
argument_list|()
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
literal|"while creating process: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|args
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|args
index|[
literal|0
index|]
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Processes.java
end_comment

end_unit

