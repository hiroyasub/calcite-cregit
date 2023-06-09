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
name|config
operator|.
name|CalciteConnectionProperty
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
name|config
operator|.
name|Lex
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
name|runtime
operator|.
name|Hook
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
name|validate
operator|.
name|SqlConformanceEnum
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
name|test
operator|.
name|CalciteAssert
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
name|Holder
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
name|Sources
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
name|TestUtil
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
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|OutputStream
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
name|io
operator|.
name|StringWriter
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
name|SQLException
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
name|Consumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|TestUtil
operator|.
name|rethrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|startsWith
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assumptions
operator|.
name|assumeFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assumptions
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Unit tests for the OS (operating system) adapter.  *  *<p>Also please run the following tests manually, from your shell:  *  *<ul>  *<li>./sqlsh select \* from du  *<li>./sqlsh select \* from files  *<li>./sqlsh select \* from git_commits  *<li>./sqlsh select \* from ps  *<li>(echo cats; echo and dogs) | ./sqlsh select \* from stdin  *<li>./sqlsh select \* from vmstat  *</ul>  */
end_comment

begin_class
class|class
name|OsAdapterTest
block|{
specifier|private
specifier|static
name|boolean
name|isWindows
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
return|;
block|}
comment|/** Returns whether there is a ".git" directory in this directory or in a    * directory between this directory and root. */
specifier|private
specifier|static
name|boolean
name|hasGit
parameter_list|()
block|{
name|assumeToolExists
argument_list|(
literal|"git"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|path
init|=
name|Sources
operator|.
name|of
argument_list|(
name|OsAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|f
operator|==
literal|null
operator|||
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
comment|// abandon hope
block|}
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
argument_list|(
parameter_list|(
name|dir
parameter_list|,
name|name
parameter_list|)
lambda|->
name|name
operator|.
name|equals
argument_list|(
literal|".git"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
operator|&&
name|files
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
literal|true
return|;
comment|// there is a ".git" subdirectory
block|}
name|f
operator|=
name|f
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assumeToolExists
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|assumeTrue
argument_list|(
name|checkProcessExists
argument_list|(
name|command
argument_list|)
argument_list|,
parameter_list|()
lambda|->
name|command
operator|+
literal|" does not exist"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|checkProcessExists
parameter_list|(
name|String
name|command
parameter_list|)
block|{
try|try
block|{
name|Process
name|process
init|=
operator|new
name|ProcessBuilder
argument_list|()
operator|.
name|command
argument_list|(
name|command
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|process
argument_list|)
expr_stmt|;
name|int
name|errCode
init|=
name|process
operator|.
name|waitFor
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|errCode
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AssertionError
decl||
name|IOException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Test
name|void
name|testDu
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'du' table does not work on Windows"
argument_list|)
expr_stmt|;
name|assumeToolExists
argument_list|(
literal|"du"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from du"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|,
name|startsWith
argument_list|(
literal|"./"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDuFilterSortLimit
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'du' table does not work on Windows"
argument_list|)
expr_stmt|;
name|assumeToolExists
argument_list|(
literal|"du"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from du where path like '%/src/test/java/%'\n"
operator|+
literal|"order by 1 limit 2"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|,
name|startsWith
argument_list|(
literal|"./"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// because of "limit 2"
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFiles
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'files' table does not work on Windows"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct type from files"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"type=d"
argument_list|,
literal|"type=f"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPs
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'ps' table does not work on Windows"
argument_list|)
expr_stmt|;
name|assumeToolExists
argument_list|(
literal|"ps"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ps"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|c
init|=
name|r
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
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
name|c
condition|;
name|i
operator|++
control|)
block|{
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
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPsDistinct
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'ps' table does not work on Windows"
argument_list|)
expr_stmt|;
name|assumeToolExists
argument_list|(
literal|"ps"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct `user` from ps"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGitCommits
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|hasGit
argument_list|()
argument_list|,
literal|"no git"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from git_commits"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGitCommitsTop
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|hasGit
argument_list|()
argument_list|,
literal|"no git"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select author from git_commits\n"
operator|+
literal|"group by 1 order by count(*) desc limit 2"
decl_stmt|;
name|sql
argument_list|(
name|q
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"author=Julian Hyde<julianhyde@gmail.com>"
argument_list|,
literal|"author=Julian Hyde<jhyde@apache.org>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJps
parameter_list|()
block|{
name|assumeToolExists
argument_list|(
literal|"jps"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select pid, info from jps"
decl_stmt|;
name|sql
argument_list|(
name|q
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testVmstat
parameter_list|()
block|{
name|assumeFalse
argument_list|(
name|isWindows
argument_list|()
argument_list|,
literal|"Skip: the 'files' table does not work on Windows"
argument_list|)
expr_stmt|;
name|assumeToolExists
argument_list|(
literal|"vmstat"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from vmstat"
argument_list|)
operator|.
name|returns
argument_list|(
name|r
lambda|->
block|{
try|try
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|c
init|=
name|r
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
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
name|c
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|r
operator|.
name|getLong
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|r
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStdin
parameter_list|()
throws|throws
name|SQLException
block|{
try|try
init|(
name|Hook
operator|.
name|Closeable
name|ignore
init|=
name|Hook
operator|.
name|STANDARD_STREAMS
operator|.
name|addThread
argument_list|(
operator|(
name|Consumer
argument_list|<
name|Holder
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
operator|)
name|o
lambda|->
block|{
specifier|final
name|Object
index|[]
name|values
init|=
name|o
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|in
init|=
operator|(
name|InputStream
operator|)
name|values
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|String
name|s
init|=
literal|"First line\n"
operator|+
literal|"Second line"
decl_stmt|;
specifier|final
name|ByteArrayInputStream
name|in2
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|OutputStream
name|out
init|=
operator|(
name|OutputStream
operator|)
name|values
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|OutputStream
name|err
init|=
operator|(
name|OutputStream
operator|)
name|values
index|[
literal|2
index|]
decl_stmt|;
name|o
operator|.
name|set
argument_list|(
operator|new
name|Object
index|[]
block|{
name|in2
operator|,
name|out
operator|,
name|err
block|}
argument_list|)
init|;              }
block_content|)
block|)
block|{
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"select count(*) as c from stdin"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"2\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_class

begin_function
unit|}    @
name|Test
name|void
name|testStdinExplain
parameter_list|()
block|{
comment|// Can't execute stdin, because junit's stdin never ends;
comment|// so just run explain
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableAggregate(group=[{}], c=[COUNT()])\n"
operator|+
literal|"  EnumerableTableFunctionScan(invocation=[stdin(true)], "
operator|+
literal|"rowType=[RecordType(INTEGER ordinal, VARCHAR line)], "
operator|+
literal|"elementType=[class [Ljava.lang.Object;])"
decl_stmt|;
name|sql
argument_list|(
literal|"select count(*) as c from stdin"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSqlShellFormat
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|String
name|q
init|=
literal|"select * from (values (-1, true, 'a'),"
operator|+
literal|" (2, false, 'b, c'),"
operator|+
literal|" (3, unknown, cast(null as char(1)))) as t(x, y, z)"
decl_stmt|;
specifier|final
name|String
name|empty
init|=
name|q
operator|+
literal|" where false"
decl_stmt|;
specifier|final
name|String
name|spacedOut
init|=
literal|"-1 true a   \n"
operator|+
literal|"2 false b, c\n"
operator|+
literal|"3 null null\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"spaced"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|spacedOut
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"spaced"
argument_list|,
name|empty
argument_list|)
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// default is 'spaced'
name|assertThat
argument_list|(
name|foo
argument_list|(
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|spacedOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|headersOut
init|=
literal|"x y z\n"
operator|+
name|spacedOut
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"headers"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|headersOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|headersEmptyOut
init|=
literal|"x y z\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"headers"
argument_list|,
name|empty
argument_list|)
argument_list|,
name|is
argument_list|(
name|headersEmptyOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|jsonOut
init|=
literal|"[\n"
operator|+
literal|"{\n"
operator|+
literal|"  \"x\": -1,\n"
operator|+
literal|"  \"y\": true,\n"
operator|+
literal|"  \"z\": \"a   \"\n"
operator|+
literal|"},\n"
operator|+
literal|"{\n"
operator|+
literal|"  \"x\": 2,\n"
operator|+
literal|"  \"y\": false,\n"
operator|+
literal|"  \"z\": \"b, c\"\n"
operator|+
literal|"},\n"
operator|+
literal|"{\n"
operator|+
literal|"  \"x\": 3,\n"
operator|+
literal|"  \"y\": null,\n"
operator|+
literal|"  \"z\": null\n"
operator|+
literal|"}\n"
operator|+
literal|"]\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"json"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|jsonOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|jsonEmptyOut
init|=
literal|"[\n"
operator|+
literal|"]\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"json"
argument_list|,
name|empty
argument_list|)
argument_list|,
name|is
argument_list|(
name|jsonEmptyOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|csvEmptyOut
init|=
literal|"[\n"
operator|+
literal|"]\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"json"
argument_list|,
name|empty
argument_list|)
argument_list|,
name|is
argument_list|(
name|csvEmptyOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|csvOut
init|=
literal|"x,y,z\n"
operator|+
literal|"-1,true,a   \n"
operator|+
literal|"2,false,\"b, c\"\n"
operator|+
literal|"3,,"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"csv"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|csvOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mysqlOut
init|=
literal|""
operator|+
literal|"+----+-------+------+\n"
operator|+
literal|"|  x | y     | z    |\n"
operator|+
literal|"+----+-------+------+\n"
operator|+
literal|"| -1 | true  | a    |\n"
operator|+
literal|"|  2 | false | b, c |\n"
operator|+
literal|"|  3 |       |      |\n"
operator|+
literal|"+----+-------+------+\n"
operator|+
literal|"(3 rows)\n"
operator|+
literal|"\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"mysql"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|mysqlOut
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mysqlEmptyOut
init|=
literal|""
operator|+
literal|"+---+---+---+\n"
operator|+
literal|"| x | y | z |\n"
operator|+
literal|"+---+---+---+\n"
operator|+
literal|"+---+---+---+\n"
operator|+
literal|"(0 rows)\n"
operator|+
literal|"\n"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"mysql"
argument_list|,
name|empty
argument_list|)
argument_list|,
name|is
argument_list|(
name|mysqlEmptyOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|String
name|foo
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ByteArrayInputStream
name|inStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
specifier|final
name|InputStreamReader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|inStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|outSw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|outSw
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|errSw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|err
init|=
operator|new
name|PrintWriter
argument_list|(
name|errSw
argument_list|)
decl_stmt|;
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
return|return
name|Util
operator|.
name|toLinux
argument_list|(
name|outSw
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSqlShellHelp
parameter_list|()
throws|throws
name|SQLException
block|{
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
literal|"  -h --help  Print this help\n"
decl_stmt|;
specifier|final
name|String
name|q
init|=
literal|"select 1"
decl_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"--help"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|help
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|foo
argument_list|(
literal|"-h"
argument_list|,
name|q
argument_list|)
argument_list|,
name|is
argument_list|(
name|help
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|s
init|=
name|foo
argument_list|(
literal|"-o"
argument_list|,
literal|"bad"
argument_list|,
name|q
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"unknown format: bad"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
specifier|static
name|CalciteAssert
operator|.
name|AssertQuery
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|SqlShell
operator|.
name|MODEL
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|LEX
argument_list|,
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

