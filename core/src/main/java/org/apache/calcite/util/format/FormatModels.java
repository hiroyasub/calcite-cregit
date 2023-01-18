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
operator|.
name|format
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|ConcurrentHashMap
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|format
operator|.
name|FormatElementEnum
operator|.
name|D
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|DAY
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|DD
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|DDD
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|DY
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF1
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF2
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF3
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF4
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF5
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|FF6
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|HH24
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|IW
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|MI
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|MM
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|MON
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|MONTH
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|Q
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|SS
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|TZR
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|WW
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|YY
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
name|format
operator|.
name|FormatElementEnum
operator|.
name|YYYY
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Utilities for {@link FormatModel}.  */
end_comment

begin_class
specifier|public
class|class
name|FormatModels
block|{
specifier|private
name|FormatModels
parameter_list|()
block|{
block|}
comment|/** The format model consisting of built-in format elements.    *    *<p>Due to the design of {@link FormatElementEnum}, it is similar to    * Oracle's format model.    */
specifier|public
specifier|static
specifier|final
name|FormatModel
name|DEFAULT
decl_stmt|;
comment|/** Format model for BigQuery.    *    *<p>BigQuery format element reference:    *<a href="https://cloud.google.com/bigquery/docs/reference/standard-sql/format-elements">    * BigQuery Standard SQL Format Elements</a>.    */
specifier|public
specifier|static
specifier|final
name|FormatModel
name|BIG_QUERY
decl_stmt|;
static|static
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FormatElementEnum
name|fe
range|:
name|FormatElementEnum
operator|.
name|values
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|fe
operator|.
name|toString
argument_list|()
argument_list|,
name|fe
argument_list|)
expr_stmt|;
block|}
name|DEFAULT
operator|=
name|create
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%A"
argument_list|,
name|DAY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%a"
argument_list|,
name|DY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%B"
argument_list|,
name|MONTH
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%b"
argument_list|,
name|MON
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%c"
argument_list|,
name|compositeElement
argument_list|(
literal|"The date and time representation (English);"
argument_list|,
name|DY
argument_list|,
name|literalElement
argument_list|(
literal|" "
argument_list|)
argument_list|,
name|MON
argument_list|,
name|literalElement
argument_list|(
literal|" "
argument_list|)
argument_list|,
name|DD
argument_list|,
name|literalElement
argument_list|(
literal|" "
argument_list|)
argument_list|,
name|HH24
argument_list|,
name|literalElement
argument_list|(
literal|":"
argument_list|)
argument_list|,
name|MI
argument_list|,
name|literalElement
argument_list|(
literal|":"
argument_list|)
argument_list|,
name|SS
argument_list|,
name|literalElement
argument_list|(
literal|" "
argument_list|)
argument_list|,
name|YYYY
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%d"
argument_list|,
name|DD
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E1S"
argument_list|,
name|FF1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E2S"
argument_list|,
name|FF2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E3S"
argument_list|,
name|FF3
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E4S"
argument_list|,
name|FF4
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E5S"
argument_list|,
name|FF5
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%E*S"
argument_list|,
name|FF6
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%H"
argument_list|,
name|HH24
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%j"
argument_list|,
name|DDD
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%M"
argument_list|,
name|MI
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%m"
argument_list|,
name|MM
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%Q"
argument_list|,
name|Q
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%R"
argument_list|,
name|compositeElement
argument_list|(
literal|"The time in the format %H:%M"
argument_list|,
name|HH24
argument_list|,
name|literalElement
argument_list|(
literal|":"
argument_list|)
argument_list|,
name|MI
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%S"
argument_list|,
name|SS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%u"
argument_list|,
name|D
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%V"
argument_list|,
name|IW
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%W"
argument_list|,
name|WW
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%x"
argument_list|,
name|compositeElement
argument_list|(
literal|"The date representation in MM/DD/YY format"
argument_list|,
name|MM
argument_list|,
name|literalElement
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|DD
argument_list|,
name|literalElement
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|YY
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%Y"
argument_list|,
name|YYYY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%y"
argument_list|,
name|YY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"%Z"
argument_list|,
name|TZR
argument_list|)
expr_stmt|;
name|BIG_QUERY
operator|=
name|create
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generates a {@link Pattern} using the keys of a {@link FormatModel} element    * map. This pattern is used in {@link FormatModel#parse(String)} to help    * locate known format elements in a string.    */
specifier|private
specifier|static
name|Pattern
name|regexFromMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|elementMap
parameter_list|)
block|{
name|StringBuilder
name|regex
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|elementMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|regex
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")|"
argument_list|)
expr_stmt|;
block|}
comment|// remove the last '|'
name|regex
operator|.
name|setLength
argument_list|(
name|regex
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a {@link FormatModel} that uses the provided map to identify    * {@link FormatElement}s while parsing a format string.    */
specifier|public
specifier|static
name|FormatModel
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|elementMap
parameter_list|)
block|{
specifier|final
name|Pattern
name|pattern
init|=
name|regexFromMap
argument_list|(
name|elementMap
argument_list|)
decl_stmt|;
return|return
operator|new
name|FormatModelImpl
argument_list|(
name|pattern
argument_list|,
name|elementMap
argument_list|)
return|;
block|}
comment|/**    * Creates a literal format element.    */
specifier|public
specifier|static
name|FormatElement
name|literalElement
parameter_list|(
name|String
name|literal
parameter_list|)
block|{
return|return
operator|new
name|FormatModelElementLiteral
argument_list|(
name|literal
argument_list|)
return|;
block|}
comment|/**    * Creates a composite format element from the provided list of elements    * and description.    */
specifier|public
specifier|static
name|FormatElement
name|compositeElement
parameter_list|(
name|String
name|description
parameter_list|,
name|FormatElement
modifier|...
name|fmtElements
parameter_list|)
block|{
return|return
operator|new
name|CompositeFormatElement
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fmtElements
argument_list|)
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/** Implementation of {@link FormatModel} based on a list of format    * elements. */
specifier|private
specifier|static
class|class
name|FormatModelImpl
implements|implements
name|FormatModel
block|{
specifier|final
name|Pattern
name|pattern
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|elementMap
decl_stmt|;
comment|/** Cache of parsed format strings.      *      *<p>NOTE: The current implementation could grow without bounds.      * A per-thread cache would be better, or a cache tied to a statement      * execution (e.g. in DataContext). Also limit to a say 100 entries. */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FormatElement
argument_list|>
argument_list|>
name|memoizedElements
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|FormatModelImpl
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|elementMap
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|requireNonNull
argument_list|(
name|pattern
argument_list|,
literal|"pattern"
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementMap
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|elementMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|getElementMap
parameter_list|()
block|{
return|return
name|elementMap
return|;
block|}
specifier|private
name|List
argument_list|<
name|FormatElement
argument_list|>
name|internalParse
parameter_list|(
name|String
name|format
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|FormatElement
argument_list|>
name|elements
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|format
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|literal
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// Add any leading literal text before next element match
name|literal
operator|=
name|format
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|literal
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
name|literalElement
argument_list|(
name|literal
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add the element match - use literal as default to be safe.
name|String
name|key
init|=
name|matcher
operator|.
name|group
argument_list|()
decl_stmt|;
name|elements
operator|.
name|add
argument_list|(
name|getElementMap
argument_list|()
operator|.
name|getOrDefault
argument_list|(
name|key
argument_list|,
name|literalElement
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|// add any remaining literal text after last element match
name|literal
operator|=
name|format
operator|.
name|substring
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|literal
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
name|literalElement
argument_list|(
name|literal
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|elements
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|FormatElement
argument_list|>
name|parse
parameter_list|(
name|String
name|format
parameter_list|)
block|{
return|return
name|memoizedElements
operator|.
name|computeIfAbsent
argument_list|(
name|format
argument_list|,
name|this
operator|::
name|internalParse
argument_list|)
return|;
block|}
block|}
comment|/**    * A format element that is literal text.    */
specifier|private
specifier|static
class|class
name|FormatModelElementLiteral
implements|implements
name|FormatElement
block|{
specifier|private
specifier|final
name|String
name|literal
decl_stmt|;
name|FormatModelElementLiteral
parameter_list|(
name|String
name|literal
parameter_list|)
block|{
name|this
operator|.
name|literal
operator|=
name|requireNonNull
argument_list|(
name|literal
argument_list|,
literal|"literal"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
return|return
name|literal
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Represents literal text in a format string"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|literal
return|;
block|}
block|}
comment|/**    * A format element comprised of one or more {@link FormatElement} entries.    */
specifier|private
specifier|static
class|class
name|CompositeFormatElement
implements|implements
name|FormatElement
block|{
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|FormatElement
argument_list|>
name|formatElements
decl_stmt|;
name|CompositeFormatElement
parameter_list|(
name|List
argument_list|<
name|FormatElement
argument_list|>
name|formatElements
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|formatElements
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|formatElements
argument_list|)
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|requireNonNull
argument_list|(
name|description
argument_list|,
literal|"description"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|flatten
argument_list|(
name|ele
lambda|->
name|buf
operator|.
name|append
argument_list|(
name|ele
operator|.
name|format
argument_list|(
name|date
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Applies a consumer to each format element that make up the composite      * element.      *      *<p>For example, {@code %R} in Google SQL represents the hour in 24-hour      * format (e.g., 00..23) followed by the minute as a decimal number.      * {@code flatten(i -> println(i.toString())); } would print "HH24:MI".      */
annotation|@
name|Override
specifier|public
name|void
name|flatten
parameter_list|(
name|Consumer
argument_list|<
name|FormatElement
argument_list|>
name|consumer
parameter_list|)
block|{
name|formatElements
operator|.
name|forEach
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|this
operator|.
name|description
return|;
block|}
block|}
block|}
end_class

end_unit

