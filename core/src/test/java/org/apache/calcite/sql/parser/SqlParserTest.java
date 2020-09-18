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
name|sql
operator|.
name|parser
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
name|avatica
operator|.
name|util
operator|.
name|Casing
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
name|avatica
operator|.
name|util
operator|.
name|Quoting
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
name|SqlCall
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
name|SqlDialect
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
name|SqlExplain
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
name|SqlIdentifier
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
name|SqlKind
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
name|SqlLiteral
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
name|SqlNode
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
name|SqlNodeList
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
name|SqlSelect
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
name|SqlSetOption
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
name|SqlWriterConfig
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
name|dialect
operator|.
name|AnsiSqlDialect
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
name|parser
operator|.
name|impl
operator|.
name|SqlParserImpl
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|test
operator|.
name|SqlTests
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
name|util
operator|.
name|SqlShuttle
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
name|SqlConformance
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
name|DiffTestCase
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
name|tools
operator|.
name|Hoist
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
name|ConversionUtil
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
name|SourceStringReader
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
name|Disabled
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
name|Reader
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|TreeSet
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
name|function
operator|.
name|UnaryOperator
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
name|Collectors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|containsString
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
name|CoreMatchers
operator|.
name|not
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
name|assertNotSame
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
comment|/**  * A<code>SqlParserTest</code> is a unit-test for  * {@link SqlParser the SQL parser}.  *  *<p>To reuse this test for an extension parser, implement the  * {@link #parserImplFactory()} method to return the extension parser  * implementation.  */
end_comment

begin_class
specifier|public
class|class
name|SqlParserTest
block|{
comment|/**    * List of reserved keywords.    *    *<p>Each keyword is followed by tokens indicating whether it is reserved in    * the SQL:92, SQL:99, SQL:2003, SQL:2011, SQL:2014 standards and in Calcite.    *    *<p>The standard keywords are derived from    *<a href="https://developer.mimer.com/wp-content/uploads/2018/05/Standard-SQL-Reserved-Words-Summary.pdf">Mimer</a>    * and from the specification.    *    *<p>If a new<b>reserved</b> keyword is added to the parser, include it in    * this list, flagged "c". If the keyword is not intended to be a reserved    * keyword, add it to the non-reserved keyword list in the parser.    */
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|RESERVED_KEYWORDS
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"ABS"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ABSOLUTE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ACTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ADD"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"AFTER"
argument_list|,
literal|"99"
argument_list|,
literal|"ALL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ALLOCATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ALLOW"
argument_list|,
literal|"c"
argument_list|,
literal|"ALTER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"AND"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ANY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ARE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ARRAY"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ARRAY_AGG"
argument_list|,
literal|"2011"
argument_list|,
literal|"ARRAY_MAX_CARDINALITY"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"AS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ASC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ASENSITIVE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ASSERTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ASYMMETRIC"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"AT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ATOMIC"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"AUTHORIZATION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"AVG"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BEFORE"
argument_list|,
literal|"99"
argument_list|,
literal|"BEGIN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BEGIN_FRAME"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BEGIN_PARTITION"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BETWEEN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BIGINT"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BINARY"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BIT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"c"
argument_list|,
literal|"BIT_LENGTH"
argument_list|,
literal|"92"
argument_list|,
literal|"BLOB"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BOTH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"BREADTH"
argument_list|,
literal|"99"
argument_list|,
literal|"BY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CALL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CALLED"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CARDINALITY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CASCADE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"CASCADED"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CASE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CAST"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CATALOG"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"CEIL"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CEILING"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CHARACTER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CHARACTER_LENGTH"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CHAR_LENGTH"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CHECK"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CLASSIFIER"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CLOB"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CLOSE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COALESCE"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COLLATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COLLATION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"COLLECT"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COLUMN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COMMIT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CONDITION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CONNECT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CONNECTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"CONSTRAINT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CONSTRAINTS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"CONSTRUCTOR"
argument_list|,
literal|"99"
argument_list|,
literal|"CONTAINS"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CONTINUE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"CONVERT"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CORR"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CORRESPONDING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COUNT"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COVAR_POP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"COVAR_SAMP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CREATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CROSS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CUBE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CUME_DIST"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_CATALOG"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_DATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_DEFAULT_TRANSFORM_GROUP"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_PATH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_ROLE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_ROW"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_SCHEMA"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_TIME"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_TIMESTAMP"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_TRANSFORM_GROUP_FOR_TYPE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURRENT_USER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CURSOR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"CYCLE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DATA"
argument_list|,
literal|"99"
argument_list|,
literal|"DATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DAY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DAYS"
argument_list|,
literal|"2011"
argument_list|,
literal|"DEALLOCATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DEC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DECIMAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DECLARE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DEFAULT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DEFERRABLE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DEFERRED"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DEFINE"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DELETE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DENSE_RANK"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DEPTH"
argument_list|,
literal|"99"
argument_list|,
literal|"DEREF"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DESC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DESCRIBE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DESCRIPTOR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DETERMINISTIC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DIAGNOSTICS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DISALLOW"
argument_list|,
literal|"c"
argument_list|,
literal|"DISCONNECT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DISTINCT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"DOMAIN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DROP"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"DYNAMIC"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EACH"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ELEMENT"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ELSE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ELSEIF"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"EMPTY"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"END"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"END-EXEC"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"END_FRAME"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"END_PARTITION"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EQUALS"
argument_list|,
literal|"99"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ESCAPE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EVERY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXCEPT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXCEPTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"EXEC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXECUTE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXISTS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXIT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"EXP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXPLAIN"
argument_list|,
literal|"c"
argument_list|,
literal|"EXTEND"
argument_list|,
literal|"c"
argument_list|,
literal|"EXTERNAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"EXTRACT"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FALSE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FETCH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FILTER"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FIRST"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"FIRST_VALUE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FLOAT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FLOOR"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FOR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FOREIGN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FOREVER"
argument_list|,
literal|"2011"
argument_list|,
literal|"FOUND"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"FRAME_ROW"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FREE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FROM"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FULL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FUNCTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"FUSION"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GENERAL"
argument_list|,
literal|"99"
argument_list|,
literal|"GET"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GLOBAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"GOTO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"GRANT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GROUP"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GROUPING"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"GROUPS"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"HANDLER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"HAVING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"HOLD"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"HOUR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"HOURS"
argument_list|,
literal|"2011"
argument_list|,
literal|"IDENTITY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"IF"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"IMMEDIATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"IMMEDIATELY"
argument_list|,
literal|"IMPORT"
argument_list|,
literal|"c"
argument_list|,
literal|"IN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INDICATOR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INITIAL"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INITIALLY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"INNER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INOUT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INPUT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"INSENSITIVE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INSERT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INTERSECT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INTERSECTION"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INTERVAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"INTO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"IS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ISOLATION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ITERATE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"JOIN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_ARRAY"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_ARRAYAGG"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_EXISTS"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_OBJECT"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_OBJECTAGG"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_QUERY"
argument_list|,
literal|"c"
argument_list|,
literal|"JSON_VALUE"
argument_list|,
literal|"c"
argument_list|,
literal|"KEEP"
argument_list|,
literal|"2011"
argument_list|,
literal|"KEY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"LAG"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LANGUAGE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LARGE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LAST"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"LAST_VALUE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LATERAL"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LEAD"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LEADING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LEAVE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"LEFT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LEVEL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"LIKE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LIKE_REGEX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LIMIT"
argument_list|,
literal|"c"
argument_list|,
literal|"LN"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LOCAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LOCALTIME"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LOCALTIMESTAMP"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"LOCATOR"
argument_list|,
literal|"99"
argument_list|,
literal|"LOOP"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"LOWER"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MAP"
argument_list|,
literal|"99"
argument_list|,
literal|"MATCH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MATCHES"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MATCH_NUMBER"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MATCH_RECOGNIZE"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MAX"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MAX_CARDINALITY"
argument_list|,
literal|"2011"
argument_list|,
literal|"MEASURES"
argument_list|,
literal|"c"
argument_list|,
literal|"MEMBER"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MERGE"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"METHOD"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MIN"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MINUS"
argument_list|,
literal|"c"
argument_list|,
literal|"MINUTE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MINUTES"
argument_list|,
literal|"2011"
argument_list|,
literal|"MOD"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MODIFIES"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MODULE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MONTH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"MULTISET"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NAMES"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"NATIONAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NATURAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NCHAR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NCLOB"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NEW"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NEXT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"c"
argument_list|,
literal|"NO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NONE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NORMALIZE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NOT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NTH_VALUE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NTILE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NULL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NULLIF"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"NUMERIC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OBJECT"
argument_list|,
literal|"99"
argument_list|,
literal|"OCCURRENCES_REGEX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OCTET_LENGTH"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OF"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OFFSET"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OLD"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OMIT"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ON"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ONE"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ONLY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OPEN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OPTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"OR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ORDER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ORDINALITY"
argument_list|,
literal|"99"
argument_list|,
literal|"OUT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OUTER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OUTPUT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"OVER"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OVERLAPS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"OVERLAY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PAD"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PARAMETER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PARTIAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PARTITION"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PATH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PATTERN"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PER"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERCENT"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERCENTILE_CONT"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERCENTILE_DISC"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERCENT_RANK"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERIOD"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PERMUTE"
argument_list|,
literal|"c"
argument_list|,
literal|"PORTION"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"POSITION"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"POSITION_REGEX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"POWER"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PRECEDES"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PRECISION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PREPARE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PRESERVE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PREV"
argument_list|,
literal|"c"
argument_list|,
literal|"PRIMARY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PRIOR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PRIVILEGES"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"PROCEDURE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"PUBLIC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"RANGE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RANK"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"READ"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"READS"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RECURSIVE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REF"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REFERENCES"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REFERENCING"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_AVGX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_AVGY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_COUNT"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_INTERCEPT"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_R2"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_SLOPE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_SXX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_SXY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REGR_SYY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RELATIVE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"RELEASE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REPEAT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"RESET"
argument_list|,
literal|"c"
argument_list|,
literal|"RESIGNAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"RESTRICT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"RESULT"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RETURN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RETURNS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"REVOKE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RIGHT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ROLE"
argument_list|,
literal|"99"
argument_list|,
literal|"ROLLBACK"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ROLLUP"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ROUTINE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"ROW"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ROWS"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"ROW_NUMBER"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"RUNNING"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SAVEPOINT"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SCHEMA"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"SCOPE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SCROLL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SEARCH"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SECOND"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SECONDS"
argument_list|,
literal|"2011"
argument_list|,
literal|"SECTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"SEEK"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SELECT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SENSITIVE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SESSION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"SESSION_USER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SET"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SETS"
argument_list|,
literal|"99"
argument_list|,
literal|"SHOW"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SIGNAL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"SIMILAR"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SIZE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"SKIP"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SMALLINT"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SOME"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SPACE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"SPECIFIC"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SPECIFICTYPE"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SQL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SQLCODE"
argument_list|,
literal|"92"
argument_list|,
literal|"SQLERROR"
argument_list|,
literal|"92"
argument_list|,
literal|"SQLEXCEPTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SQLSTATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SQLWARNING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SQRT"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"START"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"STATE"
argument_list|,
literal|"99"
argument_list|,
literal|"STATIC"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"STDDEV_POP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"STDDEV_SAMP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"STREAM"
argument_list|,
literal|"c"
argument_list|,
literal|"SUBMULTISET"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SUBSET"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SUBSTRING"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SUBSTRING_REGEX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SUCCEEDS"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SUM"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SYMMETRIC"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SYSTEM"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SYSTEM_TIME"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"SYSTEM_USER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TABLE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TABLESAMPLE"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TEMPORARY"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"THEN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TIME"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TIMESTAMP"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TIMEZONE_HOUR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TIMEZONE_MINUTE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TINYINT"
argument_list|,
literal|"c"
argument_list|,
literal|"TO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRAILING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRANSACTION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"TRANSLATE"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRANSLATE_REGEX"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRANSLATION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TREAT"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRIGGER"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRIM"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRIM_ARRAY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRUE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"TRUNCATE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UESCAPE"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UNDER"
argument_list|,
literal|"99"
argument_list|,
literal|"UNDO"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"UNION"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UNIQUE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UNKNOWN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UNNEST"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UNTIL"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"UPDATE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UPPER"
argument_list|,
literal|"92"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"UPSERT"
argument_list|,
literal|"c"
argument_list|,
literal|"USAGE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"USER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"USING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VALUE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VALUES"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VALUE_OF"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VARBINARY"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VARCHAR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VARYING"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VAR_POP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VAR_SAMP"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VERSION"
argument_list|,
literal|"2011"
argument_list|,
literal|"VERSIONING"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"VERSIONS"
argument_list|,
literal|"2011"
argument_list|,
literal|"VIEW"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"WHEN"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WHENEVER"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WHERE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WHILE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"WIDTH_BUCKET"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WINDOW"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WITH"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WITHIN"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WITHOUT"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"WORK"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"WRITE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"YEAR"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|,
literal|"2003"
argument_list|,
literal|"2011"
argument_list|,
literal|"2014"
argument_list|,
literal|"c"
argument_list|,
literal|"YEARS"
argument_list|,
literal|"2011"
argument_list|,
literal|"ZONE"
argument_list|,
literal|"92"
argument_list|,
literal|"99"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ANY
init|=
literal|"(?s).*"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|boolean
index|[]
argument_list|>
name|LINUXIFY
init|=
name|ThreadLocal
operator|.
name|withInitial
argument_list|(
parameter_list|()
lambda|->
operator|new
name|boolean
index|[]
block|{
literal|true
block|}
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlWriterConfig
name|SQL_WRITER_CONFIG
init|=
name|SqlPrettyWriter
operator|.
name|config
argument_list|()
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|true
argument_list|)
operator|.
name|withUpdateSetListNewline
argument_list|(
literal|false
argument_list|)
operator|.
name|withFromFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|BIG_QUERY
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|BIG_QUERY
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|CALCITE
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|MSSQL
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MSSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|MYSQL
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|MYSQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|ORACLE
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|ORACLE
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|POSTGRESQL
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|POSTGRESQL
operator|.
name|getDialect
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlDialect
name|REDSHIFT
init|=
name|SqlDialect
operator|.
name|DatabaseProduct
operator|.
name|REDSHIFT
operator|.
name|getDialect
argument_list|()
decl_stmt|;
name|Quoting
name|quoting
init|=
name|Quoting
operator|.
name|DOUBLE_QUOTE
decl_stmt|;
name|Casing
name|unquotedCasing
init|=
name|Casing
operator|.
name|TO_UPPER
decl_stmt|;
name|Casing
name|quotedCasing
init|=
name|Casing
operator|.
name|UNCHANGED
decl_stmt|;
name|SqlConformance
name|conformance
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
decl_stmt|;
specifier|protected
name|Tester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|TesterImpl
argument_list|()
return|;
block|}
specifier|protected
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|parser
lambda|->
block|{
block|}
argument_list|)
return|;
block|}
specifier|protected
name|Sql
name|expr
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|parser
lambda|->
block|{
block|}
argument_list|)
return|;
block|}
comment|/** Creates an instance of helper class {@link SqlList} to test parsing a    * list of statements. */
specifier|protected
name|SqlList
name|sqlList
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|SqlList
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/**    * Implementors of custom parsing logic who want to reuse this test should    * override this method with the factory for their extension parser.    */
specifier|protected
name|SqlParserImplFactory
name|parserImplFactory
parameter_list|()
block|{
return|return
name|SqlParserImpl
operator|.
name|FACTORY
return|;
block|}
specifier|public
name|SqlParser
name|getSqlParser
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|getSqlParser
argument_list|(
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
argument_list|,
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|SqlParser
name|getSqlParser
parameter_list|(
name|Reader
name|source
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlParser
operator|.
name|Config
name|configBuilder
init|=
name|SqlParser
operator|.
name|config
argument_list|()
operator|.
name|withParserFactory
argument_list|(
name|parserImplFactory
argument_list|()
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|quoting
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|unquotedCasing
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|quotedCasing
argument_list|)
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
decl_stmt|;
specifier|final
name|SqlParser
operator|.
name|Config
name|config
init|=
name|transform
operator|.
name|apply
argument_list|(
name|configBuilder
argument_list|)
decl_stmt|;
return|return
name|SqlParser
operator|.
name|create
argument_list|(
name|source
argument_list|,
name|config
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|getTransform
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
return|return
name|dialect
operator|==
literal|null
condition|?
name|UnaryOperator
operator|.
name|identity
argument_list|()
else|:
name|dialect
operator|::
name|configureParser
return|;
block|}
comment|/** Returns a {@link Matcher} that succeeds if the given {@link SqlNode} is a    * DDL statement. */
specifier|public
specifier|static
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|isDdl
parameter_list|()
block|{
return|return
operator|new
name|BaseMatcher
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|item
parameter_list|)
block|{
return|return
name|item
operator|instanceof
name|SqlNode
operator|&&
name|SqlKind
operator|.
name|DDL
operator|.
name|contains
argument_list|(
operator|(
operator|(
name|SqlNode
operator|)
name|item
operator|)
operator|.
name|getKind
argument_list|()
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
name|appendText
argument_list|(
literal|"isDdl"
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** Returns a {@link Matcher} that succeeds if the given {@link SqlNode} is a    * VALUES that contains a ROW that contains an identifier whose {@code i}th    * element is quoted. */
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|isQuoted
parameter_list|(
specifier|final
name|int
name|i
parameter_list|,
specifier|final
name|boolean
name|quoted
parameter_list|)
block|{
return|return
operator|new
name|CustomTypeSafeMatcher
argument_list|<
name|SqlNode
argument_list|>
argument_list|(
literal|"quoting"
argument_list|)
block|{
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|SqlNode
name|item
parameter_list|)
block|{
specifier|final
name|SqlCall
name|valuesCall
init|=
operator|(
name|SqlCall
operator|)
name|item
decl_stmt|;
specifier|final
name|SqlCall
name|rowCall
init|=
name|valuesCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlIdentifier
name|id
init|=
name|rowCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|id
operator|.
name|isComponentQuoted
argument_list|(
name|i
argument_list|)
operator|==
name|quoted
return|;
block|}
block|}
return|;
block|}
specifier|protected
name|SortedSet
argument_list|<
name|String
argument_list|>
name|getReservedKeywords
parameter_list|()
block|{
return|return
name|keywords
argument_list|(
literal|"c"
argument_list|)
return|;
block|}
comment|/** Returns whether a word is reserved in this parser. This method can be    * used to disable tests that behave differently with different collections    * of reserved words. */
specifier|protected
name|boolean
name|isReserved
parameter_list|(
name|String
name|word
parameter_list|)
block|{
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|metadata
init|=
name|getSqlParser
argument_list|(
literal|""
argument_list|)
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
return|return
name|metadata
operator|.
name|isReservedWord
argument_list|(
name|word
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|SortedSet
argument_list|<
name|String
argument_list|>
name|keywords
parameter_list|(
name|String
name|dialect
parameter_list|)
block|{
specifier|final
name|ImmutableSortedSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|ImmutableSortedSet
operator|.
name|naturalOrder
argument_list|()
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|w
range|:
name|RESERVED_KEYWORDS
control|)
block|{
switch|switch
condition|(
name|w
condition|)
block|{
case|case
literal|"92"
case|:
case|case
literal|"99"
case|:
case|case
literal|"2003"
case|:
case|case
literal|"2011"
case|:
case|case
literal|"2014"
case|:
case|case
literal|"c"
case|:
assert|assert
name|r
operator|!=
literal|null
assert|;
if|if
condition|(
name|dialect
operator|==
literal|null
operator|||
name|dialect
operator|.
name|equals
argument_list|(
name|w
argument_list|)
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
assert|assert
name|r
operator|==
literal|null
operator|||
name|r
operator|.
name|compareTo
argument_list|(
name|w
argument_list|)
operator|<
literal|0
operator|:
literal|"table should be sorted: "
operator|+
name|w
assert|;
name|r
operator|=
name|w
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Tests that when there is an error, non-reserved keywords such as "A",    * "ABSOLUTE" (which naturally arise whenever a production uses    * "&lt;IDENTIFIER&gt;") are removed, but reserved words such as "AND"    * remain.    */
annotation|@
name|Test
name|void
name|testExceptionCleanup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 0.5e1^.1^ from sales.emps"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \".1\" at line 1, column 13.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"<EOF> \n"
operator|+
literal|"    \"AS\" \\.\\.\\.\n"
operator|+
literal|"    \"EXCEPT\" \\.\\.\\.\n"
operator|+
literal|".*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInvalidToken
parameter_list|()
block|{
comment|// Causes problems to the test infrastructure because the token mgr
comment|// throws a java.lang.Error. The usual case is that the parser throws
comment|// an exception.
name|sql
argument_list|(
literal|"values (a^#^b)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Lexical error at line 1, column 10\\.  Encountered: \"#\" \\(35\\), after : \"\""
argument_list|)
expr_stmt|;
block|}
comment|// TODO: should fail in parser
annotation|@
name|Test
name|void
name|testStarAsFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * as x from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT * AS `X`\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFromStarFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from sales^.^*"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\. \\*\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno AS x from sales^.^*"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\. \\*\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp^.^*"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\. \\*\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno AS x from emp^.^*"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\. \\*\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno AS x from ^*^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\*\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHyphenatedTableName
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from bigquery^-^foo-bar.baz"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `bigquery-foo-bar`.baz"
argument_list|)
expr_stmt|;
comment|// Like BigQuery, MySQL allows back-ticks.
name|sql
argument_list|(
literal|"select `baz`.`buzz` from foo.`baz`"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT baz.buzz\n"
operator|+
literal|"FROM foo.baz"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `baz`.`buzz`\n"
operator|+
literal|"FROM `foo`.`baz`"
argument_list|)
expr_stmt|;
comment|// Unlike BigQuery, MySQL does not allow hyphenated identifiers.
name|sql
argument_list|(
literal|"select `baz`.`buzz` from foo^-^bar.`baz`"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT baz.buzz\n"
operator|+
literal|"FROM `foo-bar`.baz"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
comment|// No hyphenated identifiers as table aliases.
name|sql
argument_list|(
literal|"select * from foo.baz as hyphenated^-^alias-not-allowed"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from foo.baz as `hyphenated-alias-allowed-if-quoted`"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM foo.baz AS `hyphenated-alias-allowed-if-quoted`"
argument_list|)
expr_stmt|;
comment|// No hyphenated identifiers as column names.
name|sql
argument_list|(
literal|"select * from foo-bar.baz cross join (select alpha-omega from t) as t"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `foo-bar`.baz\n"
operator|+
literal|"CROSS JOIN (SELECT (alpha - omega)\n"
operator|+
literal|"FROM t) AS t"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from bigquery-foo-bar.baz as hyphenated^-^alias-not-allowed"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into bigquery^-^public-data.foo values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Non-query expression encountered in illegal context"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INSERT INTO `bigquery-public-data`.foo\n"
operator|+
literal|"VALUES (1)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"update bigquery^-^public-data.foo set a = b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"UPDATE `bigquery-public-data`.foo SET a = b"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"delete from bigquery^-^public-data.foo where a = 5"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DELETE FROM `bigquery-public-data`.foo\n"
operator|+
literal|"WHERE (a = 5)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mergeSql
init|=
literal|"merge into bigquery^-^public-data.emps e\n"
operator|+
literal|"using (\n"
operator|+
literal|"  select *\n"
operator|+
literal|"  from bigquery-public-data.tempemps\n"
operator|+
literal|"  where deptno is null) t\n"
operator|+
literal|"on e.empno = t.empno\n"
operator|+
literal|"when matched then\n"
operator|+
literal|"  update set name = t.name, deptno = t.deptno,\n"
operator|+
literal|"    salary = t.salary * .1\n"
operator|+
literal|"when not matched then\n"
operator|+
literal|"    insert (name, dept, salary)\n"
operator|+
literal|"    values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|mergeExpected
init|=
literal|"MERGE INTO `bigquery-public-data`.emps AS e\n"
operator|+
literal|"USING (SELECT *\n"
operator|+
literal|"FROM `bigquery-public-data`.tempemps\n"
operator|+
literal|"WHERE (deptno IS NULL)) AS t\n"
operator|+
literal|"ON (e.empno = t.empno)\n"
operator|+
literal|"WHEN MATCHED THEN"
operator|+
literal|" UPDATE SET name = t.name, deptno = t.deptno,"
operator|+
literal|" salary = (t.salary * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN"
operator|+
literal|" INSERT (name, dept, salary)"
operator|+
literal|" (VALUES (t.name, 10, (t.salary * 0.15)))"
decl_stmt|;
name|sql
argument_list|(
name|mergeSql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
name|mergeExpected
argument_list|)
expr_stmt|;
comment|// Hyphenated identifiers may not contain spaces, even in BigQuery.
name|sql
argument_list|(
literal|"select * from bigquery ^-^ foo - bar as t where x< y"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHyphenatedColumnName
parameter_list|()
block|{
comment|// While BigQuery allows hyphenated table names, no dialect allows
comment|// hyphenated column names; they are parsed as arithmetic minus.
specifier|final
name|String
name|expected
init|=
literal|"SELECT (`FOO` - `BAR`)\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
specifier|final
name|String
name|expectedBigQuery
init|=
literal|"SELECT (foo - bar)\n"
operator|+
literal|"FROM emp"
decl_stmt|;
name|sql
argument_list|(
literal|"select foo-bar from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedBigQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDerivedColumnList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp as e (empno, gender) where true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` AS `E` (`EMPNO`, `GENDER`)\n"
operator|+
literal|"WHERE TRUE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDerivedColumnListInJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e (empno, gender)\n"
operator|+
literal|" join dept as d (deptno, dname) on emp.deptno = dept.deptno"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` AS `E` (`EMPNO`, `GENDER`)\n"
operator|+
literal|"INNER JOIN `DEPT` AS `D` (`DEPTNO`, `DNAME`) ON (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case that does not reproduce but is related to    *<a href="https://issues.apache.org/jira/browse/CALCITE-2637">[CALCITE-2637]    * Prefix '-' operator failed between BETWEEN and AND</a>. */
annotation|@
name|Test
name|void
name|testBetweenAnd
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where deptno between - DEPTNO + 1 and 5"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`DEPTNO` BETWEEN ASYMMETRIC ((- `DEPTNO`) + 1) AND 5)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBetweenAnd2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where deptno between - DEPTNO + 1 and - empno - 3"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`DEPTNO` BETWEEN ASYMMETRIC ((- `DEPTNO`) + 1)"
operator|+
literal|" AND ((- `EMPNO`) - 3))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testDerivedColumnListNoAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp e (empno, gender) where true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
comment|// jdbc syntax
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testEmbeddedCall
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{call foo(?, ?)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testEmbeddedFunction
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{? = call bar (?, ?)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testColumnAliasWithAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 as foo from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1 AS `FOO`\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testColumnAliasWithoutAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 foo from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1 AS `FOO`\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEmbeddedDate
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{d '1998-10-22'}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DATE '1998-10-22'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEmbeddedTime
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{t '16:22:34'}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '16:22:34'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEmbeddedTimestamp
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{ts '1998-10-22 16:22:34'}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '1998-10-22 16:22:34'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNot
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select not true, not false, not null, not unknown from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (NOT TRUE), (NOT FALSE), (NOT NULL), (NOT UNKNOWN)\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBooleanPrecedenceAndAssociativity
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from t where true and false"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (TRUE AND FALSE)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where null or unknown and unknown"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (NULL OR (UNKNOWN AND UNKNOWN))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where true and (true or true) or false"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((TRUE AND (TRUE OR TRUE)) OR FALSE)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where 1 and true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (1 AND TRUE)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanAssociativity
parameter_list|()
block|{
name|expr
argument_list|(
literal|"NOT a = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT (`A` = `B`))"
argument_list|)
expr_stmt|;
comment|// comparison operators are left-associative
name|expr
argument_list|(
literal|"x< y< z"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`X`< `Y`)< `Z`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x< y<= z = a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(((`X`< `Y`)<= `Z`) = `A`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x< y<= z = a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((((`A` = `X`)< `Y`)<= `Z`) = `A`)"
argument_list|)
expr_stmt|;
comment|// IS NULL has lower precedence than comparison
name|expr
argument_list|(
literal|"a = x is null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = `X`) IS NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x is not null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = `X`) IS NOT NULL)"
argument_list|)
expr_stmt|;
comment|// BETWEEN, IN, LIKE have higher precedence than comparison
name|expr
argument_list|(
literal|"a = x between y = b and z = c"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` BETWEEN ASYMMETRIC (`Y` = `B`) AND `Z`)) = `C`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x like y = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` LIKE `Y`)) = `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x not like y = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` NOT LIKE `Y`)) = `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x similar to y = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` SIMILAR TO `Y`)) = `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x not similar to y = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` NOT SIMILAR TO `Y`)) = `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a = x not in (y, z) = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` = (`X` NOT IN (`Y`, `Z`))) = `B`)"
argument_list|)
expr_stmt|;
comment|// LIKE has higher precedence than IS NULL
name|expr
argument_list|(
literal|"a like b is null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` LIKE `B`) IS NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a not like b is not null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A` NOT LIKE `B`) IS NOT NULL)"
argument_list|)
expr_stmt|;
comment|// = has higher precedence than NOT
name|expr
argument_list|(
literal|"NOT a = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT (`A` = `B`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT a = NOT b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT (`A` = (NOT `B`)))"
argument_list|)
expr_stmt|;
comment|// IS NULL has higher precedence than NOT
name|expr
argument_list|(
literal|"NOT a IS NULL"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT (`A` IS NULL))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT a = b IS NOT NULL"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT ((`A` = `B`) IS NOT NULL))"
argument_list|)
expr_stmt|;
comment|// NOT has higher precedence than AND, which  has higher precedence than OR
name|expr
argument_list|(
literal|"NOT a AND NOT b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((NOT `A`) AND (NOT `B`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT a OR NOT b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((NOT `A`) OR (NOT `B`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT a = b AND NOT c = d OR NOT e = f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(((NOT (`A` = `B`)) AND (NOT (`C` = `D`))) OR (NOT (`E` = `F`)))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT a = b OR NOT c = d AND NOT e = f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((NOT (`A` = `B`)) OR ((NOT (`C` = `D`)) AND (NOT (`E` = `F`))))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NOT NOT a = b OR NOT NOT c = d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((NOT (NOT (`A` = `B`))) OR (NOT (NOT (`C` = `D`))))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsBooleans
parameter_list|()
block|{
name|String
index|[]
name|inOuts
init|=
block|{
literal|"NULL"
block|,
literal|"TRUE"
block|,
literal|"FALSE"
block|,
literal|"UNKNOWN"
block|}
decl_stmt|;
for|for
control|(
name|String
name|inOut
range|:
name|inOuts
control|)
block|{
name|sql
argument_list|(
literal|"select * from t where nOt fAlSe Is "
operator|+
name|inOut
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (NOT (FALSE IS "
operator|+
name|inOut
operator|+
literal|"))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where c1=1.1 IS NOT "
operator|+
name|inOut
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`C1` = 1.1) IS NOT "
operator|+
name|inOut
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testIsBooleanPrecedenceAndAssociativity
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from t where x is unknown is not unknown"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`X` IS UNKNOWN) IS NOT UNKNOWN)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t where not true is unknown"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (NOT (TRUE IS UNKNOWN))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x is unknown is not unknown is false is not false"
operator|+
literal|" is true is not true is null is not null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((((((((`X` IS UNKNOWN) IS NOT UNKNOWN) IS FALSE) IS NOT FALSE) IS TRUE) IS NOT TRUE) IS NULL) IS NOT NULL)"
argument_list|)
expr_stmt|;
comment|// combine IS postfix operators with infix (AND) and prefix (NOT) ops
specifier|final
name|String
name|sql
init|=
literal|"select * from t "
operator|+
literal|"where x is unknown is false "
operator|+
literal|"and x is unknown is true "
operator|+
literal|"or not y is unknown is not null"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((((`X` IS UNKNOWN) IS FALSE)"
operator|+
literal|" AND ((`X` IS UNKNOWN) IS TRUE))"
operator|+
literal|" OR (NOT ((`Y` IS UNKNOWN) IS NOT NULL)))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualNotEqual
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'abc'=123"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('abc' = 123)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'<>123"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('abc'<> 123)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'<>123='def'<>456"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((('abc'<> 123) = 'def')<> 456)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'<>123=('def'<>456)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(('abc'<> 123) = ('def'<> 456))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBangEqualIsBad
parameter_list|()
block|{
comment|// Quoth www.ocelot.ca:
comment|//   "Other relators besides '=' are what you'd expect if
comment|//   you've used any programming language:> and>= and< and<=. The
comment|//   only potential point of confusion is that the operator for 'not
comment|//   equals' is<> as in BASIC. There are many texts which will tell
comment|//   you that != is SQL's not-equals operator; those texts are false;
comment|//   it's one of those unstampoutable urban myths."
comment|// Therefore, we only support != with certain SQL conformance levels.
name|expr
argument_list|(
literal|"'abc'^!=^123"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Bang equal '!=' is not allowed under the current SQL conformance level"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from t where price between 1 and 2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`PRICE` BETWEEN ASYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where price between symmetric 1 and 2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`PRICE` BETWEEN SYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where price not between symmetric 1 and 2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`PRICE` NOT BETWEEN SYMMETRIC 1 AND 2)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where price between ASYMMETRIC 1 and 2+2*2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`PRICE` BETWEEN ASYMMETRIC 1 AND (2 + (2 * 2)))"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select * from t\n"
operator|+
literal|" where price> 5\n"
operator|+
literal|" and price not between 1 + 2 and 3 * 4 AnD price is null"
decl_stmt|;
specifier|final
name|String
name|expected0
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (((`PRICE`> 5) "
operator|+
literal|"AND (`PRICE` NOT BETWEEN ASYMMETRIC (1 + 2) AND (3 * 4))) "
operator|+
literal|"AND (`PRICE` IS NULL))"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select * from t\n"
operator|+
literal|"where price> 5\n"
operator|+
literal|"and price between 1 + 2 and 3 * 4 + price is null"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`PRICE`> 5) "
operator|+
literal|"AND ((`PRICE` BETWEEN ASYMMETRIC (1 + 2) AND ((3 * 4) + `PRICE`)) "
operator|+
literal|"IS NULL))"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select * from t\n"
operator|+
literal|"where price> 5\n"
operator|+
literal|"and price between 1 + 2 and 3 * 4 or price is null"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (((`PRICE`> 5) "
operator|+
literal|"AND (`PRICE` BETWEEN ASYMMETRIC (1 + 2) AND (3 * 4))) "
operator|+
literal|"OR (`PRICE` IS NULL))"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"values a between c and d and e and f between g and h"
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"VALUES ("
operator|+
literal|"ROW((((`A` BETWEEN ASYMMETRIC `C` AND `D`) AND `E`)"
operator|+
literal|" AND (`F` BETWEEN ASYMMETRIC `G` AND `H`))))"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a between b or c^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*BETWEEN operator has no terminating AND"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a ^between^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"between<EOF>\" at line 1, column 10.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a between symmetric 1^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*BETWEEN operator has no terminating AND"
argument_list|)
expr_stmt|;
comment|// precedence of BETWEEN is higher than AND and OR, but lower than '+'
name|sql
argument_list|(
literal|"values a between b and c + 2 or d and e"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(((`A` BETWEEN ASYMMETRIC `B` AND (`C` + 2)) OR (`D` AND `E`))))"
argument_list|)
expr_stmt|;
comment|// '=' has slightly lower precedence than BETWEEN; both are left-assoc
name|sql
argument_list|(
literal|"values x = a between b and c = d = e"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((((`X` = (`A` BETWEEN ASYMMETRIC `B` AND `C`)) = `D`) = `E`)))"
argument_list|)
expr_stmt|;
comment|// AND doesn't match BETWEEN if it's between parentheses!
name|sql
argument_list|(
literal|"values a between b or (c and d) or e and f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` BETWEEN ASYMMETRIC ((`B` OR (`C` AND `D`)) OR `E`) AND `F`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOperateOnColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select c1*1,c2  + 2,c3/3,c4-4,c5*c4  from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (`C1` * 1), (`C2` + 2), (`C3` / 3), (`C4` - 4), (`C5` * `C4`)\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRow
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select t.r.\"EXPR$1\", t.r.\"EXPR$0\" from (select (1,2) r from sales.depts) t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `T`.`R`.`EXPR$1`, `T`.`R`.`EXPR$0`\n"
operator|+
literal|"FROM (SELECT (ROW(1, 2)) AS `R`\n"
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.r.\"EXPR$1\".\"EXPR$2\" "
operator|+
literal|"from (select ((1,2),(3,4,5)) r from sales.depts) t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `T`.`R`.`EXPR$1`.`EXPR$2`\n"
operator|+
literal|"FROM (SELECT (ROW((ROW(1, 2)), (ROW(3, 4, 5)))) AS `R`\n"
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.r.\"EXPR$1\".\"EXPR$2\" "
operator|+
literal|"from (select ((1,2),(3,4,5,6)) r from sales.depts) t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `T`.`R`.`EXPR$1`.`EXPR$2`\n"
operator|+
literal|"FROM (SELECT (ROW((ROW(1, 2)), (ROW(3, 4, 5, 6)))) AS `R`\n"
operator|+
literal|"FROM `SALES`.`DEPTS`) AS `T`"
argument_list|)
expr_stmt|;
comment|// Conformance DEFAULT and LENIENT support explicit row value constructor
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
specifier|final
name|String
name|selectRow
init|=
literal|"select ^row(t1a, t2a)^ from t1"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT (ROW(`T1A`, `T2A`))\n"
operator|+
literal|"FROM `T1`"
decl_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|LENIENT
expr_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|pattern
init|=
literal|"ROW expression encountered in illegal context"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|ORACLE_12
expr_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
expr_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|selectRow
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
specifier|final
name|String
name|whereRow
init|=
literal|"select 1 from t2 where ^row (x, y)^< row (a, b)"
decl_stmt|;
specifier|final
name|String
name|whereExpected
init|=
literal|"SELECT 1\n"
operator|+
literal|"FROM `T2`\n"
operator|+
literal|"WHERE ((ROW(`X`, `Y`))< (ROW(`A`, `B`)))"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|whereRow
argument_list|)
operator|.
name|ok
argument_list|(
name|whereExpected
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|whereRow
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
specifier|final
name|String
name|whereRow2
init|=
literal|"select 1 from t2 where ^(x, y)^< (a, b)"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|whereRow2
argument_list|)
operator|.
name|ok
argument_list|(
name|whereExpected
argument_list|)
expr_stmt|;
comment|// After this point, SqlUnparserTest has problems.
comment|// We generate ROW in a dialect that does not allow ROW in all contexts.
comment|// So bail out.
name|assumeFalse
argument_list|(
name|isUnparserTest
argument_list|()
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|whereRow2
argument_list|)
operator|.
name|ok
argument_list|(
name|whereExpected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowValueExpression
parameter_list|()
block|{
specifier|final
name|String
name|expected0
init|=
literal|"INSERT INTO \"EMPS\"\n"
operator|+
literal|"VALUES (ROW(1, 'Fred')),\n"
operator|+
literal|"(ROW(2, 'Eric'))"
decl_stmt|;
name|String
name|sql
init|=
literal|"insert into emps values (1,'Fred'),(2, 'Eric')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|CALCITE
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"INSERT INTO `emps`\n"
operator|+
literal|"VALUES (1, 'Fred'),\n"
operator|+
literal|"(2, 'Eric')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"INSERT INTO \"EMPS\"\n"
operator|+
literal|"VALUES (1, 'Fred'),\n"
operator|+
literal|"(2, 'Eric')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|ORACLE
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"INSERT INTO [EMPS]\n"
operator|+
literal|"VALUES (1, 'Fred'),\n"
operator|+
literal|"(2, 'Eric')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MSSQL
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
block|}
comment|/** Whether this is a sub-class that tests un-parsing as well as parsing. */
specifier|protected
name|boolean
name|isUnparserTest
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Test
name|void
name|testRowWithDot
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select (1,2).a from c.t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT ((ROW(1, 2)).`A`)\nFROM `C`.`T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select row(1,2).a from c.t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT ((ROW(1, 2)).`A`)\nFROM `C`.`T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select tbl.foo(0).col.bar from tbl"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT ((`TBL`.`FOO`(0).`COL`).`BAR`)\nFROM `TBL`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPeriod
parameter_list|()
block|{
comment|// We don't have a PERIOD constructor currently;
comment|// ROW constructor is sufficient for now.
name|expr
argument_list|(
literal|"period (date '1969-01-05', interval '2-3' year to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(ROW(DATE '1969-01-05', INTERVAL '2-3' YEAR TO MONTH))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverlaps
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|ops
init|=
block|{
literal|"overlaps"
block|,
literal|"equals"
block|,
literal|"precedes"
block|,
literal|"succeeds"
block|,
literal|"immediately precedes"
block|,
literal|"immediately succeeds"
block|}
decl_stmt|;
specifier|final
name|String
index|[]
name|periods
init|=
block|{
literal|"period "
block|,
literal|""
block|}
decl_stmt|;
for|for
control|(
name|String
name|period
range|:
name|periods
control|)
block|{
for|for
control|(
name|String
name|op
range|:
name|ops
control|)
block|{
name|checkPeriodPredicate
argument_list|(
operator|new
name|Checker
argument_list|(
name|op
argument_list|,
name|period
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|checkPeriodPredicate
parameter_list|(
name|Checker
name|checker
parameter_list|)
block|{
name|checker
operator|.
name|checkExp
argument_list|(
literal|"$p(x,xx) $op $p(y,yy)"
argument_list|,
literal|"(PERIOD (`X`, `XX`) $op PERIOD (`Y`, `YY`))"
argument_list|)
expr_stmt|;
name|checker
operator|.
name|checkExp
argument_list|(
literal|"$p(x,xx) $op $p(y,yy) or false"
argument_list|,
literal|"((PERIOD (`X`, `XX`) $op PERIOD (`Y`, `YY`)) OR FALSE)"
argument_list|)
expr_stmt|;
name|checker
operator|.
name|checkExp
argument_list|(
literal|"true and not $p(x,xx) $op $p(y,yy) or false"
argument_list|,
literal|"((TRUE AND (NOT (PERIOD (`X`, `XX`) $op PERIOD (`Y`, `YY`)))) OR FALSE)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checker
operator|.
name|period
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|checker
operator|.
name|checkExp
argument_list|(
literal|"$p(x,xx,xxx) $op $p(y,yy) or false"
argument_list|,
literal|"((PERIOD (`X`, `XX`) $op PERIOD (`Y`, `YY`)) OR FALSE)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// 3-argument rows are valid in the parser, rejected by the validator
name|checker
operator|.
name|checkExpFails
argument_list|(
literal|"$p(x,xx^,^xxx) $op $p(y,yy) or false"
argument_list|,
literal|"(?s).*Encountered \",\" at .*"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Parses a list of statements (that contains only one statement). */
annotation|@
name|Test
name|void
name|testStmtListWithSelect
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithSelectAndSemicolon
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp, dept;"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithTwoSelect
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp, dept ; select * from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithTwoSelectSemicolon
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp, dept ; select * from emp, dept;"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithSelectDelete
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"DELETE FROM `EMP`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp, dept; delete from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithSelectDeleteUpdate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp, dept; "
operator|+
literal|"delete from emp; "
operator|+
literal|"update emps set empno = empno + 1"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"DELETE FROM `EMP`"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"UPDATE `EMPS` SET `EMPNO` = (`EMPNO` + 1)"
decl_stmt|;
name|sqlList
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected1
argument_list|,
name|expected2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithSemiColonInComment
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"select * from emp, dept; // comment with semicolon ; values 1\n"
operator|+
literal|"values 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"VALUES (ROW(2))"
decl_stmt|;
name|sqlList
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithSemiColonInWhere
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`NAME` LIKE 'toto;')"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"DELETE FROM `EMP`"
decl_stmt|;
name|sqlList
argument_list|(
literal|"select * from emp where name like 'toto;'; delete from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStmtListWithInsertSelectInsert
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into dept (name, deptno) values ('a', 123); "
operator|+
literal|"select * from emp where name like 'toto;'; "
operator|+
literal|"insert into dept (name, deptno) values ('b', 123);"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `DEPT` (`NAME`, `DEPTNO`)\n"
operator|+
literal|"VALUES (ROW('a', 123))"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`NAME` LIKE 'toto;')"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"INSERT INTO `DEPT` (`NAME`, `DEPTNO`)\n"
operator|+
literal|"VALUES (ROW('b', 123))"
decl_stmt|;
name|sqlList
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|,
name|expected1
argument_list|,
name|expected2
argument_list|)
expr_stmt|;
block|}
comment|/** Should fail since the first statement lacks semicolon. */
annotation|@
name|Test
name|void
name|testStmtListWithoutSemiColon1
parameter_list|()
block|{
name|sqlList
argument_list|(
literal|"select * from emp where name like 'toto' "
operator|+
literal|"^delete^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"delete\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** Should fail since the third statement lacks semicolon. */
annotation|@
name|Test
name|void
name|testStmtListWithoutSemiColon2
parameter_list|()
block|{
name|sqlList
argument_list|(
literal|"select * from emp where name like 'toto'; "
operator|+
literal|"delete from emp; "
operator|+
literal|"insert into dept (name, deptno) values ('a', 123) "
operator|+
literal|"^select^ * from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"select\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsDistinctFrom
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select x is distinct from y from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (`X` IS DISTINCT FROM `Y`)\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x is distinct from y"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`X` IS DISTINCT FROM `Y`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x is distinct from (4,5,6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`X` IS DISTINCT FROM (ROW(4, 5, 6)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x is distinct from row (4,5,6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`X` IS DISTINCT FROM (ROW(4, 5, 6)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where true is distinct from true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (TRUE IS DISTINCT FROM TRUE)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where true is distinct from true is true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((TRUE IS DISTINCT FROM TRUE) IS TRUE)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select x is not distinct from y from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (`X` IS NOT DISTINCT FROM `Y`)\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where true is not distinct from true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (TRUE IS NOT DISTINCT FROM TRUE)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFloor
parameter_list|()
block|{
name|expr
argument_list|(
literal|"floor(1.5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(1.5)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to epoch)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO EPOCH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to day)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to dow)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO DOW)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to doy)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO DOY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to week)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO WEEK)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to quarter)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO QUARTER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to year)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO YEAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to decade)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO DECADE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to century)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO CENTURY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x to millennium)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR(`X` TO MILLENNIUM)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to epoch)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO EPOCH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' hour to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' HOUR TO MINUTE))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' hour to minute to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' HOUR TO MINUTE) TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to day)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to dow)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DOW)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to doy)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DOY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to week)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO WEEK)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to quarter)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO QUARTER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to year)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO YEAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to decade)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DECADE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to century)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO CENTURY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(x + interval '1:20' minute to second to millennium)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"FLOOR((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO MILLENNIUM)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCeil
parameter_list|()
block|{
name|expr
argument_list|(
literal|"ceil(3453.2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(3453.2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to epoch)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO EPOCH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to day)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to dow)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO DOW)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to doy)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO DOY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to week)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO WEEK)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to quarter)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO QUARTER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to year)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO YEAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to decade)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO DECADE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to century)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO CENTURY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x to millennium)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL(`X` TO MILLENNIUM)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to epoch)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO EPOCH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' hour to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' HOUR TO MINUTE))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' hour to minute to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' HOUR TO MINUTE) TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to day)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to dow)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DOW)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to doy)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DOY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to week)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO WEEK)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to quarter)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO QUARTER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to year)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO YEAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to decade)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO DECADE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to century)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO CENTURY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(x + interval '1:20' minute to second to millennium)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CEIL((`X` + INTERVAL '1:20' MINUTE TO SECOND) TO MILLENNIUM)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCast
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(x as boolean)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS BOOLEAN)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as integer)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTEGER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varchar(1))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARCHAR(1))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as date)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DATE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time without time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time with local time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME WITH LOCAL TIME ZONE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp without time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp with local time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP WITH LOCAL TIME ZONE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time(0))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME(0))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time(0) without time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME(0))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time(0) with local time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIME(0) WITH LOCAL TIME ZONE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp(0))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP(0))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp(0) without time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP(0))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp(0) with local time zone)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP(0) WITH LOCAL TIME ZONE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TIMESTAMP)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as decimal(1,1))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DECIMAL(1, 1))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as char(1))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS CHAR(1))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as binary(1))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS BINARY(1))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varbinary(1))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARBINARY(1))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as tinyint)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS TINYINT)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as smallint)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS SMALLINT)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as bigint)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS BIGINT)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as real)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS REAL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as double)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DOUBLE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as decimal)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DECIMAL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as decimal(0))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DECIMAL(0))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as decimal(1,2))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS DECIMAL(1, 2))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('foo' as bar)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST('foo' AS `BAR`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastFails
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(x as time with ^time^ zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"time\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as time(0) with ^time^ zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"time\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp with ^time^ zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"time\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as timestamp(0) with ^time^ zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"time\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varchar(10) ^with^ local time zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"with\" at line 1, column 23.\n.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varchar(10) ^without^ time zone)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"without\" at line 1, column 23.\n.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeAndSimilar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from t where x like '%abc%'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`X` LIKE '%abc%')"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x+1 not siMilaR to '%abc%' ESCAPE 'e'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`X` + 1) NOT SIMILAR TO '%abc%' ESCAPE 'e')"
argument_list|)
expr_stmt|;
comment|// LIKE has higher precedence than AND
name|sql
argument_list|(
literal|"select * from t where price> 5 and x+2*2 like y*3+2 escape (select*from t)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`PRICE`> 5) AND ((`X` + (2 * 2)) LIKE ((`Y` * 3) + 2) ESCAPE (SELECT *\n"
operator|+
literal|"FROM `T`)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a and b like c"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` AND (`B` LIKE `C`))))"
argument_list|)
expr_stmt|;
comment|// LIKE has higher precedence than AND
name|sql
argument_list|(
literal|"values a and b like c escape d and e"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(((`A` AND (`B` LIKE `C` ESCAPE `D`)) AND `E`)))"
argument_list|)
expr_stmt|;
comment|// LIKE has same precedence as '='; LIKE is right-assoc, '=' is left
name|sql
argument_list|(
literal|"values a = b like c = d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(((`A` = (`B` LIKE `C`)) = `D`)))"
argument_list|)
expr_stmt|;
comment|// Nested LIKE
name|sql
argument_list|(
literal|"values a like b like c escape d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` LIKE (`B` LIKE `C` ESCAPE `D`))))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a like b like c escape d and false"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(((`A` LIKE (`B` LIKE `C` ESCAPE `D`)) AND FALSE)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values a like b like c like d escape e escape f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` LIKE (`B` LIKE (`C` LIKE `D` ESCAPE `E`) ESCAPE `F`))))"
argument_list|)
expr_stmt|;
comment|// Mixed LIKE and SIMILAR TO
name|sql
argument_list|(
literal|"values a similar to b like c similar to d escape e escape f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` SIMILAR TO (`B` LIKE (`C` SIMILAR TO `D` ESCAPE `E`) ESCAPE `F`))))"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isReserved
argument_list|(
literal|"ESCAPE"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
literal|"select * from t where ^escape^ 'e'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"escape\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|// LIKE with +
name|sql
argument_list|(
literal|"values a like b + c escape d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` LIKE (`B` + `C`) ESCAPE `D`)))"
argument_list|)
expr_stmt|;
comment|// LIKE with ||
name|sql
argument_list|(
literal|"values a like b || c escape d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` LIKE (`B` || `C`) ESCAPE `D`)))"
argument_list|)
expr_stmt|;
comment|// ESCAPE with no expression
if|if
condition|(
name|isReserved
argument_list|(
literal|"ESCAPE"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
literal|"values a ^like^ escape d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"like escape\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|// ESCAPE with no expression
if|if
condition|(
name|isReserved
argument_list|(
literal|"ESCAPE"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
literal|"values a like b || c ^escape^ and false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"escape and\" at line 1, column 22.*"
argument_list|)
expr_stmt|;
block|}
comment|// basic SIMILAR TO
name|sql
argument_list|(
literal|"select * from t where x similar to '%abc%'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`X` SIMILAR TO '%abc%')"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t where x+1 not siMilaR to '%abc%' ESCAPE 'e'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`X` + 1) NOT SIMILAR TO '%abc%' ESCAPE 'e')"
argument_list|)
expr_stmt|;
comment|// SIMILAR TO has higher precedence than AND
name|sql
argument_list|(
literal|"select * from t where price> 5 and x+2*2 SIMILAR TO y*3+2 escape (select*from t)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((`PRICE`> 5) AND ((`X` + (2 * 2)) SIMILAR TO ((`Y` * 3) + 2) ESCAPE (SELECT *\n"
operator|+
literal|"FROM `T`)))"
argument_list|)
expr_stmt|;
comment|// Mixed LIKE and SIMILAR TO
name|sql
argument_list|(
literal|"values a similar to b like c similar to d escape e escape f"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` SIMILAR TO (`B` LIKE (`C` SIMILAR TO `D` ESCAPE `E`) ESCAPE `F`))))"
argument_list|)
expr_stmt|;
comment|// SIMILAR TO with sub-query
name|sql
argument_list|(
literal|"values a similar to (select * from t where a like b escape c) escape d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((`A` SIMILAR TO (SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`A` LIKE `B` ESCAPE `C`)) ESCAPE `D`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFoo
parameter_list|()
block|{
block|}
annotation|@
name|Test
name|void
name|testArithmeticOperators
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1-2+3*4/5/6-7"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(((1 - 2) + (((3 * 4) / 5) / 6)) - 7)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"power(2,3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"POWER(2, 3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"aBs(-2.3e-2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ABS(-2.3E-2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"MOD(5             ,\t\f\r\n2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"MOD(5, 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ln(5.43  )"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LN(5.43)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"log10(- -.2  )"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOG10(0.2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept where exists (select 1 from emp where emp.deptno = dept.deptno)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"WHERE (EXISTS (SELECT 1\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExistsInWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where 1 = 2 and exists (select 1 from dept) and 3 = 4"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (((1 = 2) AND (EXISTS (SELECT 1\n"
operator|+
literal|"FROM `DEPT`))) AND (3 = 4))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFromWithAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from emp as e where 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `EMP` AS `E`\n"
operator|+
literal|"WHERE 1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConcat
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a' || 'b'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('a' || 'b')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testReverseSolidus
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'\\'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'\\'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubstring
parameter_list|()
block|{
name|expr
argument_list|(
literal|"substring('a'\nFROM \t  1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' FROM 1 FOR 3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 1 FOR 3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' FROM 'reg' FOR '\\')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 'reg' FOR '\\')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', 'reg', '\\')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 'reg' FOR '\\')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', 1, 2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 1 FOR 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' , 1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SUBSTRING('a' FROM 1)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select substring('Eggs and ham', 1, 3 + 2) || ' benedict' from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SUBSTRING('Eggs and ham' FROM 1 FOR (3 + 2)) || ' benedict')\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"log10(1)\r\n"
operator|+
literal|"+power(2, mod(\r\n"
operator|+
literal|"3\n"
operator|+
literal|"\t\t\f\n"
operator|+
literal|",ln(4))*log10(5)-6*log10(7/abs(8)+9))*power(10,11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(LOG10(1) + (POWER(2, ((MOD(3, LN(4)) * LOG10(5))"
operator|+
literal|" - (6 * LOG10(((7 / ABS(8)) + 9))))) * POWER(10, 11)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionWithDistinct
parameter_list|()
block|{
name|expr
argument_list|(
literal|"count(DISTINCT 1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COUNT(DISTINCT 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"count(ALL 1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COUNT(ALL 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"count(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COUNT(1)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(1), count(distinct 2) from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT COUNT(1), COUNT(DISTINCT 2)\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionCallWithDot
parameter_list|()
block|{
name|expr
argument_list|(
literal|"foo(a,b).c"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`FOO`(`A`, `B`).`C`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionInFunction
parameter_list|()
block|{
name|expr
argument_list|(
literal|"ln(power(2,2))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LN(POWER(2, 2))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionNamedArgument
parameter_list|()
block|{
name|expr
argument_list|(
literal|"foo(x => 1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO`(`X` => 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(x => 1, \"y\" => 'a', z => x<= y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO`(`X` => 1, `y` => 'a', `Z` => (`X`<= `Y`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(x.y ^=>^ 1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"=>\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(a => 1, x.y ^=>^ 2, c => 3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"=>\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionDefaultArgument
parameter_list|()
block|{
name|sql
argument_list|(
literal|"foo(1, DEFAULT, default, 'default', \"default\", 3)"
argument_list|)
operator|.
name|expression
argument_list|()
operator|.
name|ok
argument_list|(
literal|"`FOO`(1, DEFAULT, DEFAULT, 'default', `default`, 3)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"foo(DEFAULT)"
argument_list|)
operator|.
name|expression
argument_list|()
operator|.
name|ok
argument_list|(
literal|"`FOO`(DEFAULT)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"foo(x => 1, DEFAULT)"
argument_list|)
operator|.
name|expression
argument_list|()
operator|.
name|ok
argument_list|(
literal|"`FOO`(`X` => 1, DEFAULT)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"foo(y => DEFAULT, x => 1)"
argument_list|)
operator|.
name|expression
argument_list|()
operator|.
name|ok
argument_list|(
literal|"`FOO`(`Y` => DEFAULT, `X` => 1)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"foo(x => 1, y => DEFAULT)"
argument_list|)
operator|.
name|expression
argument_list|()
operator|.
name|ok
argument_list|(
literal|"`FOO`(`X` => 1, `Y` => DEFAULT)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(DISTINCT DEFAULT) from t group by x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT SUM(DISTINCT DEFAULT)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"GROUP BY `X`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(x ^+^ DEFAULT)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\+ DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(0, x ^+^ DEFAULT + y)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\+ DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo(0, DEFAULT ^+^ y)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\+\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDefault
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^DEFAULT^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Incorrect syntax near the keyword 'DEFAULT' at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(empno ^+^ DEFAULT as double) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\+ DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno ^+^ DEFAULT + deptno from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\+ DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select power(0, DEFAULT ^+^ empno) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\+\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp join dept on ^DEFAULT^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Incorrect syntax near the keyword 'DEFAULT' at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where empno ^>^ DEFAULT or deptno< 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"> DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp order by ^DEFAULT^ desc"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Incorrect syntax near the keyword 'DEFAULT' at .*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `DEPT` (`NAME`, `DEPTNO`)\n"
operator|+
literal|"VALUES (ROW('a', DEFAULT))"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into dept (name, deptno) values ('a', DEFAULT)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into dept (name, deptno) values ('a', 1 ^+^ DEFAULT)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\+ DEFAULT\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into dept (name, deptno) select 'a', ^DEFAULT^ from (values 0)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Incorrect syntax near the keyword 'DEFAULT' at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" sum(sal) filter (where gender = 'F') as femaleSal,\n"
operator|+
literal|" sum(sal) filter (where true) allSal,\n"
operator|+
literal|" count(distinct deptno) filter (where (deptno< 40))\n"
operator|+
literal|"from emp"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" SUM(`SAL`) FILTER (WHERE (`GENDER` = 'F')) AS `FEMALESAL`,"
operator|+
literal|" SUM(`SAL`) FILTER (WHERE TRUE) AS `ALLSAL`,"
operator|+
literal|" COUNT(DISTINCT `DEPTNO`) FILTER (WHERE (`DEPTNO`< 40))\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, min(foo) as x from emp group by deptno, gender"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`, MIN(`FOO`) AS `X`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`, `GENDER`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupEmpty
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) from emp group by ()"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY ()"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from emp group by () having 1 = 2 order by 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT COUNT(*)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY ()\n"
operator|+
literal|"HAVING (1 = 2)\n"
operator|+
literal|"ORDER BY 3"
argument_list|)
expr_stmt|;
comment|// Used to be invalid, valid now that we support grouping sets.
name|sql
argument_list|(
literal|"select 1 from emp group by (), x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY (), `X`"
argument_list|)
expr_stmt|;
comment|// Used to be invalid, valid now that we support grouping sets.
name|sql
argument_list|(
literal|"select 1 from emp group by x, ()"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `X`, ()"
argument_list|)
expr_stmt|;
comment|// parentheses do not an empty GROUP BY make
name|sql
argument_list|(
literal|"select 1 from emp group by (empno + deptno)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY (`EMPNO` + `DEPTNO`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHavingAfterGroup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp group by deptno, emp\n"
operator|+
literal|"having count(*)> 5 and 1 = 2 order by 5, 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`, `EMP`\n"
operator|+
literal|"HAVING ((COUNT(*)> 5) AND (1 = 2))\n"
operator|+
literal|"ORDER BY 5, 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHavingBeforeGroupFails
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp\n"
operator|+
literal|"having count(*)> 5 and deptno< 4 ^group^ by deptno, emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"group\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHavingNoGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno from emp having count(*)> 5"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"HAVING (COUNT(*)> 5)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupingSets
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets (deptno, (deptno, gender), ())"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY GROUPING SETS(`DEPTNO`, (`DEPTNO`, `GENDER`), ())"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets ((deptno, gender), (deptno), (), gender)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY GROUPING SETS((`DEPTNO`, `GENDER`), `DEPTNO`, (), `GENDER`)"
argument_list|)
expr_stmt|;
comment|// Grouping sets must have parentheses
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets ^deptno^, (deptno, gender), ()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"deptno\" at line 2, column 24.\n"
operator|+
literal|"Was expecting:\n"
operator|+
literal|"    \"\\(\" .*"
argument_list|)
expr_stmt|;
comment|// Nested grouping sets, cube, rollup, grouping sets all OK
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets (deptno, grouping sets (e, d), (),\n"
operator|+
literal|"  cube (x, y), rollup(p, q))\n"
operator|+
literal|"order by a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY GROUPING SETS(`DEPTNO`, GROUPING SETS(`E`, `D`), (), CUBE(`X`, `Y`), ROLLUP(`P`, `Q`))\n"
operator|+
literal|"ORDER BY `A`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets (())"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY GROUPING SETS(())"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupByCube
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp\n"
operator|+
literal|"group by cube ((a, b), (c, d))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY CUBE((`A`, `B`), (`C`, `D`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupByCube2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp\n"
operator|+
literal|"group by cube ((a, b), (c, d)) order by a"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY CUBE((`A`, `B`), (`C`, `D`))\n"
operator|+
literal|"ORDER BY `A`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select deptno from emp\n"
operator|+
literal|"group by cube (^)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\)\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupByRollup
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno from emp\n"
operator|+
literal|"group by rollup (deptno, deptno + 1, gender)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY ROLLUP(`DEPTNO`, (`DEPTNO` + 1), `GENDER`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// Nested rollup not ok
specifier|final
name|String
name|sql1
init|=
literal|"select deptno from emp\n"
operator|+
literal|"group by rollup (deptno^, rollup(e, d))"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \", rollup\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGrouping
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno, grouping(deptno) from emp\n"
operator|+
literal|"group by grouping sets (deptno, (deptno, gender), ())"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DEPTNO`, GROUPING(`DEPTNO`)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY GROUPING SETS(`DEPTNO`, (`DEPTNO`, `GENDER`), ())"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWith
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with femaleEmps as (select * from emps where gender = 'F')"
operator|+
literal|"select deptno from femaleEmps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"WITH `FEMALEEMPS` AS (SELECT *\n"
operator|+
literal|"FROM `EMPS`\n"
operator|+
literal|"WHERE (`GENDER` = 'F')) (SELECT `DEPTNO`\n"
operator|+
literal|"FROM `FEMALEEMPS`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWith2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with femaleEmps as (select * from emps where gender = 'F'),\n"
operator|+
literal|"marriedFemaleEmps(x, y) as (select * from femaleEmps where maritaStatus = 'M')\n"
operator|+
literal|"select deptno from femaleEmps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"WITH `FEMALEEMPS` AS (SELECT *\n"
operator|+
literal|"FROM `EMPS`\n"
operator|+
literal|"WHERE (`GENDER` = 'F')), `MARRIEDFEMALEEMPS` (`X`, `Y`) AS (SELECT *\n"
operator|+
literal|"FROM `FEMALEEMPS`\n"
operator|+
literal|"WHERE (`MARITASTATUS` = 'M')) (SELECT `DEPTNO`\n"
operator|+
literal|"FROM `FEMALEEMPS`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithFails
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with femaleEmps as ^select^ *\n"
operator|+
literal|"from emps where gender = 'F'\n"
operator|+
literal|"select deptno from femaleEmps"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"select\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with v(i,c) as (values (1, 'a'), (2, 'bb'))\n"
operator|+
literal|"select c, i from v"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"WITH `V` (`I`, `C`) AS (VALUES (ROW(1, 'a')),\n"
operator|+
literal|"(ROW(2, 'bb'))) (SELECT `C`, `I`\n"
operator|+
literal|"FROM `V`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithNestedFails
parameter_list|()
block|{
comment|// SQL standard does not allow WITH to contain WITH
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"^with^ dept2 as (select * from dept)\n"
operator|+
literal|"select 1 as uno from emp, dept"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"with\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithNestedInSubQuery
parameter_list|()
block|{
comment|// SQL standard does not allow sub-query to contain WITH but we do
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"(\n"
operator|+
literal|"  with dept2 as (select * from dept)\n"
operator|+
literal|"  select 1 as uno from empDept)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"WITH `EMP2` AS (SELECT *\n"
operator|+
literal|"FROM `EMP`) (WITH `DEPT2` AS (SELECT *\n"
operator|+
literal|"FROM `DEPT`) (SELECT 1 AS `UNO`\n"
operator|+
literal|"FROM `EMPDEPT`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithUnion
parameter_list|()
block|{
comment|// Per the standard WITH ... SELECT ... UNION is valid even without parens.
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from emp2\n"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"WITH `EMP2` AS (SELECT *\n"
operator|+
literal|"FROM `EMP`) (SELECT *\n"
operator|+
literal|"FROM `EMP2`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP2`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIdentifier
parameter_list|()
block|{
name|expr
argument_list|(
literal|"ab"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`AB`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     \"a  \"\" b!c\""
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`a  \" b!c`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     ^`^a  \" b!c`"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"\"x`y`z\""
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`x``y``z`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^`^x`y`z`"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"myMap[field] + myArray[1 + 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`MYMAP`[`FIELD`] + `MYARRAY`[(1 + 2)])"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES a"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES \"a\""
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES \"a\".\"b\""
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES \"a\".b"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBackTickIdentifier
parameter_list|()
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|BACK_TICK
expr_stmt|;
name|expr
argument_list|(
literal|"ab"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`AB`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     `a  \" b!c`"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`a  \" b!c`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     ^\"^a  \"\" b!c\""
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^\"^x`y`z\""
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"`x``y``z`"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`x``y``z`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"myMap[field] + myArray[1 + 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`MYMAP`[`FIELD`] + `MYARRAY`[(1 + 2)])"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES a"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES `a`"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBracketIdentifier
parameter_list|()
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|BRACKET
expr_stmt|;
name|expr
argument_list|(
literal|"ab"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`AB`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     [a  \" b!c]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`a  \" b!c`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     ^`^a  \" b!c`"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"     ^\"^a  \"\" b!c\""
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"[x`y`z]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`x``y``z`"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^\"^x`y`z\""
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^`^x``y``z`"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"[anything [even brackets]] is].[ok]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`anything [even brackets] is`.`ok`"
argument_list|)
expr_stmt|;
comment|// What would be a call to the 'item' function in DOUBLE_QUOTE and BACK_TICK
comment|// is a table alias.
name|sql
argument_list|(
literal|"select * from myMap[field], myArray[1 + 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `MYMAP` AS `field`,\n"
operator|+
literal|"`MYARRAY` AS `1 + 2`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from myMap [field], myArray [1 + 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `MYMAP` AS `field`,\n"
operator|+
literal|"`MYARRAY` AS `1 + 2`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES a"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"VALUES [a]"
argument_list|)
operator|.
name|node
argument_list|(
name|isQuoted
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBackTickQuery
parameter_list|()
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|BACK_TICK
expr_stmt|;
name|sql
argument_list|(
literal|"select `x`.`b baz` from `emp` as `x` where `x`.deptno in (10, 20)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `x`.`b baz`\n"
operator|+
literal|"FROM `emp` AS `x`\n"
operator|+
literal|"WHERE (`x`.`DEPTNO` IN (10, 20))"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4080">[CALCITE-4080]    * Allow character literals as column aliases, if    * SqlConformance.allowCharLiteralAlias()</a>. */
annotation|@
name|Test
name|void
name|testSingleQuotedAlias
parameter_list|()
block|{
specifier|final
name|String
name|expectingAlias
init|=
literal|"Expecting alias, found character literal"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select 1 as ^'a b'^ from t"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|expectingAlias
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
specifier|final
name|String
name|sql1b
init|=
literal|"SELECT 1 AS `a b`\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|sql1b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|sql1b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|sql1b
argument_list|)
expr_stmt|;
comment|// valid on MSSQL (alias contains a single quote)
specifier|final
name|String
name|sql2
init|=
literal|"with t as (select 1 as ^'x''y'^)\n"
operator|+
literal|"select [x'y] from t as [u]"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|quoting
operator|=
name|Quoting
operator|.
name|BRACKET
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|expectingAlias
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
specifier|final
name|String
name|sql2b
init|=
literal|"WITH `T` AS (SELECT 1 AS `x'y`) (SELECT `x'y`\n"
operator|+
literal|"FROM `T` AS `u`)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
comment|// also valid on MSSQL
specifier|final
name|String
name|sql3
init|=
literal|"with [t] as (select 1 as [x]) select [x] from [t]"
decl_stmt|;
specifier|final
name|String
name|sql3b
init|=
literal|"WITH `t` AS (SELECT 1 AS `x`) (SELECT `x`\n"
operator|+
literal|"FROM `t`)"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|quoting
operator|=
name|Quoting
operator|.
name|BRACKET
expr_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|sql3b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|sql3b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|sql3b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|sql3b
argument_list|)
expr_stmt|;
comment|// char literal as table alias is invalid on MSSQL (and others)
specifier|final
name|String
name|sql4
init|=
literal|"with t as (select 1 as x) select x from t as ^'u'^"
decl_stmt|;
specifier|final
name|String
name|sql4b
init|=
literal|"(?s)Encountered \"\\\\'u\\\\'\" at .*"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
name|sql4b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
name|sql4b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
name|sql4b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
name|sql4b
argument_list|)
expr_stmt|;
comment|// char literal as table alias (without AS) is invalid on MSSQL (and others)
specifier|final
name|String
name|sql5
init|=
literal|"with t as (select 1 as x) select x from t ^'u'^"
decl_stmt|;
specifier|final
name|String
name|sql5b
init|=
literal|"(?s)Encountered \"\\\\'u\\\\'\" at .*"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|fails
argument_list|(
name|sql5b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|fails
argument_list|(
name|sql5b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|fails
argument_list|(
name|sql5b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|fails
argument_list|(
name|sql5b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (10, 20) and gender = 'F'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE ((`DEPTNO` IN (10, 20)) AND (`GENDER` = 'F'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInListEmptyFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (^)^ and gender = 'F'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\)\" at line 1, column 36\\..*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (select deptno from dept)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`DEPTNO` IN (SELECT `DEPTNO`\n"
operator|+
literal|"FROM `DEPT`))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSomeEveryAndIntersectionAggQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select some(deptno = 10), every(deptno> 0), intersection(multiset[1,2]) from dept"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT SOME((`DEPTNO` = 10)), EVERY((`DEPTNO`> 0)), INTERSECTION((MULTISET[1, 2]))\n"
operator|+
literal|"FROM `DEPT`"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tricky for the parser - looks like "IN (scalar, scalar)" but isn't.    */
annotation|@
name|Test
name|void
name|testInQueryWithComma
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (select deptno from dept group by 1, 2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`DEPTNO` IN (SELECT `DEPTNO`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"GROUP BY 1, 2))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInSetop
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (\n"
operator|+
literal|"(select deptno from dept union select * from dept)"
operator|+
literal|"except\n"
operator|+
literal|"select * from dept) and false"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE ((`DEPTNO` IN ((SELECT `DEPTNO`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`)\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`)) AND FALSE)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSome
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where sal> some (select comm from emp)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`SAL`> SOME (SELECT `COMM`\n"
operator|+
literal|"FROM `EMP`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// ANY is a synonym for SOME
specifier|final
name|String
name|sql2
init|=
literal|"select * from emp\n"
operator|+
literal|"where sal> any (select comm from emp)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select * from emp\n"
operator|+
literal|"where name like (select ^some^ name from emp)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"some name\" at .*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"select * from emp\n"
operator|+
literal|"where name like some (select name from emp)"
decl_stmt|;
specifier|final
name|String
name|expected4
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`NAME` LIKE SOME((SELECT `NAME`\n"
operator|+
literal|"FROM `EMP`)))"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|(
name|expected4
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql5
init|=
literal|"select * from emp where empno = any (10,20)"
decl_stmt|;
specifier|final
name|String
name|expected5
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`EMPNO` = SOME (10, 20))"
decl_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|ok
argument_list|(
name|expected5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where sal<= all (select comm from emp) or sal> 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE ((`SAL`<= ALL (SELECT `COMM`\n"
operator|+
literal|"FROM `EMP`)) OR (`SAL`> 10))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAllList
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"where sal<= all (12, 20, 30)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`SAL`<= ALL (12, 20, 30))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnion
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a union select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a union all select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a union distinct select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionOrder
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select a, b from t "
operator|+
literal|"union all "
operator|+
literal|"select x, y from u "
operator|+
literal|"order by 1 asc, 2 desc"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT `A`, `B`\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT `X`, `Y`\n"
operator|+
literal|"FROM `U`)\n"
operator|+
literal|"ORDER BY 1, 2 DESC"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderUnion
parameter_list|()
block|{
comment|// ORDER BY inside UNION not allowed
name|sql
argument_list|(
literal|"select a from t order by a\n"
operator|+
literal|"^union^ all\n"
operator|+
literal|"select b from t order by b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"union\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLimitUnion
parameter_list|()
block|{
comment|// LIMIT inside UNION not allowed
name|sql
argument_list|(
literal|"select a from t limit 10\n"
operator|+
literal|"^union^ all\n"
operator|+
literal|"select b from t order by b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"union\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnionOfNonQueryFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from emp union ^2^ + 5"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Non-query expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
comment|/**    * In modern SQL, a query can occur almost everywhere that an expression    * can. This test tests the few exceptions.    */
annotation|@
name|Test
name|void
name|testQueryInIllegalContext
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 0, multiset[^(^select * from emp), 2] from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 0, multiset[1, ^(^select * from emp), 2, 3] from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExcept
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a except select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a except all select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"EXCEPT ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a except distinct select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests MINUS, which is equivalent to EXCEPT but only supported in some    * conformance levels (e.g. ORACLE). */
annotation|@
name|Test
name|void
name|testSetMinus
parameter_list|()
block|{
specifier|final
name|String
name|pattern
init|=
literal|"MINUS is not allowed under the current SQL conformance level"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select col1 from table1 ^MINUS^ select col1 from table2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|ORACLE_10
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(SELECT `COL1`\n"
operator|+
literal|"FROM `TABLE1`\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT `COL1`\n"
operator|+
literal|"FROM `TABLE2`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select col1 from table1 MINUS ALL select col1 from table2"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"(SELECT `COL1`\n"
operator|+
literal|"FROM `TABLE1`\n"
operator|+
literal|"EXCEPT ALL\n"
operator|+
literal|"SELECT `COL1`\n"
operator|+
literal|"FROM `TABLE2`)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
comment|/** MINUS is a<b>reserved</b> keyword in Calcite in all conformances, even    * in the default conformance, where it is not allowed as an alternative to    * EXCEPT. (It is reserved in Oracle but not in any version of the SQL    * standard.) */
annotation|@
name|Test
name|void
name|testMinusIsReserved
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^minus^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"minus\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^minus^ select"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"minus\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from t as ^minus^ where x< y"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"minus\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersect
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a intersect select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INTERSECT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a intersect all select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INTERSECT ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a intersect distinct select * from a"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INTERSECT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `A`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinCross
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a as a2 cross join b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A` AS `A2`\n"
operator|+
literal|"CROSS JOIN `B`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinOn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a left join b on 1 = 1 and 2 = 2 where 3 = 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"LEFT JOIN `B` ON ((1 = 1) AND (2 = 2))\n"
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinOnParentheses
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
return|return;
block|}
name|sql
argument_list|(
literal|"select * from a\n"
operator|+
literal|" left join (b join c as c1 on 1 = 1) on 2 = 2\n"
operator|+
literal|"where 3 = 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"LEFT JOIN (`B` INNER JOIN `C` AS `C1` ON (1 = 1)) ON (2 = 2)\n"
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same as {@link #testJoinOnParentheses()} but fancy aliases.    */
annotation|@
name|Test
name|void
name|testJoinOnParenthesesPlus
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
return|return;
block|}
name|sql
argument_list|(
literal|"select * from a\n"
operator|+
literal|" left join (b as b1 (x, y) join (select * from c) c1 on 1 = 1) on 2 = 2\n"
operator|+
literal|"where 3 = 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"LEFT JOIN (`B` AS `B1` (`X`, `Y`) INNER JOIN (SELECT *\n"
operator|+
literal|"FROM `C`) AS `C1` ON (1 = 1)) ON (2 = 2)\n"
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplicitTableInJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a left join (table b) on 2 = 2 where 3 = 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"LEFT JOIN (TABLE `B`) ON (2 = 2)\n"
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubQueryInJoin
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
return|return;
block|}
name|sql
argument_list|(
literal|"select * from (select * from a cross join b) as ab\n"
operator|+
literal|" left join ((table c) join d on 2 = 2) on 3 = 3\n"
operator|+
literal|" where 4 = 4"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"CROSS JOIN `B`) AS `AB`\n"
operator|+
literal|"LEFT JOIN ((TABLE `C`) INNER JOIN `D` ON (2 = 2)) ON (3 = 3)\n"
operator|+
literal|"WHERE (4 = 4)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOuterJoinNoiseWord
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a left outer join b on 1 = 1 and 2 = 2 where 3 = 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"LEFT JOIN `B` ON ((1 = 1) AND (2 = 2))\n"
operator|+
literal|"WHERE (3 = 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a join (select * from b) as b2 on true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INNER JOIN (SELECT *\n"
operator|+
literal|"FROM `B`) AS `B2` ON TRUE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFullInnerJoinFails
parameter_list|()
block|{
comment|// cannot have more than one of INNER, FULL, LEFT, RIGHT, CROSS
name|sql
argument_list|(
literal|"select * from a ^full^ inner join b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"full inner\" at line 1, column 17.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFullOuterJoin
parameter_list|()
block|{
comment|// OUTER is an optional extra to LEFT, RIGHT, or FULL
name|sql
argument_list|(
literal|"select * from a full outer join b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"FULL JOIN `B`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInnerOuterJoinFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a ^inner^ outer join b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"inner outer\" at line 1, column 17.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testJoinAssociativity
parameter_list|()
block|{
comment|// joins are left-associative
comment|// 1. no parens needed
name|sql
argument_list|(
literal|"select * from (a natural left join b) left join c on b.c1 = c.c1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)\n"
argument_list|)
expr_stmt|;
comment|// 2. parens needed
name|sql
argument_list|(
literal|"select * from a natural left join (b left join c on b.c1 = c.c1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)\n"
argument_list|)
expr_stmt|;
comment|// 3. same as 1
name|sql
argument_list|(
literal|"select * from a natural left join b left join c on b.c1 = c.c1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (`A` NATURAL LEFT JOIN `B`) LEFT JOIN `C` ON (`B`.`C1` = `C`.`C1`)\n"
argument_list|)
expr_stmt|;
block|}
comment|// Note: "select * from a natural cross join b" is actually illegal SQL
comment|// ("cross" is the only join type which cannot be modified with the
comment|// "natural") but the parser allows it; we and catch it at validate time
annotation|@
name|Test
name|void
name|testNaturalCrossJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a natural cross join b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"NATURAL CROSS JOIN `B`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJoinUsing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from a join b using (x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INNER JOIN `B` USING (`X`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from a join b using (^)^ where c = d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"[)]\" at line 1, column 31.*"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests CROSS APPLY, which is equivalent to CROSS JOIN and LEFT JOIN but    * only supported in some conformance levels (e.g. SQL Server). */
annotation|@
name|Test
name|void
name|testApply
parameter_list|()
block|{
specifier|final
name|String
name|pattern
init|=
literal|"APPLY operator is not allowed under the current SQL conformance level"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"cross apply table(ramp(deptno)) as t(a^)^"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"CROSS JOIN LATERAL TABLE(`RAMP`(`DEPTNO`)) AS `T` (`A`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// Supported in Oracle 12 but not Oracle 10
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|ORACLE_10
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|ORACLE_12
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Tests OUTER APPLY. */
annotation|@
name|Test
name|void
name|testOuterApply
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept outer apply table(ramp(deptno))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"LEFT JOIN LATERAL TABLE(`RAMP`(`DEPTNO`)) ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOuterApplySubQuery
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"outer apply (select * from emp where emp.deptno = dept.deptno)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"LEFT JOIN LATERAL (SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)) ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOuterApplyValues
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"outer apply (select * from emp where emp.deptno = dept.deptno)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"LEFT JOIN LATERAL (SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)) ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Even in SQL Server conformance mode, we do not yet support    * 'function(args)' as an abbreviation for 'table(function(args)'. */
annotation|@
name|Test
name|void
name|testOuterApplyFunctionFails
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept outer apply ramp(deptno^)^)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\)\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCrossOuterApply
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|SQL_SERVER_2008
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"cross apply table(ramp(deptno)) as t(a)\n"
operator|+
literal|"outer apply table(ramp2(a))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"CROSS JOIN LATERAL TABLE(`RAMP`(`DEPTNO`)) AS `T` (`A`)\n"
operator|+
literal|"LEFT JOIN LATERAL TABLE(`RAMP2`(`A`)) ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableSample
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select * from ("
operator|+
literal|"  select * "
operator|+
literal|"  from emp "
operator|+
literal|"  join dept on emp.deptno = dept.deptno"
operator|+
literal|"  where gender = 'F'"
operator|+
literal|"  order by sal) tablesample substitute('medium')"
decl_stmt|;
specifier|final
name|String
name|expected0
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"INNER JOIN `DEPT` ON (`EMP`.`DEPTNO` = `DEPT`.`DEPTNO`)\n"
operator|+
literal|"WHERE (`GENDER` = 'F')\n"
operator|+
literal|"ORDER BY `SAL`) TABLESAMPLE SUBSTITUTE('MEDIUM')"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select * "
operator|+
literal|"from emp as x tablesample substitute('medium') "
operator|+
literal|"join dept tablesample substitute('lar' /* split */ 'ge') on x.deptno = dept.deptno"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` AS `X` TABLESAMPLE SUBSTITUTE('MEDIUM')\n"
operator|+
literal|"INNER JOIN `DEPT` TABLESAMPLE SUBSTITUTE('LARGE') ON (`X`.`DEPTNO` = `DEPT`.`DEPTNO`)"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select * "
operator|+
literal|"from emp as x tablesample bernoulli(50)"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` AS `X` TABLESAMPLE BERNOULLI(50.0)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select * "
operator|+
literal|"from emp as x "
operator|+
literal|"tablesample bernoulli(50) REPEATABLE(10) "
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` AS `X` TABLESAMPLE BERNOULLI(50.0) REPEATABLE(10)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
comment|// test repeatable with invalid int literal.
name|sql
argument_list|(
literal|"select * "
operator|+
literal|"from emp as x "
operator|+
literal|"tablesample bernoulli(50) REPEATABLE(^100000000000000000000^) "
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Literal '100000000000000000000' "
operator|+
literal|"can not be parsed to type 'java\\.lang\\.Integer'"
argument_list|)
expr_stmt|;
comment|// test repeatable with invalid negative int literal.
name|sql
argument_list|(
literal|"select * "
operator|+
literal|"from emp as x "
operator|+
literal|"tablesample bernoulli(50) REPEATABLE(-^100000000000000000000^) "
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Literal '100000000000000000000' "
operator|+
literal|"can not be parsed to type 'java\\.lang\\.Integer'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLiteral
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'foo'"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"100"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 as uno, 'x' as x, null as n from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1 AS `UNO`, 'x' AS `X`, NULL AS `N`\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
comment|// Even though it looks like a date, it's just a string.
name|expr
argument_list|(
literal|"'2004-06-01'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'2004-06-01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-.25"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"-0.25"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55'"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.900'"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1234'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1234'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1236'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.1236'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.9999'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-06-01 15:55:55.9999'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"NULL"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testContinuedLiteral
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'abba'\n'abba'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'abba'\n'abba'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abba'\n'0001'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'abba'\n'0001'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"N'yabba'\n'dabba'\n'doo'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'yabba'\n'dabba'\n'doo'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_iso-8859-1'yabba'\n'dabba'\n'don''t'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'yabba'\n'dabba'\n'don''t'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'01aa'\n'03ff'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"X'01AA'\n'03FF'"
argument_list|)
expr_stmt|;
comment|// a bad hexstring
name|sql
argument_list|(
literal|"x'01aa'\n^'vvvv'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that ambiguity between extended string literals and character string    * aliases is always resolved in favor of extended string literals. */
annotation|@
name|Test
name|void
name|testContinuedLiteralAlias
parameter_list|()
block|{
specifier|final
name|String
name|expectingAlias
init|=
literal|"Expecting alias, found character literal"
decl_stmt|;
comment|// Not ambiguous, because of 'as'.
specifier|final
name|String
name|sql0
init|=
literal|"select 1 an_alias,\n"
operator|+
literal|"  x'01'\n"
operator|+
literal|"  'ab' as x\n"
operator|+
literal|"from t"
decl_stmt|;
specifier|final
name|String
name|sql0b
init|=
literal|"SELECT 1 AS `AN_ALIAS`, X'01'\n"
operator|+
literal|"'AB' AS `X`\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|sql0b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|sql0b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|sql0b
argument_list|)
expr_stmt|;
comment|// Is 'ab' an alias or is it part of the x'01' 'ab' continued binary string
comment|// literal? It's ambiguous, but we prefer the latter.
specifier|final
name|String
name|sql1
init|=
literal|"select 1 ^'an alias'^,\n"
operator|+
literal|"  x'01'\n"
operator|+
literal|"  'ab'\n"
operator|+
literal|"from t"
decl_stmt|;
specifier|final
name|String
name|sql1b
init|=
literal|"SELECT 1 AS `an alias`, X'01'\n"
operator|+
literal|"'AB'\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|expectingAlias
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|sql1b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|sql1b
argument_list|)
expr_stmt|;
comment|// Parser prefers continued character and binary string literals over
comment|// character string aliases, regardless of whether the dialect allows
comment|// character string aliases.
specifier|final
name|String
name|sql2
init|=
literal|"select 'continued'\n"
operator|+
literal|"  'char literal, not alias',\n"
operator|+
literal|"  x'01'\n"
operator|+
literal|"  'ab'\n"
operator|+
literal|"from t"
decl_stmt|;
specifier|final
name|String
name|sql2b
init|=
literal|"SELECT 'continued'\n"
operator|+
literal|"'char literal, not alias', X'01'\n"
operator|+
literal|"'AB'\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|MYSQL_5
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
expr_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|sql2b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMixedFrom
parameter_list|()
block|{
comment|// REVIEW: Is this syntax even valid?
name|sql
argument_list|(
literal|"select * from a join b using (x), c join d using (y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"INNER JOIN `B` USING (`X`),\n"
operator|+
literal|"`C`\n"
operator|+
literal|"INNER JOIN `D` USING (`Y`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMixedStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select emp.*, 1 as foo from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `EMP`.*, 1 AS `FOO`\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSchemaTableStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select schem.emp.*, emp.empno * dept.deptno\n"
operator|+
literal|"from schem.emp, dept"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `SCHEM`.`EMP`.*, (`EMP`.`EMPNO` * `DEPT`.`DEPTNO`)\n"
operator|+
literal|"FROM `SCHEM`.`EMP`,\n"
operator|+
literal|"`DEPT`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCatalogSchemaTableStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cat.schem.emp.* from cat.schem.emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `CAT`.`SCHEM`.`EMP`.*\n"
operator|+
literal|"FROM `CAT`.`SCHEM`.`EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAliasedStar
parameter_list|()
block|{
comment|// OK in parser; validator will give error
name|sql
argument_list|(
literal|"select emp.* as foo from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `EMP`.* AS `FOO`\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept where not not exists (select * from emp) and true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"WHERE ((NOT (NOT (EXISTS (SELECT *\n"
operator|+
literal|"FROM `EMP`)))) AND TRUE)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrder
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp order by empno, gender desc, deptno asc, empno asc, name desc"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"ORDER BY `EMPNO`, `GENDER` DESC, `DEPTNO`, `EMPNO`, `NAME` DESC"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderNullsFirst
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"order by gender desc nulls last,\n"
operator|+
literal|" deptno asc nulls first,\n"
operator|+
literal|" empno nulls last"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"ORDER BY `GENDER` DESC NULLS LAST, `DEPTNO` NULLS FIRST,"
operator|+
literal|" `EMPNO` NULLS LAST"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderInternal
parameter_list|()
block|{
name|sql
argument_list|(
literal|"(select * from emp order by empno) union select * from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"ORDER BY `EMPNO`)\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (select * from t order by x, y) where a = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `X`, `Y`)\n"
operator|+
literal|"WHERE (`A` = `B`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderIllegalInExpression
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select (select 1 from foo order by x,y) from t where a = b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SELECT 1\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `X`, `Y`)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`A` = `B`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select (1 ^order^ by x, y) from t where a = b"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY unexpected"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrderOffsetFetch
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select a from foo order by b, c offset 1 row fetch first 2 row only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// as above, but ROWS rather than ROW
name|sql
argument_list|(
literal|"select a from foo order by b, c offset 1 rows fetch first 2 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// as above, but NEXT (means same as FIRST)
name|sql
argument_list|(
literal|"select a from foo order by b, c offset 1 rows fetch next 3 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// as above, but omit the ROWS noise word after OFFSET. This is not
comment|// compatible with SQL:2008 but allows the Postgres syntax
comment|// "LIMIT ... OFFSET".
name|sql
argument_list|(
literal|"select a from foo order by b, c offset 1 fetch next 3 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// as above, omit OFFSET
name|sql
argument_list|(
literal|"select a from foo order by b, c fetch next 3 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// FETCH, no ORDER BY or OFFSET
name|sql
argument_list|(
literal|"select a from foo fetch next 4 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"FETCH NEXT 4 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// OFFSET, no ORDER BY or FETCH
name|sql
argument_list|(
literal|"select a from foo offset 1 row"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 1 ROWS"
argument_list|)
expr_stmt|;
comment|// OFFSET and FETCH, no ORDER BY
name|sql
argument_list|(
literal|"select a from foo offset 1 row fetch next 3 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// OFFSET and FETCH, with dynamic parameters
name|sql
argument_list|(
literal|"select a from foo offset ? row fetch next ? rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET ? ROWS\n"
operator|+
literal|"FETCH NEXT ? ROWS ONLY"
argument_list|)
expr_stmt|;
comment|// missing ROWS after FETCH
name|sql
argument_list|(
literal|"select a from foo offset 1 fetch next 3 ^only^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"only\" at .*"
argument_list|)
expr_stmt|;
comment|// FETCH before OFFSET is illegal
name|sql
argument_list|(
literal|"select a from foo fetch next 3 rows only ^offset^ 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"offset\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/**    * "LIMIT ... OFFSET ..." is the postgres equivalent of SQL:2008    * "OFFSET ... FETCH". It all maps down to a parse tree that looks like    * SQL:2008.    */
annotation|@
name|Test
name|void
name|testLimit
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select a from foo order by b, c limit 2 offset 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select a from foo order by b, c limit 2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select a from foo order by b, c offset 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `B`, `C`\n"
operator|+
literal|"OFFSET 1 ROWS"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case that does not reproduce but is related to    *<a href="https://issues.apache.org/jira/browse/CALCITE-1238">[CALCITE-1238]    * Unparsing LIMIT without ORDER BY after validation</a>. */
annotation|@
name|Test
name|void
name|testLimitWithoutOrder
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 2"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLimitOffsetWithoutOrder
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 1 ROWS\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 2 offset 1"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLimitStartCount
parameter_list|()
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
specifier|final
name|String
name|error
init|=
literal|"'LIMIT start, count' is not allowed under the "
operator|+
literal|"current SQL conformance level"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 1,^2^"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
comment|// "limit all" is equivalent to no limit
specifier|final
name|String
name|expected0
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit all"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"ORDER BY `X`"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo order by x limit all"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|LENIENT
expr_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 2 ROWS\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 2,3"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
comment|// "offset 4" overrides the earlier "2"
specifier|final
name|String
name|expected3
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 4 ROWS\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 2,3 offset 4"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
comment|// "fetch next 4" overrides the earlier "limit 3"
specifier|final
name|String
name|expected4
init|=
literal|"SELECT `A`\n"
operator|+
literal|"FROM `FOO`\n"
operator|+
literal|"OFFSET 2 ROWS\n"
operator|+
literal|"FETCH NEXT 4 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
literal|"select a from foo limit 2,3 fetch next 4 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected4
argument_list|)
expr_stmt|;
comment|// "limit start, all" is not valid
name|sql
argument_list|(
literal|"select a from foo limit 2, ^all^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"all\" at line 1.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlInlineComment
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from t --this is a comment\n"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t--\n"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t--this is a comment\n"
operator|+
literal|"where a>b-- this is comment\n"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (`A`> `B`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t\n--select"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultilineComment
parameter_list|()
block|{
comment|// on single line
name|sql
argument_list|(
literal|"select 1 /* , 2 */, 3 from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1, 3\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
comment|// on several lines
name|sql
argument_list|(
literal|"select /* 1,\n"
operator|+
literal|" 2,\n"
operator|+
literal|" */ 3 from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 3\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
comment|// stuff inside comment
name|sql
argument_list|(
literal|"values ( /** 1, 2 + ** */ 3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(3))"
argument_list|)
expr_stmt|;
comment|// comment in string is preserved
name|sql
argument_list|(
literal|"values ('a string with /* a comment */ in it')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW('a string with /* a comment */ in it'))"
argument_list|)
expr_stmt|;
comment|// SQL:2003, 5.2, syntax rule # 8 "There shall be no<separator>
comment|// separating the<minus sign>s of a<simple comment introducer>".
name|sql
argument_list|(
literal|"values (- -1\n"
operator|+
literal|")"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values (--1+\n"
operator|+
literal|"2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(2))"
argument_list|)
expr_stmt|;
comment|// end of multiline comment without start
if|if
condition|(
name|Bug
operator|.
name|FRG73_FIXED
condition|)
block|{
name|sql
argument_list|(
literal|"values (1 */ 2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
comment|// SQL:2003, 5.2, syntax rule #10 "Within a<bracket comment context>,
comment|// any<solidus> immediately followed by an<asterisk> without any
comment|// intervening<separator> shall be considered to be the<bracketed
comment|// comment introducer> for a<separator> that is a<bracketed
comment|// comment>".
comment|// comment inside a comment
comment|// Spec is unclear what should happen, but currently it crashes the
comment|// parser, and that's bad
if|if
condition|(
name|Bug
operator|.
name|FRG73_FIXED
condition|)
block|{
name|sql
argument_list|(
literal|"values (1 + /* comment /* inner comment */ */ 2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
comment|// single-line comment inside multiline comment is illegal
comment|//
comment|// SQL-2003, 5.2: "Note 63 - Conforming programs should not place
comment|//<simple comment> within a<bracketed comment> because if such a
comment|//<simple comment> contains the sequence of characters "*/" without
comment|// a preceding "/*" in the same<simple comment>, it will prematurely
comment|// terminate the containing<bracketed comment>.
if|if
condition|(
name|Bug
operator|.
name|FRG73_FIXED
condition|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"values /* multiline contains -- singline */\n"
operator|+
literal|" (1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"xxx"
argument_list|)
expr_stmt|;
block|}
comment|// non-terminated multi-line comment inside single-line comment
if|if
condition|(
name|Bug
operator|.
name|FRG73_FIXED
condition|)
block|{
comment|// Test should fail, and it does, but it should give "*/" as the
comment|// erroneous token.
specifier|final
name|String
name|sql
init|=
literal|"values ( -- rest of line /* a comment\n"
operator|+
literal|" 1, ^*/^ 2)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Encountered \"/\\*\" at"
argument_list|)
expr_stmt|;
block|}
name|sql
argument_list|(
literal|"values (1 + /* comment -- rest of line\n"
operator|+
literal|" rest of comment */ 2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW((1 + 2)))"
argument_list|)
expr_stmt|;
comment|// multiline comment inside single-line comment
name|sql
argument_list|(
literal|"values -- rest of line /* a comment */\n"
operator|+
literal|"(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1))"
argument_list|)
expr_stmt|;
comment|// non-terminated multiline comment inside single-line comment
name|sql
argument_list|(
literal|"values -- rest of line /* a comment\n"
operator|+
literal|"(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1))"
argument_list|)
expr_stmt|;
comment|// even if comment abuts the tokens at either end, it becomes a space
name|sql
argument_list|(
literal|"values ('abc'/* a comment*/'def')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW('abc'\n'def'))"
argument_list|)
expr_stmt|;
comment|// comment which starts as soon as it has begun
name|sql
argument_list|(
literal|"values /**/ (1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1))"
argument_list|)
expr_stmt|;
block|}
comment|// expressions
annotation|@
name|Test
name|void
name|testParseNumber
parameter_list|()
block|{
comment|// Exacts
name|expr
argument_list|(
literal|"1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+1."
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"-1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"- -1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.0"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1.0"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-3.2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"-3.2"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1."
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|".1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"0.1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"2500000000"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"2500000000"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"5000000000"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"5000000000"
argument_list|)
expr_stmt|;
comment|// Approximates
name|expr
argument_list|(
literal|"1e1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1E1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+1e1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1E1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1.1E1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e+1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1.1E1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e-1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1.1E-1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+1.1e-1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1.1E-1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.E3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1E3"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.e-3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1E-3"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.e+3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1E3"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|".5E3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"5E2"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+.5e3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"5E2"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-.5E3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"-5E2"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|".5e-32"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"5E-33"
argument_list|)
expr_stmt|;
comment|// Mix integer/decimals/approx
name|expr
argument_list|(
literal|"3. + 2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(3 + 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1++2+3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((1 + 2) + 3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1- -2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 - -2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1++2.3e-4++.5e-6++.7++8"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((((1 + 2.3E-4) + 5E-7) + 0.7) + 8)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1- -2.3e-4 - -.5e-6  -\n"
operator|+
literal|"-.7++8"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((((1 - -2.3E-4) - -5E-7) - -0.7) + 8)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1+-2.*-3.e-1/-4"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + ((-2 * -3E-1) / -4))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testParseNumberFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT 0.5e1^.1^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered .*\\.1.* at line 1.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinusPrefixInExpression
parameter_list|()
block|{
name|expr
argument_list|(
literal|"-(1+2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(- (1 + 2))"
argument_list|)
expr_stmt|;
block|}
comment|// operator precedence
annotation|@
name|Test
name|void
name|testPrecedence0
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1 + 2 * 3 * 4 + 5"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((1 + ((2 * 3) * 4)) + 5)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrecedence1
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1 + 2 * (3 * (4 + 5))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + (2 * (3 * (4 + 5))))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrecedence2
parameter_list|()
block|{
name|expr
argument_list|(
literal|"- - 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
comment|// special case for unary minus
block|}
annotation|@
name|Test
name|void
name|testPrecedence2b
parameter_list|()
block|{
name|expr
argument_list|(
literal|"not not 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NOT (NOT 1))"
argument_list|)
expr_stmt|;
comment|// two prefixes
block|}
annotation|@
name|Test
name|void
name|testPrecedence3
parameter_list|()
block|{
name|expr
argument_list|(
literal|"- 1 is null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(-1 IS NULL)"
argument_list|)
expr_stmt|;
comment|// prefix vs. postfix
block|}
annotation|@
name|Test
name|void
name|testPrecedence4
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1 - -2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 - -2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '-'
block|}
annotation|@
name|Test
name|void
name|testPrecedence5
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1++2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + 2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '+'
name|expr
argument_list|(
literal|"1+ +2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + 2)"
argument_list|)
expr_stmt|;
comment|// infix, prefix '+'
block|}
annotation|@
name|Test
name|void
name|testPrecedenceSetOps
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from a union "
operator|+
literal|"select * from b intersect "
operator|+
literal|"select * from c intersect "
operator|+
literal|"select * from d except "
operator|+
literal|"select * from e except "
operator|+
literal|"select * from f union "
operator|+
literal|"select * from g"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"((((SELECT *\n"
operator|+
literal|"FROM `A`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"((SELECT *\n"
operator|+
literal|"FROM `B`\n"
operator|+
literal|"INTERSECT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `C`)\n"
operator|+
literal|"INTERSECT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `D`))\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `E`)\n"
operator|+
literal|"EXCEPT\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `F`)\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `G`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryInFrom
parameter_list|()
block|{
comment|// one query with 'as', the other without
name|sql
argument_list|(
literal|"select * from (select * from emp) as e join (select * from dept) d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `EMP`) AS `E`\n"
operator|+
literal|"INNER JOIN (SELECT *\n"
operator|+
literal|"FROM `DEPT`) AS `D`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQuotesInString
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a''b'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'a''b'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'''x'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'''x'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"''"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"''"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'Quoted strings aren''t \"hard\"'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'Quoted strings aren''t \"hard\"'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testScalarQueryInWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where 3 = (select count(*) from dept where dept.deptno = emp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE (3 = (SELECT COUNT(*)\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"WHERE (`DEPT`.`DEPTNO` = `EMP`.`DEPTNO`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testScalarQueryInSelect
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select x, (select count(*) from dept where dept.deptno = emp.deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `X`, (SELECT COUNT(*)\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"WHERE (`DEPT`.`DEPTNO` = `EMP`.`DEPTNO`))\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`,\n"
operator|+
literal|"`DEPT`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFrom
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 2+2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (2 + 2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFrom2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 2+2 as x, 'a' as y"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (2 + 2) AS `X`, 'a' AS `Y`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectDistinctWithoutFrom
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select distinct 2+2 as x, 'a' as y"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT DISTINCT (2 + 2) AS `X`, 'a' AS `Y`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFromWhereFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 2+2 as x ^where^ 1> 2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"where\" at line .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFromGroupByFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 2+2 as x ^group^ by 1, 2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"group\" at line .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectWithoutFromHavingFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 2+2 as x ^having^ 1> 2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"having\" at line .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectList3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1, emp.*, 2 from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1, `EMP`.*, 2\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectList4
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^from^ emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"from\" at line .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCompoundStar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sales.emp.address.zipcode,\n"
operator|+
literal|" sales.emp.address.*\n"
operator|+
literal|"from sales.emp"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `SALES`.`EMP`.`ADDRESS`.`ZIPCODE`,"
operator|+
literal|" `SALES`.`EMP`.`ADDRESS`.*\n"
operator|+
literal|"FROM `SALES`.`EMP`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select distinct foo from bar"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT DISTINCT `FOO`\n"
operator|+
literal|"FROM `BAR`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectAll
parameter_list|()
block|{
comment|// "unique" is the default -- so drop the keyword
name|sql
argument_list|(
literal|"select * from (select all foo from bar) as xyz"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT ALL `FOO`\n"
operator|+
literal|"FROM `BAR`) AS `XYZ`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectStream
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream foo from bar"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT STREAM `FOO`\n"
operator|+
literal|"FROM `BAR`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectStreamDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream distinct foo from bar"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT STREAM DISTINCT `FOO`\n"
operator|+
literal|"FROM `BAR`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where empno> 5 and gender = 'F'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WHERE ((`EMPNO`> 5) AND (`GENDER` = 'F'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNestedSelect
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (select * from emp)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `EMP`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testValues
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values(1,'two')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1, 'two'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testValuesExplicitRow
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values row(1,'two')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"VALUES (ROW(1, 'two'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFromValues
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (values(1,'two'), 3, (4, 'five'))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (VALUES (ROW(1, 'two')),\n"
operator|+
literal|"(ROW(3)),\n"
operator|+
literal|"(ROW(4, 'five')))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFromValuesWithoutParens
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from ^values^('x')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"values\" at line 1, column 15\\.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"    \"LATERAL\" \\.\\.\\.\n"
operator|+
literal|"    \"TABLE\" \\.\\.\\.\n"
operator|+
literal|"    \"UNNEST\" \\.\\.\\.\n"
operator|+
literal|"<IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"<HYPHENATED_IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"<QUOTED_IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"<BACK_QUOTED_IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"<BRACKET_QUOTED_IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"<UNICODE_QUOTED_IDENTIFIER> \\.\\.\\.\n"
operator|+
literal|"    \"\\(\" \\.\\.\\.\n.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEmptyValues
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (values(^)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\)\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-493">[CALCITE-493]    * Add EXTEND clause, for defining columns and their types at query/DML    * time</a>. */
annotation|@
name|Test
name|void
name|testTableExtend
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null) where true"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10))\n"
operator|+
literal|"WHERE TRUE"
argument_list|)
expr_stmt|;
comment|// with table alias
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null) as t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10)) AS `T`"
argument_list|)
expr_stmt|;
comment|// as previous, without AS
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null) t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10)) AS `T`"
argument_list|)
expr_stmt|;
comment|// with table alias and column alias list
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null) as t(a, b)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10)) AS `T` (`A`, `B`)"
argument_list|)
expr_stmt|;
comment|// as previous, without AS
name|sql
argument_list|(
literal|"select * from emp extend (x int, y varchar(10) not null) t(a, b)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10)) AS `T` (`A`, `B`)"
argument_list|)
expr_stmt|;
comment|// omit EXTEND
name|sql
argument_list|(
literal|"select * from emp (x int, y varchar(10) not null) t(a, b)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10)) AS `T` (`A`, `B`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp (x int, y varchar(10) not null) where x = y"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` EXTEND (`X` INTEGER, `Y` VARCHAR(10))\n"
operator|+
literal|"WHERE (`X` = `Y`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplicitTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"table emp"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TABLE `EMP`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"table ^123^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"123\" at line 1, column 7\\.\n.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplicitTableOrdered
parameter_list|()
block|{
name|sql
argument_list|(
literal|"table emp order by name"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TABLE `EMP`)\n"
operator|+
literal|"ORDER BY `NAME`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectFromExplicitTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (table emp)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM (TABLE `EMP`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectFromBareExplicitTableFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table ^emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"emp\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (table ^(^select empno from emp))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\(\".*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(ramp(3, 4))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`RAMP`(3, 4))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDescriptor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(ramp(descriptor(column_name)))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`RAMP`(DESCRIPTOR(`COLUMN_NAME`)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(ramp(descriptor(\"COLUMN_NAME\")))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`RAMP`(DESCRIPTOR(`COLUMN_NAME`)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(ramp(descriptor(column_name1, column_name2, column_name3)))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`RAMP`(DESCRIPTOR(`COLUMN_NAME1`, `COLUMN_NAME2`, `COLUMN_NAME3`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTableWithCursorParam
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(dedup(cursor(select * from emps),'name'))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`DEDUP`((CURSOR ((SELECT *\n"
operator|+
literal|"FROM `EMPS`))), 'name'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTableWithColumnListParam
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(dedup(cursor(select * from emps),"
operator|+
literal|"row(empno, name)))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM TABLE(`DEDUP`((CURSOR ((SELECT *\n"
operator|+
literal|"FROM `EMPS`))), (ROW(`EMPNO`, `NAME`))))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLateral
parameter_list|()
block|{
comment|// Bad: LATERAL table
name|sql
argument_list|(
literal|"select * from lateral ^emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"emp\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral table ^emp^ as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"emp\" at .*"
argument_list|)
expr_stmt|;
comment|// Bad: LATERAL TABLE schema.table
name|sql
argument_list|(
literal|"select * from lateral table ^scott^.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"scott\" at .*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM LATERAL TABLE(`RAMP`(1))"
decl_stmt|;
comment|// Good: LATERAL TABLE function(arg, arg)
name|sql
argument_list|(
literal|"select * from lateral table(ramp(1))"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral table(ramp(1)) as t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
operator|+
literal|" AS `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral table(ramp(1)) as t(x)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
operator|+
literal|" AS `T` (`X`)"
argument_list|)
expr_stmt|;
comment|// Bad: Parentheses make it look like a sub-query
name|sql
argument_list|(
literal|"select * from lateral (table^(^ramp(1)))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\(\" at .*"
argument_list|)
expr_stmt|;
comment|// Good: LATERAL (subQuery)
specifier|final
name|String
name|expected2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM LATERAL (SELECT *\n"
operator|+
literal|"FROM `EMP`)"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from lateral (select * from emp)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral (select * from emp) as t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
operator|+
literal|" AS `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral (select * from emp) as t(x)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
operator|+
literal|" AS `T` (`X`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTemporalTable
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select stream * from orders, products\n"
operator|+
literal|"for system_time as of TIMESTAMP '2011-01-02 00:00:00'"
decl_stmt|;
specifier|final
name|String
name|expected0
init|=
literal|"SELECT STREAM *\n"
operator|+
literal|"FROM `ORDERS`,\n"
operator|+
literal|"`PRODUCTS` FOR SYSTEM_TIME AS OF TIMESTAMP '2011-01-02 00:00:00'"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
comment|// Can not use explicit LATERAL keyword.
specifier|final
name|String
name|sql1
init|=
literal|"select stream * from orders, LATERAL ^products_temporal^\n"
operator|+
literal|"for system_time as of TIMESTAMP '2011-01-02 00:00:00'"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"(?s)Encountered \"products_temporal\" at line .*"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
comment|// Inner join with a specific timestamp
specifier|final
name|String
name|sql2
init|=
literal|"select stream * from orders join products_temporal\n"
operator|+
literal|"for system_time as of timestamp '2011-01-02 00:00:00'\n"
operator|+
literal|"on orders.productid = products_temporal.productid"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT STREAM *\n"
operator|+
literal|"FROM `ORDERS`\n"
operator|+
literal|"INNER JOIN `PRODUCTS_TEMPORAL` "
operator|+
literal|"FOR SYSTEM_TIME AS OF TIMESTAMP '2011-01-02 00:00:00' "
operator|+
literal|"ON (`ORDERS`.`PRODUCTID` = `PRODUCTS_TEMPORAL`.`PRODUCTID`)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
comment|// Left join with a timestamp field
specifier|final
name|String
name|sql3
init|=
literal|"select stream * from orders left join products_temporal\n"
operator|+
literal|"for system_time as of orders.rowtime "
operator|+
literal|"on orders.productid = products_temporal.productid"
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT STREAM *\n"
operator|+
literal|"FROM `ORDERS`\n"
operator|+
literal|"LEFT JOIN `PRODUCTS_TEMPORAL` "
operator|+
literal|"FOR SYSTEM_TIME AS OF `ORDERS`.`ROWTIME` "
operator|+
literal|"ON (`ORDERS`.`PRODUCTID` = `PRODUCTS_TEMPORAL`.`PRODUCTID`)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
comment|// Left join with a timestamp expression
specifier|final
name|String
name|sql4
init|=
literal|"select stream * from orders left join products_temporal\n"
operator|+
literal|"for system_time as of orders.rowtime - INTERVAL '3' DAY "
operator|+
literal|"on orders.productid = products_temporal.productid"
decl_stmt|;
specifier|final
name|String
name|expected4
init|=
literal|"SELECT STREAM *\n"
operator|+
literal|"FROM `ORDERS`\n"
operator|+
literal|"LEFT JOIN `PRODUCTS_TEMPORAL` "
operator|+
literal|"FOR SYSTEM_TIME AS OF (`ORDERS`.`ROWTIME` - INTERVAL '3' DAY) "
operator|+
literal|"ON (`ORDERS`.`PRODUCTID` = `PRODUCTS_TEMPORAL`.`PRODUCTID`)"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|(
name|expected4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTableWithLateral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept, lateral table(ramp(dept.deptno))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`,\n"
operator|+
literal|"LATERAL TABLE(`RAMP`(`DEPT`.`DEPTNO`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTableWithLateral2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept as d\n"
operator|+
literal|"cross join lateral table(ramp(dept.deptno)) as r"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT` AS `D`\n"
operator|+
literal|"CROSS JOIN LATERAL TABLE(`RAMP`(`DEPT`.`DEPTNO`)) AS `R`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectionTableWithLateral3
parameter_list|()
block|{
comment|// LATERAL before first table in FROM clause doesn't achieve anything, but
comment|// it's valid.
specifier|final
name|String
name|sql
init|=
literal|"select * from lateral table(ramp(dept.deptno)), dept"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM LATERAL TABLE(`RAMP`(`DEPT`.`DEPTNO`)),\n"
operator|+
literal|"`DEPT`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIllegalCursors
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^cursor^(select * from emps) from emps"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"call list(^cursor^(select * from emps))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select f(^cursor^(select * from emps)) from emps"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"CURSOR expression encountered in illegal context"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplain
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"explain plan for select * from emps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"EXPLAIN PLAN"
operator|+
literal|" INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainAsXml
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"explain plan as xml for select * from emps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"EXPLAIN PLAN"
operator|+
literal|" INCLUDING ATTRIBUTES WITH IMPLEMENTATION AS XML FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainAsJson
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"explain plan as json for select * from emps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"EXPLAIN PLAN"
operator|+
literal|" INCLUDING ATTRIBUTES WITH IMPLEMENTATION AS JSON FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainWithImpl
parameter_list|()
block|{
name|sql
argument_list|(
literal|"explain plan with implementation for select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainWithoutImpl
parameter_list|()
block|{
name|sql
argument_list|(
literal|"explain plan without implementation for select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITHOUT IMPLEMENTATION FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainWithType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"explain plan with type for (values (true))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH TYPE FOR\n"
operator|+
literal|"(VALUES (ROW(TRUE)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainJsonFormat
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"explain plan as json for select * from emps"
decl_stmt|;
name|TesterImpl
name|tester
init|=
operator|(
name|TesterImpl
operator|)
name|getTester
argument_list|()
decl_stmt|;
name|SqlExplain
name|sqlExplain
init|=
operator|(
name|SqlExplain
operator|)
name|tester
operator|.
name|parseStmtsAndHandleEx
argument_list|(
name|sql
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sqlExplain
operator|.
name|isJson
argument_list|()
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
name|testDescribeSchema
parameter_list|()
block|{
name|sql
argument_list|(
literal|"describe schema A"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE SCHEMA `A`"
argument_list|)
expr_stmt|;
comment|// Currently DESCRIBE DATABASE, DESCRIBE CATALOG become DESCRIBE SCHEMA.
comment|// See [CALCITE-1221] Implement DESCRIBE DATABASE, CATALOG, STATEMENT
name|sql
argument_list|(
literal|"describe database A"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE SCHEMA `A`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe catalog A"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE SCHEMA `A`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDescribeTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"describe emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `EMPS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe \"emps\""
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `emps`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe s.emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `S`.`EMPS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe db.c.s.emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `DB`.`C`.`S`.`EMPS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe emps col1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `EMPS` `COL1`"
argument_list|)
expr_stmt|;
comment|// BigQuery allows hyphens in schema (project) names
name|sql
argument_list|(
literal|"describe foo-bar.baz"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `foo-bar`.baz"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe table foo-bar.baz"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `foo-bar`.baz"
argument_list|)
expr_stmt|;
comment|// table keyword is OK
name|sql
argument_list|(
literal|"describe table emps col1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DESCRIBE TABLE `EMPS` `COL1`"
argument_list|)
expr_stmt|;
comment|// character literal for column name not ok
name|sql
argument_list|(
literal|"describe emps ^'col_'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\\\'col_\\\\'\" at .*"
argument_list|)
expr_stmt|;
comment|// composite column name not ok
name|sql
argument_list|(
literal|"describe emps c1^.^c2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\.\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDescribeStatement
parameter_list|()
block|{
comment|// Currently DESCRIBE STATEMENT becomes EXPLAIN.
comment|// See [CALCITE-1221] Implement DESCRIBE DATABASE, CATALOG, STATEMENT
specifier|final
name|String
name|expected0
init|=
literal|""
operator|+
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
literal|"describe statement select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|""
operator|+
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`\n"
operator|+
literal|"ORDER BY 2)"
decl_stmt|;
name|sql
argument_list|(
literal|"describe statement select * from emps order by 2"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe (select * from emps)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe statement (select * from emps)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|""
operator|+
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"(SELECT `DEPTNO`\n"
operator|+
literal|"FROM `EMPS`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT `DEPTNO`\n"
operator|+
literal|"FROM `DEPTS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"describe select deptno from emps union select deptno from depts"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|""
operator|+
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES WITH IMPLEMENTATION FOR\n"
operator|+
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"VALUES (ROW(1, 'a'))"
decl_stmt|;
name|sql
argument_list|(
literal|"describe insert into emps values (1, 'a')"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
comment|// only allow query or DML, not explain, inside describe
name|sql
argument_list|(
literal|"describe ^explain^ plan for select * from emps"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"explain\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"describe statement ^explain^ plan for select * from emps"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"explain\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelectIsNotDdl
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from t"
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertSelect
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertUnion
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS1`\n"
operator|+
literal|"UNION\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMPS2`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps select * from emps1 union select * from emps2"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertValues
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"VALUES (ROW(1, 'Fredkin'))"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps values (1,'Fredkin')"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertValuesDefault
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"VALUES (ROW(1, DEFAULT, 'Fredkin'))"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps values (1,DEFAULT,'Fredkin')"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertValuesRawDefault
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"VALUES (ROW(DEFAULT))"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps values ^default^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"default\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emps values (default)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertColumnList
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS` (`X`, `Y`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps(x,y) select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertCaseSensitiveColumnList
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `emps` (`x`, `y`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into \"emps\"(\"x\",\"y\") select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertExtendedColumnList
parameter_list|()
block|{
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS` EXTEND (`Z` BOOLEAN) (`X`, `Y`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into emps(z boolean)(x,y) select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|LENIENT
expr_stmt|;
name|expected
operator|=
literal|"INSERT INTO `EMPS` EXTEND (`Z` BOOLEAN) (`X`, `Y`, `Z`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emps(x, y, z boolean) select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnList
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"UPDATE `EMPDEFAULTS` EXTEND (`EXTRA` BOOLEAN, `NOTE` VARCHAR)"
operator|+
literal|" SET `DEPTNO` = 1"
operator|+
literal|", `EXTRA` = TRUE"
operator|+
literal|", `EMPNO` = 20"
operator|+
literal|", `ENAME` = 'Bob'"
operator|+
literal|", `NOTE` = 'legion'\n"
operator|+
literal|"WHERE (`DEPTNO` = 10)"
decl_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(extra BOOLEAN, note VARCHAR)"
operator|+
literal|" set deptno = 1, extra = true, empno = 20, ename = 'Bob', note = 'legion'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpdateCaseSensitiveExtendedColumnList
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"UPDATE `EMPDEFAULTS` EXTEND (`extra` BOOLEAN, `NOTE` VARCHAR)"
operator|+
literal|" SET `DEPTNO` = 1"
operator|+
literal|", `extra` = TRUE"
operator|+
literal|", `EMPNO` = 20"
operator|+
literal|", `ENAME` = 'Bob'"
operator|+
literal|", `NOTE` = 'legion'\n"
operator|+
literal|"WHERE (`DEPTNO` = 10)"
decl_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(\"extra\" BOOLEAN, note VARCHAR)"
operator|+
literal|" set deptno = 1, \"extra\" = true, empno = 20, ename = 'Bob', note = 'legion'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInsertCaseSensitiveExtendedColumnList
parameter_list|()
block|{
name|String
name|expected
init|=
literal|"INSERT INTO `emps` EXTEND (`z` BOOLEAN) (`x`, `y`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
literal|"insert into \"emps\"(\"z\" boolean)(\"x\",\"y\") select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|LENIENT
expr_stmt|;
name|expected
operator|=
literal|"INSERT INTO `emps` EXTEND (`z` BOOLEAN) (`x`, `y`, `z`)\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
expr_stmt|;
name|sql
argument_list|(
literal|"insert into \"emps\"(\"x\", \"y\", \"z\" boolean) select * from emps"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplainInsert
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES"
operator|+
literal|" WITH IMPLEMENTATION FOR\n"
operator|+
literal|"INSERT INTO `EMPS1`\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS2`)"
decl_stmt|;
name|sql
argument_list|(
literal|"explain plan for insert into emps1 select * from emps2"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpsertValues
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"UPSERT INTO `EMPS`\n"
operator|+
literal|"VALUES (ROW(1, 'Fredkin'))"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"upsert into emps values (1,'Fredkin')"
decl_stmt|;
if|if
condition|(
name|isReserved
argument_list|(
literal|"UPSERT"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testUpsertSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"upsert into emps select * from emp as e"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"UPSERT INTO `EMPS`\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMP` AS `E`)"
decl_stmt|;
if|if
condition|(
name|isReserved
argument_list|(
literal|"UPSERT"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testExplainUpsert
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"explain plan for upsert into emps1 values (1, 2)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"EXPLAIN PLAN INCLUDING ATTRIBUTES"
operator|+
literal|" WITH IMPLEMENTATION FOR\n"
operator|+
literal|"UPSERT INTO `EMPS1`\n"
operator|+
literal|"VALUES (ROW(1, 2))"
decl_stmt|;
if|if
condition|(
name|isReserved
argument_list|(
literal|"UPSERT"
argument_list|)
condition|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testDelete
parameter_list|()
block|{
name|sql
argument_list|(
literal|"delete from emps"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DELETE FROM `EMPS`"
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDeleteWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"delete from emps where empno=12"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DELETE FROM `EMPS`\n"
operator|+
literal|"WHERE (`EMPNO` = 12)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpdate
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update emps set empno = empno + 1, sal = sal - 1 where empno=12"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"UPDATE `EMPS` SET `EMPNO` = (`EMPNO` + 1)"
operator|+
literal|", `SAL` = (`SAL` - 1)\n"
operator|+
literal|"WHERE (`EMPNO` = 12)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeSelectSource
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emps e "
operator|+
literal|"using (select * from tempemps where deptno is null) t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set name = t.name, deptno = t.deptno, salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"MERGE INTO `EMPS` AS `E`\n"
operator|+
literal|"USING (SELECT *\n"
operator|+
literal|"FROM `TEMPEMPS`\n"
operator|+
literal|"WHERE (`DEPTNO` IS NULL)) AS `T`\n"
operator|+
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)\n"
operator|+
literal|"WHEN MATCHED THEN UPDATE SET `NAME` = `T`.`NAME`"
operator|+
literal|", `DEPTNO` = `T`.`DEPTNO`"
operator|+
literal|", `SALARY` = (`T`.`SALARY` * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Same as testMergeSelectSource but set with compound identifier. */
annotation|@
name|Test
name|void
name|testMergeSelectSource2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emps e "
operator|+
literal|"using (select * from tempemps where deptno is null) t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set e.name = t.name, e.deptno = t.deptno, e.salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"MERGE INTO `EMPS` AS `E`\n"
operator|+
literal|"USING (SELECT *\n"
operator|+
literal|"FROM `TEMPEMPS`\n"
operator|+
literal|"WHERE (`DEPTNO` IS NULL)) AS `T`\n"
operator|+
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)\n"
operator|+
literal|"WHEN MATCHED THEN UPDATE SET `E`.`NAME` = `T`.`NAME`"
operator|+
literal|", `E`.`DEPTNO` = `T`.`DEPTNO`"
operator|+
literal|", `E`.`SALARY` = (`T`.`SALARY` * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
operator|.
name|node
argument_list|(
name|not
argument_list|(
name|isDdl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMergeTableRefSource
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emps e "
operator|+
literal|"using tempemps as t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set name = t.name, deptno = t.deptno, salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"MERGE INTO `EMPS` AS `E`\n"
operator|+
literal|"USING `TEMPEMPS` AS `T`\n"
operator|+
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)\n"
operator|+
literal|"WHEN MATCHED THEN UPDATE SET `NAME` = `T`.`NAME`"
operator|+
literal|", `DEPTNO` = `T`.`DEPTNO`"
operator|+
literal|", `SALARY` = (`T`.`SALARY` * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Same with testMergeTableRefSource but set with compound identifier. */
annotation|@
name|Test
name|void
name|testMergeTableRefSource2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emps e "
operator|+
literal|"using tempemps as t "
operator|+
literal|"on e.empno = t.empno "
operator|+
literal|"when matched then update "
operator|+
literal|"set e.name = t.name, e.deptno = t.deptno, e.salary = t.salary * .1 "
operator|+
literal|"when not matched then insert (name, dept, salary) "
operator|+
literal|"values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"MERGE INTO `EMPS` AS `E`\n"
operator|+
literal|"USING `TEMPEMPS` AS `T`\n"
operator|+
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)\n"
operator|+
literal|"WHEN MATCHED THEN UPDATE SET `E`.`NAME` = `T`.`NAME`"
operator|+
literal|", `E`.`DEPTNO` = `T`.`DEPTNO`"
operator|+
literal|", `E`.`SALARY` = (`T`.`SALARY` * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBitStringNotImplemented
parameter_list|()
block|{
comment|// Bit-string is longer part of the SQL standard. We do not support it.
name|sql
argument_list|(
literal|"select (B^'1011'^ || 'foobar') from (values (true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\\\'1011\\\\'\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHexAndBinaryString
parameter_list|()
block|{
name|expr
argument_list|(
literal|"x''=X'2'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(X'' = X'2')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'fffff'=X''"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(X'FFFFF' = X'')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'1' \t\t\f\r\n"
operator|+
literal|"'2'--hi this is a comment'FF'\r\r\t\f\n"
operator|+
literal|"'34'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"X'1'\n'2'\n'34'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'1' \t\t\f\r\n"
operator|+
literal|"'000'--\n"
operator|+
literal|"'01'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"X'1'\n'000'\n'01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'1234567890abcdef'=X'fFeEdDcCbBaA'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(X'1234567890ABCDEF' = X'FFEEDDCCBBAA')"
argument_list|)
expr_stmt|;
comment|// Check the inital zeroes don't get trimmed somehow
name|expr
argument_list|(
literal|"x'001'=X'000102'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(X'001' = X'000102')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHexAndBinaryStringFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^x'FeedGoats'^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^x'abcdefG'^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Binary literal string must contain only characters '0' - '9', 'A' - 'F'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select x'1' ^x'2'^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered .x.*2.* at line 1, column 13.*"
argument_list|)
expr_stmt|;
comment|// valid syntax, but should fail in the validator
name|sql
argument_list|(
literal|"select x'1' '2' from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT X'1'\n"
operator|+
literal|"'2'\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringLiteral
parameter_list|()
block|{
name|expr
argument_list|(
literal|"_latin1'hi'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_LATIN1'hi'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"N'is it a plane? no it''s superman!'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'is it a plane? no it''s superman!'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"n'lowercase n'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'lowercase n'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'boring string'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'boring string'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_iSo-8859-1'bye'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'bye'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'three'\n' blind'\n' mice'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'three'\n' blind'\n' mice'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'three' -- comment\n' blind'\n' mice'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'three'\n' blind'\n' mice'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"N'bye' \t\r\f\f\n' bye'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'bye'\n' bye'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_iso-8859-1'bye'\n\n--\n-- this is a comment\n' bye'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_ISO-8859-1'bye'\n' bye'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_utf8'hi'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"_UTF8'hi'"
argument_list|)
expr_stmt|;
comment|// newline in string literal
name|expr
argument_list|(
literal|"'foo\rbar'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'foo\rbar'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'foo\nbar'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'foo\nbar'"
argument_list|)
expr_stmt|;
comment|// prevent test infrastructure from converting '\r\n' to '\n'
name|boolean
index|[]
name|linuxify
init|=
name|LINUXIFY
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|linuxify
index|[
literal|0
index|]
operator|=
literal|false
expr_stmt|;
name|expr
argument_list|(
literal|"'foo\r\nbar'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'foo\r\nbar'"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|linuxify
index|[
literal|0
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testStringLiteralFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select (N ^'space'^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered .*space.* at line 1, column ...*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select (_latin1\n^'newline'^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered.*newline.* at line 2, column ...*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^_unknown-charset''^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown character set 'unknown-charset'"
argument_list|)
expr_stmt|;
comment|// valid syntax, but should give a validator error
name|sql
argument_list|(
literal|"select (N'1' '2') from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT _ISO-8859-1'1'\n"
operator|+
literal|"'2'\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringLiteralChain
parameter_list|()
block|{
specifier|final
name|String
name|fooBar
init|=
literal|"'foo'\n"
operator|+
literal|"'bar'"
decl_stmt|;
specifier|final
name|String
name|fooBarBaz
init|=
literal|"'foo'\n"
operator|+
literal|"'bar'\n"
operator|+
literal|"'baz'"
decl_stmt|;
name|expr
argument_list|(
literal|"   'foo'\r'bar'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBar
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"   'foo'\r\n'bar'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBar
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"   'foo'\r\n\r\n'bar'\n'baz'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBarBaz
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"   'foo' /* a comment */ 'bar'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBar
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"   'foo' -- a comment\r\n 'bar'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBar
argument_list|)
expr_stmt|;
comment|// String literals not separated by comment or newline are OK in
comment|// parser, should fail in validator.
name|expr
argument_list|(
literal|"   'foo' 'bar'"
argument_list|)
operator|.
name|ok
argument_list|(
name|fooBar
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringLiteralDoubleQuoted
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select `deptno` as d, ^\"^deptno\" as d2 from emp"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\\\\"\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT deptno AS d, 'deptno' AS d2\n"
operator|+
literal|"FROM emp"
argument_list|)
expr_stmt|;
comment|// MySQL uses single-quotes as escapes; BigQuery uses backslashes
name|sql
argument_list|(
literal|"select 'Let''s call him \"Elvis\"!'"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|node
argument_list|(
name|isCharLiteral
argument_list|(
literal|"Let's call him \"Elvis\"!"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 'Let\\'\\'s call him \"Elvis\"!'"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|node
argument_list|(
name|isCharLiteral
argument_list|(
literal|"Let''s call him \"Elvis\"!"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 'Let\\'s ^call^ him \"Elvis\"!'"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"call\" at .*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|node
argument_list|(
name|isCharLiteral
argument_list|(
literal|"Let's call him \"Elvis\"!"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Oracle uses double-quotes as escapes in identifiers;
comment|// BigQuery uses backslashes as escapes in double-quoted character literals.
name|sql
argument_list|(
literal|"select \"Let's call him \\\"Elvis^\\^\"!\""
argument_list|)
operator|.
name|withDialect
argument_list|(
name|ORACLE
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Lexical error at line 1, column 31\\.  "
operator|+
literal|"Encountered: \"\\\\\\\\\" \\(92\\), after : \"\".*"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|node
argument_list|(
name|isCharLiteral
argument_list|(
literal|"Let's call him \"Elvis\"!"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|isCharLiteral
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|CustomTypeSafeMatcher
argument_list|<
name|SqlNode
argument_list|>
argument_list|(
name|s
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|SqlNode
name|item
parameter_list|)
block|{
specifier|final
name|SqlNodeList
name|selectList
decl_stmt|;
return|return
name|item
operator|instanceof
name|SqlSelect
operator|&&
operator|(
name|selectList
operator|=
operator|(
operator|(
name|SqlSelect
operator|)
name|item
operator|)
operator|.
name|getSelectList
argument_list|()
operator|)
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|selectList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|SqlLiteral
operator|&&
operator|(
operator|(
name|SqlLiteral
operator|)
name|selectList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValueAs
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|equals
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCaseExpression
parameter_list|()
block|{
comment|// implicit simple "ELSE NULL" case
name|expr
argument_list|(
literal|"case \t col1 when 1 then 'one' end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (`COL1` = 1) THEN 'one' ELSE NULL END)"
argument_list|)
expr_stmt|;
comment|// implicit searched "ELSE NULL" case
name|expr
argument_list|(
literal|"case when nbr is false then 'one' end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (`NBR` IS FALSE) THEN 'one' ELSE NULL END)"
argument_list|)
expr_stmt|;
comment|// multiple WHENs
name|expr
argument_list|(
literal|"case col1 when\n1.2 then 'one' when 2 then 'two' else 'three' end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (`COL1` = 1.2) THEN 'one' WHEN (`COL1` = 2) THEN 'two' ELSE 'three' END)"
argument_list|)
expr_stmt|;
comment|// sub-queries as case expression operands
name|expr
argument_list|(
literal|"case (select * from emp) when 1 then 2 end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN ((SELECT *\n"
operator|+
literal|"FROM `EMP`) = 1) THEN 2 ELSE NULL END)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when (select * from emp) then 2 end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (1 = (SELECT *\n"
operator|+
literal|"FROM `EMP`)) THEN 2 ELSE NULL END)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 2 then (select * from emp) end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (1 = 2) THEN (SELECT *\n"
operator|+
literal|"FROM `EMP`) ELSE NULL END)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 2 then 3 else (select * from emp) end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (1 = 2) THEN 3 ELSE (SELECT *\n"
operator|+
literal|"FROM `EMP`) END)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case x when 2, 4 then 3 else 4 end"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(CASE WHEN (`X` IN (2, 4)) THEN 3 ELSE 4 END)"
argument_list|)
expr_stmt|;
comment|// comma-list must not be empty
name|sql
argument_list|(
literal|"case x when 2, 4 then 3 when ^then^ 5 else 4 end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"then\" at .*"
argument_list|)
expr_stmt|;
comment|// commas not allowed in boolean case
name|sql
argument_list|(
literal|"case when b1, b2 ^when^ 2, 4 then 3 else 4 end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"when\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCaseExpressionFails
parameter_list|()
block|{
comment|// Missing 'END'
name|sql
argument_list|(
literal|"select case col1 when 1 then 'one' ^from^ t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*from.*"
argument_list|)
expr_stmt|;
comment|// Wrong 'WHEN'
name|sql
argument_list|(
literal|"select case col1 ^when1^ then 'one' end from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*when1.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNullIf
parameter_list|()
block|{
name|expr
argument_list|(
literal|"nullif(v1,v2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"NULLIF(`V1`, `V2`)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isReserved
argument_list|(
literal|"NULLIF"
argument_list|)
condition|)
block|{
name|expr
argument_list|(
literal|"1 + ^nullif^ + 3"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"nullif \\+\" at line 1, column 5.*"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testCoalesce
parameter_list|()
block|{
name|expr
argument_list|(
literal|"coalesce(v1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COALESCE(`V1`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"coalesce(v1,v2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COALESCE(`V1`, `V2`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"coalesce(v1,v2,v3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"COALESCE(`V1`, `V2`, `V3`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLiteralCollate
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|FRG78_FIXED
condition|)
block|{
return|return;
block|}
name|expr
argument_list|(
literal|"'string' collate latin1$sv_SE$mega_strength"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'string' COLLATE ISO-8859-1$sv_SE$mega_strength"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a long '\n'string' collate latin1$sv_SE$mega_strength"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"'a long ' 'string' COLLATE ISO-8859-1$sv_SE$mega_strength"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x collate iso-8859-6$ar_LB$1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`X` COLLATE ISO-8859-6$ar_LB$1"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x.y.z collate shift_jis$ja_JP$2"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`X`.`Y`.`Z` COLLATE SHIFT_JIS$ja_JP$2"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'str1'='str2' collate latin1$sv_SE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('str1' = 'str2' COLLATE ISO-8859-1$sv_SE$primary)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'str1' collate latin1$sv_SE>'str2'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('str1' COLLATE ISO-8859-1$sv_SE$primary> 'str2')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'str1' collate latin1$sv_SE<='str2' collate latin1$sv_FI"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('str1' COLLATE ISO-8859-1$sv_SE$primary<= 'str2' COLLATE ISO-8859-1$sv_FI$primary)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCharLength
parameter_list|()
block|{
name|expr
argument_list|(
literal|"char_length('string')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CHAR_LENGTH('string')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"character_length('string')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CHARACTER_LENGTH('string')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPosition
parameter_list|()
block|{
name|expr
argument_list|(
literal|"posiTion('mouse' in 'house')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"POSITION('mouse' IN 'house')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testReplace
parameter_list|()
block|{
name|expr
argument_list|(
literal|"replace('x', 'y', 'z')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"REPLACE('x', 'y', 'z')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDateLiteral
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT DATE '1980-01-01'\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select date '1980-01-01' from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT TIME '00:00:00'\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select time '00:00:00' from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT TIMESTAMP '1980-01-01 00:00:00'\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select timestamp '1980-01-01 00:00:00' from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT INTERVAL '3' DAY\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select interval '3' day from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected4
init|=
literal|"SELECT INTERVAL '5:6' HOUR TO MINUTE\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select interval '5:6' hour to minute from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected4
argument_list|)
expr_stmt|;
block|}
comment|// check date/time functions.
annotation|@
name|Test
name|void
name|testTimeDate
parameter_list|()
block|{
comment|// CURRENT_TIME - returns time w/ timezone
name|expr
argument_list|(
literal|"CURRENT_TIME(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIME(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_TIME() FROM foo",
comment|//     "SELECT CURRENT_TIME() FROM `FOO`");
name|expr
argument_list|(
literal|"CURRENT_TIME"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIME"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CURRENT_TIME(x+y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIME((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// LOCALTIME returns time w/o TZ
name|expr
argument_list|(
literal|"LOCALTIME(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIME(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT LOCALTIME() FROM foo",
comment|//     "SELECT LOCALTIME() FROM `FOO`");
name|expr
argument_list|(
literal|"LOCALTIME"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIME"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"LOCALTIME(x+y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIME((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// LOCALTIMESTAMP - returns timestamp w/o TZ
name|expr
argument_list|(
literal|"LOCALTIMESTAMP(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIMESTAMP(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT LOCALTIMESTAMP() FROM foo",
comment|//     "SELECT LOCALTIMESTAMP() FROM `FOO`");
name|expr
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"LOCALTIMESTAMP(x+y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"LOCALTIMESTAMP((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// CURRENT_DATE - returns DATE
name|expr
argument_list|(
literal|"CURRENT_DATE(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_DATE(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_DATE() FROM foo",
comment|//     "SELECT CURRENT_DATE() FROM `FOO`");
name|expr
argument_list|(
literal|"CURRENT_DATE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_DATE"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_DATE(x+y) FROM foo",
comment|//     "CURRENT_DATE((`X` + `Y`))");
comment|// CURRENT_TIMESTAMP - returns timestamp w/ TZ
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIMESTAMP(3)"
argument_list|)
expr_stmt|;
comment|// checkFails("SELECT CURRENT_TIMESTAMP() FROM foo",
comment|//     "SELECT CURRENT_TIMESTAMP() FROM `FOO`");
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP(x+y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CURRENT_TIMESTAMP((`X` + `Y`))"
argument_list|)
expr_stmt|;
comment|// Date literals
name|expr
argument_list|(
literal|"DATE '2004-12-01'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DATE '2004-12-01'"
argument_list|)
expr_stmt|;
comment|// Time literals
name|expr
argument_list|(
literal|"TIME '12:01:01'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '12:01:01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01.'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '12:01:01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01.000'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '12:01:01.000'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01.001'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '12:01:01.001'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01.01023456789'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIME '12:01:01.01023456789'"
argument_list|)
expr_stmt|;
comment|// Timestamp literals
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.1'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.1'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP  '2004-12-01 12:01:01.010234567890'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.010234567890'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.01023456789'"
argument_list|)
operator|.
name|same
argument_list|()
expr_stmt|;
comment|// Failures.
name|sql
argument_list|(
literal|"^DATE '12/21/99'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal DATE literal.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^TIME '1230:33'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^TIME '12:00:00 PM'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^TIMESTAMP '12-21-99, 12:30:00'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIMESTAMP literal.*"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests for casting to/from date/time types.    */
annotation|@
name|Test
name|void
name|testDateTimeCast
parameter_list|()
block|{
comment|//   checkExp("CAST(DATE '2001-12-21' AS CHARACTER VARYING)",
comment|// "CAST(2001-12-21)");
name|expr
argument_list|(
literal|"CAST('2001-12-21' AS DATE)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST('2001-12-21' AS DATE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CAST(12 AS DATE)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(12 AS DATE)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"CAST('2000-12-21' AS DATE ^NOT^ NULL)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"NOT\" at line 1, column 27.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"CAST('foo' as ^1^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"1\" at line 1, column 15.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"Cast(DATE '2004-12-21' AS VARCHAR(10))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(DATE '2004-12-21' AS VARCHAR(10))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTrim
parameter_list|()
block|{
name|expr
argument_list|(
literal|"trim('mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(BOTH 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim('mustache')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(BOTH ' ' FROM 'mustache')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim(TRAILING FROM 'mustache')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(TRAILING ' ' FROM 'mustache')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim(bOth 'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(BOTH 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim( lEaDing       'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(LEADING 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim(\r\n\ttrailing\n  'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(TRAILING 'mustache' FROM 'beard')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim (coalesce(cast(null as varchar(2)))||"
operator|+
literal|"' '||coalesce('junk ',''))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRIM(BOTH ' ' FROM ((COALESCE(CAST(NULL AS VARCHAR(2))) || "
operator|+
literal|"' ') || COALESCE('junk ', '')))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"trim(^from^ 'beard')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*'FROM' without operands preceding it is illegal.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConvertAndTranslate
parameter_list|()
block|{
name|expr
argument_list|(
literal|"convert('abc' using conversion)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CONVERT('abc' USING `CONVERSION`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"translate('abc' using lazy_translation)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRANSLATE('abc' USING `LAZY_TRANSLATION`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTranslate3
parameter_list|()
block|{
name|expr
argument_list|(
literal|"translate('aaabbbccc', 'ab', '+-')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"TRANSLATE('aaabbbccc', 'ab', '+-')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverlay
parameter_list|()
block|{
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"OVERLAY('ABCdef' PLACING 'abc' FROM 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for 3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"OVERLAY('ABCdef' PLACING 'abc' FROM 1 FOR 3)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJdbcFunctionCall
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{fn apa(1,'1')}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn APA(1, '1') }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{ Fn apa(log10(ln(1))+2)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn APA((LOG10(LN(1)) + 2)) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fN apa(*)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn APA(*) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{   FN\t\r\n apa()}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn APA() }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn insert()}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn INSERT() }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(foo, SQL_VARCHAR)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(`FOO`, SQL_VARCHAR) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(log10(100), integer)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(LOG10(100), SQL_INTEGER) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, SQL_INTERVAL_YEAR)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_INTERVAL_YEAR) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, SQL_INTERVAL_YEAR_TO_MONTH)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_INTERVAL_YEAR_TO_MONTH) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, ^sql_interval_year_to_day^)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"sql_interval_year_to_day\" at line 1, column 16\\.\n.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, sql_interval_day)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_INTERVAL_DAY) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, sql_interval_day_to_minute)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_INTERVAL_DAY_TO_MINUTE) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(^)^}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\)\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(\"123\", SMALLINT^(^3)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\(\" at.*"
argument_list|)
expr_stmt|;
comment|// Regular types (without SQL_) are OK for regular types, but not for
comment|// intervals.
name|expr
argument_list|(
literal|"{fn convert(1, INTEGER)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_INTEGER) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, VARCHAR)}"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"{fn CONVERT(1, SQL_VARCHAR) }"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, VARCHAR^(^5))}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\(\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, ^INTERVAL^ YEAR TO MONTH)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"INTERVAL\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn convert(1, ^INTERVAL^ YEAR)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"INTERVAL\" at.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWindowReference
parameter_list|()
block|{
name|expr
argument_list|(
literal|"sum(sal) over (w)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (`W`))"
argument_list|)
expr_stmt|;
comment|// Only 1 window reference allowed
name|expr
argument_list|(
literal|"sum(sal) over (w ^w1^ partition by deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"w1\" at.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWindowInSubQuery
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|" select sum(x) over w, sum(y) over w\n"
operator|+
literal|" from s\n"
operator|+
literal|" window w as (range interval '1' minute preceding))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT (SUM(`X`) OVER `W`), (SUM(`Y`) OVER `W`)\n"
operator|+
literal|"FROM `S`\n"
operator|+
literal|"WINDOW `W` AS (RANGE INTERVAL '1' MINUTE PRECEDING))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWindowSpec
parameter_list|()
block|{
comment|// Correct syntax
specifier|final
name|String
name|sql1
init|=
literal|"select count(z) over w as foo\n"
operator|+
literal|"from Bids\n"
operator|+
literal|"window w as (partition by y + yy, yyy\n"
operator|+
literal|" order by x\n"
operator|+
literal|" rows between 2 preceding and 2 following)"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT (COUNT(`Z`) OVER `W`) AS `FOO`\n"
operator|+
literal|"FROM `BIDS`\n"
operator|+
literal|"WINDOW `W` AS (PARTITION BY (`Y` + `YY`), `YYY` ORDER BY `X` ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING)"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select count(*) over w\n"
operator|+
literal|"from emp window w as (rows 2 preceding)"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT (COUNT(*) OVER `W`)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WINDOW `W` AS (ROWS 2 PRECEDING)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
comment|// Chained string literals are valid syntax. They are unlikely to be
comment|// semantically valid, because intervals are usually numeric or
comment|// datetime.
comment|// Note: literal chain is not yet replaced with combined literal
comment|// since we are just parsing, and not validating the sql.
specifier|final
name|String
name|sql3
init|=
literal|"select count(*) over w from emp window w as (\n"
operator|+
literal|"  rows 'foo' 'bar'\n"
operator|+
literal|"       'baz' preceding)"
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT (COUNT(*) OVER `W`)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"WINDOW `W` AS (ROWS 'foo'\n'bar'\n'baz' PRECEDING)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
comment|// Partition clause out of place. Found after ORDER BY
specifier|final
name|String
name|sql4
init|=
literal|"select count(z) over w as foo\n"
operator|+
literal|"from Bids\n"
operator|+
literal|"window w as (partition by y order by x ^partition^ by y)"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"partition\".*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql5
init|=
literal|"select count(z) over w as foo\n"
operator|+
literal|"from Bids window w as (order by x ^partition^ by y)"
decl_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"partition\".*"
argument_list|)
expr_stmt|;
comment|// Cannot partition by sub-query
name|sql
argument_list|(
literal|"select sum(a) over (partition by ^(^select 1 from t), x) from t2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Query expression encountered in illegal context"
argument_list|)
expr_stmt|;
comment|// AND is required in BETWEEN clause of window frame
specifier|final
name|String
name|sql7
init|=
literal|"select sum(x) over\n"
operator|+
literal|" (order by x range between unbounded preceding ^unbounded^ following)"
decl_stmt|;
name|sql
argument_list|(
name|sql7
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"unbounded\".*"
argument_list|)
expr_stmt|;
comment|// WINDOW keyword is not permissible.
name|sql
argument_list|(
literal|"select sum(x) over ^window^ (order by x) from bids"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"window\".*"
argument_list|)
expr_stmt|;
comment|// ORDER BY must be before Frame spec
name|sql
argument_list|(
literal|"select sum(x) over (rows 2 preceding ^order^ by x) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"order\".*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWindowSpecPartial
parameter_list|()
block|{
comment|// ALLOW PARTIAL is the default, and is omitted when the statement is
comment|// unparsed.
name|sql
argument_list|(
literal|"select sum(x) over (order by x allow partial) from bids"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(x) over (order by x) from bids"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(x) over (order by x disallow partial) from bids"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X` DISALLOW PARTIAL))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(x) over (order by x) from bids"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (SUM(`X`) OVER (ORDER BY `X`))\n"
operator|+
literal|"FROM `BIDS`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNullTreatment
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select lead(x) respect nulls over (w) from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (LEAD(`X`) RESPECT NULLS OVER (`W`))\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, sum(sal) respect nulls from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`, SUM(`SAL`) RESPECT NULLS\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, sum(sal) ignore nulls from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `DEPTNO`, SUM(`SAL`) IGNORE NULLS\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select col1,\n"
operator|+
literal|" collect(col2) ignore nulls\n"
operator|+
literal|"   within group (order by col3)\n"
operator|+
literal|"   filter (where 1 = 0)\n"
operator|+
literal|"   over (rows 10 preceding)\n"
operator|+
literal|" as c\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by col1 limit 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `COL1`, (COLLECT(`COL2`) IGNORE NULLS"
operator|+
literal|" WITHIN GROUP (ORDER BY `COL3`)"
operator|+
literal|" FILTER (WHERE (1 = 0)) OVER (ROWS 10 PRECEDING)) AS `C`\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `COL1`\n"
operator|+
literal|"FETCH NEXT 10 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// See [CALCITE-2993] ParseException may be thrown for legal
comment|// SQL queries due to incorrect "LOOKAHEAD(1)" hints
name|sql
argument_list|(
literal|"select lead(x) ignore from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT LEAD(`X`) AS `IGNORE`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select lead(x) respect from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT LEAD(`X`) AS `RESPECT`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAs
parameter_list|()
block|{
comment|// AS is optional for column aliases
name|sql
argument_list|(
literal|"select x y from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `X` AS `Y`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select x AS y from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `X` AS `Y`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(x) y from t group by z"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT SUM(`X`) AS `Y`\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"GROUP BY `Z`"
argument_list|)
expr_stmt|;
comment|// Even after OVER
name|sql
argument_list|(
literal|"select count(z) over w foo from Bids window w as (order by x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (COUNT(`Z`) OVER `W`) AS `FOO`\n"
operator|+
literal|"FROM `BIDS`\n"
operator|+
literal|"WINDOW `W` AS (ORDER BY `X`)"
argument_list|)
expr_stmt|;
comment|// AS is optional for table correlation names
specifier|final
name|String
name|expected
init|=
literal|"SELECT `X`\n"
operator|+
literal|"FROM `T` AS `T1`"
decl_stmt|;
name|sql
argument_list|(
literal|"select x from t as t1"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select x from t t1"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// AS is required in WINDOW declaration
name|sql
argument_list|(
literal|"select sum(x) over w from bids window w ^(order by x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\(\".*"
argument_list|)
expr_stmt|;
comment|// Error if OVER and AS are in wrong order
name|sql
argument_list|(
literal|"select count(*) as foo ^over^ w from Bids window w (order by x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"over\".*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAsAliases
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select x from t as t1 (a, b) where foo"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `X`\n"
operator|+
literal|"FROM `T` AS `T1` (`A`, `B`)\n"
operator|+
literal|"WHERE `FOO`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select x from (values (1, 2), (3, 4)) as t1 (\"a\", b) where \"a\"> b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `X`\n"
operator|+
literal|"FROM (VALUES (ROW(1, 2)),\n"
operator|+
literal|"(ROW(3, 4))) AS `T1` (`a`, `B`)\n"
operator|+
literal|"WHERE (`a`> `B`)"
argument_list|)
expr_stmt|;
comment|// must have at least one column
name|sql
argument_list|(
literal|"select x from (values (1, 2), (3, 4)) as t1 (^)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\)\" at .*"
argument_list|)
expr_stmt|;
comment|// cannot have expressions
name|sql
argument_list|(
literal|"select x from t as t1 (x ^+^ y)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Was expecting one of:\n"
operator|+
literal|"    \"\\)\" \\.\\.\\.\n"
operator|+
literal|"    \",\" \\.\\.\\..*"
argument_list|)
expr_stmt|;
comment|// cannot have compound identifiers
name|sql
argument_list|(
literal|"select x from t as t1 (x^.^y)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Was expecting one of:\n"
operator|+
literal|"    \"\\)\" \\.\\.\\.\n"
operator|+
literal|"    \",\" \\.\\.\\..*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOver
parameter_list|()
block|{
name|expr
argument_list|(
literal|"sum(sal) over ()"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER ())"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (partition by x, y)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (PARTITION BY `X`, `Y`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (order by x desc, y asc)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (ORDER BY `X` DESC, `Y`))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (rows 5 preceding)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (ROWS 5 PRECEDING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between interval '1' second preceding\n"
operator|+
literal|" and interval '1' second following)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1' SECOND PRECEDING "
operator|+
literal|"AND INTERVAL '1' SECOND FOLLOWING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between interval '1:03' hour preceding\n"
operator|+
literal|" and interval '2' minute following)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1:03' HOUR PRECEDING "
operator|+
literal|"AND INTERVAL '2' MINUTE FOLLOWING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between interval '5' day preceding\n"
operator|+
literal|" and current row)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '5' DAY PRECEDING "
operator|+
literal|"AND CURRENT ROW))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range interval '5' day preceding)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE INTERVAL '5' DAY PRECEDING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between unbounded preceding and current row)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN UNBOUNDED PRECEDING "
operator|+
literal|"AND CURRENT ROW))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range unbounded preceding)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE UNBOUNDED PRECEDING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between current row and unbounded preceding)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN CURRENT ROW "
operator|+
literal|"AND UNBOUNDED PRECEDING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between current row and unbounded following)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN CURRENT ROW "
operator|+
literal|"AND UNBOUNDED FOLLOWING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between 6 preceding\n"
operator|+
literal|" and interval '1:03' hour preceding)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN 6 PRECEDING "
operator|+
literal|"AND INTERVAL '1:03' HOUR PRECEDING))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"sum(sal) over (range between interval '1' second following\n"
operator|+
literal|" and interval '5' day following)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(SUM(`SAL`) OVER (RANGE BETWEEN INTERVAL '1' SECOND FOLLOWING "
operator|+
literal|"AND INTERVAL '5' DAY FOLLOWING))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testElementFunc
parameter_list|()
block|{
name|expr
argument_list|(
literal|"element(a)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ELEMENT(`A`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCardinalityFunc
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cardinality(a)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CARDINALITY(`A`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMemberOf
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a member of b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MEMBER OF `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a member of multiset[b]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MEMBER OF (MULTISET[`B`]))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubMultisetrOf
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a submultiset of b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` SUBMULTISET OF `B`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsASet
parameter_list|()
block|{
name|expr
argument_list|(
literal|"b is a set"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`B` IS A SET)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a is a set"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` IS A SET)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultiset
parameter_list|()
block|{
name|expr
argument_list|(
literal|"multiset[1]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET[1])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET[1, 2.3])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1,    '2']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET[1, '2'])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2)]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET[(ROW(1, 2))])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2),ROW(3,4)]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET[(ROW(1, 2)), (ROW(3, 4))])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset(select*from T)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MULTISET ((SELECT *\n"
operator|+
literal|"FROM `T`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetUnion
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a multiset union b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET UNION ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset union all b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET UNION ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset union distinct b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET UNION DISTINCT `B`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetExcept
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a multiset EXCEPT b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET EXCEPT ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset EXCEPT all b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET EXCEPT ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset EXCEPT distinct b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET EXCEPT DISTINCT `B`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetIntersect
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a multiset INTERSECT b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET INTERSECT ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset INTERSECT all b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET INTERSECT ALL `B`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a multiset INTERSECT distinct b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(`A` MULTISET INTERSECT DISTINCT `B`)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetMixed
parameter_list|()
block|{
name|expr
argument_list|(
literal|"multiset[1] MULTISET union b"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((MULTISET[1]) MULTISET UNION ALL `B`)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"a MULTISET union b "
operator|+
literal|"multiset intersect c "
operator|+
literal|"multiset except d "
operator|+
literal|"multiset union e"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(((`A` MULTISET UNION ALL "
operator|+
literal|"(`B` MULTISET INTERSECT ALL `C`)) "
operator|+
literal|"MULTISET EXCEPT ALL `D`) MULTISET UNION ALL `E`)"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMapItem
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a['foo']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`['foo']"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a['x' || 'y']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`[('x' || 'y')]"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a['foo'] ['bar']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`['foo']['bar']"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a['foo']['bar']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`['foo']['bar']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMapItemPrecedence
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1 + a['foo'] * 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + (`A`['foo'] * 3))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 * a['foo'] + 3"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((1 * `A`['foo']) + 3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a['foo']['bar']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`['foo']['bar']"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a[b['foo' || 'bar']]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`[`B`[('foo' || 'bar')]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayElement
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a[1]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`[1]"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a[b[1]]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`[`B`[1]]"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a[b[1 + 2] + 3]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`A`[(`B`[(1 + 2)] + 3)]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayElementWithDot
parameter_list|()
block|{
name|expr
argument_list|(
literal|"a[1+2].b.c[2].d"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(((`A`[(1 + 2)].`B`).`C`)[2].`D`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"a[b[1]].c.f0[d[1]]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`A`[`B`[1]].`C`).`F0`)[`D`[1]]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayValueConstructor
parameter_list|()
block|{
name|expr
argument_list|(
literal|"array[1, 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(ARRAY[1, 2])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"array [1, 2]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(ARRAY[1, 2])"
argument_list|)
expr_stmt|;
comment|// with space
comment|// parser allows empty array; validator will reject it
name|expr
argument_list|(
literal|"array[]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(ARRAY[])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"array[(1, 'a'), (2, 'b')]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(ARRAY[(ROW(1, 'a')), (ROW(2, 'b'))])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastAsCollectionType
parameter_list|()
block|{
comment|// test array type.
name|expr
argument_list|(
literal|"cast(a as int array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS INTEGER ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as varchar(5) array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS VARCHAR(5) ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as int array array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS INTEGER ARRAY ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as varchar(5) array array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS VARCHAR(5) ARRAY ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as int array^<^10>)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"<\" at line 1, column 20.\n.*"
argument_list|)
expr_stmt|;
comment|// test multiset type.
name|expr
argument_list|(
literal|"cast(a as int multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS INTEGER MULTISET)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as varchar(5) multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS VARCHAR(5) MULTISET)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as int multiset array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS INTEGER MULTISET ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as varchar(5) multiset array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS VARCHAR(5) MULTISET ARRAY)"
argument_list|)
expr_stmt|;
comment|// test row type nested in collection type.
name|expr
argument_list|(
literal|"cast(a as row(f0 int array multiset, f1 varchar(5) array) array multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS "
operator|+
literal|"ROW(`F0` INTEGER ARRAY MULTISET, "
operator|+
literal|"`F1` VARCHAR(5) ARRAY) "
operator|+
literal|"ARRAY MULTISET)"
argument_list|)
expr_stmt|;
comment|// test UDT collection type.
name|expr
argument_list|(
literal|"cast(a as MyUDT array multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS `MYUDT` ARRAY MULTISET)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastAsRowType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(a as row(f0 int, f1 varchar))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS ROW(`F0` INTEGER, `F1` VARCHAR))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as row(f0 int not null, f1 varchar null))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS ROW(`F0` INTEGER, `F1` VARCHAR NULL))"
argument_list|)
expr_stmt|;
comment|// test nested row type.
name|expr
argument_list|(
literal|"cast(a as row("
operator|+
literal|"f0 row(ff0 int not null, ff1 varchar null) null, "
operator|+
literal|"f1 timestamp not null))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS ROW("
operator|+
literal|"`F0` ROW(`FF0` INTEGER, `FF1` VARCHAR NULL) NULL, "
operator|+
literal|"`F1` TIMESTAMP))"
argument_list|)
expr_stmt|;
comment|// test row type in collection data types.
name|expr
argument_list|(
literal|"cast(a as row(f0 bigint not null, f1 decimal null) array)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS ROW(`F0` BIGINT, `F1` DECIMAL NULL) ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(a as row(f0 varchar not null, f1 timestamp null) multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`A` AS ROW(`F0` VARCHAR, `F1` TIMESTAMP NULL) MULTISET)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMapValueConstructor
parameter_list|()
block|{
name|expr
argument_list|(
literal|"map[1, 'x', 2, 'y']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MAP[1, 'x', 2, 'y'])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"map [1, 'x', 2, 'y']"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MAP[1, 'x', 2, 'y'])"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"map[]"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(MAP[])"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalYearPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' YEAR"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' year(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' YEAR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' year(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' YEAR(2)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' year(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' YEAR(10)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' year(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' YEAR(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' year(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' YEAR(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' year"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' YEAR"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR TO MONTH that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalYearToMonthPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99-11' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99-11' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99-0' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99-0' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1-2' year(2) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99-11' year(2) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99-11' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99-0' year(2) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99-0' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647-11' year(10) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647-11' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0-0' year(1) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0-0' YEAR(1) TO MONTH"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2006-2' year(4) to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2006-2' YEAR(4) TO MONTH"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1-2' year to month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MONTH that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMonthPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' MONTH"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' month(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MONTH(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' month(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' MONTH(2)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' month(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' MONTH(10)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' month(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' MONTH(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' month(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' MONTH(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' month"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' DAY"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' day(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' day(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' DAY(2)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' day(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' DAY(10)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' day(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' DAY(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' day(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' DAY(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' DAY"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO HOUR that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayToHourPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1 2' day(2) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23' day(2) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0' day(2) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647 23' day(10) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647 23' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0 0' day(1) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0' DAY(1) TO HOUR"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345 2' day(4) to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345 2' DAY(4) TO HOUR"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1 2' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1 2' DAY TO HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO MINUTE that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayToMinutePositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1 2:3' day(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59' day(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0' day(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647 23:59' day(10) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647 23:59' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0 0:0' day(1) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0:0' DAY(1) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345 6:7' day(4) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345 6:7' DAY(4) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1 2:3' day to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1 2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO SECOND that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalDayToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59:59' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59:59' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0:0' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0:0' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59:59.999999' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0:0.0' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1 2:3:4' day(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59:59' day(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59:59' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0:0' day(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0:0' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 23:59:59.999999' day to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 23:59:59.999999' DAY TO SECOND(6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99 0:0:0.0' day to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99 0:0:0.0' DAY TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647 23:59:59' day(10) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647 23:59:59' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2147483647 23:59:59.999999999' day(10) to second(9)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647 23:59:59.999999999' DAY(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0 0:0:0' day(1) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(1) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '0 0:0:0.0' day(1) to second(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0:0:0.0' DAY(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345 6:7:8' day(4) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345 6:7:8' DAY(4) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2345 6:7:8.9012' day(4) to second(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345 6:7:8.9012' DAY(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1 2:3:4' day to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1 2:3:4' DAY TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' HOUR"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' hour(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' hour(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' HOUR(2)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' hour(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' HOUR(10)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' hour(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' HOUR(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' hour(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' HOUR(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO MINUTE that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourToMinutePositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '23:59' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '23:59' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '2:3' hour(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '23:59' hour(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '23:59' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0' hour(2) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647:59' hour(10) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647:59' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0:0' hour(1) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0' HOUR(1) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345:7' hour(4) to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345:7' HOUR(4) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-1:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+1:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+2:3' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO SECOND that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalHourToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '23:59:59' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '23:59:59' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0:0' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0:0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '23:59:59.999999' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '23:59:59.999999' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0:0.0' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '2:3:4' hour(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3:4' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:59:59' hour(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:59:59' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0:0' hour(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0:0' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '23:59:59.999999' hour to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '23:59:59.999999' HOUR TO SECOND(6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0:0.0' hour to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0:0.0' HOUR TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647:59:59' hour(10) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647:59:59' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2147483647:59:59.999999999' hour(10) to second(9)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647:59:59.999999999' HOUR(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0:0:0' hour(1) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(1) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '0:0:0.0' hour(1) to second(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0:0.0' HOUR(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345:7:8' hour(4) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345:7:8' HOUR(4) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2345:7:8.9012' hour(4) to second(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345:7:8.9012' HOUR(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+2:3:4' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+2:3:4' HOUR TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMinutePositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' MINUTE"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' minute(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MINUTE(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' minute(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' MINUTE(2)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' minute(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' MINUTE(10)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' minute(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' MINUTE(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' minute(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' MINUTE(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE TO SECOND that should pass both parser    * and validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteToSecondPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '2:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '59:59' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '59:59' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '59:59.999999' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '59:59.999999' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0.0' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0.0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '2:4' minute(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:4' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '59:59' minute(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '59:59' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0' minute(2) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:59.999999' minute to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:59.999999' MINUTE TO SECOND(6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99:0.0' minute to second(6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99:0.0' MINUTE TO SECOND(6)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647:59' minute(10) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647:59' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2147483647:59.999999999' minute(10) to second(9)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647:59.999999999' MINUTE(10) TO SECOND(9)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0:0' minute(1) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0' MINUTE(1) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '0:0.0' minute(1) to second(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0.0' MINUTE(1) TO SECOND(1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '2345:8' minute(4) to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345:8' MINUTE(4) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2345:7.8901' minute(4) to second(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345:7.8901' MINUTE(4) TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '-3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '+3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+3:4' minute to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+3:4' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... SECOND that should pass both parser and    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXPositive() tests.    */
specifier|public
name|void
name|subTestIntervalSecondPositive
parameter_list|()
block|{
comment|// default precision
name|expr
argument_list|(
literal|"interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' SECOND"
argument_list|)
expr_stmt|;
comment|// explicit precision equal to default
name|expr
argument_list|(
literal|"interval '1' second(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' SECOND(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' second(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' SECOND(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second(2,6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' SECOND(2, 6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '99' second(2,6)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '99' SECOND(2, 6)"
argument_list|)
expr_stmt|;
comment|// max precision
name|expr
argument_list|(
literal|"interval '2147483647' second(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647' SECOND(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2147483647.999999999' second(9,9)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483647.999999999' SECOND(9, 9)"
argument_list|)
expr_stmt|;
comment|// min precision
name|expr
argument_list|(
literal|"interval '0' second(1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' SECOND(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '0.0' second(1,1)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0.0' SECOND(1, 1)"
argument_list|)
expr_stmt|;
comment|// alternate precision
name|expr
argument_list|(
literal|"interval '1234' second(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234' SECOND(4)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1234.56789' second(4,5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1234.56789' SECOND(4, 5)"
argument_list|)
expr_stmt|;
comment|// sign
name|expr
argument_list|(
literal|"interval '+1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'+1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '+1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval +'-1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'+1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'+1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'-1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'-1' SECOND"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalYearFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' YEAR(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' YEAR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' YEAR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' YEAR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' YEAR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' YEAR(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' YEAR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' YEAR(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' YEAR(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' YEAR(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' YEAR(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' YEAR(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' YEAR(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' YEAR(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' YEAR(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' YEAR(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' YEAR(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' YEAR(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' YEAR(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... YEAR TO MONTH that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalYearToMonthFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1-2' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1-2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1--2' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1--2' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100-0' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100-0' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100-0' YEAR(2) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100-0' YEAR(2) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000-0' YEAR(3) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000-0' YEAR(3) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000-0' YEAR(3) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000-0' YEAR(3) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648-0' YEAR(10) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648-0' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648-0' YEAR(10) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648-0' YEAR(10) TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-12' YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-12' YEAR TO MONTH"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1-1' YEAR(11) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-1' YEAR(11) TO MONTH"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0-0' YEAR(0) TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0-0' YEAR(0) TO MONTH"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MONTH that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalMonthFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' MONTH(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' MONTH(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' MONTH"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' MONTH"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' MONTH(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' MONTH(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' MONTH(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' MONTH(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' MONTH(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' MONTH(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' MONTH(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' MONTH(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' MONTH(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' MONTH(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' MONTH(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MONTH(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' MONTH(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' MONTH(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalDayFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' DAY(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' DAY(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' DAY"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' DAY"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' DAY"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' DAY(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' DAY(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' DAY(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' DAY(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' DAY(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' DAY(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' DAY(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' DAY(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' DAY(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' DAY(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' DAY(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' DAY(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' DAY(0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testVisitSqlInsertWithSqlShuttle
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into emps select * from emps"
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|getSqlParser
argument_list|(
name|sql
argument_list|)
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNodeVisited
init|=
name|sqlNode
operator|.
name|accept
argument_list|(
operator|new
name|SqlShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|identifier
operator|.
name|names
argument_list|,
name|identifier
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|sqlNodeVisited
argument_list|,
name|sqlNode
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sqlNodeVisited
operator|.
name|getKind
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlKind
operator|.
name|INSERT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlInsertSqlBasicCallToString
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql0
init|=
literal|"insert into emps select * from emps"
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode0
init|=
name|getSqlParser
argument_list|(
name|sql0
argument_list|)
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNodeVisited0
init|=
name|sqlNode0
operator|.
name|accept
argument_list|(
operator|new
name|SqlShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|identifier
operator|.
name|names
argument_list|,
name|identifier
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|str0
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|assertEquals
argument_list|(
name|linux
argument_list|(
name|sqlNodeVisited0
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|str0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into emps select empno from emps"
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode1
init|=
name|getSqlParser
argument_list|(
name|sql1
argument_list|)
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNodeVisited1
init|=
name|sqlNode1
operator|.
name|accept
argument_list|(
operator|new
name|SqlShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlNode
name|visit
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|identifier
operator|.
name|names
argument_list|,
name|identifier
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|String
name|str1
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"(SELECT `EMPNO`\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|assertEquals
argument_list|(
name|linux
argument_list|(
name|sqlNodeVisited1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|str1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO HOUR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalDayToHourFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 x' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 x' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL ' ' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ' ' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' DAY(2) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1 1' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1 1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 -1' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 -1' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 24' DAY TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 24' DAY TO HOUR"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO HOUR"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO HOUR"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO MINUTE that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalDayToMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL ' :' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ' :' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'x 1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'x 1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 x:1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 x:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:x' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:x' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:1.2' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:1.2' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2:3' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1 1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1 1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 -1:1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 -1:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:-1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:-1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 24:1' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 24:1' DAY TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:60' DAY TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:60' DAY TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0' DAY(0) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... DAY TO SECOND that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalDayToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL ' ::' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ' ::' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL ' ::.' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ' ::.' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2:x' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2:x' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2:3' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2:3' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.2' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.2' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' DAY(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2345 6:7:8901' DAY TO SECOND(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2345 6:7:8901' DAY TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1 1:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1 1:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 -1:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 -1:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:-1:1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:-1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:-1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:-1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:1.-1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:1.-1' DAY TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100 0' DAY(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000 0' DAY(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000 0' DAY(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648 0' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648 0' DAY(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 24:1:1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 24:1:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:60:1' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:60:1' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:60' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:60' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:1.0000001' DAY TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:1.0000001' DAY TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:1:1.0001' DAY TO SECOND(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:1:1.0001' DAY TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY(11) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY TO SECOND(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(0) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(0) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalHourFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' HOUR(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' HOUR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' HOUR"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' HOUR(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' HOUR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' HOUR(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' HOUR(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' HOUR(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' HOUR(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' HOUR(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' HOUR(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' HOUR(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' HOUR(10)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' HOUR"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' HOUR(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' HOUR(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO MINUTE that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalHourToMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL ':' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ':' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2:3' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2:3' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1:1' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1:1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:-1' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:-1' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100:0' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100:0' HOUR(2) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0' HOUR(2) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000:0' HOUR(3) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000:0' HOUR(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000:0' HOUR(3) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000:0' HOUR(3) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648:0' HOUR(10) TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:24' HOUR TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:24' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1:1' HOUR(11) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1' HOUR(11) TO MINUTE"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0:0' HOUR(0) TO MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0' HOUR(0) TO MINUTE"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... HOUR TO SECOND that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalHourToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '::' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '::' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '::.' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '::.' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2:x' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2:x' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:x:3' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:x:3' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.x' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.x' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '6:7:8901' HOUR TO SECOND(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '6:7:8901' HOUR TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1:1:1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1:1:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:-1:1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:-1:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:-1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:-1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.-1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.-1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100:0:0' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0:0' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100:0:0' HOUR(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0:0' HOUR(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000:0:0' HOUR(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000:0:0' HOUR(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000:0:0' HOUR(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000:0:0' HOUR(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648:0:0' HOUR(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:60:1' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:60:1' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:60' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:60' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.0000001' HOUR TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.0000001' HOUR TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.0001' HOUR TO SECOND(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.0001' HOUR TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1:1:1' HOUR(11) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1' HOUR(11) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1' HOUR TO SECOND(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1' HOUR TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(0) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(0) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' HOUR TO SECOND(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0:0' HOUR TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL '-' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' MINUTE(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' MINUTE(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' MINUTE"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' MINUTE"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' MINUTE(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' MINUTE(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' MINUTE(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' MINUTE(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' MINUTE(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' MINUTE(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' MINUTE(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' MINUTE(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' MINUTE(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' MINUTE(10)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' MINUTE(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MINUTE(11)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' MINUTE(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' MINUTE(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... MINUTE TO SECOND that should pass parser but    * fail validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalMinuteToSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL ':' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ':' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL ':.' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ':.' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.2' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:x' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:x' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'x:3' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'x:3' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1.x' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1.x' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1:2' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '7:8901' MINUTE TO SECOND(4)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '7:8901' MINUTE TO SECOND(4)"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1:1' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1:1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:-1' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:-1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1.-1' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1.-1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
comment|//  plus>max value for mid/end fields
name|expr
argument_list|(
literal|"INTERVAL '100:0' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100:0' MINUTE(2) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100:0' MINUTE(2) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000:0' MINUTE(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000:0' MINUTE(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000:0' MINUTE(3) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000:0' MINUTE(3) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648:0' MINUTE(10) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:60' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:60' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1.0000001' MINUTE TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1.0000001' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1:1.0001' MINUTE TO SECOND(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1:1.0001' MINUTE TO SECOND(3)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1:1' MINUTE(11) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1' MINUTE(11) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:1' MINUTE TO SECOND(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1' MINUTE TO SECOND(10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0:0' MINUTE(0) TO SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0' MINUTE(0) TO SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0' MINUTE TO SECOND(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0:0' MINUTE TO SECOND(0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for INTERVAL... SECOND that should pass parser but fail    * validator. A substantially identical set of tests exists in    * SqlValidatorTest, and any changes here should be synchronized there.    * Similarly, any changes to tests here should be echoed appropriately to    * each of the other 12 subTestIntervalXXXFailsValidation() tests.    */
specifier|public
name|void
name|subTestIntervalSecondFailsValidation
parameter_list|()
block|{
comment|// Qualifier - field mismatches
name|expr
argument_list|(
literal|"INTERVAL ':' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL ':' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '.' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '.' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.x' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.x' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'x.1' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'x.1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 2' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 2' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:2' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:2' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' SECOND(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1-2' SECOND(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL 'bogus text' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'bogus text' SECOND"
argument_list|)
expr_stmt|;
comment|// negative field values
name|expr
argument_list|(
literal|"INTERVAL '--1' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '--1' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.-1' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.-1' SECOND"
argument_list|)
expr_stmt|;
comment|// Field value out of range
comment|//  (default, explicit default, alt, neg alt, max, neg max)
name|expr
argument_list|(
literal|"INTERVAL '100' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '100' SECOND(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '100' SECOND(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1000' SECOND(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1000' SECOND(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-1000' SECOND(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1000' SECOND(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '2147483648' SECOND(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2147483648' SECOND(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-2147483648' SECOND(10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-2147483648' SECOND(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.0000001' SECOND"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.0000001' SECOND"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.0000001' SECOND(2)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.0000001' SECOND(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.0001' SECOND(2, 3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.0001' SECOND(2, 3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.000000001' SECOND(2, 9)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.000000001' SECOND(2, 9)"
argument_list|)
expr_stmt|;
comment|// precision> maximum
name|expr
argument_list|(
literal|"INTERVAL '1' SECOND(11)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1' SECOND(11)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.1' SECOND(1, 10)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1.1' SECOND(1, 10)"
argument_list|)
expr_stmt|;
comment|// precision< minimum allowed)
comment|// note: parser will catch negative values, here we
comment|// just need to check for 0
name|expr
argument_list|(
literal|"INTERVAL '0' SECOND(0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' SECOND(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' SECOND(1, 0)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '0' SECOND(1, 0)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Runs tests for each of the thirteen different main types of INTERVAL    * qualifiers (YEAR, YEAR TO MONTH, etc.) Tests in this section fall into    * two categories:    *    *<ul>    *<li>xxxPositive: tests that should pass parser and validator</li>    *<li>xxxFailsValidation: tests that should pass parser but fail validator    *</li>    *</ul>    *    *<p>A substantially identical set of tests exists in SqlValidatorTest, and    * any changes here should be synchronized there.    */
annotation|@
name|Test
name|void
name|testIntervalLiterals
parameter_list|()
block|{
name|subTestIntervalYearPositive
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMonthPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourPositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinutePositive
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalSecondPositive
argument_list|()
expr_stmt|;
name|subTestIntervalYearFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalYearToMonthFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMonthFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToHourFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalDayToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourToMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalHourToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalMinuteToSecondFailsValidation
argument_list|()
expr_stmt|;
name|subTestIntervalSecondFailsValidation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnparseableIntervalQualifiers
parameter_list|()
block|{
comment|// No qualifier
name|expr
argument_list|(
literal|"interval '1^'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Encountered \"<EOF>\" at line 1, column 12\\.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"    \"DAY\" \\.\\.\\.\n"
operator|+
literal|"    \"DAYS\" \\.\\.\\.\n"
operator|+
literal|"    \"HOUR\" \\.\\.\\.\n"
operator|+
literal|"    \"HOURS\" \\.\\.\\.\n"
operator|+
literal|"    \"MINUTE\" \\.\\.\\.\n"
operator|+
literal|"    \"MINUTES\" \\.\\.\\.\n"
operator|+
literal|"    \"MONTH\" \\.\\.\\.\n"
operator|+
literal|"    \"MONTHS\" \\.\\.\\.\n"
operator|+
literal|"    \"SECOND\" \\.\\.\\.\n"
operator|+
literal|"    \"SECONDS\" \\.\\.\\.\n"
operator|+
literal|"    \"YEAR\" \\.\\.\\.\n"
operator|+
literal|"    \"YEARS\" \\.\\.\\.\n"
operator|+
literal|"    "
argument_list|)
expr_stmt|;
comment|// illegal qualifiers, no precision in either field
name|expr
argument_list|(
literal|"interval '1' year ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"to year\" at line 1, column 19.\n"
operator|+
literal|"Was expecting one of:\n"
operator|+
literal|"<EOF> \n"
operator|+
literal|"    \"\\(\" \\.\\.\\.\n"
operator|+
literal|"    \"\\.\" \\.\\.\\..*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualifiers, including precision in start field
name|expr
argument_list|(
literal|"interval '1' year(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ year"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ month"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualifiers, including precision in end field
name|expr
argument_list|(
literal|"interval '1' year ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year to month^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day to hour^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day to minute^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour to minute^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// illegal qualifiers, including precision in start and end field
name|expr
argument_list|(
literal|"interval '1' year(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) to month^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year(3) ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' month(3) ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnparseableIntervalQualifiers2
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval '1-2' day(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) to hour^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) to minute^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' day(3) to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) to minute^(^2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' hour(3) to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' minute(3) to second(2^,^6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ year(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ month(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ day(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ hour(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ minute(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ second(2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' second(3) ^to^ second(2,6)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// precision of -1 (< minimum allowed)
name|expr
argument_list|(
literal|"INTERVAL '0' YEAR(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0-0' YEAR(^-^1) TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' MONTH(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' DAY(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0 0' DAY(^-^1) TO HOUR"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0 0' DAY(^-^1) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY(^-^1) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0 0:0:0' DAY TO SECOND(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' HOUR(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0' HOUR(^-^1) TO MINUTE"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' HOUR(^-^1) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0:0' HOUR TO SECOND(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' MINUTE(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0' MINUTE(^-^1) TO SECOND"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0:0' MINUTE TO SECOND(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' SECOND(^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '0' SECOND(1, ^-^1)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// These may actually be legal per SQL2003, as the first field is
comment|// "more significant" than the last, but we do not support them
name|expr
argument_list|(
literal|"interval '1' day(3) ^to^ day"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour(3) ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' minute(3) ^to^ minute"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second(3) ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second(3,1) ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second(2,3) ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second(2,2) ^to^ second(3)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// Invalid units
name|expr
argument_list|(
literal|"INTERVAL '2' ^MILLENNIUM^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1-2' ^MILLENNIUM^ TO CENTURY"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '10' ^CENTURY^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '10' ^DECADE^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '4' ^QUARTER^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that plural time units are allowed when not in strict mode. */
annotation|@
name|Test
name|void
name|testIntervalPluralUnits
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval '2' years"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"YEARS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2' YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2:1' years to months"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"YEARS"
argument_list|,
literal|"MONTHS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:1' YEAR TO MONTH"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2' days"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"DAYS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2:1' days to hours"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"DAYS"
argument_list|,
literal|"HOURS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2:1' day to hours"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"HOURS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2:1' days to hour"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"DAYS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '2:1' DAY TO HOUR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:1' minutes to seconds"
argument_list|)
operator|.
name|hasWarning
argument_list|(
name|checkWarnings
argument_list|(
literal|"MINUTES"
argument_list|,
literal|"SECONDS"
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:1' MINUTE TO SECOND"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Consumer
argument_list|<
name|List
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
argument_list|>
name|checkWarnings
parameter_list|(
name|String
modifier|...
name|tokens
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|token
range|:
name|tokens
control|)
block|{
name|messages
operator|.
name|add
argument_list|(
literal|"Warning: use of non-standard feature '"
operator|+
name|token
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|throwables
lambda|->
block|{
name|assertThat
argument_list|(
name|throwables
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|messages
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|throwables
argument_list|,
name|messages
argument_list|)
control|)
block|{
name|assertThat
argument_list|(
name|pair
operator|.
name|left
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|pair
operator|.
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
name|void
name|testMiscIntervalQualifier
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval '-' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1 2:3:4.567' day to hour ^to^ second"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"to\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:2' minute to second(2^,^ 2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \",\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:x' hour to minute"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:x' HOUR TO MINUTE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:x:2' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1:x:2' HOUR TO SECOND"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalExpression
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval 0 day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 0 DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval 0 days"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 0 DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -10 days"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL (- 10) DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -10 days"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL (- 10) DAY"
argument_list|)
expr_stmt|;
comment|// parser requires parentheses for expressions other than numeric
comment|// literal or identifier
name|expr
argument_list|(
literal|"interval 1 ^+^ x.y days"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\+\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (1 + x.y) days"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL (1 + `X`.`Y`) DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -x second(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL (- `X`) SECOND(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -x.y second(3)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL (- `X`.`Y`) SECOND(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval 1 day ^to^ hour"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"to\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1 1' day to hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '1 1' DAY TO HOUR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalOperators
parameter_list|()
block|{
name|expr
argument_list|(
literal|"-interval '1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(- INTERVAL '1' DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day + interval '1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' DAY + INTERVAL '1' DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day - interval '1:2:3' hour to second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' DAY - INTERVAL '1:2:3' HOUR TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval -'1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL -'1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '-1' day"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL '-1' DAY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval 'wael was here^'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"<EOF>\".*"
argument_list|)
expr_stmt|;
comment|// ok in parser, not in validator
name|expr
argument_list|(
literal|"interval 'wael was here' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INTERVAL 'wael was here' HOUR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDateMinusDate
parameter_list|()
block|{
name|expr
argument_list|(
literal|"(date1 - date2) HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`DATE1` - `DATE2`) HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(date1 - date2) YEAR TO MONTH"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((`DATE1` - `DATE2`) YEAR TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(date1 - date2) HOUR> interval '1' HOUR"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(((`DATE1` - `DATE2`) HOUR)> INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^(date1 + date2) second^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal expression. "
operator|+
literal|"Was expecting ..DATETIME - DATETIME. INTERVALQUALIFIER.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^(date1,date2,date2) second^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal expression. "
operator|+
literal|"Was expecting ..DATETIME - DATETIME. INTERVALQUALIFIER.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtract
parameter_list|()
block|{
name|expr
argument_list|(
literal|"extract(year from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(YEAR FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(month from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(MONTH FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(day from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(DAY FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(hour from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(HOUR FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(minute from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(MINUTE FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(second from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(SECOND FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(dow from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(DOW FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(doy from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(DOY FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(week from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(WEEK FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(epoch from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(EPOCH FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(quarter from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(QUARTER FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(decade from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(DECADE FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(century from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(CENTURY FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(millennium from x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"EXTRACT(MILLENNIUM FROM `X`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(day ^to^ second from x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"to\".*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGeometry
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(null as ^geometry^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Geo-spatial extensions and the GEOMETRY data type are not enabled"
argument_list|)
expr_stmt|;
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|LENIENT
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as geometry)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(NULL AS GEOMETRY)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalArithmetics
parameter_list|()
block|{
name|expr
argument_list|(
literal|"TIME '23:59:59' - interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TIME '23:59:59' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2000-01-01 23:59:59.1' - interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TIMESTAMP '2000-01-01 23:59:59.1' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"DATE '2000-01-01' - interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(DATE '2000-01-01' - INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '23:59:59' + interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TIME '23:59:59' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2000-01-01 23:59:59.1' + interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(TIMESTAMP '2000-01-01 23:59:59.1' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"DATE '2000-01-01' + interval '1' hour "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(DATE '2000-01-01' + INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour + TIME '23:59:59' "
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR + TIME '23:59:59')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour * 8"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR * 8)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 * interval '1' hour"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 * INTERVAL '1' HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour / 8"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR / 8)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalCompare
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval '1' hour = interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR = INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour<> interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR<> INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour< interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR< INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour<= interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR<= INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour> interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR> INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour>= interval '1' second"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(INTERVAL '1' HOUR>= INTERVAL '1' SECOND)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastToInterval
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(x as interval year)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL YEAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval year to month)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL YEAR TO MONTH)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval day)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL DAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval day to hour)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL DAY TO HOUR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval day to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL DAY TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval day to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL DAY TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval hour to minute)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL HOUR TO MINUTE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval hour to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL HOUR TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as interval minute to second)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS INTERVAL MINUTE TO SECOND)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '3-2' year to month as CHAR(5))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(INTERVAL '3-2' YEAR TO MONTH AS CHAR(5))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastToVarchar
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(x as varchar(5))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARCHAR(5))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varchar)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARCHAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varBINARY(5))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARBINARY(5))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x as varbinary)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST(`X` AS VARBINARY)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAddAndDiff
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|tsi
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|List
argument_list|<
name|String
argument_list|>
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"MICROSECOND"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"FRAC_SECOND"
argument_list|,
literal|"MICROSECOND"
argument_list|,
literal|"SQL_TSI_MICROSECOND"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"NANOSECOND"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"NANOSECOND"
argument_list|,
literal|"SQL_TSI_FRAC_SECOND"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"SECOND"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"SECOND"
argument_list|,
literal|"SQL_TSI_SECOND"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"MINUTE"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"MINUTE"
argument_list|,
literal|"SQL_TSI_MINUTE"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"HOUR"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"HOUR"
argument_list|,
literal|"SQL_TSI_HOUR"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"DAY"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"DAY"
argument_list|,
literal|"SQL_TSI_DAY"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"WEEK"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"WEEK"
argument_list|,
literal|"SQL_TSI_WEEK"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"MONTH"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"MONTH"
argument_list|,
literal|"SQL_TSI_MONTH"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"QUARTER"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"QUARTER"
argument_list|,
literal|"SQL_TSI_QUARTER"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"YEAR"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"YEAR"
argument_list|,
literal|"SQL_TSI_YEAR"
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|functions
init|=
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"timestampadd(%1$s, 12, current_timestamp)"
argument_list|)
operator|.
name|add
argument_list|(
literal|"timestampdiff(%1$s, current_timestamp, current_timestamp)"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|intervalGroup
range|:
name|tsi
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|function
range|:
name|functions
control|)
block|{
for|for
control|(
name|String
name|interval
range|:
name|intervalGroup
operator|.
name|getValue
argument_list|()
control|)
block|{
name|expr
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|function
argument_list|,
name|interval
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|function
argument_list|,
name|intervalGroup
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"`"
argument_list|)
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
block|}
block|}
name|expr
argument_list|(
literal|"timestampadd(^incorrect^, 1, current_timestamp)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Was expecting one of.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampdiff(^incorrect^, current_timestamp, current_timestamp)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Was expecting one of.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAdd
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from t\n"
operator|+
literal|"where timestampadd(sql_tsi_month, 5, hiredate)< curdate"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (TIMESTAMPADD(MONTH, 5, `HIREDATE`)< `CURDATE`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampDiff
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from t\n"
operator|+
literal|"where timestampdiff(frac_second, 5, hiredate)< curdate"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE (TIMESTAMPDIFF(MICROSECOND, 5, `HIREDATE`)< `CURDATE`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnnest
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select*from unnest(x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM UNNEST(`X`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(x) AS T"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM UNNEST(`X`) AS `T`"
argument_list|)
expr_stmt|;
comment|// UNNEST cannot be first word in query
name|sql
argument_list|(
literal|"^unnest^(x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"unnest\" at.*"
argument_list|)
expr_stmt|;
comment|// UNNEST with more than one argument
specifier|final
name|String
name|sql
init|=
literal|"select * from dept,\n"
operator|+
literal|"unnest(dept.employees, dept.managers)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`,\n"
operator|+
literal|"UNNEST(`DEPT`.`EMPLOYEES`, `DEPT`.`MANAGERS`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// LATERAL UNNEST is not valid
name|sql
argument_list|(
literal|"select * from dept, lateral ^unnest^(dept.employees)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"unnest\" at .*"
argument_list|)
expr_stmt|;
comment|// Does not generate extra parentheses around UNNEST because UNNEST is
comment|// a table expression.
specifier|final
name|String
name|sql1
init|=
literal|""
operator|+
literal|"SELECT\n"
operator|+
literal|"  item.name,\n"
operator|+
literal|"  relations.*\n"
operator|+
literal|"FROM dfs.tmp item\n"
operator|+
literal|"JOIN (\n"
operator|+
literal|"  SELECT * FROM UNNEST(item.related) i(rels)\n"
operator|+
literal|") relations\n"
operator|+
literal|"ON TRUE"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT `ITEM`.`NAME`, `RELATIONS`.*\n"
operator|+
literal|"FROM `DFS`.`TMP` AS `ITEM`\n"
operator|+
literal|"INNER JOIN (SELECT *\n"
operator|+
literal|"FROM UNNEST(`ITEM`.`RELATED`) AS `I` (`RELS`)) AS `RELATIONS` ON TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnnestWithOrdinality
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from unnest(x) with ordinality"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM UNNEST(`X`) WITH ORDINALITY"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(x) with ordinality AS T"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM UNNEST(`X`) WITH ORDINALITY AS `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(x) with ordinality AS T(c, o)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM UNNEST(`X`) WITH ORDINALITY AS `T` (`C`, `O`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(x) as T ^with^ ordinality"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"with\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testParensInFrom
parameter_list|()
block|{
comment|// UNNEST may not occur within parentheses.
comment|// FIXME should fail at "unnest"
name|sql
argument_list|(
literal|"select *from ^(^unnest(x))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\( unnest\" at .*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|sql
argument_list|(
literal|"select * from (^emp^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|sql
argument_list|(
literal|"select * from (^emp^ as x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|//<table-name> may not occur within parentheses.
name|sql
argument_list|(
literal|"select * from (^emp^) as x"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Non-query expression encountered in illegal context.*"
argument_list|)
expr_stmt|;
comment|// Parentheses around JOINs are OK, and sometimes necessary.
if|if
condition|(
literal|false
condition|)
block|{
comment|// todo:
name|sql
argument_list|(
literal|"select * from (emp join dept using (deptno))"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (emp join dept using (deptno)) join foo using (x)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testProcedureCall
parameter_list|()
block|{
name|sql
argument_list|(
literal|"call blubber(5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CALL `BLUBBER`(5)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"call \"blubber\"(5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CALL `blubber`(5)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"call whale.blubber(5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CALL `WHALE`.`BLUBBER`(5)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNewSpecification
parameter_list|()
block|{
name|expr
argument_list|(
literal|"new udt()"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NEW `UDT`())"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"new my.udt(1, 'hey')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(NEW `MY`.`UDT`(1, 'hey'))"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"new udt() is not null"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"((NEW `UDT`()) IS NOT NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 + new udt()"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"(1 + (NEW `UDT`()))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetCast
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(multiset[1] as double multiset)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CAST((MULTISET[1]) AS DOUBLE MULTISET)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAddCarets
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"values (^foo^)"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"values (foo)"
argument_list|,
literal|1
argument_list|,
literal|9
argument_list|,
literal|1
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc^def"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"abcdef"
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcdef^"
argument_list|,
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
literal|"abcdef"
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|protected
name|void
name|testMetadata
parameter_list|()
block|{
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|metadata
init|=
name|getSqlParser
argument_list|(
literal|""
argument_list|)
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"HAVING"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"BAR"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jdbcKeywords
init|=
name|metadata
operator|.
name|getJdbcKeywords
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|jdbcKeywords
operator|.
name|contains
argument_list|(
literal|",COLLECT,"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|!
name|jdbcKeywords
operator|.
name|contains
argument_list|(
literal|",SELECT,"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that reserved keywords are not added to the parser unintentionally.    * (Most keywords are non-reserved. The set of reserved words generally    * only changes with a new version of the SQL standard.)    *    *<p>If the new keyword added is intended to be a reserved keyword, update    * the {@link #RESERVED_KEYWORDS} list. If not, add the keyword to the    * non-reserved keyword list in the parser.    */
annotation|@
name|Test
name|void
name|testNoUnintendedNewReservedKeywords
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|isNotSubclass
argument_list|()
argument_list|,
literal|"don't run this test for sub-classes"
argument_list|)
expr_stmt|;
specifier|final
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|metadata
init|=
name|getSqlParser
argument_list|(
literal|""
argument_list|)
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|reservedKeywords
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|keywords92
init|=
name|keywords
argument_list|(
literal|"92"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|metadata
operator|.
name|getTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|metadata
operator|.
name|isKeyword
argument_list|(
name|s
argument_list|)
operator|&&
name|metadata
operator|.
name|isReservedWord
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|reservedKeywords
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|// Check that the parser's list of SQL:92
comment|// reserved words is consistent with keywords("92").
name|assertThat
argument_list|(
name|s
argument_list|,
name|metadata
operator|.
name|isSql92ReservedWord
argument_list|(
name|s
argument_list|)
argument_list|,
name|is
argument_list|(
name|keywords92
operator|.
name|contains
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|reason
init|=
literal|"The parser has at least one new reserved keyword. "
operator|+
literal|"Are you sure it should be reserved? Difference:\n"
operator|+
name|DiffTestCase
operator|.
name|diffLines
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|getReservedKeywords
argument_list|()
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|reservedKeywords
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|reason
argument_list|,
name|reservedKeywords
argument_list|,
name|is
argument_list|(
name|getReservedKeywords
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTabStop
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT *\n\tFROM mytable"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `MYTABLE`"
argument_list|)
expr_stmt|;
comment|// make sure that the tab stops do not affect the placement of the
comment|// error tokens
name|sql
argument_list|(
literal|"SELECT *\tFROM mytable\t\tWHERE x ^=^ = y AND b = 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"= =\" at line 1, column 32\\..*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLongIdentifiers
parameter_list|()
block|{
name|StringBuilder
name|ident128Builder
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
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|ident128Builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
operator|(
literal|'a'
operator|+
operator|(
name|i
operator|%
literal|26
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|String
name|ident128
init|=
name|ident128Builder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|ident128Upper
init|=
name|ident128
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|String
name|ident129
init|=
literal|"x"
operator|+
name|ident128
decl_stmt|;
name|String
name|ident129Upper
init|=
name|ident129
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|sql
argument_list|(
literal|"select * from "
operator|+
name|ident128
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `"
operator|+
name|ident128Upper
operator|+
literal|"`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ^"
operator|+
name|ident129
operator|+
literal|"^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Length of identifier '"
operator|+
name|ident129Upper
operator|+
literal|"' must be less than or equal to 128 characters"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select "
operator|+
name|ident128
operator|+
literal|" from mytable"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `"
operator|+
name|ident128Upper
operator|+
literal|"`\n"
operator|+
literal|"FROM `MYTABLE`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^"
operator|+
name|ident129
operator|+
literal|"^ from mytable"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Length of identifier '"
operator|+
name|ident129Upper
operator|+
literal|"' must be less than or equal to 128 characters"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that you can't quote the names of builtin functions.    *    * @see org.apache.calcite.test.SqlValidatorTest#testQuotedFunction()    */
annotation|@
name|Test
name|void
name|testQuotedFunction
parameter_list|()
block|{
name|expr
argument_list|(
literal|"\"CAST\"(1 ^as^ double)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"as\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"\"POSITION\"('b' ^in^ 'alphabet')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"in \\\\'alphabet\\\\'\" at .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"\"OVERLAY\"('a' ^PLAcing^ 'b' from 1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"PLAcing\" at.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"\"SUBSTRING\"('a' ^from^ 1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"from\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests applying a member function of a specific type as a suffix    * function. */
annotation|@
name|Test
name|void
name|testMemberFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT myColumn.func(a, b) FROM tbl"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `MYCOLUMN`.`FUNC`(`A`, `B`)\n"
operator|+
literal|"FROM `TBL`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT myColumn.mySubField.func() FROM tbl"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `MYCOLUMN`.`MYSUBFIELD`.`FUNC`()\n"
operator|+
literal|"FROM `TBL`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT tbl.myColumn.mySubField.func() FROM tbl"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `TBL`.`MYCOLUMN`.`MYSUBFIELD`.`FUNC`()\n"
operator|+
literal|"FROM `TBL`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT tbl.foo(0).col.bar(2, 3) FROM tbl"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT ((`TBL`.`FOO`(0).`COL`).`BAR`(2, 3))\n"
operator|+
literal|"FROM `TBL`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnicodeLiteral
parameter_list|()
block|{
comment|// Note that here we are constructing a SQL statement which directly
comment|// contains Unicode characters (not SQL Unicode escape sequences).  The
comment|// escaping here is Java-only, so by the time it gets to the SQL
comment|// parser, the literal already contains Unicode characters.
name|String
name|in1
init|=
literal|"values _UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"'"
decl_stmt|;
name|String
name|out1
init|=
literal|"VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"'))"
decl_stmt|;
name|sql
argument_list|(
name|in1
argument_list|)
operator|.
name|ok
argument_list|(
name|out1
argument_list|)
expr_stmt|;
comment|// Without the U& prefix, escapes are left unprocessed
name|String
name|in2
init|=
literal|"values '"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'"
decl_stmt|;
name|String
name|out2
init|=
literal|"VALUES (ROW('"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'))"
decl_stmt|;
name|sql
argument_list|(
name|in2
argument_list|)
operator|.
name|ok
argument_list|(
name|out2
argument_list|)
expr_stmt|;
comment|// Likewise, even with the U& prefix, if some other escape
comment|// character is specified, then the backslash-escape
comment|// sequences are not interpreted
name|String
name|in3
init|=
literal|"values U&'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"' UESCAPE '!'"
decl_stmt|;
name|String
name|out3
init|=
literal|"VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'))"
decl_stmt|;
name|sql
argument_list|(
name|in3
argument_list|)
operator|.
name|ok
argument_list|(
name|out3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnicodeEscapedLiteral
parameter_list|()
block|{
comment|// Note that here we are constructing a SQL statement which
comment|// contains SQL-escaped Unicode characters to be handled
comment|// by the SQL parser.
name|String
name|in
init|=
literal|"values U&'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
operator|+
literal|"'"
decl_stmt|;
name|String
name|out
init|=
literal|"VALUES (ROW(_UTF16'"
operator|+
name|ConversionUtil
operator|.
name|TEST_UNICODE_STRING
operator|+
literal|"'))"
decl_stmt|;
name|sql
argument_list|(
name|in
argument_list|)
operator|.
name|ok
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Verify that we can override with an explicit escape character
name|sql
argument_list|(
name|in
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"!"
argument_list|)
operator|+
literal|"UESCAPE '!'"
argument_list|)
operator|.
name|ok
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIllegalUnicodeEscape
parameter_list|()
block|{
name|expr
argument_list|(
literal|"U&'abc' UESCAPE '!!'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*must be exactly one character.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE ''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*must be exactly one character.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE '0'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE 'a'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE 'F'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*hex digit.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE ' '"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*whitespace.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE '+'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*plus sign.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"U&'abc' UESCAPE '\"'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*double quote.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abc' UESCAPE ^'!'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*without Unicode literal introducer.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^U&'\\0A'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*is not exactly four hex digits.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^U&'\\wxyz'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*is not exactly four hex digits.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqlOptions
parameter_list|()
throws|throws
name|SqlParseException
block|{
name|SqlNode
name|node
init|=
name|getSqlParser
argument_list|(
literal|"alter system set schema = true"
argument_list|)
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
name|SqlSetOption
name|opt
init|=
operator|(
name|SqlSetOption
operator|)
name|node
decl_stmt|;
name|assertThat
argument_list|(
name|opt
operator|.
name|getScope
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"SYSTEM"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlPrettyWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|format
argument_list|(
name|opt
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"\"SCHEMA\""
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SqlPrettyWriter
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|format
argument_list|(
name|opt
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"TRUE"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SqlPrettyWriter
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|format
argument_list|(
name|opt
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"ALTER SYSTEM SET \"SCHEMA\" = TRUE"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set \"a number\" = 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `a number` = 1"
argument_list|)
operator|.
name|node
argument_list|(
name|isDdl
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set flag = false"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `FLAG` = FALSE"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set approx = -12.3450"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `APPROX` = -12.3450"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set onOff = on"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `ONOFF` = `ON`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set onOff = off"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `ONOFF` = `OFF`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set baz = foo"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `BAZ` = `FOO`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system set \"a\".\"number\" = 1"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM SET `a`.`number` = 1"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"set approx = -12.3450"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SET `APPROX` = -12.3450"
argument_list|)
operator|.
name|node
argument_list|(
name|isDdl
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|getSqlParser
argument_list|(
literal|"reset schema"
argument_list|)
operator|.
name|parseStmt
argument_list|()
expr_stmt|;
name|opt
operator|=
operator|(
name|SqlSetOption
operator|)
name|node
expr_stmt|;
name|assertThat
argument_list|(
name|opt
operator|.
name|getScope
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SqlPrettyWriter
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|format
argument_list|(
name|opt
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"\"SCHEMA\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|opt
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|SqlPrettyWriter
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|format
argument_list|(
name|opt
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"RESET \"SCHEMA\""
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system RESET flag"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM RESET `FLAG`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"reset onOff"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"RESET `ONOFF`"
argument_list|)
operator|.
name|node
argument_list|(
name|isDdl
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"reset \"this\".\"is\".\"sparta\""
argument_list|)
operator|.
name|ok
argument_list|(
literal|"RESET `this`.`is`.`sparta`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"alter system reset all"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM RESET `ALL`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"reset all"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"RESET `ALL`"
argument_list|)
expr_stmt|;
comment|// expressions not allowed
name|sql
argument_list|(
literal|"alter system set aString = 'abc' ^||^ 'def' "
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"\\|\\|\" at line 1, column 34\\..*"
argument_list|)
expr_stmt|;
comment|// multiple assignments not allowed
name|sql
argument_list|(
literal|"alter system set x = 1^,^ y = 2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \",\" at line 1, column 23\\..*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSequence
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select next value for my_schema.my_seq from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (NEXT VALUE FOR `MY_SCHEMA`.`MY_SEQ`)\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select next value for my_schema.my_seq as s from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (NEXT VALUE FOR `MY_SCHEMA`.`MY_SEQ`) AS `S`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select next value for my_seq as s from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT (NEXT VALUE FOR `MY_SEQ`) AS `S`\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 + next value for s + current value for s from t"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT ((1 + (NEXT VALUE FOR `S`)) + (CURRENT VALUE FOR `S`))\n"
operator|+
literal|"FROM `T`"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t where next value for my_seq< 10"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((NEXT VALUE FOR `MY_SEQ`)< 10)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from t\n"
operator|+
literal|"where next value for my_seq< 10 fetch next 3 rows only"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"WHERE ((NEXT VALUE FOR `MY_SEQ`)< 10)\n"
operator|+
literal|"FETCH NEXT 3 ROWS ONLY"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into t values next value for my_seq, current value for my_seq"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INSERT INTO `T`\n"
operator|+
literal|"VALUES (ROW((NEXT VALUE FOR `MY_SEQ`))),\n"
operator|+
literal|"(ROW((CURRENT VALUE FOR `MY_SEQ`)))"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into t values (1, current value for my_seq)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"INSERT INTO `T`\n"
operator|+
literal|"VALUES (ROW(1, (CURRENT VALUE FOR `MY_SEQ`)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivot
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR job in ('CLERK' AS c))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` PIVOT (SUM(`SAL`) AS `SAL`"
operator|+
literal|" FOR `JOB` IN ('CLERK' AS `C`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// As previous, but parentheses around singleton column.
specifier|final
name|String
name|sql2
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR (job) in ('CLERK' AS c))"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testPivot()} but composite FOR and two composite values. */
annotation|@
name|Test
name|void
name|testPivotComposite
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR (job, deptno) IN\n"
operator|+
literal|" (('CLERK', 10) AS c10, ('MANAGER', 20) AS m20))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` PIVOT (SUM(`SAL`) AS `SAL` FOR (`JOB`, `DEPTNO`)"
operator|+
literal|" IN (('CLERK', 10) AS `C10`, ('MANAGER', 20) AS `M20`))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Pivot with no values. */
annotation|@
name|Test
name|void
name|testPivotWithoutValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR job IN ())"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP` PIVOT (SUM(`SAL`) AS `SAL` FOR `JOB` IN ())"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** In PIVOT, FOR clause must contain only simple identifiers. */
annotation|@
name|Test
name|void
name|testPivotErrorExpressionInFor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR deptno ^-^10 IN (10, 20)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testPivotErrorExpressionInFor()} but more than one column. */
annotation|@
name|Test
name|void
name|testPivotErrorExpressionInCompositeFor
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR (job, deptno ^-^10)\n"
operator|+
literal|" IN (('CLERK', 10), ('MANAGER', 20))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"-\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** More complex PIVOT case (multiple aggregates, composite FOR, multiple    * values with and without aliases). */
annotation|@
name|Test
name|void
name|testPivot2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT deptno, job, sal\n"
operator|+
literal|"    FROM   emp)\n"
operator|+
literal|"PIVOT (SUM(sal) AS sum_sal, COUNT(*) AS \"COUNT\"\n"
operator|+
literal|"    FOR (job, deptno)\n"
operator|+
literal|"    IN (('CLERK', 10),\n"
operator|+
literal|"        ('MANAGER', 20) mgr20,\n"
operator|+
literal|"        ('ANALYST', 10) AS \"a10\"))\n"
operator|+
literal|"ORDER BY deptno"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT `DEPTNO`, `JOB`, `SAL`\n"
operator|+
literal|"FROM `EMP`) PIVOT (SUM(`SAL`) AS `SUM_SAL`, COUNT(*) AS `COUNT` "
operator|+
literal|"FOR (`JOB`, `DEPTNO`) "
operator|+
literal|"IN (('CLERK', 10),"
operator|+
literal|" ('MANAGER', 20) AS `MGR20`,"
operator|+
literal|" ('ANALYST', 10) AS `a10`))\n"
operator|+
literal|"ORDER BY `DEPTNO`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    partition by type, price\n"
operator|+
literal|"    order by type asc, price desc\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PARTITION BY `TYPE`, `PRICE`\n"
operator|+
literal|"ORDER BY `TYPE`, `PRICE` DESC\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+$)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)) $)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (^^strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (^ ((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (^^strt down+ up+$)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (^ ((`STRT` (`DOWN` +)) (`UP` +)) $)\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from (select * from t) match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down* up?)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `T`) MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` *)) (`UP` ?)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt {-down-} up?)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` ({- `DOWN` -})) (`UP` ?)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize7
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down{2} up{3,})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` { 2 })) (`UP` { 3, })))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize8
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down{,2} up{3,5})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` { , 2 })) (`UP` { 3, 5 })))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize9
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt {-down+-} {-up*-})\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` ({- (`DOWN` +) -})) ({- (`UP` *) -})))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize10
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern ( A B C | A C B | B A C | B C A | C A B | C B A)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      A as A.price> PREV(A.price),\n"
operator|+
literal|"      B as B.price< prev(B.price),\n"
operator|+
literal|"      C as C.price> prev(C.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN ((((((((`A` `B`) `C`) | ((`A` `C`) `B`)) | ((`B` `A`) `C`)) "
operator|+
literal|"| ((`B` `C`) `A`)) | ((`C` `A`) `B`)) | ((`C` `B`) `A`)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`A` AS (`A`.`PRICE`> PREV(`A`.`PRICE`, 1)), "
operator|+
literal|"`B` AS (`B`.`PRICE`< PREV(`B`.`PRICE`, 1)), "
operator|+
literal|"`C` AS (`C`.`PRICE`> PREV(`C`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognize11
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize (\n"
operator|+
literal|"    pattern ( \"a\" \"b c\")\n"
operator|+
literal|"    define\n"
operator|+
literal|"      \"A\" as A.price> PREV(A.price),\n"
operator|+
literal|"      \"b c\" as \"b c\".foo\n"
operator|+
literal|"  ) as mr(c1, c2) join e as x on foo = baz"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN ((`a` `b c`))\n"
operator|+
literal|"DEFINE `A` AS (`A`.`PRICE`> PREV(`A`.`PRICE`, 1)),"
operator|+
literal|" `b c` AS `b c`.`FOO`) AS `MR` (`C1`, `C2`)\n"
operator|+
literal|"INNER JOIN `E` AS `X` ON (`FOO` = `BAZ`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeDefineClause
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> NEXT(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> NEXT(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeDefineClause2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< FIRST(down.price),\n"
operator|+
literal|"      up as up.price> LAST(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< FIRST(`DOWN`.`PRICE`, 0)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> LAST(`UP`.`PRICE`, 0))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeDefineClause3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price,1),\n"
operator|+
literal|"      up as up.price> LAST(up.price + up.TAX)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> LAST((`UP`.`PRICE` + `UP`.`TAX`), 0))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeDefineClause4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price,1),\n"
operator|+
literal|"      up as up.price> PREV(LAST(up.price + up.TAX),3)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(LAST((`UP`.`PRICE` + `UP`.`TAX`), 0), 3))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"   MATCH_NUMBER() as match_num,"
operator|+
literal|"   CLASSIFIER() as var_match,"
operator|+
literal|"   STRT.ts as start_ts,"
operator|+
literal|"   LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   LAST(up.ts) as end_ts"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES (MATCH_NUMBER ()) AS `MATCH_NUM`, "
operator|+
literal|"(CLASSIFIER()) AS `VAR_MATCH`, "
operator|+
literal|"`STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"LAST(`UP`.`TS`, 0) AS `END_TS`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"  FINAL LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   LAST(up.ts) as end_ts"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"FINAL LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"LAST(`UP`.`TS`, 0) AS `END_TS`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"  RUNNING LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   LAST(up.ts) as end_ts"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"RUNNING LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"LAST(`UP`.`TS`, 0) AS `END_TS`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"  FINAL count(up.ts) as up_ts,"
operator|+
literal|"  FINAL count(ts) as total_ts,"
operator|+
literal|"  RUNNING count(ts) as cnt_ts,"
operator|+
literal|"  price - strt.price as price_dif"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES FINAL COUNT(`UP`.`TS`) AS `UP_TS`, "
operator|+
literal|"FINAL COUNT(`TS`) AS `TOTAL_TS`, "
operator|+
literal|"RUNNING COUNT(`TS`) AS `CNT_TS`, "
operator|+
literal|"(`PRICE` - `STRT`.`PRICE`) AS `PRICE_DIF`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))) AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"  FIRST(STRT.ts) as strt_ts,"
operator|+
literal|"  LAST(DOWN.ts) as down_ts,"
operator|+
literal|"  AVG(DOWN.ts) as avg_down_ts"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES FIRST(`STRT`.`TS`, 0) AS `STRT_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `DOWN_TS`, "
operator|+
literal|"AVG(`DOWN`.`TS`) AS `AVG_DOWN_TS`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeMeasures6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures "
operator|+
literal|"  FIRST(STRT.ts) as strt_ts,"
operator|+
literal|"  LAST(DOWN.ts) as down_ts,"
operator|+
literal|"  FINAL SUM(DOWN.ts) as sum_down_ts"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES FIRST(`STRT`.`TS`, 0) AS `STRT_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `DOWN_TS`, "
operator|+
literal|"FINAL SUM(`DOWN`.`TS`) AS `SUM_DOWN_TS`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip to next row\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP TO NEXT ROW\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip past last row\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP PAST LAST ROW\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip to FIRST down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP TO FIRST `DOWN`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip to LAST down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST `DOWN`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip to down\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST `DOWN`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2993">[CALCITE-2993]    * ParseException may be thrown for legal SQL queries due to incorrect    * "LOOKAHEAD(1)" hints</a>. */
annotation|@
name|Test
name|void
name|testMatchRecognizePatternSkip6
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"     after match skip to last\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"AFTER MATCH SKIP TO LAST `LAST`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeSubset1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE "
operator|+
literal|"`DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeSubset2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"   LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   AVG(stdn.price) as stdn_avg"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"AVG(`STDN`.`PRICE`) AS `STDN_AVG`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeSubset3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"   LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   AVG(stdn.price) as stdn_avg"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"AVG(`STDN`.`PRICE`) AS `STDN_AVG`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`)), (`STDN2` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeRowsPerMatch1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"   LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   AVG(stdn.price) as stdn_avg"
operator|+
literal|"   ONE ROW PER MATCH"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"AVG(`STDN`.`PRICE`) AS `STDN_AVG`\n"
operator|+
literal|"ONE ROW PER MATCH\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`)), (`STDN2` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeRowsPerMatch2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"   measures STRT.ts as start_ts,"
operator|+
literal|"   LAST(DOWN.ts) as bottom_ts,"
operator|+
literal|"   AVG(stdn.price) as stdn_avg"
operator|+
literal|"   ALL ROWS PER MATCH"
operator|+
literal|"    pattern (strt down+ up+)\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"AVG(`STDN`.`PRICE`) AS `STDN_AVG`\n"
operator|+
literal|"ALL ROWS PER MATCH\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +)))\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`)), (`STDN2` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeWithin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"  from t match_recognize\n"
operator|+
literal|"  (\n"
operator|+
literal|"    order by rowtime\n"
operator|+
literal|"    measures STRT.ts as start_ts,\n"
operator|+
literal|"      LAST(DOWN.ts) as bottom_ts,\n"
operator|+
literal|"      AVG(stdn.price) as stdn_avg\n"
operator|+
literal|"    pattern (strt down+ up+) within interval '3' second\n"
operator|+
literal|"    subset stdn = (strt, down), stdn2 = (strt, down)\n"
operator|+
literal|"    define\n"
operator|+
literal|"      down as down.price< PREV(down.price),\n"
operator|+
literal|"      up as up.price> prev(up.price)\n"
operator|+
literal|"  ) mr"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T` MATCH_RECOGNIZE(\n"
operator|+
literal|"ORDER BY `ROWTIME`\n"
operator|+
literal|"MEASURES `STRT`.`TS` AS `START_TS`, "
operator|+
literal|"LAST(`DOWN`.`TS`, 0) AS `BOTTOM_TS`, "
operator|+
literal|"AVG(`STDN`.`PRICE`) AS `STDN_AVG`\n"
operator|+
literal|"PATTERN (((`STRT` (`DOWN` +)) (`UP` +))) WITHIN INTERVAL '3' SECOND\n"
operator|+
literal|"SUBSET (`STDN` = (`STRT`, `DOWN`)), (`STDN2` = (`STRT`, `DOWN`))\n"
operator|+
literal|"DEFINE `DOWN` AS (`DOWN`.`PRICE`< PREV(`DOWN`.`PRICE`, 1)), "
operator|+
literal|"`UP` AS (`UP`.`PRICE`> PREV(`UP`.`PRICE`, 1))"
operator|+
literal|") AS `MR`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithinGroupClause1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select col1,\n"
operator|+
literal|" collect(col2) within group (order by col3)\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by col1 limit 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `COL1`,"
operator|+
literal|" COLLECT(`COL2`) WITHIN GROUP (ORDER BY `COL3`)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `COL1`\n"
operator|+
literal|"FETCH NEXT 10 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithinGroupClause2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select collect(col2) within group (order by col3)\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by col1 limit 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT"
operator|+
literal|" COLLECT(`COL2`) WITHIN GROUP (ORDER BY `COL3`)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `COL1`\n"
operator|+
literal|"FETCH NEXT 10 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithinGroupClause3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select collect(col2) within group (^)^ "
operator|+
literal|"from t order by col1 limit 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\)\" at line 1, column 36\\..*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithinGroupClause4
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select col1,\n"
operator|+
literal|" collect(col2) within group (order by col3, col4)\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by col1 limit 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `COL1`,"
operator|+
literal|" COLLECT(`COL2`) WITHIN GROUP (ORDER BY `COL3`, `COL4`)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `COL1`\n"
operator|+
literal|"FETCH NEXT 10 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWithinGroupClause5
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select col1,\n"
operator|+
literal|" collect(col2) within group (\n"
operator|+
literal|"  order by col3 desc nulls first, col4 asc nulls last)\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by col1 limit 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `COL1`, COLLECT(`COL2`) "
operator|+
literal|"WITHIN GROUP (ORDER BY `COL3` DESC NULLS FIRST, `COL4` NULLS LAST)\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `COL1`\n"
operator|+
literal|"FETCH NEXT 10 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonValueExpressionOperator
parameter_list|()
block|{
name|expr
argument_list|(
literal|"foo format json"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO` FORMAT JSON"
argument_list|)
expr_stmt|;
comment|// Currently, encoding js not valid
name|expr
argument_list|(
literal|"foo format json encoding utf8"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO` FORMAT JSON"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo format json encoding utf16"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO` FORMAT JSON"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"foo format json encoding utf32"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"`FOO` FORMAT JSON"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"null format json"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"NULL FORMAT JSON"
argument_list|)
expr_stmt|;
comment|// Test case to eliminate choice conflict on token<FORMAT>
name|sql
argument_list|(
literal|"select foo format from tab"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `FOO` AS `FORMAT`\n"
operator|+
literal|"FROM `TAB`"
argument_list|)
expr_stmt|;
comment|// Test case to eliminate choice conflict on token<ENCODING>
name|sql
argument_list|(
literal|"select foo format json encoding from tab"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `FOO` FORMAT JSON AS `ENCODING`\n"
operator|+
literal|"FROM `TAB`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonExists
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_exists('{\"foo\": \"bar\"}', 'lax $.foo')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_EXISTS('{\"foo\": \"bar\"}', 'lax $.foo')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_exists('{\"foo\": \"bar\"}', 'lax $.foo' error on error)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_EXISTS('{\"foo\": \"bar\"}', 'lax $.foo' ERROR ON ERROR)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonValue
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_value('{\"foo\": \"100\"}', 'lax $.foo' "
operator|+
literal|"returning integer)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_VALUE('{\"foo\": \"100\"}', 'lax $.foo' "
operator|+
literal|"RETURNING INTEGER)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\": \"100\"}', 'lax $.foo' "
operator|+
literal|"returning integer default 10 on empty error on error)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_VALUE('{\"foo\": \"100\"}', 'lax $.foo' "
operator|+
literal|"RETURNING INTEGER DEFAULT 10 ON EMPTY ERROR ON ERROR)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonQuery
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' WITHOUT ARRAY WRAPPER)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' WITH WRAPPER)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITH UNCONDITIONAL ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' WITH UNCONDITIONAL WRAPPER)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITH UNCONDITIONAL ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' WITH CONDITIONAL WRAPPER)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITH CONDITIONAL ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' NULL ON EMPTY)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' ERROR ON EMPTY)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER ERROR ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' EMPTY ARRAY ON EMPTY)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER EMPTY ARRAY ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' EMPTY OBJECT ON EMPTY)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER EMPTY OBJECT ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' NULL ON ERROR)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY NULL ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' ERROR ON ERROR)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY ERROR ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' EMPTY ARRAY ON ERROR)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY EMPTY ARRAY ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' EMPTY OBJECT ON ERROR)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER NULL ON EMPTY EMPTY OBJECT ON ERROR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\": \"bar\"}', 'lax $' EMPTY ARRAY ON EMPTY "
operator|+
literal|"EMPTY OBJECT ON ERROR)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_QUERY('{\"foo\": \"bar\"}', "
operator|+
literal|"'lax $' WITHOUT ARRAY WRAPPER EMPTY ARRAY ON EMPTY EMPTY OBJECT ON ERROR)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonObject
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_object('foo': 'bar')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE 'bar' NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': 'bar', 'foo2': 'bar2')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE 'bar', KEY 'foo2' VALUE 'bar2' NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo' value 'bar')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE 'bar' NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object(key 'foo' value 'bar')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE 'bar' NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE NULL NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': null absent on null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE NULL ABSENT ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': json_object('foo': 'bar') format json)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY 'foo' VALUE "
operator|+
literal|"JSON_OBJECT(KEY 'foo' VALUE 'bar' NULL ON NULL) "
operator|+
literal|"FORMAT JSON NULL ON NULL)"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
return|return;
block|}
comment|// "LOOKAHEAD(2) list = JsonNameAndValue()" does not generate
comment|// valid LOOKAHEAD codes for the case "key: value".
comment|//
comment|// You can see the generated codes that are located at method
comment|// SqlParserImpl#JsonObjectFunctionCall. Looking ahead fails
comment|// immediately after seeking the tokens<KEY> and<COLON>.
name|expr
argument_list|(
literal|"json_object(key: value)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECT(KEY `KEY` VALUE `VALUE` NULL ON NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_type('11.56')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_TYPE('11.56')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_type('{}')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_TYPE('{}')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_type(null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_TYPE(NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_type('[\"foo\",null]')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_TYPE('[\"foo\",null]')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_type('{\"foo\": \"100\"}')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_TYPE('{\"foo\": \"100\"}')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonDepth
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_depth('11.56')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_DEPTH('11.56')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth('{}')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_DEPTH('{}')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth(null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_DEPTH(NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth('[\"foo\",null]')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_DEPTH('[\"foo\",null]')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth('{\"foo\": \"100\"}')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_DEPTH('{\"foo\": \"100\"}')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonLength
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_length('{\"foo\": \"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_LENGTH('{\"foo\": \"bar\"}')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\": \"bar\"}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_LENGTH('{\"foo\": \"bar\"}', 'lax $')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\": \"bar\"}', 'strict $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_LENGTH('{\"foo\": \"bar\"}', 'strict $')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\": \"bar\"}', 'invalid $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_LENGTH('{\"foo\": \"bar\"}', 'invalid $')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonKeys
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_keys('{\"foo\": \"bar\"}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_KEYS('{\"foo\": \"bar\"}', 'lax $')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_keys('{\"foo\": \"bar\"}', 'strict $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_KEYS('{\"foo\": \"bar\"}', 'strict $')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_keys('{\"foo\": \"bar\"}', 'invalid $')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_KEYS('{\"foo\": \"bar\"}', 'invalid $')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonRemove
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_REMOVE('[\"a\", [\"b\", \"c\"], \"d\"]', '$')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$[1]', '$[0]')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_REMOVE('[\"a\", [\"b\", \"c\"], \"d\"]', '$[1]', '$[0]')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonObjectAgg
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_objectagg(k_column: v_column)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE `V_COLUMN` NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg(k_column value v_column)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE `V_COLUMN` NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg(key k_column value v_column)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE `V_COLUMN` NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg(k_column: null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE NULL NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg(k_column: null absent on null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE NULL ABSENT ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg(k_column: json_object(k_column: v_column) format json)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_OBJECTAGG(KEY `K_COLUMN` VALUE "
operator|+
literal|"JSON_OBJECT(KEY `K_COLUMN` VALUE `V_COLUMN` NULL ON NULL) "
operator|+
literal|"FORMAT JSON NULL ON NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonArray
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_array('foo')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAY('foo' ABSENT ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_array(null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAY(NULL ABSENT ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_array(null null on null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAY(NULL NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_array(json_array('foo', 'bar') format json)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAY(JSON_ARRAY('foo', 'bar' ABSENT ON NULL) FORMAT JSON ABSENT ON NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonPretty
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_pretty('foo')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_PRETTY('foo')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_pretty(null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_PRETTY(NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonStorageSize
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_storage_size('foo')"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_STORAGE_SIZE('foo')"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_storage_size(null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_STORAGE_SIZE(NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonArrayAgg1
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_arrayagg(\"column\")"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAYAGG(`column` ABSENT ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_arrayagg(\"column\" null on null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAYAGG(`column` NULL ON NULL)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_arrayagg(json_array(\"column\") format json)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAYAGG(JSON_ARRAY(`column` ABSENT ON NULL) FORMAT JSON ABSENT ON NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonArrayAgg2
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_arrayagg(\"column\" order by \"column\")"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAYAGG(`column` ABSENT ON NULL) WITHIN GROUP (ORDER BY `column`)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_arrayagg(\"column\") within group (order by \"column\")"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"JSON_ARRAYAGG(`column` ABSENT ON NULL) WITHIN GROUP (ORDER BY `column`)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^json_arrayagg(\"column\" order by \"column\") within group (order by \"column\")^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Including both WITHIN GROUP\\(\\.\\.\\.\\) and inside ORDER BY "
operator|+
literal|"in a single JSON_ARRAYAGG call is not allowed.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonPredicate
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'{}' is json"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS JSON VALUE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is json value"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS JSON VALUE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is json object"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS JSON OBJECT)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'[]' is json array"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('[]' IS JSON ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'100' is json scalar"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('100' IS JSON SCALAR)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS NOT JSON VALUE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json value"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS NOT JSON VALUE)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json object"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('{}' IS NOT JSON OBJECT)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'[]' is not json array"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('[]' IS NOT JSON ARRAY)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'100' is not json scalar"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"('100' IS NOT JSON SCALAR)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testParseWithReader
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|query
init|=
literal|"select * from dual"
decl_stmt|;
name|SqlParser
name|sqlParserReader
init|=
name|getSqlParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|,
name|b
lambda|->
name|b
argument_list|)
decl_stmt|;
name|SqlNode
name|node1
init|=
name|sqlParserReader
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
name|SqlParser
name|sqlParserString
init|=
name|getSqlParser
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SqlNode
name|node2
init|=
name|sqlParserString
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|node2
operator|.
name|toString
argument_list|()
argument_list|,
name|node1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConfigureFromDialect
parameter_list|()
block|{
comment|// Calcite's default converts unquoted identifiers to upper case
name|sql
argument_list|(
literal|"select unquotedColumn from \"doubleQuotedTable\""
argument_list|)
operator|.
name|withDialect
argument_list|(
name|CALCITE
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT \"UNQUOTEDCOLUMN\"\n"
operator|+
literal|"FROM \"doubleQuotedTable\""
argument_list|)
expr_stmt|;
comment|// MySQL leaves unquoted identifiers unchanged
name|sql
argument_list|(
literal|"select unquotedColumn from `doubleQuotedTable`"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `unquotedColumn`\n"
operator|+
literal|"FROM `doubleQuotedTable`"
argument_list|)
expr_stmt|;
comment|// Oracle converts unquoted identifiers to upper case
name|sql
argument_list|(
literal|"select unquotedColumn from \"doubleQuotedTable\""
argument_list|)
operator|.
name|withDialect
argument_list|(
name|ORACLE
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT \"UNQUOTEDCOLUMN\"\n"
operator|+
literal|"FROM \"doubleQuotedTable\""
argument_list|)
expr_stmt|;
comment|// PostgreSQL converts unquoted identifiers to lower case
name|sql
argument_list|(
literal|"select unquotedColumn from \"doubleQuotedTable\""
argument_list|)
operator|.
name|withDialect
argument_list|(
name|POSTGRESQL
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT \"unquotedcolumn\"\n"
operator|+
literal|"FROM \"doubleQuotedTable\""
argument_list|)
expr_stmt|;
comment|// Redshift converts all identifiers to lower case
name|sql
argument_list|(
literal|"select unquotedColumn from \"doubleQuotedTable\""
argument_list|)
operator|.
name|withDialect
argument_list|(
name|REDSHIFT
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT \"unquotedcolumn\"\n"
operator|+
literal|"FROM \"doublequotedtable\""
argument_list|)
expr_stmt|;
comment|// BigQuery leaves quoted and unquoted identifiers unchanged
name|sql
argument_list|(
literal|"select unquotedColumn from `doubleQuotedTable`"
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT unquotedColumn\n"
operator|+
literal|"FROM doubleQuotedTable"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4230">[CALCITE-4230]    * In Babel for BigQuery, split quoted table names that contain dots</a>. */
annotation|@
name|Test
name|void
name|testSplitIdentifier
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from `bigquery-public-data.samples.natality`"
decl_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select *\n"
operator|+
literal|"from `bigquery-public-data`.`samples`.`natality`"
decl_stmt|;
specifier|final
name|String
name|expectedSplit
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `bigquery-public-data`.samples.natality"
decl_stmt|;
specifier|final
name|String
name|expectedNoSplit
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `bigquery-public-data.samples.natality`"
decl_stmt|;
specifier|final
name|String
name|expectedSplitMysql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `bigquery-public-data`.`samples`.`natality`"
decl_stmt|;
comment|// In BigQuery, an identifier containing dots is split into sub-identifiers.
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSplit
argument_list|)
expr_stmt|;
comment|// In MySQL, identifiers are not split.
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedNoSplit
argument_list|)
expr_stmt|;
comment|// Query with split identifiers produces split AST. No surprise there.
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withDialect
argument_list|(
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSplit
argument_list|)
expr_stmt|;
comment|// Similar to previous; we just quote simple identifiers on unparse.
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withDialect
argument_list|(
name|MYSQL
argument_list|)
operator|.
name|ok
argument_list|(
name|expectedSplitMysql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testParenthesizedSubQueries
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `TAB`) AS `X`"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"SELECT * FROM (((SELECT * FROM tab))) X"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT * FROM ((((((((((((SELECT * FROM tab)))))))))))) X"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQueryHint
parameter_list|()
block|{
specifier|final
name|String
name|sql1
init|=
literal|"select "
operator|+
literal|"/*+ properties(k1='v1', k2='v2', 'a.b.c'='v3'), "
operator|+
literal|"no_hash_join, Index(idx1, idx2), "
operator|+
literal|"repartition(3) */ "
operator|+
literal|"empno, ename, deptno from emps"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2', 'a.b.c' = 'v3'), "
operator|+
literal|"`NO_HASH_JOIN`, "
operator|+
literal|"`INDEX`(`IDX1`, `IDX2`), "
operator|+
literal|"`REPARTITION`(3) */\n"
operator|+
literal|"`EMPNO`, `ENAME`, `DEPTNO`\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
comment|// Hint item right after the token "/*+"
specifier|final
name|String
name|sql2
init|=
literal|"select /*+properties(k1='v1', k2='v2')*/ empno from emps"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2') */\n"
operator|+
literal|"`EMPNO`\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
comment|// Hint item without parentheses
specifier|final
name|String
name|sql3
init|=
literal|"select /*+ simple_hint */ empno, ename, deptno from emps limit 2"
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT\n"
operator|+
literal|"/*+ `SIMPLE_HINT` */\n"
operator|+
literal|"`EMPNO`, `ENAME`, `DEPTNO`\n"
operator|+
literal|"FROM `EMPS`\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableHintsInQuery
parameter_list|()
block|{
specifier|final
name|String
name|hint
init|=
literal|"/*+ PROPERTIES(K1 ='v1', K2 ='v2'), INDEX(IDX0, IDX1) */"
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select * from t %s"
argument_list|,
name|hint
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|(
name|expected1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select * from\n"
operator|+
literal|"(select * from t %s union all select * from t %s )"
argument_list|,
name|hint
argument_list|,
name|hint
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select * from t %s join t %s"
argument_list|,
name|hint
argument_list|,
name|hint
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */\n"
operator|+
literal|"INNER JOIN `T`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableHintsInInsert
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into emps\n"
operator|+
literal|"/*+ PROPERTIES(k1='v1', k2='v2'), INDEX(idx0, idx1) */\n"
operator|+
literal|"select * from emps"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"INSERT INTO `EMPS`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX0`, `IDX1`) */\n"
operator|+
literal|"(SELECT *\n"
operator|+
literal|"FROM `EMPS`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableHintsInDelete
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emps\n"
operator|+
literal|"/*+ properties(k1='v1', k2='v2'), index(idx1, idx2), no_hash_join */\n"
operator|+
literal|"where empno=12"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"DELETE FROM `EMPS`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), `INDEX`(`IDX1`, `IDX2`), `NO_HASH_JOIN` */\n"
operator|+
literal|"WHERE (`EMPNO` = 12)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableHintsInUpdate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emps\n"
operator|+
literal|"/*+ properties(k1='v1', k2='v2'), index(idx1, idx2), no_hash_join */\n"
operator|+
literal|"set empno = empno + 1, sal = sal - 1\n"
operator|+
literal|"where empno=12"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"UPDATE `EMPS`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), "
operator|+
literal|"`INDEX`(`IDX1`, `IDX2`), `NO_HASH_JOIN` */ "
operator|+
literal|"SET `EMPNO` = (`EMPNO` + 1)"
operator|+
literal|", `SAL` = (`SAL` - 1)\n"
operator|+
literal|"WHERE (`EMPNO` = 12)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableHintsInMerge
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"merge into emps\n"
operator|+
literal|"/*+ properties(k1='v1', k2='v2'), index(idx1, idx2), no_hash_join */ e\n"
operator|+
literal|"using tempemps as t\n"
operator|+
literal|"on e.empno = t.empno\n"
operator|+
literal|"when matched then update\n"
operator|+
literal|"set name = t.name, deptno = t.deptno, salary = t.salary * .1\n"
operator|+
literal|"when not matched then insert (name, dept, salary)\n"
operator|+
literal|"values(t.name, 10, t.salary * .15)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"MERGE INTO `EMPS`\n"
operator|+
literal|"/*+ `PROPERTIES`(`K1` = 'v1', `K2` = 'v2'), "
operator|+
literal|"`INDEX`(`IDX1`, `IDX2`), `NO_HASH_JOIN` */ "
operator|+
literal|"AS `E`\n"
operator|+
literal|"USING `TEMPEMPS` AS `T`\n"
operator|+
literal|"ON (`E`.`EMPNO` = `T`.`EMPNO`)\n"
operator|+
literal|"WHEN MATCHED THEN UPDATE SET `NAME` = `T`.`NAME`"
operator|+
literal|", `DEPTNO` = `T`.`DEPTNO`"
operator|+
literal|", `SALARY` = (`T`.`SALARY` * 0.1)\n"
operator|+
literal|"WHEN NOT MATCHED THEN INSERT (`NAME`, `DEPT`, `SALARY`) "
operator|+
literal|"(VALUES (ROW(`T`.`NAME`, 10, (`T`.`SALARY` * 0.15))))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInvalidHintFormat
parameter_list|()
block|{
specifier|final
name|String
name|sql1
init|=
literal|"select "
operator|+
literal|"/*+ properties(^k1^=123, k2='v2'), no_hash_join() */ "
operator|+
literal|"empno, ename, deptno from emps"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"k1 = 123\" at .*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select "
operator|+
literal|"/*+ properties(k1, k2^=^'v2'), no_hash_join */ "
operator|+
literal|"empno, ename, deptno from emps"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"=\" at line 1, column 29.\n.*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select "
operator|+
literal|"/*+ no_hash_join() */ "
operator|+
literal|"empno, ename, deptno from emps"
decl_stmt|;
comment|// Allow empty options.
specifier|final
name|String
name|expected3
init|=
literal|"SELECT\n"
operator|+
literal|"/*+ `NO_HASH_JOIN` */\n"
operator|+
literal|"`EMPNO`, `ENAME`, `DEPTNO`\n"
operator|+
literal|"FROM `EMPS`"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|(
name|expected3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"select "
operator|+
literal|"/*+ properties(^a^.b.c=123, k2='v2') */"
operator|+
literal|"empno, ename, deptno from emps"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"a .\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link Hoist}. */
annotation|@
name|Test
specifier|protected
name|void
name|testHoist
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 as x,\n"
operator|+
literal|"  'ab' || 'c' as y\n"
operator|+
literal|"from emp /* comment with 'quoted string'? */ as e\n"
operator|+
literal|"where deptno< 40\n"
operator|+
literal|"and hiredate> date '2010-05-06'"
decl_stmt|;
specifier|final
name|Hoist
operator|.
name|Hoisted
name|hoisted
init|=
name|Hoist
operator|.
name|create
argument_list|(
name|Hoist
operator|.
name|config
argument_list|()
argument_list|)
operator|.
name|hoist
argument_list|(
name|sql
argument_list|)
decl_stmt|;
comment|// Simple toString converts each variable to '?N'
specifier|final
name|String
name|expected
init|=
literal|"select ?0 as x,\n"
operator|+
literal|"  ?1 || ?2 as y\n"
operator|+
literal|"from emp /* comment with 'quoted string'? */ as e\n"
operator|+
literal|"where deptno< ?3\n"
operator|+
literal|"and hiredate> ?4"
decl_stmt|;
name|assertThat
argument_list|(
name|hoisted
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
comment|// As above, using the function explicitly.
name|assertThat
argument_list|(
name|hoisted
operator|.
name|substitute
argument_list|(
name|Hoist
operator|::
name|ordinalString
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simple toString converts each variable to '?N'
specifier|final
name|String
name|expected1
init|=
literal|"select 1 as x,\n"
operator|+
literal|"  ?1 || ?2 as y\n"
operator|+
literal|"from emp /* comment with 'quoted string'? */ as e\n"
operator|+
literal|"where deptno< 40\n"
operator|+
literal|"and hiredate> date '2010-05-06'"
decl_stmt|;
name|assertThat
argument_list|(
name|hoisted
operator|.
name|substitute
argument_list|(
name|Hoist
operator|::
name|ordinalStringIfChar
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Custom function converts variables to '[N:TYPE:VALUE]'
specifier|final
name|String
name|expected2
init|=
literal|"select [0:DECIMAL:1] as x,\n"
operator|+
literal|"  [1:CHAR:ab] || [2:CHAR:c] as y\n"
operator|+
literal|"from emp /* comment with 'quoted string'? */ as e\n"
operator|+
literal|"where deptno< [3:DECIMAL:40]\n"
operator|+
literal|"and hiredate> [4:DATE:2010-05-06]"
decl_stmt|;
name|assertThat
argument_list|(
name|hoisted
operator|.
name|substitute
argument_list|(
name|SqlParserTest
operator|::
name|varToStr
argument_list|)
argument_list|,
name|is
argument_list|(
name|expected2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|String
name|varToStr
parameter_list|(
name|Hoist
operator|.
name|Variable
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|.
name|node
operator|instanceof
name|SqlLiteral
condition|)
block|{
name|SqlLiteral
name|literal
init|=
operator|(
name|SqlLiteral
operator|)
name|v
operator|.
name|node
decl_stmt|;
return|return
literal|"["
operator|+
name|v
operator|.
name|ordinal
operator|+
literal|":"
operator|+
name|literal
operator|.
name|getTypeName
argument_list|()
operator|+
literal|":"
operator|+
name|literal
operator|.
name|toValue
argument_list|()
operator|+
literal|"]"
return|;
block|}
else|else
block|{
return|return
literal|"["
operator|+
name|v
operator|.
name|ordinal
operator|+
literal|"]"
return|;
block|}
block|}
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**    * Callback to control how test actions are performed.    */
specifier|protected
interface|interface
name|Tester
block|{
name|void
name|checkList
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
function_decl|;
name|void
name|check
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
function_decl|;
name|void
name|checkExp
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
function_decl|;
name|void
name|checkFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|boolean
name|list
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
name|void
name|checkExpFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
name|void
name|checkNode
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|matcher
parameter_list|)
function_decl|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Default implementation of {@link Tester}.    */
specifier|protected
class|class
name|TesterImpl
implements|implements
name|Tester
block|{
specifier|private
name|void
name|check
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|SqlDialect
name|dialect2
init|=
name|Util
operator|.
name|first
argument_list|(
name|dialect
argument_list|,
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|SqlWriterConfig
name|c2
init|=
name|SQL_WRITER_CONFIG
operator|.
name|withDialect
argument_list|(
name|dialect2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|c
lambda|->
name|c2
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkList
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
specifier|final
name|SqlNodeList
name|sqlNodeList
init|=
name|parseStmtsAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sqlNodeList
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|)
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
name|sqlNodeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|sqlNode
init|=
name|sqlNodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|sqlNode
argument_list|,
literal|null
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|check
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseStmtAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|transform
argument_list|,
name|parserChecker
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|sqlNode
argument_list|,
name|dialect
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseStmtAndHandleEx
parameter_list|(
name|String
name|sql
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|Reader
name|reader
init|=
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|getSqlParser
argument_list|(
name|reader
argument_list|,
name|transform
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parser
operator|.
name|parseStmt
argument_list|()
expr_stmt|;
name|parserChecker
operator|.
name|accept
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while parsing SQL: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|sqlNode
return|;
block|}
comment|/** Parses a list of statements. */
specifier|protected
name|SqlNodeList
name|parseStmtsAndHandleEx
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|SqlNodeList
name|sqlNodeList
decl_stmt|;
try|try
block|{
name|sqlNodeList
operator|=
name|getSqlParser
argument_list|(
name|sql
argument_list|)
operator|.
name|parseStmtList
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while parsing SQL: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|sqlNodeList
return|;
block|}
specifier|public
name|void
name|checkExp
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parseExpressionAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|transform
argument_list|,
name|parserChecker
argument_list|)
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlNode
name|parseExpressionAndHandleEx
parameter_list|(
name|String
name|sql
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
specifier|final
name|SqlParser
name|parser
init|=
name|getSqlParser
argument_list|(
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
argument_list|,
name|transform
argument_list|)
decl_stmt|;
name|sqlNode
operator|=
name|parser
operator|.
name|parseExpression
argument_list|()
expr_stmt|;
name|parserChecker
operator|.
name|accept
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while parsing expression: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|sqlNode
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|boolean
name|list
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|reader
init|=
operator|new
name|SourceStringReader
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|getSqlParser
argument_list|(
name|reader
argument_list|,
name|transform
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
condition|)
block|{
name|sqlNode
operator|=
name|parser
operator|.
name|parseStmtList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|sqlNode
operator|=
name|parser
operator|.
name|parseStmt
argument_list|()
expr_stmt|;
block|}
name|Util
operator|.
name|discard
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
name|checkEx
argument_list|(
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkNode
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|matcher
parameter_list|)
block|{
try|try
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|reader
init|=
operator|new
name|SourceStringReader
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|getSqlParser
argument_list|(
name|reader
argument_list|,
name|transform
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parser
operator|.
name|parseStmt
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sqlNode
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
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
comment|/**      * Tests that an expression throws an exception which matches the given      * pattern.      */
annotation|@
name|Override
specifier|public
name|void
name|checkExpFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
specifier|final
name|Reader
name|reader
init|=
operator|new
name|SourceStringReader
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|getSqlParser
argument_list|(
name|reader
argument_list|,
name|transform
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|sqlNode
init|=
name|parser
operator|.
name|parseExpression
argument_list|()
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
name|checkEx
argument_list|(
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkEx
parameter_list|(
name|String
name|expectedMsgPattern
parameter_list|,
name|StringAndPos
name|sap
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
name|SqlTests
operator|.
name|checkEx
argument_list|(
name|thrown
argument_list|,
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|,
name|SqlTests
operator|.
name|Stage
operator|.
name|VALIDATE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isNotSubclass
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|SqlParserTest
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Implementation of {@link Tester} which makes sure that the results of    * unparsing a query are consistent with the original query.    */
specifier|public
class|class
name|UnparsingTesterImpl
extends|extends
name|TesterImpl
block|{
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|simple
parameter_list|()
block|{
return|return
name|c
lambda|->
name|c
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|false
argument_list|)
operator|.
name|withUpdateSetListNewline
argument_list|(
literal|false
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|0
argument_list|)
operator|.
name|withFromFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
return|;
block|}
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|simpleWithParens
parameter_list|()
block|{
return|return
name|simple
argument_list|()
operator|.
name|andThen
argument_list|(
name|withParens
argument_list|()
argument_list|)
operator|::
name|apply
return|;
block|}
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|simpleWithParensAnsi
parameter_list|()
block|{
return|return
name|simpleWithParens
argument_list|()
operator|.
name|andThen
argument_list|(
name|withAnsi
argument_list|()
argument_list|)
operator|::
name|apply
return|;
block|}
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|withParens
parameter_list|()
block|{
return|return
name|c
lambda|->
name|c
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|withAnsi
parameter_list|()
block|{
return|return
name|c
lambda|->
name|c
operator|.
name|withDialect
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
specifier|private
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|randomize
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|c
lambda|->
name|c
operator|.
name|withFoldLength
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|*
literal|20
operator|+
literal|3
argument_list|)
operator|.
name|withHavingFolding
argument_list|(
name|nextLineFolding
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|withWhereFolding
argument_list|(
name|nextLineFolding
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|withSelectFolding
argument_list|(
name|nextLineFolding
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|withFromFolding
argument_list|(
name|nextLineFolding
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|withGroupByFolding
argument_list|(
name|nextLineFolding
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|withClauseStartsLine
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|toSqlString
parameter_list|(
name|SqlNodeList
name|sqlNodeList
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|sqlNodeList
operator|.
name|getList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|node
lambda|->
name|node
operator|.
name|toSqlString
argument_list|(
name|transform
argument_list|)
operator|.
name|getSql
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|";"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|SqlWriterConfig
operator|.
name|LineFolding
name|nextLineFolding
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|nextEnum
argument_list|(
name|random
argument_list|,
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|class
argument_list|)
return|;
block|}
specifier|private
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|nextEnum
parameter_list|(
name|Random
name|random
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
block|{
specifier|final
name|E
index|[]
name|constants
init|=
name|enumClass
operator|.
name|getEnumConstants
argument_list|()
decl_stmt|;
return|return
name|constants
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|constants
operator|.
name|length
argument_list|)
index|]
return|;
block|}
specifier|private
name|void
name|checkList
parameter_list|(
name|SqlNodeList
name|sqlNodeList
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|sqlNodeList
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|)
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
name|sqlNodeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|sqlNode
init|=
name|sqlNodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Unparse with no dialect, always parenthesize.
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|simpleWithParensAnsi
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|linux
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkList
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
name|SqlNodeList
name|sqlNodeList
init|=
name|parseStmtsAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
decl_stmt|;
name|checkList
argument_list|(
name|sqlNodeList
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// Unparse again in Calcite dialect (which we can parse), and
comment|// minimal parentheses.
specifier|final
name|String
name|sql1
init|=
name|toSqlString
argument_list|(
name|sqlNodeList
argument_list|,
name|simple
argument_list|()
argument_list|)
decl_stmt|;
comment|// Parse and unparse again.
name|SqlNodeList
name|sqlNodeList2
decl_stmt|;
specifier|final
name|Quoting
name|q
init|=
name|quoting
decl_stmt|;
try|try
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|DOUBLE_QUOTE
expr_stmt|;
name|sqlNodeList2
operator|=
name|parseStmtsAndHandleEx
argument_list|(
name|sql1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|quoting
operator|=
name|q
expr_stmt|;
block|}
specifier|final
name|String
name|sql2
init|=
name|toSqlString
argument_list|(
name|sqlNodeList2
argument_list|,
name|simple
argument_list|()
argument_list|)
decl_stmt|;
comment|// Should be the same as we started with.
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
comment|// Now unparse again in the null dialect.
comment|// If the unparser is not including sufficient parens to override
comment|// precedence, the problem will show up here.
name|checkList
argument_list|(
name|sqlNodeList2
argument_list|,
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql3
init|=
name|toSqlString
argument_list|(
name|sqlNodeList
argument_list|,
name|randomize
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sql3
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|check
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
init|=
name|parseStmtAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|transform
argument_list|,
name|parserChecker
argument_list|)
decl_stmt|;
comment|// Unparse with the given dialect, always parenthesize.
specifier|final
name|SqlDialect
name|dialect2
init|=
name|Util
operator|.
name|first
argument_list|(
name|dialect
argument_list|,
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform2
init|=
name|simpleWithParens
argument_list|()
operator|.
name|andThen
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withDialect
argument_list|(
name|dialect2
argument_list|)
argument_list|)
operator|::
name|apply
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|transform2
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
comment|// Unparse again in Calcite dialect (which we can parse), and
comment|// minimal parentheses.
specifier|final
name|String
name|sql1
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|simple
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Parse and unparse again.
name|SqlNode
name|sqlNode2
decl_stmt|;
specifier|final
name|Quoting
name|q
init|=
name|quoting
decl_stmt|;
try|try
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|DOUBLE_QUOTE
expr_stmt|;
name|sqlNode2
operator|=
name|parseStmtAndHandleEx
argument_list|(
name|sql1
argument_list|,
name|b
lambda|->
name|b
argument_list|,
name|parser
lambda|->
block|{ }
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|quoting
operator|=
name|q
expr_stmt|;
block|}
specifier|final
name|String
name|sql2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
name|simple
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Should be the same as we started with.
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
comment|// Now unparse again in the given dialect.
comment|// If the unparser is not including sufficient parens to override
comment|// precedence, the problem will show up here.
specifier|final
name|String
name|actual2
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|transform2
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now unparse with a randomly configured SqlPrettyWriter.
comment|// (This is a much a test for SqlPrettyWriter as for the parser.)
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql3
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|randomize
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sql3
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|SqlNode
name|sqlNode4
decl_stmt|;
try|try
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|DOUBLE_QUOTE
expr_stmt|;
name|sqlNode4
operator|=
name|parseStmtAndHandleEx
argument_list|(
name|sql1
argument_list|,
name|b
lambda|->
name|b
argument_list|,
name|parser
lambda|->
block|{ }
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|quoting
operator|=
name|q
expr_stmt|;
block|}
specifier|final
name|String
name|sql4
init|=
name|sqlNode4
operator|.
name|toSqlString
argument_list|(
name|simple
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkExp
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expected
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
init|=
name|getTransform
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
init|=
name|parseExpressionAndHandleEx
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|transform
argument_list|,
name|parserChecker
argument_list|)
decl_stmt|;
comment|// Unparse with no dialect, always parenthesize.
specifier|final
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform2
init|=
name|c
lambda|->
name|simpleWithParens
argument_list|()
operator|.
name|apply
argument_list|(
name|c
argument_list|)
operator|.
name|withDialect
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|transform2
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
comment|// Unparse again in Calcite dialect (which we can parse), and
comment|// minimal parentheses.
specifier|final
name|String
name|sql1
init|=
name|sqlNode
operator|.
name|toSqlString
argument_list|(
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Parse and unparse again.
comment|// (Turn off parser checking, and use double-quotes.)
name|SqlNode
name|sqlNode2
decl_stmt|;
specifier|final
name|Quoting
name|q
init|=
name|quoting
decl_stmt|;
try|try
block|{
name|quoting
operator|=
name|Quoting
operator|.
name|DOUBLE_QUOTE
expr_stmt|;
name|sqlNode2
operator|=
name|parseExpressionAndHandleEx
argument_list|(
name|sql1
argument_list|,
name|transform
argument_list|,
name|parser
lambda|->
block|{
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|quoting
operator|=
name|q
expr_stmt|;
block|}
specifier|final
name|String
name|sql2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
name|UnaryOperator
operator|.
name|identity
argument_list|()
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
comment|// Should be the same as we started with.
name|assertEquals
argument_list|(
name|sql1
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
comment|// Now unparse again in the null dialect.
comment|// If the unparser is not including sufficient parens to override
comment|// precedence, the problem will show up here.
specifier|final
name|String
name|actual2
init|=
name|sqlNode2
operator|.
name|toSqlString
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|linux
argument_list|(
name|actual2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|boolean
name|list
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
comment|// Do nothing. We're not interested in unparsing invalid SQL
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkExpFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
comment|// Do nothing. We're not interested in unparsing invalid SQL
block|}
block|}
comment|/** Converts a string to linux format (LF line endings rather than CR-LF),    * except if disabled in {@link #LINUXIFY}. */
specifier|private
name|String
name|linux
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|LINUXIFY
operator|.
name|get
argument_list|()
index|[
literal|0
index|]
condition|)
block|{
name|s
operator|=
name|Util
operator|.
name|toLinux
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/** Helper class for building fluent code such as    * {@code sql("values 1").ok();}. */
specifier|protected
class|class
name|Sql
block|{
specifier|private
specifier|final
name|StringAndPos
name|sap
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|expression
decl_stmt|;
specifier|private
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
specifier|private
specifier|final
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
decl_stmt|;
name|Sql
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|boolean
name|expression
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
name|this
operator|.
name|sap
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sap
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|parserChecker
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parserChecker
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sql
name|same
parameter_list|()
block|{
return|return
name|ok
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|ok
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
if|if
condition|(
name|expression
condition|)
block|{
name|getTester
argument_list|()
operator|.
name|checkExp
argument_list|(
name|sap
argument_list|,
name|dialect
argument_list|,
name|expected
argument_list|,
name|parserChecker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getTester
argument_list|()
operator|.
name|check
argument_list|(
name|sap
argument_list|,
name|dialect
argument_list|,
name|expected
argument_list|,
name|parserChecker
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|fails
parameter_list|(
name|String
name|expectedMsgPattern
parameter_list|)
block|{
if|if
condition|(
name|expression
condition|)
block|{
name|getTester
argument_list|()
operator|.
name|checkExpFails
argument_list|(
name|sap
argument_list|,
name|dialect
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getTester
argument_list|()
operator|.
name|checkFails
argument_list|(
name|sap
argument_list|,
name|dialect
argument_list|,
literal|false
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|hasWarning
parameter_list|(
name|Consumer
argument_list|<
name|List
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
argument_list|>
name|messageMatcher
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sap
argument_list|,
name|expression
argument_list|,
name|dialect
argument_list|,
name|parser
lambda|->
name|messageMatcher
operator|.
name|accept
argument_list|(
name|parser
operator|.
name|getWarnings
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|node
parameter_list|(
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|matcher
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkNode
argument_list|(
name|sap
argument_list|,
name|dialect
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Flags that this is an expression, not a whole query. */
specifier|public
name|Sql
name|expression
parameter_list|()
block|{
return|return
name|expression
condition|?
name|this
else|:
operator|new
name|Sql
argument_list|(
name|sap
argument_list|,
literal|true
argument_list|,
name|dialect
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withDialect
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sap
argument_list|,
name|expression
argument_list|,
name|dialect
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
block|}
comment|/** Helper class for building fluent code,    * similar to {@link Sql}, but used to manipulate    * a list of statements, such as    * {@code sqlList("select * from a;").ok();}. */
specifier|protected
class|class
name|SqlList
block|{
specifier|private
specifier|final
name|StringAndPos
name|sap
decl_stmt|;
name|SqlList
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sap
operator|=
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlList
name|ok
parameter_list|(
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkList
argument_list|(
name|sap
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|SqlList
name|fails
parameter_list|(
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|checkFails
argument_list|(
name|sap
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/** Runs tests on period operators such as OVERLAPS, IMMEDIATELY PRECEDES. */
specifier|private
class|class
name|Checker
block|{
specifier|final
name|String
name|op
decl_stmt|;
specifier|final
name|String
name|period
decl_stmt|;
name|Checker
parameter_list|(
name|String
name|op
parameter_list|,
name|String
name|period
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
block|}
specifier|public
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|expr
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"$op"
argument_list|,
name|op
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$p"
argument_list|,
name|period
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
operator|.
name|replace
argument_list|(
literal|"$op"
argument_list|,
name|op
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|expr
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"$op"
argument_list|,
name|op
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$p"
argument_list|,
name|period
argument_list|)
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
operator|.
name|replace
argument_list|(
literal|"$op"
argument_list|,
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

