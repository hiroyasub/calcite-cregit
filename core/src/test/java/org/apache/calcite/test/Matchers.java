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
name|plan
operator|.
name|RelOptUtil
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
name|rel
operator|.
name|RelNode
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
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CustomTypeSafeMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Factory
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
name|hamcrest
operator|.
name|TypeSafeMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
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
name|SQLException
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_comment
comment|/**  * Matchers for testing SQL queries.  */
end_comment

begin_class
specifier|public
class|class
name|Matchers
block|{
specifier|private
name|Matchers
parameter_list|()
block|{
block|}
comment|/** Allows passing the actual result from the {@code matchesSafely} method to    * the {@code describeMismatchSafely} method that will show the difference. */
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Object
argument_list|>
name|THREAD_ACTUAL
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Creates a matcher that matches if the examined result set returns the    * given collection of rows in some order.    *    *<p>Closes the result set after reading.    *    *<p>For example:    *<pre>assertThat(statement.executeQuery("select empno from emp"),    *   returnsUnordered("empno=1234", "empno=100"));</pre>    */
specifier|public
specifier|static
name|Matcher
argument_list|<
name|?
super|super
name|ResultSet
argument_list|>
name|returnsUnordered
parameter_list|(
name|String
modifier|...
name|lines
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|lines
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedList
argument_list|)
expr_stmt|;
return|return
operator|new
name|CustomTypeSafeMatcher
argument_list|<
name|ResultSet
argument_list|>
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|lines
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|describeMismatchSafely
parameter_list|(
name|ResultSet
name|item
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
specifier|final
name|Object
name|value
init|=
name|THREAD_ACTUAL
operator|.
name|get
argument_list|()
decl_stmt|;
name|THREAD_ACTUAL
operator|.
name|remove
argument_list|()
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"was "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|CalciteAssert
operator|.
name|toStringList
argument_list|(
name|resultSet
argument_list|,
name|actualList
argument_list|)
expr_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
name|THREAD_ACTUAL
operator|.
name|set
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|equals
init|=
name|actualList
operator|.
name|equals
argument_list|(
name|expectedList
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|equals
condition|)
block|{
name|THREAD_ACTUAL
operator|.
name|set
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
block|}
return|return
name|equals
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Comparable
parameter_list|>
name|Matcher
argument_list|<
name|Iterable
argument_list|<
name|E
argument_list|>
argument_list|>
name|equalsUnordered
parameter_list|(
name|E
modifier|...
name|lines
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|toStringList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|lines
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedList
argument_list|)
expr_stmt|;
specifier|final
name|String
name|description
init|=
name|Util
operator|.
name|lines
argument_list|(
name|expectedList
argument_list|)
decl_stmt|;
return|return
operator|new
name|CustomTypeSafeMatcher
argument_list|<
name|Iterable
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|(
name|description
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|describeMismatchSafely
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|actuals
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|toStringList
argument_list|(
name|actuals
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"was "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|Util
operator|.
name|lines
argument_list|(
name|actualList
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|actuals
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|actualList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|toStringList
argument_list|(
name|actuals
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|actualList
argument_list|)
expr_stmt|;
return|return
name|actualList
operator|.
name|equals
argument_list|(
name|expectedList
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Iterable
argument_list|<
name|String
argument_list|>
name|toStringList
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|items
parameter_list|)
block|{
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|items
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Util
operator|.
name|toImmutableList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a matcher that matches when the examined object is within    * {@code epsilon} of the specified<code>operand</code>.    */
annotation|@
name|Factory
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|Matcher
argument_list|<
name|T
argument_list|>
name|within
parameter_list|(
name|T
name|value
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
return|return
operator|new
name|IsWithin
argument_list|<
name|T
argument_list|>
argument_list|(
name|value
argument_list|,
name|epsilon
argument_list|)
return|;
block|}
comment|/** Creates a matcher by applying a function to a value before calling    * another matcher. */
specifier|public
specifier|static
parameter_list|<
name|F
parameter_list|,
name|T
parameter_list|>
name|Matcher
argument_list|<
name|F
argument_list|>
name|compose
parameter_list|(
name|Matcher
argument_list|<
name|T
argument_list|>
name|matcher
parameter_list|,
name|Function
argument_list|<
name|F
argument_list|,
name|T
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|ComposingMatcher
argument_list|<>
argument_list|(
name|matcher
argument_list|,
name|f
argument_list|)
return|;
block|}
comment|/**    * Creates a Matcher that matches when the examined string is equal to the    * specified {@code value} when all Windows-style line endings ("\r\n")    * have been converted to Unix-style line endings ("\n").    *    *<p>Thus, if {@code foo()} is a function that returns "hello{newline}world"    * in the current operating system's line endings, then    *    *<blockquote>    *   assertThat(foo(), isLinux("hello\nworld"));    *</blockquote>    *    *<p>will succeed on all platforms.    *    * @see Util#toLinux(String)    */
annotation|@
name|Factory
specifier|public
specifier|static
name|Matcher
argument_list|<
name|String
argument_list|>
name|isLinux
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
return|return
name|compose
argument_list|(
name|Is
operator|.
name|is
argument_list|(
name|value
argument_list|)
argument_list|,
name|input
lambda|->
name|input
operator|==
literal|null
condition|?
literal|null
else|:
name|Util
operator|.
name|toLinux
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a Matcher that matches a {@link RelNode} its string representation,    * after converting Windows-style line endings ("\r\n")    * to Unix-style line endings ("\n"), is equal to the given {@code value}.    */
annotation|@
name|Factory
specifier|public
specifier|static
name|Matcher
argument_list|<
name|RelNode
argument_list|>
name|hasTree
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
return|return
name|compose
argument_list|(
name|Is
operator|.
name|is
argument_list|(
name|value
argument_list|)
argument_list|,
name|input
lambda|->
block|{
comment|// Convert RelNode to a string with Linux line-endings
return|return
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
comment|/**    * Creates a matcher that matches when the examined string is equal to the    * specified<code>operand</code> when all Windows-style line endings ("\r\n")    * have been converted to Unix-style line endings ("\n").    *    *<p>Thus, if {@code foo()} is a function that returns "hello{newline}world"    * in the current operating system's line endings, then    *    *<blockquote>    *   assertThat(foo(), isLinux("hello\nworld"));    *</blockquote>    *    *<p>will succeed on all platforms.    *    * @see Util#toLinux(String)    */
annotation|@
name|Factory
specifier|public
specifier|static
name|Matcher
argument_list|<
name|String
argument_list|>
name|containsStringLinux
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|compose
argument_list|(
name|CoreMatchers
operator|.
name|containsString
argument_list|(
name|value
argument_list|)
argument_list|,
name|Util
operator|::
name|toLinux
argument_list|)
return|;
block|}
comment|/**    * Is the numeric value within a given difference another value?    *    * @param<T> Value type    */
specifier|public
specifier|static
class|class
name|IsWithin
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|BaseMatcher
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|T
name|expectedValue
decl_stmt|;
specifier|private
specifier|final
name|double
name|epsilon
decl_stmt|;
specifier|public
name|IsWithin
parameter_list|(
name|T
name|expectedValue
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|epsilon
operator|>=
literal|0D
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedValue
operator|=
name|expectedValue
expr_stmt|;
name|this
operator|.
name|epsilon
operator|=
name|epsilon
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|actualValue
parameter_list|)
block|{
return|return
name|isWithin
argument_list|(
name|actualValue
argument_list|,
name|expectedValue
argument_list|,
name|epsilon
argument_list|)
return|;
block|}
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendValue
argument_list|(
name|expectedValue
operator|+
literal|" +/-"
operator|+
name|epsilon
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isWithin
parameter_list|(
name|Object
name|actual
parameter_list|,
name|Number
name|expected
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
return|return
name|expected
operator|==
literal|null
return|;
block|}
if|if
condition|(
name|actual
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|double
name|a
init|=
operator|(
operator|(
name|Number
operator|)
name|actual
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|min
init|=
name|expected
operator|.
name|doubleValue
argument_list|()
operator|-
name|epsilon
decl_stmt|;
specifier|final
name|double
name|max
init|=
name|expected
operator|.
name|doubleValue
argument_list|()
operator|+
name|epsilon
decl_stmt|;
return|return
name|min
operator|<=
name|a
operator|&&
name|a
operator|<=
name|max
return|;
block|}
block|}
comment|/** Matcher that transforms the input value using a function before    * passing to another matcher.    *    * @param<F> From type: the type of value to be matched    * @param<T> To type: type returned by function, and the resulting matcher    */
specifier|private
specifier|static
class|class
name|ComposingMatcher
parameter_list|<
name|F
parameter_list|,
name|T
parameter_list|>
extends|extends
name|TypeSafeMatcher
argument_list|<
name|F
argument_list|>
block|{
specifier|private
specifier|final
name|Matcher
argument_list|<
name|T
argument_list|>
name|matcher
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|F
argument_list|,
name|T
argument_list|>
name|f
decl_stmt|;
name|ComposingMatcher
parameter_list|(
name|Matcher
argument_list|<
name|T
argument_list|>
name|matcher
parameter_list|,
name|Function
argument_list|<
name|F
argument_list|,
name|T
argument_list|>
name|f
parameter_list|)
block|{
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
name|this
operator|.
name|f
operator|=
name|f
expr_stmt|;
block|}
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|F
name|item
parameter_list|)
block|{
return|return
name|matcher
operator|.
name|matches
argument_list|(
name|f
operator|.
name|apply
argument_list|(
name|item
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|matcher
operator|.
name|describeTo
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Matchers.java
end_comment

end_unit

