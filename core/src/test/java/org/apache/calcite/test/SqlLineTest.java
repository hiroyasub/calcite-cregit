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
name|test
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
name|Bug
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
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|ByteArrayOutputStream
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
name|PrintStream
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
name|Collections
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
name|sqlline
operator|.
name|SqlLine
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
name|equalTo
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
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests that we can invoke SqlLine on a Calcite connection.  */
end_comment

begin_class
class|class
name|SqlLineTest
block|{
comment|/**    * Execute a script with "sqlline -f".    *    * @throws java.lang.Throwable On error    * @return The stderr and stdout from running the script    * @param args Script arguments    */
specifier|private
specifier|static
name|Pair
argument_list|<
name|SqlLine
operator|.
name|Status
argument_list|,
name|String
argument_list|>
name|run
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|SqlLine
name|sqlline
init|=
operator|new
name|SqlLine
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|sqllineOutputStream
init|=
operator|new
name|PrintStream
argument_list|(
name|os
argument_list|,
literal|false
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|sqlline
operator|.
name|setOutputStream
argument_list|(
name|sqllineOutputStream
argument_list|)
expr_stmt|;
name|sqlline
operator|.
name|setErrorStream
argument_list|(
name|sqllineOutputStream
argument_list|)
expr_stmt|;
name|SqlLine
operator|.
name|Status
name|status
init|=
name|SqlLine
operator|.
name|Status
operator|.
name|OK
decl_stmt|;
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"[sqlline-35] Make Sqlline.begin public"
argument_list|)
expr_stmt|;
comment|// TODO: status = sqlline.begin(args, null, false);
return|return
name|Pair
operator|.
name|of
argument_list|(
name|status
argument_list|,
name|os
operator|.
name|toString
argument_list|(
literal|"UTF8"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Pair
argument_list|<
name|SqlLine
operator|.
name|Status
argument_list|,
name|String
argument_list|>
name|runScript
parameter_list|(
name|File
name|scriptFile
parameter_list|,
name|boolean
name|flag
parameter_list|)
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|args
argument_list|,
literal|"-u"
argument_list|,
literal|"jdbc:calcite:"
argument_list|,
literal|"-n"
argument_list|,
literal|"sa"
argument_list|,
literal|"-p"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|flag
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-f"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|args
operator|.
name|add
argument_list|(
literal|"--run="
operator|+
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|run
argument_list|(
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Attempts to execute a simple script file with the -f option to SqlLine.    * Tests for presence of an expected pattern in the output (stdout or stderr).    *    * @param scriptText Script text    * @param flag Command flag (--run or -f)    * @param statusMatcher Checks whether status is as expected    * @param outputMatcher Checks whether output is as expected    * @throws Exception on command execution error    */
specifier|private
name|void
name|checkScriptFile
parameter_list|(
name|String
name|scriptText
parameter_list|,
name|boolean
name|flag
parameter_list|,
name|Matcher
argument_list|<
name|SqlLine
operator|.
name|Status
argument_list|>
name|statusMatcher
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|outputMatcher
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Put the script content in a temp file
name|File
name|scriptFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"foo"
argument_list|,
literal|"temp"
argument_list|)
decl_stmt|;
name|scriptFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
try|try
init|(
name|PrintWriter
name|w
init|=
name|Util
operator|.
name|printWriter
argument_list|(
name|scriptFile
argument_list|)
init|)
block|{
name|w
operator|.
name|print
argument_list|(
name|scriptText
argument_list|)
expr_stmt|;
block|}
name|Pair
argument_list|<
name|SqlLine
operator|.
name|Status
argument_list|,
name|String
argument_list|>
name|pair
init|=
name|runScript
argument_list|(
name|scriptFile
argument_list|,
name|flag
argument_list|)
decl_stmt|;
comment|// Check output before status. It gives a better clue what went wrong.
name|assertThat
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|outputMatcher
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pair
operator|.
name|left
argument_list|,
name|statusMatcher
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|delete
init|=
name|scriptFile
operator|.
name|delete
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|delete
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlLine
parameter_list|()
throws|throws
name|Throwable
block|{
name|checkScriptFile
argument_list|(
literal|"!tables"
argument_list|,
literal|false
argument_list|,
name|equalTo
argument_list|(
name|SqlLine
operator|.
name|Status
operator|.
name|OK
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

