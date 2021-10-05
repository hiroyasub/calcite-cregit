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
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|Suppliers
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
name|ImmutableSortedSet
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
name|Assertions
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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

begin_comment
comment|/**  * Static utilities for JUnit tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TestUtil
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Pattern
name|LINE_BREAK_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\r\n|\r|\n"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|TAB_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LINE_BREAK
init|=
literal|"\\\\n\""
operator|+
name|Util
operator|.
name|LINE_SEPARATOR
operator|+
literal|" + \""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JAVA_VERSION
init|=
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Supplier
argument_list|<
name|Integer
argument_list|>
name|GUAVA_MAJOR_VERSION
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
name|TestUtil
operator|::
name|computeGuavaMajorVersion
argument_list|)
operator|::
name|get
decl_stmt|;
comment|/** Matches a number with at least four zeros after the point. */
specifier|private
specifier|static
specifier|final
name|Pattern
name|TRAILING_ZERO_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"-?[0-9]+\\.([0-9]*[1-9])?(00000*[0-9][0-9]?)"
argument_list|)
decl_stmt|;
comment|/** Matches a number with at least four nines after the point. */
specifier|private
specifier|static
specifier|final
name|Pattern
name|TRAILING_NINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"-?[0-9]+\\.([0-9]*[0-8])?(99999*[0-9][0-9]?)"
argument_list|)
decl_stmt|;
comment|/** This is to be used by {@link #rethrow(Throwable, String)} to add extra information via    * {@link Throwable#addSuppressed(Throwable)}. */
specifier|private
specifier|static
class|class
name|ExtraInformation
extends|extends
name|Throwable
block|{
name|ExtraInformation
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|static
name|void
name|assertEqualsVerbose
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|actual
parameter_list|)
block|{
name|Assertions
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
parameter_list|()
lambda|->
literal|"Expected:\n"
operator|+
name|expected
operator|+
literal|"\nActual:\n"
operator|+
name|actual
operator|+
literal|"\nActual java:\n"
operator|+
name|toJavaString
argument_list|(
name|actual
argument_list|)
operator|+
literal|'\n'
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts a string (which may contain quotes and newlines) into a java    * literal.    *    *<p>For example,    *<pre><code>string with "quotes" split    * across lines</code></pre>    *    *<p>becomes    *    *<blockquote><pre><code>"string with \"quotes\" split" + NL +    *  "across lines"</code></pre></blockquote>    */
specifier|public
specifier|static
name|String
name|quoteForJava
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|s
operator|=
name|Util
operator|.
name|replace
argument_list|(
name|s
argument_list|,
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
expr_stmt|;
name|s
operator|=
name|Util
operator|.
name|replace
argument_list|(
name|s
argument_list|,
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
expr_stmt|;
name|s
operator|=
name|LINE_BREAK_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
name|LINE_BREAK
argument_list|)
expr_stmt|;
name|s
operator|=
name|TAB_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\t"
argument_list|)
expr_stmt|;
name|s
operator|=
literal|"\""
operator|+
name|s
operator|+
literal|"\""
expr_stmt|;
specifier|final
name|String
name|spurious
init|=
literal|" + \n\"\""
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|spurious
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
name|spurious
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Converts a string (which may contain quotes and newlines) into a java    * literal.    *    *<p>For example,</p>    *    *<blockquote><pre><code>string with "quotes" split    * across lines</code></pre></blockquote>    *    *<p>becomes</p>    *    *<blockquote><pre><code>TestUtil.fold(    *  "string with \"quotes\" split\n",    *  + "across lines")</code></pre></blockquote>    */
specifier|public
specifier|static
name|String
name|toJavaString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
comment|// Convert [string with "quotes" split
comment|// across lines]
comment|// into [fold(
comment|// "string with \"quotes\" split\n"
comment|// + "across lines")]
comment|//
name|s
operator|=
name|Util
operator|.
name|replace
argument_list|(
name|s
argument_list|,
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
expr_stmt|;
name|s
operator|=
name|LINE_BREAK_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
name|LINE_BREAK
argument_list|)
expr_stmt|;
name|s
operator|=
name|TAB_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\t"
argument_list|)
expr_stmt|;
name|s
operator|=
literal|"\""
operator|+
name|s
operator|+
literal|"\""
expr_stmt|;
name|String
name|spurious
init|=
literal|"\n \\+ \"\""
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
name|spurious
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
name|spurious
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Combines an array of strings, each representing a line, into a single    * string containing line separators.    */
specifier|public
specifier|static
name|String
name|fold
parameter_list|(
name|String
modifier|...
name|strings
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
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
name|buf
operator|.
name|append
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Quotes a string for Java or JSON. */
specifier|public
specifier|static
name|String
name|escapeString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|escapeString
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
name|s
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Quotes a string for Java or JSON, into a builder. */
specifier|public
specifier|static
name|StringBuilder
name|escapeString
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|lastChar
init|=
literal|0
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
operator|++
name|i
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\n'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\n"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\r'
case|:
if|if
condition|(
name|lastChar
operator|!=
literal|'\n'
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\\r"
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
break|break;
block|}
name|lastChar
operator|=
name|c
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
return|;
block|}
comment|/**    * Quotes a pattern.    */
specifier|public
specifier|static
name|String
name|quotePattern
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"\\."
argument_list|)
operator|.
name|replace
argument_list|(
literal|"+"
argument_list|,
literal|"\\+"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"{"
argument_list|,
literal|"\\{"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"}"
argument_list|,
literal|"\\}"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"|"
argument_list|,
literal|"\\||"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$"
argument_list|,
literal|"\\$"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"?"
argument_list|,
literal|"\\?"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|"\\*"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"("
argument_list|,
literal|"\\("
argument_list|)
operator|.
name|replace
argument_list|(
literal|")"
argument_list|,
literal|"\\)"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"["
argument_list|,
literal|"\\["
argument_list|)
operator|.
name|replace
argument_list|(
literal|"]"
argument_list|,
literal|"\\]"
argument_list|)
return|;
block|}
comment|/** Removes floating-point rounding errors from the end of a string.    *    *<p>{@code 12.300000006} becomes {@code 12.3};    * {@code -12.37999999991} becomes {@code -12.38}. */
specifier|public
specifier|static
name|String
name|correctRoundedFloat
parameter_list|(
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
return|return
name|s
return|;
block|}
specifier|final
name|Matcher
name|m
init|=
name|TRAILING_ZERO_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Matcher
name|m2
init|=
name|TRAILING_NINE_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|m2
operator|.
name|matches
argument_list|()
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
name|m2
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'0'
case|:
case|case
literal|'1'
case|:
case|case
literal|'2'
case|:
case|case
literal|'3'
case|:
case|case
literal|'4'
case|:
case|case
literal|'5'
case|:
case|case
literal|'6'
case|:
case|case
literal|'7'
case|:
case|case
literal|'8'
case|:
comment|// '12.3499999996' became '12.34', now we make it '12.35'
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
operator|(
name|char
operator|)
operator|(
name|c
operator|+
literal|1
operator|)
expr_stmt|;
break|break;
case|case
literal|'.'
case|:
comment|// '12.9999991' became '12.', which we leave as is.
break|break;
block|}
block|}
block|}
return|return
name|s
return|;
block|}
comment|/**    * Returns the Java major version: 7 for JDK 1.7, 8 for JDK 8, 10 for    * JDK 10, etc. depending on current system property {@code java.version}.    */
specifier|public
specifier|static
name|int
name|getJavaMajorVersion
parameter_list|()
block|{
return|return
name|majorVersionFromString
argument_list|(
name|JAVA_VERSION
argument_list|)
return|;
block|}
comment|/**    * Detects java major version given long format of full JDK version.    * See<a href="http://openjdk.java.net/jeps/223">JEP 223: New Version-String Scheme</a>.    *    * @param version current version as string usually from {@code java.version} property.    * @return major java version ({@code 8, 9, 10, 11} etc.)    */
annotation|@
name|VisibleForTesting
specifier|static
name|int
name|majorVersionFromString
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|version
argument_list|,
literal|"version"
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|.
name|startsWith
argument_list|(
literal|"1."
argument_list|)
condition|)
block|{
comment|// running on version<= 8 (expecting string of type: x.y.z*)
specifier|final
name|String
index|[]
name|versions
init|=
name|version
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|versions
index|[
literal|1
index|]
argument_list|)
return|;
block|}
comment|// probably running on> 8 (just get first integer which is major version)
name|Matcher
name|matcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\d+"
argument_list|)
operator|.
name|matcher
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|lookingAt
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't parse (detect) JDK version from "
operator|+
name|version
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the Guava major version. */
specifier|public
specifier|static
name|int
name|getGuavaMajorVersion
parameter_list|()
block|{
return|return
name|GUAVA_MAJOR_VERSION
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Computes the Guava major version. */
specifier|private
specifier|static
name|int
name|computeGuavaMajorVersion
parameter_list|()
block|{
comment|// A list of classes and the Guava version that they were introduced.
comment|// The list should not contain any classes that are removed in future
comment|// versions of Guava.
return|return
operator|new
name|VersionChecker
argument_list|()
operator|.
name|tryClass
argument_list|(
literal|2
argument_list|,
literal|"com.google.common.collect.ImmutableList"
argument_list|)
operator|.
name|tryClass
argument_list|(
literal|14
argument_list|,
literal|"com.google.common.reflect.Parameter"
argument_list|)
operator|.
name|tryClass
argument_list|(
literal|17
argument_list|,
literal|"com.google.common.base.VerifyException"
argument_list|)
operator|.
name|tryClass
argument_list|(
literal|21
argument_list|,
literal|"com.google.common.io.RecursiveDeleteOption"
argument_list|)
operator|.
name|tryClass
argument_list|(
literal|23
argument_list|,
literal|"com.google.common.util.concurrent.FluentFuture"
argument_list|)
operator|.
name|tryClass
argument_list|(
literal|26
argument_list|,
literal|"com.google.common.util.concurrent.ExecutionSequencer"
argument_list|)
operator|.
name|bestVersion
return|;
block|}
comment|/** Returns the JVM vendor. */
specifier|public
specifier|static
name|String
name|getJavaVirtualMachineVendor
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.vm.vendor"
argument_list|)
return|;
block|}
comment|/** Given a list, returns the number of elements that are not between an    * element that is less and an element that is greater. */
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Comparable
argument_list|<
name|E
argument_list|>
parameter_list|>
name|SortedSet
argument_list|<
name|E
argument_list|>
name|outOfOrderItems
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|)
block|{
name|E
name|previous
init|=
literal|null
decl_stmt|;
specifier|final
name|ImmutableSortedSet
operator|.
name|Builder
argument_list|<
name|E
argument_list|>
name|b
init|=
name|ImmutableSortedSet
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
for|for
control|(
name|E
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|.
name|compareTo
argument_list|(
name|e
argument_list|)
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|e
expr_stmt|;
block|}
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Checks if exceptions have give substring. That is handy to prevent logging SQL text twice */
specifier|public
specifier|static
name|boolean
name|hasMessage
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|String
name|substring
parameter_list|)
block|{
while|while
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|String
name|message
init|=
name|t
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
name|message
operator|.
name|contains
argument_list|(
name|substring
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|t
operator|=
name|t
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Rethrows given exception keeping stacktraces clean and compact. */
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Throwable
parameter_list|>
name|RuntimeException
name|rethrow
parameter_list|(
name|Throwable
name|e
parameter_list|)
throws|throws
name|E
block|{
if|if
condition|(
name|e
operator|instanceof
name|InvocationTargetException
condition|)
block|{
name|e
operator|=
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|(
name|E
operator|)
name|e
throw|;
block|}
comment|/** Rethrows given exception keeping stacktraces clean and compact. */
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Throwable
parameter_list|>
name|RuntimeException
name|rethrow
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|E
block|{
name|e
operator|.
name|addSuppressed
argument_list|(
operator|new
name|ExtraInformation
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|E
operator|)
name|e
throw|;
block|}
comment|/** Returns string representation of the given {@link Throwable}. */
specifier|public
specifier|static
name|String
name|printStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Checks whether a given class exists, and updates a version if it does. */
specifier|private
specifier|static
class|class
name|VersionChecker
block|{
name|int
name|bestVersion
init|=
operator|-
literal|1
decl_stmt|;
name|VersionChecker
name|tryClass
parameter_list|(
name|int
name|version
parameter_list|,
name|String
name|className
parameter_list|)
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|bestVersion
operator|=
name|Math
operator|.
name|max
argument_list|(
name|version
argument_list|,
name|bestVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

