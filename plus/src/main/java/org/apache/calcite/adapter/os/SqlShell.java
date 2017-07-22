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
name|linq4j
operator|.
name|Linq4j
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
name|JsonBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
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
name|LinkedHashMap
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
name|Locale
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Command that executes its arguments as a SQL query  * against Calcite's OS adapter.  */
end_comment

begin_class
specifier|public
class|class
name|SqlShell
block|{
specifier|static
specifier|final
name|String
name|MODEL
init|=
name|model
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
decl_stmt|;
specifier|private
specifier|final
name|InputStreamReader
name|in
decl_stmt|;
specifier|private
specifier|final
name|PrintWriter
name|out
decl_stmt|;
specifier|private
specifier|final
name|PrintWriter
name|err
decl_stmt|;
name|SqlShell
parameter_list|(
name|InputStreamReader
name|in
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|PrintWriter
name|err
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|err
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|model
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  version: '1.0',\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  defaultSchema: 'os',\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"   schemas: [\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"     {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"       \"name\": \"os\",\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"       \"tables\": [ {\n"
argument_list|)
expr_stmt|;
name|addView
argument_list|(
name|b
argument_list|,
literal|"du"
argument_list|,
literal|"select *, \"size_k\" * 1024 as \"size_b\"\n"
operator|+
literal|"from table(\"du\"(true))"
argument_list|)
expr_stmt|;
name|addView
argument_list|(
name|b
argument_list|,
literal|"files"
argument_list|,
literal|"select * from table(\"files\"('.'))"
argument_list|)
expr_stmt|;
name|addView
argument_list|(
name|b
argument_list|,
literal|"git_commits"
argument_list|,
literal|"select * from table(\"git_commits\"(true))"
argument_list|)
expr_stmt|;
name|addView
argument_list|(
name|b
argument_list|,
literal|"ps"
argument_list|,
literal|"select * from table(\"ps\"(true))"
argument_list|)
expr_stmt|;
name|addView
argument_list|(
name|b
argument_list|,
literal|"stdin"
argument_list|,
literal|"select * from table(\"stdin\"(true))"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"       } ],\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"       functions: [ {\n"
argument_list|)
expr_stmt|;
name|addFunction
argument_list|(
name|b
argument_list|,
literal|"du"
argument_list|,
name|DuTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
name|addFunction
argument_list|(
name|b
argument_list|,
literal|"files"
argument_list|,
name|FilesTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
name|addFunction
argument_list|(
name|b
argument_list|,
literal|"git_commits"
argument_list|,
name|GitCommitsTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
name|addFunction
argument_list|(
name|b
argument_list|,
literal|"ps"
argument_list|,
name|PsTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
name|addFunction
argument_list|(
name|b
argument_list|,
literal|"stdin"
argument_list|,
name|StdinTableFunction
operator|.
name|class
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"       } ]\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"     }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"   ]\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Main entry point. */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
init|(
name|PrintWriter
name|err
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|err
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|;
name|InputStreamReader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|;
specifier|final
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
operator|new
name|SqlShell
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|err
argument_list|,
name|args
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|void
name|run
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|url
init|=
literal|"jdbc:calcite:lex=JAVA;conformance=LENIENT"
operator|+
literal|";model=inline:"
operator|+
name|MODEL
decl_stmt|;
specifier|final
name|String
name|help
init|=
literal|"Usage: sqlsh [OPTION]... SQL\n"
operator|+
literal|"Execute a SQL command\n"
operator|+
literal|"\n"
operator|+
literal|"Options:\n"
operator|+
literal|"  -o FORMAT  Print output in FORMAT; options are 'spaced' (the "
operator|+
literal|"default), 'csv',\n"
operator|+
literal|"             'headers', 'json', 'mysql'\n"
operator|+
literal|"  -h --help  Print this help"
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
init|;
name|Statement
name|s
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
name|String
argument_list|>
name|args
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|this
operator|.
name|args
argument_list|)
operator|.
name|enumerator
argument_list|()
decl_stmt|;
name|Format
name|format
init|=
name|Format
operator|.
name|SPACED
decl_stmt|;
while|while
condition|(
name|args
operator|.
name|moveNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|current
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-o"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|String
name|formatString
init|=
name|args
operator|.
name|current
argument_list|()
decl_stmt|;
try|try
block|{
name|format
operator|=
name|Format
operator|.
name|valueOf
argument_list|(
name|formatString
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown format: "
operator|+
name|formatString
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"missing format"
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|args
operator|.
name|current
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
operator|||
name|args
operator|.
name|current
argument_list|()
operator|.
name|equals
argument_list|(
literal|"--help"
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|help
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
if|if
condition|(
name|b
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|args
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ResultSet
name|r
init|=
name|s
operator|.
name|executeQuery
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|format
operator|.
name|output
argument_list|(
name|out
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addView
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"du"
argument_list|)
condition|)
block|{
comment|// we know that "du" is the first
name|b
operator|.
name|append
argument_list|(
literal|"}, {\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"         \"name\": \""
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         \"type\": \"view\",\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         \"sql\": \""
argument_list|)
operator|.
name|append
argument_list|(
name|sql
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\\\\""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addFunction
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|name
parameter_list|,
name|Class
name|c
parameter_list|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"du"
argument_list|)
condition|)
block|{
comment|// we know that "du" is the first
name|b
operator|.
name|append
argument_list|(
literal|"}, {\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"         \"name\": \""
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         \"className\": \""
argument_list|)
operator|.
name|append
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Output format. */
enum|enum
name|Format
block|{
name|SPACED
block|{
specifier|protected
name|void
name|output
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|ResultSet
name|r
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|int
name|n
init|=
name|r
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
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
name|n
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
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|r
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|,
name|HEADERS
block|{
specifier|protected
name|void
name|output
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|ResultSet
name|r
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ResultSetMetaData
name|m
init|=
name|r
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|m
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
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
name|n
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
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|m
operator|.
name|getColumnLabel
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|SPACED
operator|.
name|output
argument_list|(
name|out
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|CSV
block|{
specifier|protected
name|void
name|output
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|ResultSet
name|r
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// We aim to comply with https://tools.ietf.org/html/rfc4180.
comment|// It's a bug if we don't.
specifier|final
name|ResultSetMetaData
name|m
init|=
name|r
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|m
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
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
name|n
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
name|b
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|value
argument_list|(
name|b
argument_list|,
name|m
operator|.
name|getColumnLabel
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
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
name|n
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
name|b
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|value
argument_list|(
name|b
argument_list|,
name|r
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|value
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
comment|// do nothing - unfortunately same as empty string
block|}
if|else if
condition|(
name|s
operator|.
name|contains
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\"\""
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|>=
literal|0
operator|||
name|s
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
operator|>=
literal|0
operator|||
name|s
operator|.
name|indexOf
argument_list|(
literal|'\r'
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|,
name|JSON
block|{
specifier|protected
name|void
name|output
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
specifier|final
name|ResultSet
name|r
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ResultSetMetaData
name|m
init|=
name|r
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|m
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|fieldOrdinals
init|=
operator|new
name|LinkedHashMap
argument_list|<>
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|fieldOrdinals
operator|.
name|put
argument_list|(
name|m
operator|.
name|getColumnLabel
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|fieldOrdinals
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|fieldOrdinals
operator|.
name|keySet
argument_list|()
decl_stmt|;
specifier|final
name|JsonBuilder
name|json
init|=
operator|new
name|JsonBuilder
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|append
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|Maps
operator|.
name|asMap
argument_list|(
name|fields
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|String
name|columnLabel
parameter_list|)
block|{
try|try
block|{
specifier|final
name|int
name|i
init|=
name|fieldOrdinals
operator|.
name|get
argument_list|(
name|columnLabel
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|m
operator|.
name|getColumnType
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
name|Types
operator|.
name|BOOLEAN
case|:
specifier|final
name|boolean
name|b
init|=
name|r
operator|.
name|getBoolean
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
operator|!
name|b
operator|&&
name|r
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|b
return|;
case|case
name|Types
operator|.
name|DECIMAL
case|:
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
case|case
name|Types
operator|.
name|DOUBLE
case|:
specifier|final
name|double
name|d
init|=
name|r
operator|.
name|getDouble
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|0D
operator|&&
name|r
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|d
return|;
case|case
name|Types
operator|.
name|BIGINT
case|:
case|case
name|Types
operator|.
name|INTEGER
case|:
case|case
name|Types
operator|.
name|SMALLINT
case|:
case|case
name|Types
operator|.
name|TINYINT
case|:
specifier|final
name|long
name|v
init|=
name|r
operator|.
name|getLong
argument_list|(
name|i
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|0L
operator|&&
name|r
operator|.
name|wasNull
argument_list|()
condition|?
literal|null
else|:
name|v
return|;
default|default:
return|return
name|r
operator|.
name|getString
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
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
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|MYSQL
block|{
specifier|protected
name|void
name|output
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
specifier|final
name|ResultSet
name|r
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// E.g.
comment|// +-------+--------+
comment|// | EMPNO | ENAME  |
comment|// +-------+--------+
comment|// |  7369 | SMITH  |
comment|// |   822 | LEE    |
comment|// +-------+--------+
specifier|final
name|ResultSetMetaData
name|m
init|=
name|r
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|m
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|lengths
init|=
operator|new
name|int
index|[
name|n
index|]
decl_stmt|;
specifier|final
name|boolean
index|[]
name|rights
init|=
operator|new
name|boolean
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
specifier|final
name|String
name|v
init|=
name|m
operator|.
name|getColumnLabel
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|lengths
index|[
name|i
index|]
operator|=
name|v
operator|.
name|length
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|m
operator|.
name|getColumnType
argument_list|(
name|i
operator|+
literal|1
argument_list|)
condition|)
block|{
case|case
name|Types
operator|.
name|BIGINT
case|:
case|case
name|Types
operator|.
name|INTEGER
case|:
case|case
name|Types
operator|.
name|SMALLINT
case|:
case|case
name|Types
operator|.
name|TINYINT
case|:
case|case
name|Types
operator|.
name|REAL
case|:
case|case
name|Types
operator|.
name|FLOAT
case|:
case|case
name|Types
operator|.
name|DOUBLE
case|:
name|rights
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|v
init|=
name|r
operator|.
name|getString
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|length
argument_list|()
operator|>
name|lengths
index|[
name|i
index|]
condition|)
block|{
name|lengths
index|[
name|i
index|]
operator|=
name|v
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"+"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|length
range|:
name|lengths
control|)
block|{
name|pad
argument_list|(
name|b
argument_list|,
name|length
operator|+
literal|2
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|bar
init|=
name|b
operator|.
name|toString
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
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
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|value
argument_list|(
name|b
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|lengths
index|[
name|i
index|]
argument_list|,
name|rights
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" |"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|bar
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|h
init|=
name|n
init|;
name|h
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|h
operator|++
control|)
block|{
specifier|final
name|int
name|i
init|=
name|h
operator|%
name|n
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|value
argument_list|(
name|b
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|h
argument_list|)
argument_list|,
name|lengths
index|[
name|i
index|]
argument_list|,
name|rights
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" |"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|int
name|rowCount
init|=
operator|(
name|values
operator|.
name|size
argument_list|()
operator|/
name|n
operator|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|rowCount
operator|==
literal|1
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"(1 row)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|print
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|rowCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" rows)"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|value
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|String
name|value
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|right
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|pad
argument_list|(
name|b
argument_list|,
name|length
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|pad
init|=
name|length
operator|-
name|value
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|pad
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|right
condition|)
block|{
name|pad
argument_list|(
name|b
argument_list|,
name|pad
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pad
argument_list|(
name|b
argument_list|,
name|pad
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|pad
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|int
name|pad
parameter_list|,
name|char
name|c
parameter_list|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|pad
condition|;
name|j
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|;
specifier|protected
specifier|abstract
name|void
name|output
argument_list|(
name|PrintWriter
name|out
argument_list|,
name|ResultSet
name|r
argument_list|)
throws|throws
name|SQLException
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlShell.java
end_comment

end_unit

