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
name|ReflectUtil
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
name|incava
operator|.
name|diff
operator|.
name|Diff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|incava
operator|.
name|diff
operator|.
name|Difference
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
name|AfterEach
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
name|BeforeEach
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|LineNumberReader
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
name|OutputStreamWriter
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
name|io
operator|.
name|Writer
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|fail
import|;
end_import

begin_comment
comment|/**  * DiffTestCase is an abstract base for JUnit tests which produce multi-line  * output to be verified by diffing against a pre-existing reference file.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DiffTestCase
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|testCaseName
decl_stmt|;
comment|/**    * Name of current .log file.    */
specifier|protected
name|File
name|logFile
decl_stmt|;
comment|/**    * Name of current .ref file.    */
specifier|protected
name|File
name|refFile
decl_stmt|;
comment|/**    * OutputStream for current test log.    */
specifier|protected
name|OutputStream
name|logOutputStream
decl_stmt|;
comment|/**    * Diff masks defined so far    */
comment|// private List diffMasks;
specifier|private
name|String
name|diffMasks
decl_stmt|;
name|Matcher
name|compiledDiffMatcher
decl_stmt|;
specifier|private
name|String
name|ignorePatterns
decl_stmt|;
name|Matcher
name|compiledIgnoreMatcher
decl_stmt|;
comment|/**    * Whether to give verbose message if diff fails.    */
specifier|private
name|boolean
name|verbose
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Initializes a new DiffTestCase.    *    * @param testCaseName Test case name    */
specifier|protected
name|DiffTestCase
parameter_list|(
name|String
name|testCaseName
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|testCaseName
operator|=
name|testCaseName
expr_stmt|;
comment|// diffMasks = new ArrayList();
name|diffMasks
operator|=
literal|""
expr_stmt|;
name|ignorePatterns
operator|=
literal|""
expr_stmt|;
name|compiledIgnoreMatcher
operator|=
literal|null
expr_stmt|;
name|compiledDiffMatcher
operator|=
literal|null
expr_stmt|;
name|String
name|verboseVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|DiffTestCase
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".verbose"
argument_list|)
decl_stmt|;
if|if
condition|(
name|verboseVal
operator|!=
literal|null
condition|)
block|{
name|verbose
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|BeforeEach
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
comment|// diffMasks.clear();
name|diffMasks
operator|=
literal|""
expr_stmt|;
name|ignorePatterns
operator|=
literal|""
expr_stmt|;
name|compiledIgnoreMatcher
operator|=
literal|null
expr_stmt|;
name|compiledDiffMatcher
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|AfterEach
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|logOutputStream
operator|!=
literal|null
condition|)
block|{
name|logOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|logOutputStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Initializes a diff-based test. Any existing .log and .dif files    * corresponding to this test case are deleted, and a new, empty .log file    * is created. The default log file location is a subdirectory under the    * result getTestlogRoot(), where the subdirectory name is based on the    * unqualified name of the test class. The generated log file name will be    * testMethodName.log, and the expected reference file will be    * testMethodName.ref.    *    * @return Writer for log file, which caller should use as a destination for    * test output to be diffed    */
specifier|protected
name|Writer
name|openTestLog
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|testClassDir
init|=
operator|new
name|File
argument_list|(
name|getTestlogRoot
argument_list|()
argument_list|,
name|ReflectUtil
operator|.
name|getUnqualifiedClassName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|testClassDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|testLogFile
init|=
operator|new
name|File
argument_list|(
name|testClassDir
argument_list|,
name|testCaseName
argument_list|)
decl_stmt|;
return|return
operator|new
name|OutputStreamWriter
argument_list|(
name|openTestLogOutputStream
argument_list|(
name|testLogFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
comment|/**    * @return the root under which testlogs should be written    */
specifier|protected
specifier|abstract
name|File
name|getTestlogRoot
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Initializes a diff-based test, overriding the default log file naming    * scheme altogether.    *    * @param testFileSansExt full path to log filename, without .log/.ref    *                        extension    */
specifier|protected
name|OutputStream
name|openTestLogOutputStream
parameter_list|(
name|File
name|testFileSansExt
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|logOutputStream
operator|==
literal|null
assert|;
name|logFile
operator|=
operator|new
name|File
argument_list|(
name|testFileSansExt
operator|.
name|toString
argument_list|()
operator|+
literal|".log"
argument_list|)
expr_stmt|;
name|logFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|refFile
operator|=
operator|new
name|File
argument_list|(
name|testFileSansExt
operator|.
name|toString
argument_list|()
operator|+
literal|".ref"
argument_list|)
expr_stmt|;
name|logOutputStream
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
return|return
name|logOutputStream
return|;
block|}
comment|/**    * Finishes a diff-based test. Output that was written to the Writer    * returned by openTestLog is diffed against a .ref file, and if any    * differences are detected, the test case fails. Note that the diff used is    * just a boolean test, and does not create any .dif ouput.    *    *<p>NOTE: if you wrap the Writer returned by openTestLog() (e.g. with a    * PrintWriter), be sure to flush the wrapping Writer before calling this    * method.</p>    *    * @see #diffFile(File, File)    */
specifier|protected
name|void
name|diffTestLog
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|logOutputStream
operator|!=
literal|null
assert|;
name|logOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|logOutputStream
operator|=
literal|null
expr_stmt|;
if|if
condition|(
operator|!
name|refFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Reference file "
operator|+
name|refFile
operator|+
literal|" does not exist"
argument_list|)
expr_stmt|;
block|}
name|diffFile
argument_list|(
name|logFile
argument_list|,
name|refFile
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compares a log file with its reference log.    *    *<p>Usually, the log file and the reference log are in the same directory,    * one ending with '.log' and the other with '.ref'.    *    *<p>If the files are identical, removes logFile.    *    * @param logFile Log file    * @param refFile Reference log    */
specifier|protected
name|void
name|diffFile
parameter_list|(
name|File
name|logFile
parameter_list|,
name|File
name|refFile
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|BufferedReader
name|logReader
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|refReader
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// NOTE: Use of diff.mask is deprecated, use diff_mask.
name|String
name|diffMask
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"diff.mask"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|diffMask
operator|!=
literal|null
condition|)
block|{
name|addDiffMask
argument_list|(
name|diffMask
argument_list|)
expr_stmt|;
block|}
name|diffMask
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"diff_mask"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|diffMask
operator|!=
literal|null
condition|)
block|{
name|addDiffMask
argument_list|(
name|diffMask
argument_list|)
expr_stmt|;
block|}
name|logReader
operator|=
name|Util
operator|.
name|reader
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
name|refReader
operator|=
name|Util
operator|.
name|reader
argument_list|(
name|refFile
argument_list|)
expr_stmt|;
name|LineNumberReader
name|logLineReader
init|=
operator|new
name|LineNumberReader
argument_list|(
name|logReader
argument_list|)
decl_stmt|;
name|LineNumberReader
name|refLineReader
init|=
operator|new
name|LineNumberReader
argument_list|(
name|refReader
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|logLine
init|=
name|logLineReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|String
name|refLine
init|=
name|refLineReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|logLine
operator|!=
literal|null
operator|)
operator|&&
name|matchIgnorePatterns
argument_list|(
name|logLine
argument_list|)
condition|)
block|{
comment|// System.out.println("logMatch Line:" + logLine);
name|logLine
operator|=
name|logLineReader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|refLine
operator|!=
literal|null
operator|)
operator|&&
name|matchIgnorePatterns
argument_list|(
name|refLine
argument_list|)
condition|)
block|{
comment|// System.out.println("refMatch Line:" + logLine);
name|refLine
operator|=
name|refLineReader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|logLine
operator|==
literal|null
operator|)
operator|||
operator|(
name|refLine
operator|==
literal|null
operator|)
condition|)
block|{
if|if
condition|(
name|logLine
operator|!=
literal|null
condition|)
block|{
name|diffFail
argument_list|(
name|logFile
argument_list|,
name|logLineReader
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|refLine
operator|!=
literal|null
condition|)
block|{
name|diffFail
argument_list|(
name|logFile
argument_list|,
name|refLineReader
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|logLine
operator|=
name|applyDiffMask
argument_list|(
name|logLine
argument_list|)
expr_stmt|;
name|refLine
operator|=
name|applyDiffMask
argument_list|(
name|refLine
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|logLine
operator|.
name|equals
argument_list|(
name|refLine
argument_list|)
condition|)
block|{
name|diffFail
argument_list|(
name|logFile
argument_list|,
name|logLineReader
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|logReader
operator|!=
literal|null
condition|)
block|{
name|logReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|refReader
operator|!=
literal|null
condition|)
block|{
name|refReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// no diffs detected, so delete redundant .log file
name|logFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a diff mask. Strings matching the given regular expression will be    * masked before diffing. This can be used to suppress spurious diffs on a    * case-by-case basis.    *    * @param mask a regular expression, as per String.replaceAll    */
specifier|protected
name|void
name|addDiffMask
parameter_list|(
name|String
name|mask
parameter_list|)
block|{
comment|// diffMasks.add(mask);
if|if
condition|(
name|diffMasks
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|diffMasks
operator|=
name|mask
expr_stmt|;
block|}
else|else
block|{
name|diffMasks
operator|=
name|diffMasks
operator|+
literal|"|"
operator|+
name|mask
expr_stmt|;
block|}
name|Pattern
name|compiledDiffPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|diffMasks
argument_list|)
decl_stmt|;
name|compiledDiffMatcher
operator|=
name|compiledDiffPattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addIgnorePattern
parameter_list|(
name|String
name|javaPattern
parameter_list|)
block|{
if|if
condition|(
name|ignorePatterns
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ignorePatterns
operator|=
name|javaPattern
expr_stmt|;
block|}
else|else
block|{
name|ignorePatterns
operator|=
name|ignorePatterns
operator|+
literal|"|"
operator|+
name|javaPattern
expr_stmt|;
block|}
name|Pattern
name|compiledIgnorePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|ignorePatterns
argument_list|)
decl_stmt|;
name|compiledIgnoreMatcher
operator|=
name|compiledIgnorePattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|applyDiffMask
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|compiledDiffMatcher
operator|!=
literal|null
condition|)
block|{
name|compiledDiffMatcher
operator|.
name|reset
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// we assume most of lines do not match
comment|// so compiled matches will be faster than replaceAll.
if|if
condition|(
name|compiledDiffMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|s
operator|.
name|replaceAll
argument_list|(
name|diffMasks
argument_list|,
literal|"XYZZY"
argument_list|)
return|;
block|}
block|}
return|return
name|s
return|;
block|}
specifier|private
name|boolean
name|matchIgnorePatterns
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|compiledIgnoreMatcher
operator|!=
literal|null
condition|)
block|{
name|compiledIgnoreMatcher
operator|.
name|reset
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|compiledIgnoreMatcher
operator|.
name|matches
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|diffFail
parameter_list|(
name|File
name|logFile
parameter_list|,
name|int
name|lineNumber
parameter_list|)
block|{
specifier|final
name|String
name|message
init|=
literal|"diff detected at line "
operator|+
name|lineNumber
operator|+
literal|" in "
operator|+
name|logFile
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
if|if
condition|(
name|inIde
argument_list|()
condition|)
block|{
comment|// If we're in IntelliJ, it's worth printing the 'expected
comment|//<...> actual<...>' string, becauase IntelliJ can format
comment|// this intelligently. Otherwise, use the more concise
comment|// diff format.
name|assertEquals
argument_list|(
name|fileContents
argument_list|(
name|refFile
argument_list|)
argument_list|,
name|fileContents
argument_list|(
name|logFile
argument_list|)
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
name|diff
argument_list|(
name|refFile
argument_list|,
name|logFile
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|message
operator|+
literal|'\n'
operator|+
name|s
operator|+
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns whether this test is running inside the IntelliJ IDE.    *    * @return whether we're running in IntelliJ.    */
specifier|private
specifier|static
name|boolean
name|inIde
parameter_list|()
block|{
name|Throwable
name|runtimeException
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|runtimeException
operator|.
name|fillInStackTrace
argument_list|()
expr_stmt|;
specifier|final
name|StackTraceElement
index|[]
name|stackTrace
init|=
name|runtimeException
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|StackTraceElement
name|lastStackTraceElement
init|=
name|stackTrace
index|[
name|stackTrace
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
comment|// Junit test launched from IntelliJ 6.0
if|if
condition|(
name|lastStackTraceElement
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"com.intellij.rt.execution.junit.JUnitStarter"
argument_list|)
operator|&&
name|lastStackTraceElement
operator|.
name|getMethodName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"main"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Application launched from IntelliJ 6.0
if|if
condition|(
name|lastStackTraceElement
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"com.intellij.rt.execution.application.AppMain"
argument_list|)
operator|&&
name|lastStackTraceElement
operator|.
name|getMethodName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"main"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns a string containing the difference between the contents of two    * files. The string has a similar format to the UNIX 'diff' utility.    */
specifier|public
specifier|static
name|String
name|diff
parameter_list|(
name|File
name|file1
parameter_list|,
name|File
name|file2
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lines1
init|=
name|fileLines
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines2
init|=
name|fileLines
argument_list|(
name|file2
argument_list|)
decl_stmt|;
return|return
name|diffLines
argument_list|(
name|lines1
argument_list|,
name|lines2
argument_list|)
return|;
block|}
comment|/**    * Returns a string containing the difference between the two sets of lines.    */
specifier|public
specifier|static
name|String
name|diffLines
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|lines1
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|lines2
parameter_list|)
block|{
specifier|final
name|Diff
argument_list|<
name|String
argument_list|>
name|differencer
init|=
operator|new
name|Diff
argument_list|<>
argument_list|(
name|lines1
argument_list|,
name|lines2
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Difference
argument_list|>
name|differences
init|=
name|differencer
operator|.
name|execute
argument_list|()
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Difference
name|d
range|:
name|differences
control|)
block|{
specifier|final
name|int
name|as
init|=
name|d
operator|.
name|getAddedStart
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
name|ae
init|=
name|d
operator|.
name|getAddedEnd
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
name|ds
init|=
name|d
operator|.
name|getDeletedStart
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|int
name|de
init|=
name|d
operator|.
name|getDeletedEnd
argument_list|()
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|ae
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|de
operator|==
literal|0
condition|)
block|{
comment|// no change
block|}
else|else
block|{
comment|// a deletion: "<ds>,<de>d<as>"
name|sw
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ds
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|de
operator|>
name|ds
condition|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|de
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sw
operator|.
name|append
argument_list|(
literal|"d"
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|as
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ds
operator|-
literal|1
init|;
name|i
operator|<
name|de
condition|;
operator|++
name|i
control|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|"< "
argument_list|)
operator|.
name|append
argument_list|(
name|lines1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|de
operator|==
literal|0
condition|)
block|{
comment|// an addition: "<ds>a<as,ae>"
name|sw
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ds
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|as
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ae
operator|>
name|as
condition|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ae
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sw
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|as
operator|-
literal|1
init|;
name|i
operator|<
name|ae
condition|;
operator|++
name|i
control|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
operator|.
name|append
argument_list|(
name|lines2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// a change: "<ds>,<de>c<as>,<ae>
name|sw
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ds
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|de
operator|>
name|ds
condition|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|de
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sw
operator|.
name|append
argument_list|(
literal|"c"
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|as
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ae
operator|>
name|as
condition|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|ae
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sw
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ds
operator|-
literal|1
init|;
name|i
operator|<
name|de
condition|;
operator|++
name|i
control|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|"< "
argument_list|)
operator|.
name|append
argument_list|(
name|lines1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|sw
operator|.
name|append
argument_list|(
literal|"---\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|as
operator|-
literal|1
init|;
name|i
operator|<
name|ae
condition|;
operator|++
name|i
control|)
block|{
name|sw
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
operator|.
name|append
argument_list|(
name|lines2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|offset
operator|=
name|offset
operator|+
operator|(
name|ae
operator|-
name|as
operator|)
operator|-
operator|(
name|de
operator|-
name|ds
operator|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a list of the lines in a given file.    *    * @param file File    * @return List of lines    */
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|fileLines
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|LineNumberReader
name|r
init|=
operator|new
name|LineNumberReader
argument_list|(
name|Util
operator|.
name|reader
argument_list|(
name|file
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
return|return
name|lines
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
comment|/**    * Returns the contents of a file as a string.    *    * @param file File    * @return Contents of the file    */
specifier|protected
specifier|static
name|String
name|fileContents
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
try|try
init|(
name|FileInputStream
name|reader
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|int
name|readCount
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|writer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|readCount
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|readCount
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|/**    * Sets whether to give verbose message if diff fails.    */
specifier|protected
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
comment|/**    * Sets the diff masks that are common to .REF files    */
specifier|protected
name|void
name|setRefFileDiffMasks
parameter_list|()
block|{
comment|// mask out source control Id
name|addDiffMask
argument_list|(
literal|"\\$Id.*\\$"
argument_list|)
expr_stmt|;
comment|// NOTE hersker 2006-06-02:
comment|// The following two patterns can be used to mask out the
comment|// sqlline JDBC URI and continuation prompts. This is useful
comment|// during transition periods when URIs are changed, or when
comment|// new drivers are deployed which have their own URIs but
comment|// should first pass the existing test suite before their
comment|// own .ref files get checked in.
comment|//
comment|// It is not recommended to use these patterns on an everyday
comment|// basis. Real differences in the output are difficult to spot
comment|// when diff-ing .ref and .log files which have different
comment|// sqlline prompts at the start of each line.
comment|// mask out sqlline JDBC URI prompt
name|addDiffMask
argument_list|(
literal|"0: \\bjdbc(:[^:>]+)+:>"
argument_list|)
expr_stmt|;
comment|// mask out different-length sqlline continuation prompts
name|addDiffMask
argument_list|(
literal|"^(\\.\\s?)+>"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

