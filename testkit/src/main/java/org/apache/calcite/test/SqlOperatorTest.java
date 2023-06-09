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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|function
operator|.
name|Function2
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
name|tree
operator|.
name|Types
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
name|plan
operator|.
name|Strong
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
name|type
operator|.
name|DelegatingTypeSystem
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|RelDataTypeSystem
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
name|type
operator|.
name|TimeFrameSet
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
name|CalciteContextException
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
name|CalciteException
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
name|SqlAggFunction
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
name|SqlCallBinding
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
name|SqlFunction
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
name|SqlJdbcFunctionCall
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
name|SqlOperandCountRange
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
name|SqlOperator
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
name|SqlSyntax
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
name|fun
operator|.
name|SqlLibrary
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
name|fun
operator|.
name|SqlLibraryOperators
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|SqlParseException
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
name|SqlParserPos
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
name|AbstractSqlTester
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
name|SqlOperatorFixture
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
name|SqlOperatorFixture
operator|.
name|VmName
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
name|SqlTestFactory
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
name|SqlTester
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
name|type
operator|.
name|BasicSqlType
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
name|type
operator|.
name|SqlOperandTypeChecker
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
name|type
operator|.
name|SqlTypeName
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
name|type
operator|.
name|SqlTypeUtil
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
name|SqlString
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
name|sql
operator|.
name|validate
operator|.
name|SqlNameMatchers
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
name|SqlValidatorImpl
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
name|SqlValidatorScope
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
name|TimestampString
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
name|TryThreadLocal
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
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
name|Throwables
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Tag
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
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|params
operator|.
name|ParameterizedTest
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
name|params
operator|.
name|provider
operator|.
name|Arguments
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
name|params
operator|.
name|provider
operator|.
name|MethodSource
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
name|params
operator|.
name|provider
operator|.
name|ValueSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Calendar
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|regex
operator|.
name|Pattern
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
name|Stream
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
operator|.
name|list
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
operator|.
name|NON_NULLABLE_SUFFIX
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
operator|.
name|PI
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
operator|.
name|QUANTIFY_OPERATORS
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isExactly
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isNullValue
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isSet
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isSingle
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isWithin
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|BAD_DATETIME_MESSAGE
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|DIVISION_BY_ZERO_MESSAGE
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|INVALID_ARGUMENTS_NUMBER
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|INVALID_CHAR_MESSAGE
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|LITERAL_OUT_OF_RANGE_MESSAGE
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
name|sql
operator|.
name|test
operator|.
name|SqlOperatorFixture
operator|.
name|OUT_OF_RANGE_MESSAGE
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
name|DateTimeStringUtils
operator|.
name|getDateFormatter
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
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
comment|/**  * Contains unit tests for all operators. Each of the methods is named after an  * operator.  *  *<p>To run, you also need an execution mechanism: parse, validate, and execute  * expressions on the operators. This is left to a {@link SqlTester} object  * which is obtained via the {@link #fixture()} method. The default tester  * merely validates calls to operators, but {@code CalciteSqlOperatorTest}  * uses a tester that executes calls and checks that results are valid.  *  *<p>Different implementations of {@link SqlTester} are possible, such as:  *  *<ul>  *<li>Execute against a JDBC database;  *<li>Parse and validate but do not evaluate expressions;  *<li>Generate a SQL script;  *<li>Analyze which operators are adequately tested.  *</ul>  *  *<p>A typical method will be named after the operator it is testing (say  *<code>testSubstringFunc</code>). It first calls  * {@link SqlOperatorFixture#setFor(SqlOperator, VmName...)}  * to declare which operator it is testing.  *  *<blockquote>  *<pre><code>  * public void testSubstringFunc() {  *     tester.setFor(SqlStdOperatorTable.substringFunc);  *     tester.checkScalar("sin(0)", "0");  *     tester.checkScalar("sin(1.5707)", "1");  * }</code></pre>  *</blockquote>  *  *<p>The rest of the method contains calls to the various {@code checkXxx}  * methods in the {@link SqlTester} interface. For an operator  * to be adequately tested, there need to be tests for:  *  *<ul>  *<li>Parsing all of its the syntactic variants.  *<li>Deriving the type of in all combinations of arguments.  *  *<ul>  *<li>Pay particular attention to nullability. For example, the result of the  * "+" operator is NOT NULL if and only if both of its arguments are NOT  * NULL.</li>  *<li>Also pay attention to precision/scale/length. For example, the maximum  * length of the "||" operator is the sum of the maximum lengths of its  * arguments.</li>  *</ul>  *</li>  *<li>Executing the function. Pay particular attention to corner cases such as  * null arguments or null results.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"MethodCanBeStatic"
argument_list|)
specifier|public
class|class
name|SqlOperatorTest
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|TesterImpl
name|TESTER
init|=
operator|new
name|TesterImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getTestTracer
argument_list|(
name|SqlOperatorTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|TODO
init|=
literal|false
decl_stmt|;
comment|/**    * Regular expression for a SQL TIME(0) value.    */
specifier|public
specifier|static
specifier|final
name|Pattern
name|TIME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9][0-9]:[0-9][0-9]:[0-9][0-9]"
argument_list|)
decl_stmt|;
comment|/**    * Regular expression for a SQL TIMESTAMP(0) value.    */
specifier|public
specifier|static
specifier|final
name|Pattern
name|TIMESTAMP_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] "
operator|+
literal|"[0-9][0-9]:[0-9][0-9]:[0-9][0-9]"
argument_list|)
decl_stmt|;
comment|/**    * Regular expression for a SQL DATE value.    */
specifier|public
specifier|static
specifier|final
name|Pattern
name|DATE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|MICROSECOND_VARIANTS
init|=
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
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|NANOSECOND_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"NANOSECOND"
argument_list|,
literal|"SQL_TSI_FRAC_SECOND"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|SECOND_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"SECOND"
argument_list|,
literal|"SQL_TSI_SECOND"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|MINUTE_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"MINUTE"
argument_list|,
literal|"SQL_TSI_MINUTE"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|HOUR_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"HOUR"
argument_list|,
literal|"SQL_TSI_HOUR"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|DAY_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"DAY"
argument_list|,
literal|"SQL_TSI_DAY"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|WEEK_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"WEEK"
argument_list|,
literal|"SQL_TSI_WEEK"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|MONTH_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"MONTH"
argument_list|,
literal|"SQL_TSI_MONTH"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|QUARTER_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"QUARTER"
argument_list|,
literal|"SQL_TSI_QUARTER"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|YEAR_VARIANTS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"YEAR"
argument_list|,
literal|"SQL_TSI_YEAR"
argument_list|)
decl_stmt|;
comment|/** Minimum and maximum values for each exact and approximate numeric    * type. */
enum|enum
name|Numeric
block|{
name|TINYINT
argument_list|(
literal|"TINYINT"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MIN_VALUE
operator|-
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
argument_list|)
block|,
name|SMALLINT
argument_list|(
literal|"SMALLINT"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MIN_VALUE
operator|-
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
argument_list|)
block|,
name|INTEGER
argument_list|(
literal|"INTEGER"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MIN_VALUE
operator|-
literal|1
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
argument_list|)
block|,
name|BIGINT
argument_list|(
literal|"BIGINT"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
operator|new
name|BigDecimal
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
operator|.
name|subtract
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
operator|new
name|BigDecimal
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|add
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
block|,
name|DECIMAL5_2
argument_list|(
literal|"DECIMAL(5, 2)"
argument_list|,
literal|"-999.99"
argument_list|,
literal|"-1000.00"
argument_list|,
literal|"999.99"
argument_list|,
literal|"1000.00"
argument_list|)
block|,
name|REAL
argument_list|(
literal|"REAL"
argument_list|,
literal|"1E-37"
argument_list|,
comment|// or Float.toString(Float.MIN_VALUE)
literal|"1e-46"
argument_list|,
literal|"3.4028234E38"
argument_list|,
comment|// or Float.toString(Float.MAX_VALUE)
literal|"1e39"
argument_list|)
block|,
name|FLOAT
argument_list|(
literal|"FLOAT"
argument_list|,
literal|"2E-307"
argument_list|,
comment|// or Double.toString(Double.MIN_VALUE)
literal|"1e-324"
argument_list|,
literal|"1.79769313486231E308"
argument_list|,
comment|// or Double.toString(Double.MAX_VALUE)
literal|"-1e309"
argument_list|)
block|,
name|DOUBLE
argument_list|(
literal|"DOUBLE"
argument_list|,
literal|"2E-307"
argument_list|,
comment|// or Double.toString(Double.MIN_VALUE)
literal|"1e-324"
argument_list|,
literal|"1.79769313486231E308"
argument_list|,
comment|// or Double.toString(Double.MAX_VALUE)
literal|"1e309"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|typeName
decl_stmt|;
comment|/** For Float and Double Java types, MIN_VALUE      * is the smallest positive value, not the smallest negative value.      * For REAL, FLOAT, DOUBLE, Win32 takes smaller values from      * win32_values.h. */
specifier|private
specifier|final
name|String
name|minNumericString
decl_stmt|;
specifier|private
specifier|final
name|String
name|minOverflowNumericString
decl_stmt|;
comment|/** For REAL, FLOAT and DOUBLE SQL types (Flaot and Double Java types), we      * use something slightly less than MAX_VALUE because round-tripping string      * to approx to string doesn't preserve MAX_VALUE on win32. */
specifier|private
specifier|final
name|String
name|maxNumericString
decl_stmt|;
specifier|private
specifier|final
name|String
name|maxOverflowNumericString
decl_stmt|;
name|Numeric
parameter_list|(
name|String
name|typeName
parameter_list|,
name|String
name|minNumericString
parameter_list|,
name|String
name|minOverflowNumericString
parameter_list|,
name|String
name|maxNumericString
parameter_list|,
name|String
name|maxOverflowNumericString
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|minNumericString
operator|=
name|minNumericString
expr_stmt|;
name|this
operator|.
name|minOverflowNumericString
operator|=
name|minOverflowNumericString
expr_stmt|;
name|this
operator|.
name|maxNumericString
operator|=
name|maxNumericString
expr_stmt|;
name|this
operator|.
name|maxOverflowNumericString
operator|=
name|maxOverflowNumericString
expr_stmt|;
block|}
comment|/** Calls a consumer for each value. Similar effect to a {@code for}      * loop, but the calling line number will show up in the call stack. */
specifier|static
name|void
name|forEach
parameter_list|(
name|Consumer
argument_list|<
name|Numeric
argument_list|>
name|consumer
parameter_list|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|TINYINT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|SMALLINT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|INTEGER
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|BIGINT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|DECIMAL5_2
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|REAL
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|FLOAT
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|DOUBLE
argument_list|)
expr_stmt|;
block|}
name|double
name|maxNumericAsDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|maxNumericString
argument_list|)
return|;
block|}
name|double
name|minNumericAsDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|minNumericString
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|boolean
index|[]
name|FALSE_TRUE
init|=
block|{
literal|false
block|,
literal|true
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|VmName
name|VM_FENNEL
init|=
name|VmName
operator|.
name|FENNEL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|VmName
name|VM_JAVA
init|=
name|VmName
operator|.
name|JAVA
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|VmName
name|VM_EXPAND
init|=
name|VmName
operator|.
name|EXPAND
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|TimeZone
name|UTC_TZ
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
decl_stmt|;
comment|// time zone for the LOCAL_{DATE,TIME,TIMESTAMP} functions
specifier|protected
specifier|static
specifier|final
name|TimeZone
name|LOCAL_TZ
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|// time zone for the CURRENT{DATE,TIME,TIMESTAMP} functions
specifier|protected
specifier|static
specifier|final
name|TimeZone
name|CURRENT_TZ
init|=
name|LOCAL_TZ
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|INVALID_ARG_FOR_POWER
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s).*Invalid argument\\(s\\) for 'POWER' function.*"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|CODE_2201F
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s).*could not calculate results for the following "
operator|+
literal|"row.*PC=5 Code=2201F.*"
argument_list|)
decl_stmt|;
comment|/**    * Whether DECIMAL type is implemented.    */
specifier|public
specifier|static
specifier|final
name|boolean
name|DECIMAL
init|=
literal|false
decl_stmt|;
comment|/** Function object that returns a string with 2 copies of each character.    * For example, {@code DOUBLER.apply("xy")} returns {@code "xxyy"}. */
specifier|private
specifier|static
specifier|final
name|UnaryOperator
argument_list|<
name|String
argument_list|>
name|DOUBLER
init|=
operator|new
name|UnaryOperator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.)"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"$1$1"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Sub-classes should override to run tests in a different environment. */
specifier|protected
name|SqlOperatorFixture
name|fixture
parameter_list|()
block|{
return|return
name|SqlOperatorFixtureImpl
operator|.
name|DEFAULT
return|;
block|}
comment|//--- Tests -----------------------------------------------------------
comment|/**    * For development. Put any old code in here.    */
annotation|@
name|Test
name|void
name|testDummy
parameter_list|()
block|{
block|}
annotation|@
name|Test
name|void
name|testSqlOperatorOverloading
parameter_list|()
block|{
specifier|final
name|SqlStdOperatorTable
name|operatorTable
init|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlOperator
name|sqlOperator
range|:
name|operatorTable
operator|.
name|getOperatorList
argument_list|()
control|)
block|{
name|String
name|operatorName
init|=
name|sqlOperator
operator|.
name|getName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|routines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlIdentifier
name|id
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|operatorName
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|operatorTable
operator|.
name|lookupOperatorOverloads
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|sqlOperator
operator|.
name|getSyntax
argument_list|()
argument_list|,
name|routines
argument_list|,
name|SqlNameMatchers
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|routines
operator|.
name|removeIf
argument_list|(
name|operator
lambda|->
operator|!
name|sqlOperator
operator|.
name|getClass
argument_list|()
operator|.
name|isInstance
argument_list|(
name|operator
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routines
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sqlOperator
argument_list|,
name|equalTo
argument_list|(
name|routines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testBetween
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BETWEEN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2 between 1 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2 between 3 and 2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2 between symmetric 3 and 2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"3 between 1 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"4 between 1 and 3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 between 4 and -3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 between -1 and -3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 between -1 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 between 1 and 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 1 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2 between 1.1 and 1.3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 2 and 3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 1.6 and 1.7"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1 between 1.1 and 1.3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0 between 1.1 and 1.3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 between 2 and 3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 between 2e0 and 3e0"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e1 between 1.6e1 and 1.7e1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'' between x'' and x''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer) between -1 and 2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"1 between -1 and cast(null as integer)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"1 between cast(null as integer) and cast(null as integer)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"1 between cast(null as integer) and 1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' between x'0A000130' and x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' between x'0A0001A0' and x'0A0001B0'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotBetween
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_BETWEEN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2 not between 1 and 3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"3 not between 1 and 3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"4 not between 1 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0 not between 1.1 and 1.3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1 not between 1.1 and 1.3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 not between 2 and 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 not between 2e0 and 3e0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' not between x'0A000130' and x'0A0001B0'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' not between x'0A0001A0' and x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that CAST and SAFE_CAST are basically equivalent but SAFE_CAST is    * only available in BigQuery library. */
annotation|@
name|Test
name|void
name|testCastVsSafeCast
parameter_list|()
block|{
comment|// SAFE_CAST is available in BigQuery library but not by default
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f0
operator|.
name|checkScalar
argument_list|(
literal|"cast(12 + 3 as varchar(10))"
argument_list|,
literal|"15"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^safe_cast(12 + 3 as varchar(10))^"
argument_list|,
literal|"No match found for function signature SAFE_CAST\\(<NUMERIC>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(12 + 3 as varchar(10))"
argument_list|,
literal|"15"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"safe_cast(12 + 3 as varchar(10))"
argument_list|,
literal|"15"
argument_list|,
literal|"VARCHAR(10)"
argument_list|)
expr_stmt|;
block|}
comment|/** Generates parameters to test both regular and safe cast. */
specifier|static
name|Stream
argument_list|<
name|Arguments
argument_list|>
name|safeParameters
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|SqlOperatorFixtureImpl
operator|.
name|DEFAULT
decl_stmt|;
name|SqlOperatorFixture
name|f2
init|=
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|SqlOperatorFixture
name|f3
init|=
name|SqlOperatorFixtures
operator|.
name|safeCastWrapper
argument_list|(
name|f2
argument_list|)
decl_stmt|;
return|return
name|Stream
operator|.
name|of
argument_list|(
parameter_list|()
lambda|->
operator|new
name|Object
index|[]
block|{
literal|false
block|,
name|f
block|}
argument_list|,
parameter_list|()
lambda|->
operator|new
name|Object
index|[]
block|{
literal|true
block|,
name|f3
block|}
argument_list|)
return|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastToString
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(cast('abc' as char(4)) as varchar(6))"
argument_list|,
literal|null
argument_list|,
literal|"abc "
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// integer
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"123"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"123"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"0"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"0"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"-123"
argument_list|,
literal|"CHAR(4)"
argument_list|,
literal|"-123"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// decimal
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"123.4"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"123.4"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"-0.0"
argument_list|,
literal|"CHAR(2)"
argument_list|,
literal|".0"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"-123.4"
argument_list|,
literal|"CHAR(6)"
argument_list|,
literal|"-123.4"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(1.29 as varchar(10))"
argument_list|,
literal|"1.29"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(.48 as varchar(10))"
argument_list|,
literal|".48"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(2.523 as char(2))"
argument_list|,
literal|"2."
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(-0.29 as varchar(10))"
argument_list|,
literal|"-.29"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(-1.29 as varchar(10))"
argument_list|,
literal|"-1.29"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// approximate
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"1.23E45"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"1.23E45"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"CAST(0 AS DOUBLE)"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"0E0"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"-1.20e-07"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"-1.2E-7"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(0e0 as varchar(5))"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"0E0"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(-45e-2 as varchar(17))"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"-4.5E-1"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(4683442.3432498375e0 as varchar(20))"
argument_list|,
literal|"CHAR(19)"
argument_list|,
literal|"4.683442343249838E6"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(-0.1 as real)"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"-1E-1"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(1.3243232e0 as varchar(4))"
argument_list|,
literal|"1.32"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(1.9e5 as char(4))"
argument_list|,
literal|"1.9E"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// string
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"'abc'"
argument_list|,
literal|"CHAR(1)"
argument_list|,
literal|"a"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"'abc'"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"abc"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast('abc' as varchar(6))"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"abc"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(' abc  ' as varchar(10))"
argument_list|,
literal|null
argument_list|,
literal|" abc  "
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"cast(cast('abc' as char(4)) as varchar(6))"
argument_list|,
literal|null
argument_list|,
literal|"abc "
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(cast('a' as char(2)) as varchar(3)) || 'x' "
argument_list|,
literal|"a x"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(cast('a' as char(3)) as varchar(5)) || 'x' "
argument_list|,
literal|"a  x"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast('a' as char(3)) || 'x'"
argument_list|,
literal|"a  x"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char_length(cast(' x ' as char(4)))"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char_length(cast(' x ' as varchar(3)))"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char_length(cast(' x ' as varchar(4)))"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char_length(cast(cast(' x ' as char(4)) as varchar(5)))"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char_length(cast(' x ' as varchar(3)))"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
comment|// date& time
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"date '2008-01-01'"
argument_list|,
literal|"CHAR(10)"
argument_list|,
literal|"2008-01-01"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"time '1:2:3'"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"01:02:03"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"timestamp '2008-1-1 1:2:3'"
argument_list|,
literal|"CHAR(19)"
argument_list|,
literal|"2008-01-01 01:02:03"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"timestamp '2008-1-1 1:2:3'"
argument_list|,
literal|"VARCHAR(30)"
argument_list|,
literal|"2008-01-01 01:02:03"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"interval '3-2' year to month"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"+3-02"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"interval '32' month"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"+32"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"interval '1 2:3:4' day to second"
argument_list|,
literal|"CHAR(11)"
argument_list|,
literal|"+1 02:03:04"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"interval '1234.56' second(4,2)"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"+1234.56"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"interval '60' day"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"+60     "
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// boolean
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"CHAR(4)"
argument_list|,
literal|"TRUE"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"CHAR(6)"
argument_list|,
literal|"TRUE  "
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"VARCHAR(6)"
argument_list|,
literal|"TRUE"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"False"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"FALSE"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(true as char(3))"
argument_list|,
literal|"TRU"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(false as char(4))"
argument_list|,
literal|"FALS"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(true as varchar(3))"
argument_list|,
literal|"TRU"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast(false as varchar(4))"
argument_list|,
literal|"FALS"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastExactNumericLimits
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// Test casting for min,max, out of range for exact numeric types
name|Numeric
operator|.
name|forEach
argument_list|(
name|numeric
lambda|->
block|{
specifier|final
name|String
name|type
init|=
name|numeric
operator|.
name|typeName
decl_stmt|;
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
name|DOUBLE
case|:
case|case
name|FLOAT
case|:
case|case
name|REAL
case|:
comment|// Skip approx types
return|return;
default|default:
comment|// fall through
block|}
comment|// Convert from literal to type
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|type
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
name|type
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// Overflow test
if|if
condition|(
name|numeric
operator|==
name|Numeric
operator|.
name|BIGINT
condition|)
block|{
comment|// Literal of range
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|maxOverflowNumericString
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|minOverflowNumericString
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|maxOverflowNumericString
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|minOverflowNumericString
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Convert from string to type
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|maxNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|minNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|numeric
operator|.
name|minNumericString
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|maxOverflowNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|minOverflowNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
comment|// Convert from type to string
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkCastFails
argument_list|(
literal|"'notnumeric'"
argument_list|,
name|type
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastToExactNumeric
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"BIGINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"SMALLINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"TINYINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"DECIMAL(4, 0)"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"BIGINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"INTEGER"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"SMALLINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"TINYINT"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"DECIMAL(4, 0)"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"1.234E3"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1234"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"-9.99E2"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-999"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"'1'"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"' 01 '"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"'-1'"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-1"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToScalarOkay
argument_list|(
literal|"' -00 '"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"0"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// string to integer
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('6543' as integer)"
argument_list|,
literal|6543
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(' -123 ' as int)"
argument_list|,
operator|-
literal|123
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('654342432412312' as bigint)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|,
literal|"654342432412312"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastStringToDecimal
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
comment|// string to decimal
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('1.29' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(' 1.25 ' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('1.21' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"1.2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(' -1.29 ' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('-1.25' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(' -1.21 ' as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(' -1.21e' as decimal(2,1))"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastIntervalToNumeric
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// interval to decimal
if|if
condition|(
name|DECIMAL
condition|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1.29' second(1,2) as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1.25' second as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-1.29' second as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-1.25' second as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-1.21' second as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-1.2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' minute as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' hour as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' day as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' month as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' year as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-5' day as decimal(2,1))"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
literal|"-5.0"
argument_list|)
expr_stmt|;
block|}
comment|// Interval to bigint
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1.25' second as bigint)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-1.29' second(1,2) as bigint)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' day as bigint)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
comment|// Interval to integer
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1.25' second as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '-1.29' second(1,2) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '5' day as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' year as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' year - INTERVAL '2' year) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' month as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' month - INTERVAL '2' month) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' day as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' day - INTERVAL '2' day) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' hour as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' hour - INTERVAL '2' hour) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' hour as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' minute - INTERVAL '2' minute) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(INTERVAL '1' minute as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast((INTERVAL '1' second - INTERVAL '2' second) as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastToInterval
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval second)"
argument_list|,
literal|"+5.000000"
argument_list|,
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval minute)"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval hour)"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval day)"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval month)"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5 as interval year)"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DECIMAL
condition|)
block|{
comment|// Due to DECIMAL rounding bugs, currently returns "+5"
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(5.7 as interval day)"
argument_list|,
literal|"+6"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(-5.7 as interval day)"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// An easier case
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(6.2 as interval day)"
argument_list|,
literal|"+6"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(3456 as interval month(4))"
argument_list|,
literal|"+3456"
argument_list|,
literal|"INTERVAL MONTH(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(-5723 as interval minute(4))"
argument_list|,
literal|"-5723"
argument_list|,
literal|"INTERVAL MINUTE(4) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastIntervalToInterval
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(interval '2 5' day to hour as interval hour to minute)"
argument_list|,
literal|"+53:00"
argument_list|,
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(interval '2 5' day to hour as interval day to minute)"
argument_list|,
literal|"+2 05:00"
argument_list|,
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(interval '2 5' day to hour as interval hour to second)"
argument_list|,
literal|"+53:00:00.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(interval '2 5' day to hour as interval hour)"
argument_list|,
literal|"+53"
argument_list|,
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(interval '-29:15' hour to minute as interval day to hour)"
argument_list|,
literal|"-1 05"
argument_list|,
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastWithRoundingToScalar
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.25 as int)"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.25E0 as int)"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.5 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5E-1 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.75 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.75E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.25 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.25E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.5 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-5E-1 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.75 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.75E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.23454 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.23454E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.23455 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5E-5 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.99995 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.99995E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.23454 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.23454E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.23455 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-5E-5 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.99995 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-1.99995E0 as int)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 9.99 round to 10.0, should give out of range error
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(9.99 as decimal(2,1))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastDecimalToDoubleToInteger
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(1.25 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(-1.25 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(1.75 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(-1.75 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(1.5 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast( cast(-1.5 as double) as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastApproxNumericLimits
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// Test casting for min, max, out of range for approx numeric types
name|Numeric
operator|.
name|forEach
argument_list|(
name|numeric
lambda|->
block|{
name|String
name|type
init|=
name|numeric
operator|.
name|typeName
decl_stmt|;
name|boolean
name|isFloat
decl_stmt|;
switch|switch
condition|(
name|numeric
condition|)
block|{
case|case
name|DOUBLE
case|:
case|case
name|FLOAT
case|:
name|isFloat
operator|=
literal|false
expr_stmt|;
break|break;
case|case
name|REAL
case|:
name|isFloat
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
comment|// Skip non-approx types
return|return;
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Convert from literal to type
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
name|isWithin
argument_list|(
name|numeric
operator|.
name|maxNumericAsDouble
argument_list|()
argument_list|,
literal|1E32
argument_list|)
else|:
name|isExactly
argument_list|(
name|numeric
operator|.
name|maxNumericAsDouble
argument_list|()
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
name|type
argument_list|,
name|isExactly
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFloat
condition|)
block|{
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|maxOverflowNumericString
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Double: Literal out of range
name|f
operator|.
name|checkCastFails
argument_list|(
name|numeric
operator|.
name|maxOverflowNumericString
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
comment|// Underflow: goes to 0
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
name|numeric
operator|.
name|minOverflowNumericString
argument_list|,
name|type
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// Convert from string to type
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|maxNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
name|isWithin
argument_list|(
name|numeric
operator|.
name|maxNumericAsDouble
argument_list|()
argument_list|,
literal|1E32
argument_list|)
else|:
name|isExactly
argument_list|(
name|numeric
operator|.
name|maxNumericAsDouble
argument_list|()
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|minNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|isExactly
argument_list|(
name|numeric
operator|.
name|minNumericAsDouble
argument_list|()
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|maxOverflowNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// Underflow: goes to 0
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|numeric
operator|.
name|minOverflowNumericString
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// Convert from type to string
comment|// Treated as DOUBLE
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
literal|null
argument_list|,
name|isFloat
condition|?
literal|null
else|:
literal|"1.79769313486231E308"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// TODO: The following tests are slightly different depending on
comment|// whether the java or fennel calc are used.
comment|// Try to make them the same
if|if
condition|(
literal|false
comment|/* fennel calc*/
condition|)
block|{
comment|// Treated as FLOAT or DOUBLE
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|type
argument_list|,
comment|// Treated as DOUBLE
name|isFloat
condition|?
literal|"3.402824E38"
else|:
literal|"1.797693134862316E308"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
literal|null
argument_list|,
comment|// Treated as FLOAT or DOUBLE
name|isFloat
condition|?
literal|null
else|:
literal|"4.940656458412465E-324"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
literal|"1.401299E-45"
else|:
literal|"4.940656458412465E-324"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|false
comment|/* JavaCalc */
condition|)
block|{
comment|// Treated as FLOAT or DOUBLE
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|maxNumericString
argument_list|,
name|type
argument_list|,
comment|// Treated as DOUBLE
name|isFloat
condition|?
literal|"3.402823E38"
else|:
literal|"1.797693134862316E308"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
literal|null
argument_list|,
name|isFloat
condition|?
literal|null
else|:
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
comment|// Treated as FLOAT or DOUBLE
name|f
operator|.
name|checkCastToString
argument_list|(
name|numeric
operator|.
name|minNumericString
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
literal|"1.401298E-45"
else|:
literal|null
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkCastFails
argument_list|(
literal|"'notnumeric'"
argument_list|,
name|type
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastToApproxNumeric
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"1"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"1.0"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"-2.3"
argument_list|,
literal|"FLOAT"
argument_list|,
name|isWithin
argument_list|(
operator|-
literal|2.3
argument_list|,
literal|0.000001
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"'1'"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"'  -1e-37  '"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"-1.0E-37"
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"1e0"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToApproxOkay
argument_list|(
literal|"0e0"
argument_list|,
literal|"REAL"
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastNull
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// null
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DECIMAL
condition|)
block|{
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as decimal(4,3))"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as double)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as varchar(10))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as char(10))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as date)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as time)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as timestamp)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval year to month)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval day to second(3))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|safe
condition|)
block|{
comment|// In the following, 'cast' becomes 'safe_cast'
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('a' as time)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('a' as int)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('2023-03-17a' as date)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('12:12:11a' as time)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('a' as interval year)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('a' as interval minute to second)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast('True' as bigint)"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1439">[CALCITE-1439]    * Handling errors during constant reduction</a>. */
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastInvalid
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
comment|// Before CALCITE-1439 was fixed, constant reduction would kick in and
comment|// generate Java constants that throw when the class is loaded, thus
comment|// ExceptionInInitializerError.
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('15' as integer)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('15.4' as integer)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('15.6' as integer)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('ue' as boolean)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('' as boolean)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('' as integer)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('' as real)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('' as double)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('' as smallint)"
argument_list|,
literal|"xxx"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test cast for DATE, TIME, TIMESTAMP types. */
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastDateTime
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIMESTAMP '1945-02-24 12:42:25.34' as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIME '12:42:25.34' as TIME)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// test rounding
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIME '12:42:25.9' as TIME)"
argument_list|,
literal|"12:42:26"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Bug
operator|.
name|FRG282_FIXED
condition|)
block|{
comment|// test precision
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIME '12:42:25.34' as TIME(2))"
argument_list|,
literal|"12:42:25.34"
argument_list|,
literal|"TIME(2) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(DATE '1945-02-24' as DATE)"
argument_list|,
literal|"1945-02-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// timestamp<-> time
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIMESTAMP '1945-02-24 12:42:25.34' as TIME)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// time<-> string
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"TIME '12:42:25'"
argument_list|,
literal|null
argument_list|,
literal|"12:42:25"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"TIME '12:42:25.34'"
argument_list|,
literal|null
argument_list|,
literal|"12:42:25.34"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
comment|// Generate the current date as a string, e.g. "2007-04-18". The value
comment|// is guaranteed to be good for at least 2 minutes, which should give
comment|// us time to run the rest of the tests.
specifier|final
name|String
name|today
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|format
argument_list|(
name|getCalendarNotTooNear
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(DATE '1945-02-24' as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Note: Casting to time(0) should lose date info and fractional
comment|// seconds, then casting back to timestamp should initialize to
comment|// current_date.
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(cast(TIMESTAMP '1945-02-24 12:42:25.34' as TIME) as TIMESTAMP)"
argument_list|,
name|today
operator|+
literal|" 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIME '12:42:25.34' as TIMESTAMP)"
argument_list|,
name|today
operator|+
literal|" 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// timestamp<-> date
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(TIMESTAMP '1945-02-24 12:42:25.34' as DATE)"
argument_list|,
literal|"1945-02-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// Note: casting to Date discards Time fields
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(cast(TIMESTAMP '1945-02-24 12:42:25.34' as DATE) as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastStringToDateTime
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:42:25' as TIME)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1:42:25' as TIME)"
argument_list|,
literal|"01:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1:2:25' as TIME)"
argument_list|,
literal|"01:02:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('  12:42:25  ' as TIME)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:42:25.34' as TIME)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG282_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:42:25.34' as TIME(2))"
argument_list|,
literal|"12:42:25.34"
argument_list|,
literal|"TIME(2) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('nottime' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1241241' as TIME)"
argument_list|,
literal|"72:40:12"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:54:78' as TIME)"
argument_list|,
literal|"12:55:18"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:34:5' as TIME)"
argument_list|,
literal|"12:34:05"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('12:3:45' as TIME)"
argument_list|,
literal|"12:03:45"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1:23:45' as TIME)"
argument_list|,
literal|"01:23:45"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// timestamp<-> string
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"TIMESTAMP '1945-02-24 12:42:25'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
comment|// TODO: casting allows one to discard precision without error
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"TIMESTAMP '1945-02-24 12:42:25.34'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24 12:42:25.34"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-02-24 12:42:25' as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-2-2 12:2:5' as TIMESTAMP)"
argument_list|,
literal|"1945-02-02 12:02:05"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('  1945-02-24 12:42:25  ' as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-02-24 12:42:25.34' as TIMESTAMP)"
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-12-31' as TIMESTAMP)"
argument_list|,
literal|"1945-12-31 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('2004-02-29' as TIMESTAMP)"
argument_list|,
literal|"2004-02-29 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG282_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-02-24 12:42:25.34' as TIMESTAMP(2))"
argument_list|,
literal|"1945-02-24 12:42:25.34"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('nottime' as TIMESTAMP)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1241241' as TIMESTAMP)"
argument_list|,
literal|"1241-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-20-24 12:42:25.34' as TIMESTAMP)"
argument_list|,
literal|"1946-08-26 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-01-24 25:42:25.34' as TIMESTAMP)"
argument_list|,
literal|"1945-01-25 01:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-1-24 12:23:34.454' as TIMESTAMP)"
argument_list|,
literal|"1945-01-24 12:23:34"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// date<-> string
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"DATE '1945-02-24'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkCastToString
argument_list|(
literal|"DATE '1945-2-24'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24"
argument_list|,
name|safe
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-02-24' as DATE)"
argument_list|,
literal|"1945-02-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast(' 1945-2-4 ' as DATE)"
argument_list|,
literal|"1945-02-04"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('  1945-02-24  ' as DATE)"
argument_list|,
literal|"1945-02-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('notdate' as DATE)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('52534253' as DATE)"
argument_list|,
literal|"7368-10-13"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cast('1945-30-24' as DATE)"
argument_list|,
literal|"1947-06-26"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// cast null
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as date)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as timestamp)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as time)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as time)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as date)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as timestamp)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as date) as timestamp)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as time) as timestamp)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as timestamp) as date)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as timestamp) as time)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMssqlConvert
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|MSSQL_CONVERT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// happy-paths (no need to test all, proper functionality is tested by CAST already
comment|// just need to make sure it works at all
name|f
operator|.
name|checkScalar
argument_list|(
literal|"convert(INTEGER, 45.4)"
argument_list|,
literal|"45"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"convert(DATE, '2000-01-01')"
argument_list|,
literal|"2000-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// null-values
name|f
operator|.
name|checkNull
argument_list|(
literal|"convert(DATE, NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMssqlConvertWithStyle
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|MSSQL_CONVERT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// ensure 'style' argument is ignored
comment|// 3rd argument 'style' is a literal. However,
comment|// AbstractSqlTester converts values to a single value in a column.
comment|// see AbstractSqlTester.buildQuery2
comment|// But CONVERT 'style' is supposed to be a literal.
comment|// So for now, they are put in a @Disabled test
name|f
operator|.
name|checkScalar
argument_list|(
literal|"convert(INTEGER, 45.4, 999)"
argument_list|,
literal|"45"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"convert(DATE, '2000-01-01', 999)"
argument_list|,
literal|"2000-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// including 'NULL' style argument
name|f
operator|.
name|checkScalar
argument_list|(
literal|"convert(DATE, '2000-01-01', NULL)"
argument_list|,
literal|"2000-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Calendar
name|getFixedCalendar
parameter_list|()
block|{
name|Calendar
name|calendar
init|=
name|Util
operator|.
name|calendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|2014
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|17
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|48
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|15
argument_list|)
expr_stmt|;
return|return
name|calendar
return|;
block|}
comment|/**    * Returns a Calendar that is the current time, pausing if we are within 2    * minutes of midnight or the top of the hour.    *    * @param timeUnit Time unit    * @return calendar    */
specifier|protected
specifier|static
name|Calendar
name|getCalendarNotTooNear
parameter_list|(
name|int
name|timeUnit
parameter_list|)
block|{
specifier|final
name|Calendar
name|cal
init|=
name|Util
operator|.
name|calendar
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
switch|switch
condition|(
name|timeUnit
condition|)
block|{
case|case
name|Calendar
operator|.
name|DAY_OF_MONTH
case|:
comment|// Within two minutes of the end of the day. Wait in 10s
comment|// increments until calendar moves into the next next day.
if|if
condition|(
operator|(
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|==
literal|23
operator|)
operator|&&
operator|(
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
operator|>=
literal|58
operator|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
continue|continue;
block|}
return|return
name|cal
return|;
case|case
name|Calendar
operator|.
name|HOUR_OF_DAY
case|:
comment|// Within two minutes of the top of the hour. Wait in 10s
comment|// increments until calendar moves into the next next day.
if|if
condition|(
name|cal
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
operator|>=
literal|58
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
continue|continue;
block|}
return|return
name|cal
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected time unit: "
operator|+
name|timeUnit
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastToBoolean
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// string to boolean
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('true' as boolean)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('false' as boolean)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  trUe' as boolean)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  tr' || 'Ue' as boolean)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  fALse' as boolean)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast('unknown' as boolean)"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(cast('true' as varchar(10))  as boolean)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(cast('false' as varchar(10)) as boolean)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(cast('blah' as varchar(10)) as boolean)"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4861">[CALCITE-4861]    * Optimisation of chained cast calls can lead to unexpected behaviour.</a>.    */
annotation|@
name|Test
name|void
name|testChainedCast
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"CAST(CAST(CAST(123456 AS TINYINT) AS INT) AS BIGINT)"
argument_list|,
literal|"Value out of range. Value:\"123456\""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCase
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'='a' then 1 end"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 2 when 1 then 'a' when 2 then 'bcd' end"
argument_list|,
literal|"bcd"
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 1 when 1 then 'a' when 2 then 'bcd' end"
argument_list|,
literal|"a  "
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 1 when 1 then cast('a' as varchar(1)) "
operator|+
literal|"when 2 then cast('bcd' as varchar(3)) end"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(3)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DECIMAL
condition|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case 2 when 1 then 11.2 "
operator|+
literal|"when 2 then 4.543 else null end"
argument_list|,
literal|"DECIMAL(5, 3)"
argument_list|,
literal|"4.543"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case 1 when 1 then 11.2 "
operator|+
literal|"when 2 then 4.543 else null end"
argument_list|,
literal|"DECIMAL(5, 3)"
argument_list|,
literal|"11.200"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case 'a' when 'a' then 1 end"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 "
operator|+
literal|"when 2 then cast(4 as bigint) else 3 end"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"11.2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 "
operator|+
literal|"when 2 then 4 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"11.2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 2 when 1 then 11.2e0 "
operator|+
literal|"when 2 then 4 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 "
operator|+
literal|"when 2 then 4.543 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"11.2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 2 when 1 then 11.2e0 "
operator|+
literal|"when 2 then 4.543 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"4.543"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"case 'a' when 'b' then 1 end"
argument_list|)
expr_stmt|;
comment|// Per spec, 'case x when y then ...'
comment|// translates to 'case when x = y then ...'
comment|// so nulls do not match.
comment|// (Unlike Oracle's 'decode(null, null, ...)', by the way.)
name|f
operator|.
name|checkString
argument_list|(
literal|"case cast(null as int)\n"
operator|+
literal|"when cast(null as int) then 'nulls match'\n"
operator|+
literal|"else 'nulls do not match' end"
argument_list|,
literal|"nulls do not match"
argument_list|,
literal|"CHAR(18) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'=cast(null as varchar(1)) then 1 "
operator|+
literal|"else 2 end"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// equivalent to "nullif('a',cast(null as varchar(1)))"
name|f
operator|.
name|checkString
argument_list|(
literal|"case when 'a' = cast(null as varchar(1)) then null "
operator|+
literal|"else 'a' end"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"case 1 when 1 then row(1,2) when 2 then row(2,3) end"
argument_list|,
literal|"ROW(INTEGER NOT NULL, INTEGER NOT NULL)"
argument_list|,
literal|"row(1,2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"case 1 when 1 then row('a','b') "
operator|+
literal|"when 2 then row('ab','cd') end"
argument_list|,
literal|"ROW(CHAR(2) NOT NULL, CHAR(2) NOT NULL)"
argument_list|,
literal|"row('a ','b ')"
argument_list|)
expr_stmt|;
block|}
comment|// multiple values in some cases (introduced in SQL:2011)
name|f
operator|.
name|checkString
argument_list|(
literal|"case 1 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"1 or 2           "
argument_list|,
literal|"CHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 2 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"1 or 2           "
argument_list|,
literal|"CHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 3 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"3                "
argument_list|,
literal|"CHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"case 4 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"none of the above"
argument_list|,
literal|"CHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
comment|// tests with SqlConformance
specifier|final
name|SqlOperatorFixture
name|f2
init|=
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 2 when 1 then 'a' when 2 then 'bcd' end"
argument_list|,
literal|"bcd"
argument_list|,
literal|"VARCHAR(3)"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 1 when 1 then 'a' when 2 then 'bcd' end"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(3)"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 1 when 1 then cast('a' as varchar(1)) "
operator|+
literal|"when 2 then cast('bcd' as varchar(3)) end"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(3)"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case cast(null as int) when cast(null as int)"
operator|+
literal|" then 'nulls match'"
operator|+
literal|" else 'nulls do not match' end"
argument_list|,
literal|"nulls do not match"
argument_list|,
literal|"VARCHAR(18) NOT NULL"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'=cast(null as varchar(1)) then 1 "
operator|+
literal|"else 2 end"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// equivalent to "nullif('a',cast(null as varchar(1)))"
name|f2
operator|.
name|checkString
argument_list|(
literal|"case when 'a' = cast(null as varchar(1)) then null "
operator|+
literal|"else 'a' end"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// multiple values in some cases (introduced in SQL:2011)
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 1 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"1 or 2"
argument_list|,
literal|"VARCHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 2 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"1 or 2"
argument_list|,
literal|"VARCHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 3 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"3"
argument_list|,
literal|"VARCHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
name|f2
operator|.
name|checkString
argument_list|(
literal|"case 4 "
operator|+
literal|"when 1, 2 then '1 or 2' "
operator|+
literal|"when 2 then 'not possible' "
operator|+
literal|"when 3, 2 then '3' "
operator|+
literal|"else 'none of the above' "
operator|+
literal|"end"
argument_list|,
literal|"none of the above"
argument_list|,
literal|"VARCHAR(17) NOT NULL"
argument_list|)
expr_stmt|;
comment|// TODO: Check case with multisets
block|}
annotation|@
name|Test
name|void
name|testCaseNull
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 1 = 1 then 10 else null end"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"case when 1 = 2 then 10 else null end"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCaseType
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"case 1 when 1 then current_timestamp else null end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"case 1 when 1 then current_timestamp "
operator|+
literal|"else current_timestamp end"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"case when true then current_timestamp else null end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"case when true then current_timestamp end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"case 'x' when 'a' then 3 when 'b' then null else 4.5 end"
argument_list|,
literal|"DECIMAL(11, 1)"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests support for JDBC functions.    *    *<p>See FRG-97 "Support for JDBC escape syntax is incomplete".    */
annotation|@
name|Test
name|void
name|testJdbcFn
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
operator|new
name|SqlJdbcFunctionCall
argument_list|(
literal|"dummy"
argument_list|)
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// There follows one test for each function in appendix C of the JDBC
comment|// 3.0 specification. The test is 'if-false'd out if the function is
comment|// not implemented or is broken.
comment|// Numeric Functions
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn ABS(-3)}"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ACOS(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.36943
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ASIN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.20135
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ATAN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.19739
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ATAN2(-2, 2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
operator|-
literal|0.78539
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CBRT(8)}"
argument_list|,
literal|2.0
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CEILING(-2.6)}"
argument_list|,
operator|-
literal|2
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn COS(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.98007
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn COT(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|4.93315
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn DEGREES(-1)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
operator|-
literal|57.29578
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn EXP(2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|7.389
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn FLOOR(2.6)}"
argument_list|,
literal|2
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn LOG(10)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.30258
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn LOG10(100)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn MOD(19, 4)}"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn PI()}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|3.14159
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn POWER(2, 3)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|8.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn RADIANS(90)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.57080
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn RAND(42)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.63708
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn ROUND(1251, -2)}"
argument_list|,
literal|1300
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^{fn ROUND(1251)}^"
argument_list|,
literal|"Cannot apply '\\{fn ROUND\\}' to "
operator|+
literal|"arguments of type '\\{fn ROUND\\}\\(<INTEGER>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn SIGN(-1)}"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn SIN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.19867
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn SQRT(4.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.04939
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn TAN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.20271
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn TRUNCATE(12.34, 1)}"
argument_list|,
literal|12.3
argument_list|,
literal|"DECIMAL(4, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn TRUNCATE(-12.34, -1)}"
argument_list|,
operator|-
literal|10
argument_list|,
literal|"DECIMAL(4, 2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// String Functions
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn ASCII('a')}"
argument_list|,
literal|97
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn ASCII('ABC')}"
argument_list|,
literal|"65"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn ASCII(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CHAR(97)}"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CONCAT('foo', 'bar')}"
argument_list|,
literal|"foobar"
argument_list|,
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn DIFFERENCE('Miller', 'miller')}"
argument_list|,
literal|"4"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn DIFFERENCE('muller', cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn REVERSE('abc')}"
argument_list|,
literal|"cba"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REVERSE(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn LEFT('abcd', 3)}"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn LEFT('abcd', 4)}"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn LEFT('abcd', 5)}"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn LEFT(cast(null as varchar(1)), 3)}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn RIGHT('abcd', 3)}"
argument_list|,
literal|"bcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn RIGHT('abcd', 4)}"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn RIGHT('abcd', 5)}"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn RIGHT(cast(null as varchar(1)), 3)}"
argument_list|)
expr_stmt|;
comment|// REVIEW: is this result correct? I think it should be "abcCdef"
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn INSERT('abc', 1, 2, 'ABCdef')}"
argument_list|,
literal|"ABCdefc"
argument_list|,
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn LCASE('foo' || 'bar')}"
argument_list|,
literal|"foobar"
argument_list|,
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn LENGTH(string)}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn LOCATE('ha', 'alphabet')}"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn LOCATE('ha', 'alphabet', 6)}"
argument_list|,
literal|0
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn LTRIM(' xxx  ')}"
argument_list|,
literal|"xxx  "
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn REPEAT('a', -100)}"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REPEAT('abc', cast(null as integer))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REPEAT(cast(null as varchar(1)), cast(null as integer))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn REPLACE('JACK and JUE','J','BL')}"
argument_list|,
literal|"BLACK and BLUE"
argument_list|,
literal|"VARCHAR(12) NOT NULL"
argument_list|)
expr_stmt|;
comment|// REPLACE returns NULL in Oracle but not in Postgres or in Calcite.
comment|// When [CALCITE-815] is implemented and SqlConformance#emptyStringIsNull is
comment|// enabled, it will return empty string as NULL.
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn REPLACE('ciao', 'ciao', '')}"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn REPLACE('hello world', 'o', '')}"
argument_list|,
literal|"hell wrld"
argument_list|,
literal|"VARCHAR(11) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE(cast(null as varchar(5)), 'ciao', '')}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE('ciao', cast(null as varchar(3)), 'zz')}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE('ciao', 'bella', cast(null as varchar(3)))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn RTRIM(' xxx  ')}"
argument_list|,
literal|" xxx"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn SOUNDEX('Miller')}"
argument_list|,
literal|"M460"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn SOUNDEX(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn SPACE(-100)}"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"{fn SPACE(cast(null as integer))}"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn SUBSTRING('abcdef', 2, 3)}"
argument_list|,
literal|"bcd"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn UCASE('xxx')}"
argument_list|,
literal|"XXX"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Time and Date Functions
name|f
operator|.
name|checkType
argument_list|(
literal|"{fn CURDATE()}"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"{fn CURTIME()}"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn DAYNAME(DATE '2014-12-10')}"
argument_list|,
comment|// Day names in root locale changed from long to short in JDK 9
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
operator|<=
literal|8
condition|?
literal|"Wednesday"
else|:
literal|"Wed"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn DAYOFMONTH(DATE '2014-12-10')}"
argument_list|,
literal|10
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn DAYOFWEEK(DATE '2014-12-10')}"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn DAYOFYEAR(DATE '2014-12-10')}"
argument_list|,
literal|"344"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn HOUR(TIMESTAMP '2014-12-10 12:34:56')}"
argument_list|,
literal|12
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn MINUTE(TIMESTAMP '2014-12-10 12:34:56')}"
argument_list|,
literal|34
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn MONTH(DATE '2014-12-10')}"
argument_list|,
literal|12
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn MONTHNAME(DATE '2014-12-10')}"
argument_list|,
comment|// Month names in root locale changed from long to short in JDK 9
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
operator|<=
literal|8
condition|?
literal|"December"
else|:
literal|"Dec"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"{fn NOW()}"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn QUARTER(DATE '2014-12-10')}"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn SECOND(TIMESTAMP '2014-12-10 12:34:56')}"
argument_list|,
literal|56
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn TIMESTAMPADD(HOUR, 5,"
operator|+
literal|" TIMESTAMP '2014-03-29 12:34:56')}"
argument_list|,
literal|"2014-03-29 17:34:56"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn TIMESTAMPDIFF(HOUR,"
operator|+
literal|" TIMESTAMP '2014-03-29 12:34:56',"
operator|+
literal|" TIMESTAMP '2014-03-29 12:34:56')}"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn TIMESTAMPDIFF(MONTH,"
operator|+
literal|" TIMESTAMP '2019-09-01 00:00:00',"
operator|+
literal|" TIMESTAMP '2020-03-01 00:00:00')}"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn WEEK(DATE '2014-12-10')}"
argument_list|,
literal|"50"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn YEAR(DATE '2014-12-10')}"
argument_list|,
literal|2014
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
comment|// System Functions
name|f
operator|.
name|checkType
argument_list|(
literal|"{fn DATABASE()}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn IFNULL('a', 'b')}"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"{fn USER()}"
argument_list|,
literal|"sa"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Conversion Functions
comment|// Legacy JDBC style
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CONVERT('123', INTEGER)}"
argument_list|,
literal|123
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
comment|// ODBC/JDBC style
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CONVERT('123', SQL_INTEGER)}"
argument_list|,
literal|123
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"{fn CONVERT(INTERVAL '1' DAY, SQL_INTERVAL_DAY_TO_SECOND)}"
argument_list|,
literal|"+1 00:00:00.000000"
argument_list|,
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testChar
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CHR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^char(97)^"
argument_list|,
literal|"No match found for function signature CHAR\\(<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char(null)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char(-1)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char(97)"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char(48)"
argument_list|,
literal|"0"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"char(0)"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|'\u0000'
argument_list|)
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^char(97.1)^"
argument_list|,
literal|"Cannot apply 'CHAR' to arguments of type 'CHAR\\(<DECIMAL\\(3, 1\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'CHAR\\(<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testChr
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CHR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^chr(97.1)^"
argument_list|,
literal|"No match found for function signature CHR\\(<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"chr(97)"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"chr(48)"
argument_list|,
literal|"0"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"chr(0)"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|'\u0000'
argument_list|)
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"chr(null)"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSelect
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select * from (values(1))"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Check return type on scalar sub-query in select list.  Note return
comment|// type is always nullable even if sub-query select value is NOT NULL.
comment|// Bug FRG-189 causes this test to fail only in SqlOperatorTest; not
comment|// in subtypes.
if|if
condition|(
name|Bug
operator|.
name|FRG189_FIXED
condition|)
block|{
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(1)))\n"
operator|+
literal|"FROM (VALUES(2))"
argument_list|,
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(CAST(10 as BIGINT))))\n"
operator|+
literal|"FROM (VALUES(CAST(10 as bigint)))"
argument_list|,
literal|"RecordType(BIGINT NOT NULL EXPR$0, BIGINT EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(10.5)))\n"
operator|+
literal|"FROM (VALUES(10.5))"
argument_list|,
literal|"RecordType(DECIMAL(3, 1) NOT NULL EXPR$0, DECIMAL(3, 1) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES('this is a char')))\n"
operator|+
literal|"FROM (VALUES('this is a char too'))"
argument_list|,
literal|"RecordType(CHAR(18) NOT NULL EXPR$0, CHAR(14) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(true)))\n"
operator|+
literal|"FROM (values(false))"
argument_list|,
literal|"RecordType(BOOLEAN NOT NULL EXPR$0, BOOLEAN EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|" SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(cast('abcd' as varchar(10)))))\n"
operator|+
literal|"FROM (VALUES(CAST('abcd' as varchar(10))))"
argument_list|,
literal|"RecordType(VARCHAR(10) NOT NULL EXPR$0, VARCHAR(10) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"SELECT *,\n"
operator|+
literal|"  (SELECT * FROM (VALUES(TIMESTAMP '2006-01-01 12:00:05')))\n"
operator|+
literal|"FROM (VALUES(TIMESTAMP '2006-01-01 12:00:05'))"
argument_list|,
literal|"RecordType(TIMESTAMP(0) NOT NULL EXPR$0, TIMESTAMP(0) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testLiteralChain
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LITERAL_CHAIN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"'buttered'\n"
operator|+
literal|"' toast'"
argument_list|,
literal|"buttered toast"
argument_list|,
literal|"CHAR(14) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"'corned'\n"
operator|+
literal|"' beef'\n"
operator|+
literal|"' on'\n"
operator|+
literal|"' rye'"
argument_list|,
literal|"corned beef on rye"
argument_list|,
literal|"CHAR(18) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"_latin1'Spaghetti'\n"
operator|+
literal|"' all''Amatriciana'"
argument_list|,
literal|"Spaghetti all'Amatriciana"
argument_list|,
literal|"CHAR(25) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'1234'\n"
operator|+
literal|"'abcd' = x'1234abcd'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'1234'\n"
operator|+
literal|"'' = x'1234'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x''\n"
operator|+
literal|"'ab' = x'ab'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testComplexLiteral
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select 2 * 2 * x from (select 2 as x)"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select 1 * 2 * 3 * x from (select 2 as x)"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select 1 + 2 + 3 + 4 + x from (select 2 as x)"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|12
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRow
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true and false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true and true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) and false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false and cast(null as boolean)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean) and true"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true and (not false)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOperator2
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then unknown else true end and true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then cast(null as boolean) "
operator|+
literal|"else true end and true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then null else true end and true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOperatorLazy
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// lazy eval returns FALSE;
comment|// eager eval executes RHS of AND and throws;
comment|// both are valid
name|f
operator|.
name|check
argument_list|(
literal|"values 1> 2 and sqrt(-4) = -2"
argument_list|,
name|SqlTests
operator|.
name|BOOLEAN_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
operator|new
name|ValueOrExceptionResultChecker
argument_list|(
literal|false
argument_list|,
name|INVALID_ARG_FOR_POWER
argument_list|,
name|CODE_2201F
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConcatOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|" 'a'||'b' "
argument_list|,
literal|"ab"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|" 'a' || cast(null as char(2)) "
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|" cast(null as char(2)) || 'b' "
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|" cast(null as char(1)) || cast(null as char(2)) "
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|" x'fe'||x'df' "
argument_list|,
literal|"fedf"
argument_list|,
literal|"BINARY(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|" cast('fe' as char(2)) || cast('df' as varchar)"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
comment|// Precision is larger than VARCHAR allows, so result is unbounded
name|f
operator|.
name|checkString
argument_list|(
literal|" cast('fe' as char(2)) || cast('df' as varchar(65535))"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|" cast('fe' as char(2)) || cast('df' as varchar(33333))"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR(33335) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"x'ff' || cast(null as varbinary)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|" cast(null as ANY) || cast(null as ANY) "
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"cast('a' as varchar) || cast('b' as varchar) "
operator|+
literal|"|| cast('c' as varchar)"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array[1, 2] || array[2, 3]"
argument_list|,
literal|"[1, 2, 2, 3]"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array[1, 2] || array[2, null]"
argument_list|,
literal|"[1, 2, 2, null]"
argument_list|,
literal|"INTEGER ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array['hello', 'world'] || array['!'] || "
operator|+
literal|"array[cast(null as char)]"
argument_list|,
literal|"[hello, world, !, null]"
argument_list|,
literal|"CHAR(5) ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer array) || array[1]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConcatFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkConcatFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkConcatFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkConcatFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkConcat2Func
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkConcatFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CONCAT_FUNCTION
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('a', 'b', 'c')"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat(cast('a' as varchar), cast('b' as varchar), "
operator|+
literal|"cast('c' as varchar))"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"concat('a', 'b', cast(null as char(2)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"concat(cast(null as ANY), 'b', cast(null as char(2)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('', '', 'a')"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('', '', '')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^concat()^"
argument_list|,
name|INVALID_ARGUMENTS_NUMBER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkConcat2Func
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CONCAT2
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat(cast('fe' as char(2)), cast('df' as varchar(65535)))"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat(cast('fe' as char(2)), cast('df' as varchar))"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat(cast('fe' as char(2)), cast('df' as varchar(33333)))"
argument_list|,
literal|"fedf"
argument_list|,
literal|"VARCHAR(33335) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('', '')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('', 'a')"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"concat('a', 'b')"
argument_list|,
literal|"ab"
argument_list|,
literal|"VARCHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"concat('a', cast(null as varchar))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^concat('a', 'b', 'c')^"
argument_list|,
name|INVALID_ARGUMENTS_NUMBER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^concat('a')^"
argument_list|,
name|INVALID_ARGUMENTS_NUMBER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModOperator
parameter_list|()
block|{
comment|// "%" is allowed under BIG_QUERY, MYSQL_5 SQL conformance levels
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENT_REMAINDER
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlConformanceEnum
argument_list|>
name|conformances
init|=
name|list
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|,
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|f0
operator|.
name|forEachConformance
argument_list|(
name|conformances
argument_list|,
name|this
operator|::
name|checkModOperator
argument_list|)
expr_stmt|;
name|f0
operator|.
name|forEachConformance
argument_list|(
name|conformances
argument_list|,
name|this
operator|::
name|checkModPrecedence
argument_list|)
expr_stmt|;
name|f0
operator|.
name|forEachConformance
argument_list|(
name|conformances
argument_list|,
name|this
operator|::
name|checkModOperatorNull
argument_list|)
expr_stmt|;
name|f0
operator|.
name|forEachConformance
argument_list|(
name|conformances
argument_list|,
name|this
operator|::
name|checkModOperatorDivByZero
argument_list|)
expr_stmt|;
block|}
name|void
name|checkModOperator
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"4%2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"8%5"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-12%7"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-12%-7"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"12%-7"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(12 as tinyint) % cast(-7 as tinyint)"
argument_list|,
literal|"TINYINT NOT NULL"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(9 as decimal(2, 0)) % 7"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"7 % cast(9 as decimal(2, 0))"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(-9 as decimal(2, 0)) % cast(7 as decimal(1, 0))"
argument_list|,
literal|"DECIMAL(1, 0) NOT NULL"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
block|}
name|void
name|checkModPrecedence
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 5 % 3 % 4 * 14 % 17"
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"(1 + 5 % 3) % 4 + 14 % 17"
argument_list|,
literal|17
argument_list|)
expr_stmt|;
block|}
name|void
name|checkModOperatorNull
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer) % 2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"4 % cast(null as tinyint)"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"4 % cast(null as decimal(12,0))"
argument_list|)
expr_stmt|;
block|}
name|void
name|checkModOperatorDivByZero
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
comment|// The extra CASE expression is to fool Janino.  It does constant
comment|// reduction and will throw the divide by zero exception while
comment|// compiling the expression.  The test framework would then issue
comment|// unexpected exception occurred during "validation".  You cannot
comment|// submit as non-runtime because the janino exception does not have
comment|// error position information and the framework is unhappy with that.
name|f
operator|.
name|checkFails
argument_list|(
literal|"3 % case 'a' when 'a' then 0 end"
argument_list|,
name|DIVISION_BY_ZERO_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDivideOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DIVIDE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"10 / 5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-10 / 5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-10 / 5.0"
argument_list|,
literal|"DECIMAL(17, 6) NOT NULL"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|" cast(10.0 as double) / 5"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|" cast(10.0 as real) / 4"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"2.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|" 6.0 / cast(10.0 as real) "
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"0.6"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"10.0 / 5.0"
argument_list|,
literal|"DECIMAL(9, 6) NOT NULL"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DECIMAL
condition|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"1.0 / 3.0"
argument_list|,
literal|"DECIMAL(8, 6) NOT NULL"
argument_list|,
literal|"0.333333"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"100.1 / 0.0001"
argument_list|,
literal|"DECIMAL(14, 7) NOT NULL"
argument_list|,
literal|"1001000.0000000"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"100.1 / 0.00000001"
argument_list|,
literal|"DECIMAL(19, 8) NOT NULL"
argument_list|,
literal|"10010000000.00000000"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"1e1 / cast(null as float)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"100.1 / 0.00000000000000001"
argument_list|,
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|,
literal|"1.001E+19"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDivideOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '-2:2' hour to minute / 3"
argument_list|,
literal|"-0:41"
argument_list|,
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2:5:12' hour to second / 2 / -3"
argument_list|,
literal|"-0:20:52.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2' day / cast(null as bigint)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval month) / 2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '3-3' year to month / 15e-1"
argument_list|,
literal|"+2-02"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '3-4' year to month / 4.5"
argument_list|,
literal|"+0-09"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualsOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1=1.0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.34=1.34"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1=1.34"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1e2=100e0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1e2=101"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1e2 as real)=cast(101 as bigint)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a'='b'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true = true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true = false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false = true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false = false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('a' as varchar(30))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a ' as varchar(30))=cast('a' as varchar(30))"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(' a' as varchar(30))=cast(' a' as varchar(30))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a ' as varchar(15))=cast('a ' as varchar(30))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(' ' as varchar(3))=cast(' ' as varchar(2))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('abcd' as varchar(2))='ab'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('b' as varchar(30))"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('a' as varchar(15))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean)=cast(null as boolean)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)=1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as varchar(10))='a'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualsOperatorInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day = interval '1' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day = interval '2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2:2:2' hour to second = interval '2' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour) = interval '2' minute"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1>2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(-1 as TINYINT)>cast(1 as TINYINT)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1 as SMALLINT)>cast(1 as SMALLINT)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2>1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>1.2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1>-1.2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>1.1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1e1>1.2e1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(-1.1 as real)> cast(-1.2 as real)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1e2>1.1e2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0>1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.2e0 as real)>1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true>false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true>true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false>false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false>true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"3.0>cast(null as double)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"DATE '2013-02-23'> DATE '1945-02-24'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"DATE '2013-02-23'> CAST(NULL AS DATE)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'>x'0A0001B0'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '1' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '5' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second> interval '2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '-2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '2' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute> interval '2' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second> interval '2' minute"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)> interval '2' minute"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute> cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsDistinctFromOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_DISTINCT_FROM
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 1.0"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is distinct from 2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is distinct from cast(null as integer)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is distinct from 1.23"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is distinct from 5.23"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-23e0 is distinct from -2.3e1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// IS DISTINCT FROM not implemented for ROW yet
if|if
condition|(
literal|false
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is distinct from row(1,1)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is distinct from row(1,2)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Intervals
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day is distinct from interval '1' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '10' hour is distinct from interval '10' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotDistinctFromOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 1.0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is not distinct from 2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is not distinct from cast(null as integer)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is not distinct from 1.23"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is not distinct from 5.23"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-23e0 is not distinct from -2.3e1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// IS NOT DISTINCT FROM not implemented for ROW yet
if|if
condition|(
literal|false
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is not distinct from row(1,1)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is not distinct from row(1,2)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Intervals
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day is not distinct from interval '1' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '10' hour is not distinct from interval '10' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOrEqualOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1>=2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1>=1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1>=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2>=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>=1.2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1>=-1.2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>=1.1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e4>=1e5"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e4>=cast(1e5 as real)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>=cast(1e5 as double)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"120000>=cast(1e5 as real)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true>=false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true>=true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false>=false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false>=true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as real)>=999"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'>=x'0A0001B0'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A0001B0'>=x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOrEqualOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '1' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '5' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second>= interval '2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '-2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '2' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute>= interval '2' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second>= interval '2' minute"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)>= interval '2' minute"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute>= cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 in (0, 1, 2)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"3 in (0, 1, 2)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) in (0, 1, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) in (0, cast(null as integer), 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG327_FIXED
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// AND has lower precedence than IN
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false and true in (false, false)"
argument_list|,
literal|false
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
name|f
operator|.
name|checkFails
argument_list|(
literal|"'foo' in (^)^"
argument_list|,
literal|"(?s).*Encountered \"\\)\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotInOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_IN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 not in (0, 1, 2)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"3 not in (0, 1, 2)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) not in (0, 1, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) not in (0, cast(null as integer), 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG327_FIXED
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) not in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 not in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// AND has lower precedence than NOT IN
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true and false not in (true, true)"
argument_list|,
literal|true
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
name|f
operator|.
name|checkFails
argument_list|(
literal|"'foo' not in (^)^"
argument_list|,
literal|"(?s).*Encountered \"\\)\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverlapsOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVERLAPS
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '1-2-3') "
operator|+
literal|"overlaps (date '1-2-3', interval '1' year)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '1-2-3') "
operator|+
literal|"overlaps (date '4-5-6', interval '1' year)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '4-5-6') "
operator|+
literal|"overlaps (date '2-2-3', date '3-4-5')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(cast(null as date), date '1-2-3') "
operator|+
literal|"overlaps (date '1-2-3', interval '1' year)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(date '1-2-3', date '1-2-3') overlaps "
operator|+
literal|"(date '1-2-3', cast(null as date))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) "
operator|+
literal|"overlaps (time '23:59:59', time '1:2:3')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) "
operator|+
literal|"overlaps (time '23:59:59', time '1:2:2')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) "
operator|+
literal|"overlaps (time '23:59:59', interval '2' hour)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(time '1:2:3', cast(null as time)) "
operator|+
literal|"overlaps (time '23:59:59', time '1:2:3')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(time '1:2:3', interval '1' second) "
operator|+
literal|"overlaps (time '23:59:59', cast(null as interval hour))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) "
operator|+
literal|"overlaps (timestamp '1-2-3 4:5:6',"
operator|+
literal|" interval '1 2:3:4.5' day to second)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) "
operator|+
literal|"overlaps (timestamp '2-2-3 4:5:6',"
operator|+
literal|" interval '1 2:3:4.5' day to second)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', cast(null as interval day) ) "
operator|+
literal|"overlaps (timestamp '1-2-3 4:5:6',"
operator|+
literal|" interval '1 2:3:4.5' day to second)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) "
operator|+
literal|"overlaps (cast(null as timestamp),"
operator|+
literal|" interval '1 2:3:4.5' day to second)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-715">[CALCITE-715]    * Add PERIOD type constructor and period operators (CONTAINS, PRECEDES,    * etc.)</a>.    *    *<p>Tests OVERLAP and similar period operators CONTAINS, EQUALS, PRECEDES,    * SUCCEEDS, IMMEDIATELY PRECEDES, IMMEDIATELY SUCCEEDS for DATE, TIME and    * TIMESTAMP values. */
annotation|@
name|Test
name|void
name|testPeriodOperators
parameter_list|()
block|{
name|String
index|[]
name|times
init|=
block|{
literal|"TIME '01:00:00'"
block|,
literal|"TIME '02:00:00'"
block|,
literal|"TIME '03:00:00'"
block|,
literal|"TIME '04:00:00'"
block|,     }
decl_stmt|;
name|String
index|[]
name|dates
init|=
block|{
literal|"DATE '1970-01-01'"
block|,
literal|"DATE '1970-02-01'"
block|,
literal|"DATE '1970-03-01'"
block|,
literal|"DATE '1970-04-01'"
block|,     }
decl_stmt|;
name|String
index|[]
name|timestamps
init|=
block|{
literal|"TIMESTAMP '1970-01-01 00:00:00'"
block|,
literal|"TIMESTAMP '1970-02-01 00:00:00'"
block|,
literal|"TIMESTAMP '1970-03-01 00:00:00'"
block|,
literal|"TIMESTAMP '1970-04-01 00:00:00'"
block|,     }
decl_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|f
argument_list|,
name|times
argument_list|)
argument_list|)
expr_stmt|;
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|f
argument_list|,
name|dates
argument_list|)
argument_list|)
expr_stmt|;
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|f
argument_list|,
name|timestamps
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|checkOverlaps
parameter_list|(
name|OverlapChecker
name|c
parameter_list|)
block|{
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) OVERLAPS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) OVERLAPS ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$1) OVERLAPS ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$2) OVERLAPS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$2) OVERLAPS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$0) OVERLAPS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) OVERLAPS ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) OVERLAPS ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) OVERLAPS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$2) OVERLAPS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$2) OVERLAPS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) OVERLAPS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) OVERLAPS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) CONTAINS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) CONTAINS ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) CONTAINS ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) CONTAINS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) CONTAINS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) CONTAINS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) CONTAINS ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) CONTAINS ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) CONTAINS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) CONTAINS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$2) CONTAINS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) CONTAINS $0"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS $0"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS $1"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS $2"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) CONTAINS $3"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS $0"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS $1"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS $2"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) CONTAINS $3"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($1,$3) CONTAINS $0"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($1,$2) CONTAINS $3"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) EQUALS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) EQUALS ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) EQUALS ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) EQUALS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) EQUALS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) EQUALS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) EQUALS ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) EQUALS ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) EQUALS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) EQUALS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$2) EQUALS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) EQUALS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) EQUALS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) EQUALS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) EQUALS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) PRECEDES ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$1) PRECEDES ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$1) PRECEDES ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) PRECEDES ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) PRECEDES ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) PRECEDES ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) PRECEDES ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) PRECEDES ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) PRECEDES ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) PRECEDES ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) PRECEDES ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) PRECEDES ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) SUCCEEDS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) SUCCEEDS ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) SUCCEEDS ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) SUCCEEDS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) SUCCEEDS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) SUCCEEDS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$2) SUCCEEDS ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) SUCCEEDS ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$2) SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) SUCCEEDS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) SUCCEEDS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) SUCCEEDS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) SUCCEEDS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) IMMEDIATELY PRECEDES ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) IMMEDIATELY PRECEDES ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$1) IMMEDIATELY PRECEDES ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY PRECEDES ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY PRECEDES ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) IMMEDIATELY PRECEDES ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) IMMEDIATELY PRECEDES ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) IMMEDIATELY PRECEDES ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$3) IMMEDIATELY PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) IMMEDIATELY PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY PRECEDES ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) IMMEDIATELY PRECEDES ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$3) IMMEDIATELY PRECEDES ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) IMMEDIATELY PRECEDES ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) IMMEDIATELY PRECEDES ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($0,$0) IMMEDIATELY SUCCEEDS ($0,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) IMMEDIATELY SUCCEEDS ($2,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$1) IMMEDIATELY SUCCEEDS ($1,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY SUCCEEDS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY SUCCEEDS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($2,$0) IMMEDIATELY SUCCEEDS ($3,$1)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$2) IMMEDIATELY SUCCEEDS ($1,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) IMMEDIATELY SUCCEEDS ($0,$2)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($2,$3) IMMEDIATELY SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$2) IMMEDIATELY SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$2) IMMEDIATELY SUCCEEDS ($2,$0)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) IMMEDIATELY SUCCEEDS ($1,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($0,$3) IMMEDIATELY SUCCEEDS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isFalse
argument_list|(
literal|"($3,$0) IMMEDIATELY SUCCEEDS ($3,$3)"
argument_list|)
expr_stmt|;
name|c
operator|.
name|isTrue
argument_list|(
literal|"($3,$0) IMMEDIATELY SUCCEEDS ($0,$0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1<1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2<1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<1.2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1<-1.2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<1.1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<1.1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<cast(1.2 as real)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1e-1<-1.2e-1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<cast(1.1 as double)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true<false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true<true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false<false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false<true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"123<cast(null as bigint)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint)<123"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<1.32"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'<x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOperatorInterval
parameter_list|()
block|{
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '1' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '5' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second< interval '2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '-2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '2' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute< interval '2' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second< interval '2' minute"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)< interval '2' minute"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute "
operator|+
literal|"< cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOrEqualOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<=2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1<=1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"2<=1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<=1.2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1<=-1.2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<=1.1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2<=1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<=cast(1e2 as real)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1000<=cast(1e2 as real)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1<=1e2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1<=cast(1e2 as real)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true<=false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true<=true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false<=false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false<=true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as real)<=cast(1 as real)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<=3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"3<=cast(null as smallint)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<=1.32"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'<=x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A0001B0'<=x'0A0001B0'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOrEqualOperatorInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '1' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '5' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second<= interval '2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '2' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '-2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '2' hour"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute<= interval '2' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second<= interval '2' minute"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)<= interval '2' minute"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute "
operator|+
literal|"<= cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinusOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-2-1"
argument_list|,
operator|-
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-2-1-5"
argument_list|,
operator|-
literal|8
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"2-1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as double) -1"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(1 as smallint)-cast(2.0 as real)"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"2.4-cast(2.0 as real)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.4
argument_list|,
literal|0.00000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"1-2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"10.0 - 5.0"
argument_list|,
literal|"DECIMAL(4, 1) NOT NULL"
argument_list|,
literal|"5.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"19.68 - 4.2"
argument_list|,
literal|"DECIMAL(5, 2) NOT NULL"
argument_list|,
literal|"15.48"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"1e1-cast(null as double)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint) - cast(null as smallint)"
argument_list|)
expr_stmt|;
comment|// TODO: Fix bug
if|if
condition|(
name|Bug
operator|.
name|FNL25_FIXED
condition|)
block|{
comment|// Should throw out of range error
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(100 as tinyint) - cast(-100 as tinyint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-20000 as smallint) - cast(20000 as smallint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.5e9 as integer) - cast(-1.5e9 as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-5e18 as bigint) - cast(5e18 as bigint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5e18 as decimal(19,0)) - cast(-5e18 as decimal(19,0))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-5e8 as decimal(19,10)) - cast(5e8 as decimal(19,10))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testMinusIntervalOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day - interval '1' day"
argument_list|,
literal|"+1"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day - interval '1' minute"
argument_list|,
literal|"+1 23:59"
argument_list|,
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' year - interval '1' month"
argument_list|,
literal|"+1-11"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' year - interval '1' month - interval '3' year"
argument_list|,
literal|"-1-01"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval day) + interval '2' hour"
argument_list|)
expr_stmt|;
comment|// Datetime minus interval
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' - interval '1:1' hour to minute"
argument_list|,
literal|"11:02:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Per [CALCITE-1632] Return types of datetime + interval
comment|// make sure that TIME values say in range
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' - interval '1' day"
argument_list|,
literal|"12:03:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' - interval '25' hour"
argument_list|,
literal|"11:03:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:03' - interval '25:0:1' hour to second"
argument_list|,
literal|"11:03:02"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '5' day"
argument_list|,
literal|"2005-02-25"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '5' day"
argument_list|,
literal|"2005-02-25"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '5' hour"
argument_list|,
literal|"2005-03-02"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '25' hour"
argument_list|,
literal|"2005-03-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '25:45' hour to minute"
argument_list|,
literal|"2005-03-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' - interval '25:45:54' hour to second"
argument_list|,
literal|"2005-03-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01' "
operator|+
literal|"- interval '-4 2:4' day to minute"
argument_list|,
literal|"2003-08-06 14:58:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Datetime minus year-month interval
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01' - interval '12' year"
argument_list|,
literal|"1991-08-02 12:54:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2003-08-02' - interval '12' year"
argument_list|,
literal|"1991-08-02"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2003-08-02' - interval '12-3' year to month"
argument_list|,
literal|"1991-05-02"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinusDateOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS_DATE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(time '12:03:34' - time '11:57:23') minute to second"
argument_list|,
literal|"+6:11.000000"
argument_list|,
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(time '12:03:23' - time '11:57:23') minute"
argument_list|,
literal|"+6"
argument_list|,
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(time '12:03:34' - time '11:57:23') minute"
argument_list|,
literal|"+6"
argument_list|,
literal|"INTERVAL MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(timestamp '2004-05-01 12:03:34'"
operator|+
literal|" - timestamp '2004-04-29 11:57:23') day to second"
argument_list|,
literal|"+2 00:06:11.000000"
argument_list|,
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(timestamp '2004-05-01 12:03:34'"
operator|+
literal|" - timestamp '2004-04-29 11:57:23') day to hour"
argument_list|,
literal|"+2 00"
argument_list|,
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(date '2004-12-02' - date '2003-12-01') day"
argument_list|,
literal|"+367"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"(cast(null as date) - date '2003-12-01') day"
argument_list|)
expr_stmt|;
comment|// combine '<datetime> +<interval>' with '<datetime> -<datetime>'
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '1969-04-29 0:0:0' +"
operator|+
literal|" (timestamp '2008-07-15 15:28:00' - "
operator|+
literal|"  timestamp '1969-04-29 0:0:0') day to second / 2"
argument_list|,
literal|"1988-12-06 07:44:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '1969-04-29' +"
operator|+
literal|" (date '2008-07-15' - "
operator|+
literal|"  date '1969-04-29') day / 2"
argument_list|,
literal|"1988-12-06"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '01:23:44' +"
operator|+
literal|" (time '15:28:00' - "
operator|+
literal|"  time '01:23:44') hour to second / 2"
argument_list|,
literal|"08:25:52"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|DT1684_FIXED
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1969-04-29' +"
operator|+
literal|" (CURRENT_DATE - "
operator|+
literal|"  date '1969-04-29') day / 2) is not null"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Add tests for year month intervals (currently not supported)
block|}
annotation|@
name|Test
name|void
name|testMultiplyOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"2*3"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"2*-3"
argument_list|,
operator|-
literal|6
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"+2*3"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"2*0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as float)*3"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"3*cast(2.0 as real)"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as real)*3.2"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"6.4"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"10.0 * 5.0"
argument_list|,
literal|"DECIMAL(5, 2) NOT NULL"
argument_list|,
literal|"50.00"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"19.68 * 4.2"
argument_list|,
literal|"DECIMAL(6, 3) NOT NULL"
argument_list|,
literal|"82.656"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(1 as real)*cast(null as real)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"2e-3*cast(null as integer)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint) * cast(4 as smallint)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FNL25_FIXED
condition|)
block|{
comment|// Should throw out of range error
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(100 as tinyint) * cast(-2 as tinyint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(200 as smallint) * cast(200 as smallint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.5e9 as integer) * cast(-2 as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5e9 as bigint) * cast(2e9 as bigint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(2e9 as decimal(19,0)) * cast(-5e9 as decimal(19,0))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5e4 as decimal(19,10)) * cast(2e4 as decimal(19,10))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testMultiplyIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2:2' hour to minute * 3"
argument_list|,
literal|"+6:06"
argument_list|,
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"3 * 2 * interval '2:5:12' hour to second"
argument_list|,
literal|"+12:31:12.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2' day * cast(null as bigint)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval month) * 2"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '3-2' year to month * 15e-1"
argument_list|,
literal|"+04-09"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '3-4' year to month * 4.5"
argument_list|,
literal|"+15-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testDatePlusInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2014-02-11' + interval '2' day"
argument_list|,
literal|"2014-02-13"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// 60 days is more than 2^32 milliseconds
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2014-02-11' + interval '60' day"
argument_list|,
literal|"2014-04-12"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1864">[CALCITE-1864]    * Allow NULL literal as argument</a>. */
annotation|@
name|Test
name|void
name|testNullOperand
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|">"
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|"<"
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|"<="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|">="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|f
argument_list|,
literal|"<>"
argument_list|)
expr_stmt|;
comment|// "!=" is allowed under ORACLE_10 SQL conformance level
specifier|final
name|SqlOperatorFixture
name|f1
init|=
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
decl_stmt|;
name|checkNullOperand
argument_list|(
name|f1
argument_list|,
literal|"<>"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkNullOperand
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|,
name|String
name|op
parameter_list|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 "
operator|+
name|op
operator|+
literal|" null"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"null "
operator|+
name|op
operator|+
literal|" -3"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"null "
operator|+
name|op
operator|+
literal|" null"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotEqualsOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1<>1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a'<>'A'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1e0<>1e1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"'a'<>cast(null as varchar(1))"
argument_list|)
expr_stmt|;
comment|// "!=" is not an acceptable alternative to "<>" under default SQL
comment|// conformance level
name|f
operator|.
name|checkFails
argument_list|(
literal|"1 ^!=^ 1"
argument_list|,
literal|"Bang equal '!=' is not allowed under the current SQL conformance level"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f1
lambda|->
block|{
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"1<> 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"1 != 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"2 != 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"1 != null"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
comment|// "!=" is allowed under BigQuery, ORACLE_10 SQL conformance levels
specifier|final
name|List
argument_list|<
name|SqlConformanceEnum
argument_list|>
name|conformances
init|=
name|list
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|,
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
decl_stmt|;
name|f
operator|.
name|forEachConformance
argument_list|(
name|conformances
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotEqualsOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<> interval '1' day"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<> interval '2' day"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2:2:2' hour to second<> interval '2' hour"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)<> interval '2' minute"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true or false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false or false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true or cast(null as boolean)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"false or cast(null as boolean)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOrOperatorLazy
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// need to evaluate 2nd argument if first evaluates to null, therefore
comment|// get error
name|f
operator|.
name|check
argument_list|(
literal|"values 1< cast(null as integer) or sqrt(-4) = -2"
argument_list|,
name|SqlTests
operator|.
name|BOOLEAN_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
operator|new
name|ValueOrExceptionResultChecker
argument_list|(
literal|null
argument_list|,
name|INVALID_ARG_FOR_POWER
argument_list|,
name|CODE_2201F
argument_list|)
argument_list|)
expr_stmt|;
comment|// Do not need to evaluate 2nd argument if first evaluates to true.
comment|// In eager evaluation, get error;
comment|// lazy evaluation returns true;
comment|// both are valid.
name|f
operator|.
name|check
argument_list|(
literal|"values 1< 2 or sqrt(-4) = -2"
argument_list|,
name|SqlTests
operator|.
name|BOOLEAN_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
operator|new
name|ValueOrExceptionResultChecker
argument_list|(
literal|true
argument_list|,
name|INVALID_ARG_FOR_POWER
argument_list|,
name|CODE_2201F
argument_list|)
argument_list|)
expr_stmt|;
comment|// NULL OR FALSE --> NULL
comment|// In eager evaluation, get error;
comment|// lazy evaluation returns NULL;
comment|// both are valid.
name|f
operator|.
name|check
argument_list|(
literal|"values 1< cast(null as integer) or sqrt(4) = -2"
argument_list|,
name|SqlTests
operator|.
name|BOOLEAN_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
operator|new
name|ValueOrExceptionResultChecker
argument_list|(
literal|null
argument_list|,
name|INVALID_ARG_FOR_POWER
argument_list|,
name|CODE_2201F
argument_list|)
argument_list|)
expr_stmt|;
comment|// NULL OR TRUE --> TRUE
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1< cast(null as integer) or sqrt(4) = 2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPlusOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"1+2"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-1+2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"1+2+3"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"1+cast(2.0 as double)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"1+cast(2.0 as double)+cast(6.0 as float)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"10.0 + 5.0"
argument_list|,
literal|"DECIMAL(4, 1) NOT NULL"
argument_list|,
literal|"15.0"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"19.68 + 4.2"
argument_list|,
literal|"DECIMAL(5, 2) NOT NULL"
argument_list|,
literal|"23.88"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"19.68 + 4.2 + 6"
argument_list|,
literal|"DECIMAL(13, 2) NOT NULL"
argument_list|,
literal|"29.88"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"19.68 + cast(4.2 as float)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|23.88
argument_list|,
literal|0.02
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint)+1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"1e-2+cast(null as double)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FNL25_FIXED
condition|)
block|{
comment|// Should throw out of range error
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(100 as tinyint) + cast(100 as tinyint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-20000 as smallint) + cast(-20000 as smallint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(1.5e9 as integer) + cast(1.5e9 as integer)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5e18 as bigint) + cast(5e18 as bigint)"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(-5e18 as decimal(19,0))"
operator|+
literal|" + cast(-5e18 as decimal(19,0))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"cast(5e8 as decimal(19,10)) + cast(5e8 as decimal(19,10))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testPlusOperatorAny
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"1+CAST(2 AS ANY)"
argument_list|,
literal|"3"
argument_list|,
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPlusIntervalOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day + interval '1' day"
argument_list|,
literal|"+3"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day + interval '1' minute"
argument_list|,
literal|"+2 00:01"
argument_list|,
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day + interval '5' minute"
operator|+
literal|" + interval '-3' second"
argument_list|,
literal|"+2 00:04:57.000000"
argument_list|,
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' year + interval '1' month"
argument_list|,
literal|"+2-01"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"interval '2' year + cast(null as interval month)"
argument_list|)
expr_stmt|;
comment|// Datetime plus interval
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' + interval '1:1' hour to minute"
argument_list|,
literal|"13:04:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Per [CALCITE-1632] Return types of datetime + interval
comment|// make sure that TIME values say in range
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' + interval '1' day"
argument_list|,
literal|"12:03:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' + interval '25' hour"
argument_list|,
literal|"13:03:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time '12:03:01' + interval '25:0:1' hour to second"
argument_list|,
literal|"13:03:02"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '5' day + date '2005-03-02'"
argument_list|,
literal|"2005-03-07"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' + interval '5' day"
argument_list|,
literal|"2005-03-07"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' + interval '5' hour"
argument_list|,
literal|"2005-03-02"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' + interval '25' hour"
argument_list|,
literal|"2005-03-03"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' + interval '25:45' hour to minute"
argument_list|,
literal|"2005-03-03"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date '2005-03-02' + interval '25:45:54' hour to second"
argument_list|,
literal|"2005-03-03"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01'"
operator|+
literal|" + interval '-4 2:4' day to minute"
argument_list|,
literal|"2003-07-29 10:50:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Datetime plus year-to-month interval
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '5-3' year to month + date '2005-03-02'"
argument_list|,
literal|"2010-06-02"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01'"
operator|+
literal|" + interval '5-3' year to month"
argument_list|,
literal|"2008-11-02 12:54:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"interval '5-3' year to month"
operator|+
literal|" + timestamp '2003-08-02 12:54:01'"
argument_list|,
literal|"2008-11-02 12:54:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDescendingOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DESC
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotNullOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is not null"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not null"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNullOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is null"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is null"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotTrueOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is not true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is not true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"select ^'a string' is not true^ from (values (1))"
argument_list|,
literal|"(?s)Cannot apply 'IS NOT TRUE' to arguments of type "
operator|+
literal|"'<CHAR\\(8\\)> IS NOT TRUE'. Supported form\\(s\\): "
operator|+
literal|"'<BOOLEAN> IS NOT TRUE'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsTrueOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is true"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotFalseOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is not false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is not false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsFalseOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is false"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotUnknownOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_UNKNOWN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is not unknown"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is not unknown"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not unknown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"unknown is not unknown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'abc' IS NOT UNKNOWN^"
argument_list|,
literal|"(?s).*Cannot apply 'IS NOT UNKNOWN'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsUnknownOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_UNKNOWN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"false is unknown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"true is unknown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is unknown"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"unknown is unknown"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"0 = 1 AND ^2 IS UNKNOWN^ AND 3> 4"
argument_list|,
literal|"(?s).*Cannot apply 'IS UNKNOWN'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsASetOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_A_SET
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 1] is a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean), cast(null as boolean)]"
operator|+
literal|" is a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean)] is a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a'] is a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] is a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b', 'a'] is a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotASetOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_A_SET
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is not a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 1] is not a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean), cast(null as boolean)]"
operator|+
literal|" is not a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean)] is not a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a'] is not a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] is not a set"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b', 'a'] is not a set"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTISET_INTERSECT
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1] multiset intersect multiset[1]"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[2] multiset intersect all multiset[1]"
argument_list|,
literal|"[]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[2] multiset intersect distinct multiset[1]"
argument_list|,
literal|"[]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1, 1] multiset intersect distinct multiset[1, 1]"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1, 1] multiset intersect all multiset[1, 1]"
argument_list|,
literal|"[1, 1]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1, 1] multiset intersect distinct multiset[1, 1]"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as integer), cast(null as integer)] "
operator|+
literal|"multiset intersect distinct multiset[cast(null as integer)]"
argument_list|,
literal|"[null]"
argument_list|,
literal|"INTEGER MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as integer), cast(null as integer)] "
operator|+
literal|"multiset intersect all multiset[cast(null as integer)]"
argument_list|,
literal|"[null]"
argument_list|,
literal|"INTEGER MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as integer), cast(null as integer)] "
operator|+
literal|"multiset intersect distinct multiset[cast(null as integer)]"
argument_list|,
literal|"[null]"
argument_list|,
literal|"INTEGER MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExceptOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTISET_EXCEPT
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1] multiset except multiset[1]"
argument_list|,
literal|"[]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1] multiset except distinct multiset[1]"
argument_list|,
literal|"[]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[2] multiset except multiset[1]"
argument_list|,
literal|"[2]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[1,2,3] multiset except multiset[1]"
argument_list|,
literal|"[2, 3]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1,2,3,2]"
operator|+
literal|" multiset except distinct multiset[1])"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1,2,3,2]"
operator|+
literal|" multiset except all multiset[1])"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except distinct multiset[1])"
operator|+
literal|" submultiset of multiset[2, 3]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except distinct multiset[1])"
operator|+
literal|" submultiset of multiset[2, 3]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except all multiset[1])"
operator|+
literal|" submultiset of multiset[2, 2, 3]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3] multiset except multiset[1]) is empty"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1] multiset except multiset[1]) is empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsEmptyOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_EMPTY
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is empty"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotEmptyOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_EMPTY
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is not empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExistsOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXISTS
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"not true"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"not false"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"not unknown"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"not cast(null as boolean)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrefixMinusOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"'a' + ^- 'b'^ + 'c'"
argument_list|,
literal|"(?s)Cannot apply '-' to arguments of type '-<CHAR\\(1\\)>'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"'a' + - 'b' + 'c'"
argument_list|,
literal|"DECIMAL(19, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-1"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"-1.23"
argument_list|,
literal|"DECIMAL(3, 2) NOT NULL"
argument_list|,
literal|"-1.23"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"-1.0e0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"-cast(null as integer)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"-cast(null as tinyint)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrefixMinusOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"-interval '-6:2:8' hour to second"
argument_list|,
literal|"+6:02:08.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"- -interval '-6:2:8' hour to second"
argument_list|,
literal|"-6:02:08.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"-interval '5' month"
argument_list|,
literal|"-5"
argument_list|,
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"-cast(null as interval day to minute)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrefixPlusOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_PLUS
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"+1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"+1.23"
argument_list|,
literal|"DECIMAL(3, 2) NOT NULL"
argument_list|,
literal|"1.23"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"+1.0e0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"+cast(null as integer)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"+cast(null as tinyint)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrefixPlusOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"+interval '-6:2:8' hour to second"
argument_list|,
literal|"-6:02:08.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"++interval '-6:2:8' hour to second"
argument_list|,
literal|"-6:02:08.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG254_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"+interval '6:2:8.234' hour to second"
argument_list|,
literal|"+06:02:08.234"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"+interval '5' month"
argument_list|,
literal|"+5"
argument_list|,
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"+cast(null as interval day to minute)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExplicitTableOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXPLICIT_TABLE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testValuesOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|VALUES
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select 'abc' from (values(true))"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotLikeOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_LIKE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' not like '_b_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' not like 'ab%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' not like '%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' not like '%cd%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' not like '%cde%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRlikeOperator
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RLIKE
argument_list|,
name|VM_EXPAND
argument_list|)
decl_stmt|;
name|checkRlike
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
argument_list|)
expr_stmt|;
name|checkRlike
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|HIVE
argument_list|)
argument_list|)
expr_stmt|;
name|checkRlikeFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkRlikeFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|checkRlike
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Merrisa@gmail.com' rlike '.+@*\\.com'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Merrisa@gmail.com' rlike '.com$'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acbd' rlike '^ac+'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acb' rlike 'acb|efg'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acb|efg' rlike 'acb\\|efg'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Acbd' rlike '^ac+'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Merrisa@gmail.com' rlike 'Merrisa_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcdef' rlike '%cd%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|NOT_RLIKE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Merrisagmail' not rlike '.+@*\\.com'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acbd' not rlike '^ac+'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acb|efg' not rlike 'acb\\|efg'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'Merrisa@gmail.com' not rlike 'Merrisa_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|checkRlikeFails
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
specifier|final
name|String
name|noRlike
init|=
literal|"(?s).*No match found for function signature RLIKE"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'Merrisa@gmail.com' rlike '.+@*\\.com'^"
argument_list|,
name|noRlike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'acb' rlike 'acb|efg'^"
argument_list|,
name|noRlike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|noNotRlike
init|=
literal|"(?s).*No match found for function signature NOT RLIKE"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'abcdef' not rlike '%cd%'^"
argument_list|,
name|noNotRlike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'Merrisa@gmail.com' not rlike 'Merrisa_'^"
argument_list|,
name|noNotRlike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeEscape
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' like 'a#_c' escape '#'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' like 'a#_c' escape '#'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' like 'a\\_c' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' like 'a\\_c' escape '\\'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a%c' like 'a\\%c' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a%cde' like 'a\\%c_e' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' like 'a%c' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' like 'a\\%c' escape '\\'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIlikeEscape
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ILIKE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' ilike 'a#_C' escape '#'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' ilike 'a#_C' escape '#'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' ilike 'a\\_C' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' ilike 'a\\_C' escape '\\'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a%c' ilike 'a\\%C' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a%cde' ilike 'a\\%C_e' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' ilike 'a%C' escape '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' ilike 'a\\%C' escape '\\'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"[CALCITE-525] Exception-handling in built-in functions"
argument_list|)
annotation|@
name|Test
name|void
name|testLikeEscape2
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'x' not like 'x' escape 'x'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xyz' not like 'xyz' escape 'xyz'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"''  like ''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'b'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'A'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '_a'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '%a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '%a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   like 'a_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc'  like 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   like '_b'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like '_d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like '%d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' like 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' like 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' like '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' like '%cd%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' like '%cde%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIlikeOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ILIKE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
specifier|final
name|String
name|noLike
init|=
literal|"No match found for function signature ILIKE"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'a' ilike 'b'^"
argument_list|,
name|noLike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'a' ilike 'b' escape 'c'^"
argument_list|,
name|noLike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|noNotLike
init|=
literal|"No match found for function signature NOT ILIKE"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'a' not ilike 'b'^"
argument_list|,
name|noNotLike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^'a' not ilike 'b' escape 'c'^"
argument_list|,
name|noNotLike
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f1
init|=
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"''  ilike ''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'b'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'A'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike '_a'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike '%a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike '%A'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike '%a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike '%A%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'a' ilike 'A%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   ilike 'a_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   ilike 'A_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abc'  ilike 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' ilike 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' ilike 'A%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   ilike '_b'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   ilike '_B'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' ilike '_d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' ilike '%d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' ilike '%D'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' ilike 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' ilike 'aB%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' ilike 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' ilike 'Ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' ilike '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' ilike '%cd%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' ilike '%CD%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' ilike '%cde%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1898">[CALCITE-1898]    * LIKE must match '.' (period) literally</a>. */
annotation|@
name|Test
name|void
name|testLikeDot
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' like 'a.c'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcde' like '%c.e'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc.e' like '%c.e'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIlikeDot
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ILIKE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' ilike 'a.c'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcde' ilike '%c.e'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc.e' ilike '%c.e'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc.e' ilike '%c.E'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotSimilarToOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_SIMILAR_TO
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc' not similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'a' || '_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'ba_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as varchar(2)) not similar to 'a_'"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as varchar(3))"
operator|+
literal|" not similar to cast(null as char(2))"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSimilarToOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIMILAR_TO
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// like LIKE
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"''  similar to ''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'b'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'A'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '_a'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '%a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '%a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   similar to 'a_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc'  similar to 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   similar to '_b'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to '_d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to '%d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' similar to 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' similar to 'ab%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' similar to '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' similar to '%cd%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' similar to '%cde%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// simple regular expressions
comment|// ab*c+d matches acd, abcd, acccd, abcccd but not abd, aabc
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acd'    similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd'   similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acccd'  similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcccd' similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abd'    similar to 'ab*c+d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc'   similar to 'ab*c+d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// compound regular expressions
comment|// x(ab|c)*y matches xy, xccy, xababcy but not xbcy
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xy'      similar to 'x(ab|c)*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xccy'    similar to 'x(ab|c)*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xababcy' similar to 'x(ab|c)*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xbcy'    similar to 'x(ab|c)*y'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// x(ab|c)+y matches xccy, xababcy but not xy, xbcy
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xy'      similar to 'x(ab|c)+y'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xccy'    similar to 'x(ab|c)+y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xababcy' similar to 'x(ab|c)+y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xbcy'    similar to 'x(ab|c)+y'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a%' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a%' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a_' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a%' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'1a' similar to '_a' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'123aXYZ' similar to '%a%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'123aXYZ' similar to '_%_a%_' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xy' similar to '(xy)' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to '[ab][bcde]d' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'bdd' similar to '[ab][bcde]d' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to '[ab]d' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '[a-e]d' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'amy' similar to 'amy|fred' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'fred' similar to 'amy|fred' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'mike' similar to 'amy|fred' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'acd' similar to 'ab*c+d' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'accccd' similar to 'ab*c+d' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to 'ab*c+d' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc' similar to 'ab*c+d' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abb' similar to 'a(b{3})' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbb' similar to 'a(b{3})' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbb' similar to 'a(b{3})' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbb' similar to 'ab{3,6}' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbbbbb' similar to 'ab{3,6}' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'' similar to 'ab?' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'ab?' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a(b?)' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'ab?' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a(b?)' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abb' similar to 'ab?' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a\\_' ESCAPE '\\' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a\\%' ESCAPE '\\' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a_' similar to 'a\\_' ESCAPE '\\' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a%' similar to 'a\\%' ESCAPE '\\' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a(b{3})' similar to 'a(b{3})' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a(b{3})' similar to 'a\\(b\\{3\\}\\)' ESCAPE '\\' "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[a-ey]d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ey]d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ex-z]d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[a-ex-z]d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[x-za-e]d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ey]?d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yyyd' similar to '[a-ey]*d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// range must be specified in []
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to 'x-zd'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x-z'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '([a-e])d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'xy' similar to 'x*?y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x*?y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to '(x?)*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x+?y'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x?+y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x*+y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// dot is a wildcard for SIMILAR TO but not LIKE
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' similar to 'a.c'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a.c' similar to 'a.c'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a.*d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' like 'a.c'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'a.c' like 'a.c'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like 'a.*d'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// The following two tests throws exception(They probably should).
comment|// "Dangling meta character '*' near index 2"
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x+*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x?*y'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// some negative tests
name|f
operator|.
name|checkFails
argument_list|(
literal|"'yd' similar to '[x-ze-a]d'"
argument_list|,
literal|"Illegal character range near index 6\n"
operator|+
literal|"\\[x-ze-a\\]d\n"
operator|+
literal|"      \\^"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// illegal range
comment|// Slightly different error message from JDK 13 onwards
specifier|final
name|String
name|expectedError
init|=
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
operator|>=
literal|13
condition|?
literal|"Illegal repetition near index 22\n"
operator|+
literal|"\\[\\:LOWER\\:\\]\\{2\\}\\[\\:DIGIT\\:\\]\\{,5\\}\n"
operator|+
literal|"                      \\^"
else|:
literal|"Illegal repetition near index 20\n"
operator|+
literal|"\\[\\:LOWER\\:\\]\\{2\\}\\[\\:DIGIT\\:\\]\\{,5\\}\n"
operator|+
literal|"                    \\^"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}[:DIGIT:]{,5}'"
argument_list|,
name|expectedError
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
literal|"'cd' similar to '[(a-e)]d' "
argument_list|,
literal|"Invalid regular expression: \\[\\(a-e\\)\\]d at 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"'yd' similar to '[(a-e)]d' "
argument_list|,
literal|"Invalid regular expression: \\[\\(a-e\\)\\]d at 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// all the following tests wrong results due to missing functionality
comment|// or defect (FRG-375, 377).
if|if
condition|(
name|Bug
operator|.
name|FRG375_FIXED
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '[a-e^c]d' "
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// FRG-375
block|}
comment|// following tests use regular character set identifiers.
comment|// Not implemented yet. FRG-377.
if|if
condition|(
name|Bug
operator|.
name|FRG377_FIXED
condition|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to '[:ALPHA:]*'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:LOWER:]{2}[:DIGIT:]*'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]*'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]*[:DIGIT:]?'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]?[:DIGIT:]*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '([:LOWER:]{2})[:DIGIT:]{2,5}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}[:DIGIT:]{2,}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}||[:DIGIT:]{4}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}[:DIGIT:]{3}'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'yd  3223' similar to '[:UPPER:]{2}  [:DIGIT:]{3}'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'YD  3223' similar to '[:UPPER:]{2}  [:DIGIT:]{3}'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'YD  3223' similar to "
operator|+
literal|"'[:UPPER:]{2}||[:WHITESPACE:]*[:DIGIT:]{4}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t3223' similar to "
operator|+
literal|"'[:UPPER:]{2}[:SPACE:]*[:DIGIT:]{4}'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t3223' similar to "
operator|+
literal|"'[:UPPER:]{2}[:WHITESPACE:]*[:DIGIT:]{4}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t\t3223' similar to "
operator|+
literal|"'([:UPPER:]{2}[:WHITESPACE:]+)||[:DIGIT:]{4}'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testEscapeOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ESCAPE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConvertFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONVERT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTranslateFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRANSLATE
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTranslate3Func
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TRANSLATE3
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^translate('aabbcc', 'ab', '+-')^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TRANSLATE3\\(<CHARACTER>,<CHARACTER>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"translate('aabbcc', 'ab', '+-')"
argument_list|,
literal|"++--cc"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"translate('aabbcc', 'ab', 'ba')"
argument_list|,
literal|"bbaacc"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"translate('aabbcc', 'ab', '')"
argument_list|,
literal|"cc"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"translate('aabbcc', '', '+-')"
argument_list|,
literal|"aabbcc"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"translate(cast('aabbcc' as varchar(10)), 'ab', '+-')"
argument_list|,
literal|"++--cc"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"translate(cast(null as varchar(7)), 'ab', '+-')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"translate('aabbcc', cast(null as varchar(2)), '+-')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"translate('aabbcc', 'ab', cast(null as varchar(2)))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|libraries
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|libraries
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverlayFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVERLAY
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1)"
argument_list|,
literal|"abcdef"
argument_list|,
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for 2)"
argument_list|,
literal|"abcCdef"
argument_list|,
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(cast('ABCdef' as varchar(10)) placing "
operator|+
literal|"cast('abc' as char(5)) from 1 for 2)"
argument_list|,
literal|"abc  Cdef"
argument_list|,
literal|"VARCHAR(15) NOT NULL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(cast('ABCdef' as char(10)) placing "
operator|+
literal|"cast('abc' as char(5)) from 1 for 2)"
argument_list|,
literal|"abc  Cdef    "
argument_list|,
literal|"VARCHAR(15) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"overlay('ABCdef' placing 'abc'"
operator|+
literal|" from 1 for cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"overlay(cast(null as varchar(1)) placing 'abc' from 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(x'ABCdef' placing x'abcd' from 1)"
argument_list|,
literal|"abcdef"
argument_list|,
literal|"VARBINARY(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(x'ABCDEF1234' placing x'2345' from 1 for 2)"
argument_list|,
literal|"2345ef1234"
argument_list|,
literal|"VARBINARY(7) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(cast(x'ABCdef' as varbinary(5)) placing "
operator|+
literal|"cast(x'abcd' as binary(3)) from 1 for 2)"
argument_list|,
literal|"abc  Cdef"
argument_list|,
literal|"VARBINARY(8) NOT NULL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"overlay(cast(x'ABCdef' as binary(5)) placing "
operator|+
literal|"cast(x'abcd' as binary(3)) from 1 for 2)"
argument_list|,
literal|"abc  Cdef    "
argument_list|,
literal|"VARBINARY(8) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"overlay(x'ABCdef' placing x'abcd'"
operator|+
literal|" from 1 for cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"overlay(cast(null as varbinary(1)) placing x'abcd' from 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"overlay(x'abcd' placing x'abcd' from cast(null as integer))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPositionFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POSITION
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abc')"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 3)"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 5)"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 6)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM -5)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc' FROM 3)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc' FROM 10)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbcc')"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 3)"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 5)"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 6)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM -5)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'cc' in x'aabbccdd' FROM 2)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc' FROM 3)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc' FROM 10)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// FRG-211
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"position('tra' in 'fdgjklewrtra')"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"position(cast(null as varchar(1)) in '0010')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"position('a' in cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"position(cast('a' as char) in cast('bca' as varchar))"
argument_list|,
literal|3
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testReplaceFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|REPLACE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPLACE('ciao', 'ciao', '')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPLACE('hello world', 'o', '')"
argument_list|,
literal|"hell wrld"
argument_list|,
literal|"VARCHAR(11) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPLACE(cast(null as varchar(5)), 'ciao', '')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPLACE('ciao', cast(null as varchar(3)), 'zz')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPLACE('ciao', 'bella', cast(null as varchar(3)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCharLengthFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHAR_LENGTH
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"char_length('abc')"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"char_length(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCharacterLengthFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHARACTER_LENGTH
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"CHARACTER_LENGTH('abc')"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"CHARACTER_LENGTH(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLengthFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LENGTH
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^length('hello')^"
argument_list|,
literal|"No match found for function signature LENGTH\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"length('hello')"
argument_list|,
literal|"5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"length('')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"length(CAST('x' as CHAR(3)))"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"length(CAST('x' as VARCHAR(4)))"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"length(CAST(NULL as CHAR(5)))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOctetLengthFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OCTET_LENGTH
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"OCTET_LENGTH(x'aabbcc')"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"OCTET_LENGTH(cast(null as varbinary(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAsciiFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ASCII
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('')"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('a')"
argument_list|,
literal|97
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('1')"
argument_list|,
literal|49
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('abc')"
argument_list|,
literal|97
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('ABC')"
argument_list|,
literal|65
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u0082')"
argument_list|,
literal|130
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u5B57')"
argument_list|,
literal|23383
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u03a9')"
argument_list|,
literal|937
argument_list|)
expr_stmt|;
comment|// omega
name|f
operator|.
name|checkNull
argument_list|(
literal|"ASCII(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testToBase64
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TO_BASE64
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64(x'546869732069732061207465737420537472696e672e')"
argument_list|,
literal|"VGhpcyBpcyBhIHRlc3QgU3RyaW5nLg=="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64(x'546869732069732061207465737420537472696e672e20636865"
operator|+
literal|"636b20726573756c7465206f7574206f66203736546869732069732061207465737420537472696e"
operator|+
literal|"672e546869732069732061207465737420537472696e672e54686973206973206120746573742053"
operator|+
literal|"7472696e672e546869732069732061207465737420537472696e672e546869732069732061207465"
operator|+
literal|"737420537472696e672e20546869732069732061207465737420537472696e672e20636865636b20"
operator|+
literal|"726573756c7465206f7574206f66203736546869732069732061207465737420537472696e672e54"
operator|+
literal|"6869732069732061207465737420537472696e672e54686973206973206120746573742053747269"
operator|+
literal|"6e672e546869732069732061207465737420537472696e672e546869732069732061207465737420"
operator|+
literal|"537472696e672e20546869732069732061207465737420537472696e672e20636865636b20726573"
operator|+
literal|"756c7465206f7574206f66203736546869732069732061207465737420537472696e672e54686973"
operator|+
literal|"2069732061207465737420537472696e672e546869732069732061207465737420537472696e672e"
operator|+
literal|"546869732069732061207465737420537472696e672e546869732069732061207465737420537472"
operator|+
literal|"696e672e')"
argument_list|,
literal|"VGhpcyBpcyBhIHRlc3QgU3RyaW5nLiBjaGVjayByZXN1bHRlIG91dCBvZiA3NlRoaXMgaXMgYSB0\n"
operator|+
literal|"ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRo\n"
operator|+
literal|"aXMgaXMgYSB0ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuIFRoaXMgaXMgYSB0ZXN0\n"
operator|+
literal|"IFN0cmluZy4gY2hlY2sgcmVzdWx0ZSBvdXQgb2YgNzZUaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhp\n"
operator|+
literal|"cyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMgYSB0ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBT\n"
operator|+
literal|"dHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLiBUaGlzIGlzIGEgdGVzdCBTdHJpbmcuIGNoZWNr\n"
operator|+
literal|"IHJlc3VsdGUgb3V0IG9mIDc2VGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMgYSB0ZXN0IFN0\n"
operator|+
literal|"cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMg\n"
operator|+
literal|"YSB0ZXN0IFN0cmluZy4="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64('This is a test String.')"
argument_list|,
literal|"VGhpcyBpcyBhIHRlc3QgU3RyaW5nLg=="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64('This is a test String. check resulte out of 76T"
operator|+
literal|"his is a test String.This is a test String.This is a test String.This is a "
operator|+
literal|"test String.This is a test String. This is a test String. check resulte out "
operator|+
literal|"of 76This is a test String.This is a test String.This is a test String.This "
operator|+
literal|"is a test String.This is a test String. This is a test String. check resulte "
operator|+
literal|"out of 76This is a test String.This is a test String.This is a test String."
operator|+
literal|"This is a test String.This is a test String.')"
argument_list|,
literal|"VGhpcyBpcyBhIHRlc3QgU3RyaW5nLiBjaGVjayByZXN1bHRlIG91dCBvZiA3NlRoaXMgaXMgYSB0\n"
operator|+
literal|"ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRo\n"
operator|+
literal|"aXMgaXMgYSB0ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuIFRoaXMgaXMgYSB0ZXN0\n"
operator|+
literal|"IFN0cmluZy4gY2hlY2sgcmVzdWx0ZSBvdXQgb2YgNzZUaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhp\n"
operator|+
literal|"cyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMgYSB0ZXN0IFN0cmluZy5UaGlzIGlzIGEgdGVzdCBT\n"
operator|+
literal|"dHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLiBUaGlzIGlzIGEgdGVzdCBTdHJpbmcuIGNoZWNr\n"
operator|+
literal|"IHJlc3VsdGUgb3V0IG9mIDc2VGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMgYSB0ZXN0IFN0\n"
operator|+
literal|"cmluZy5UaGlzIGlzIGEgdGVzdCBTdHJpbmcuVGhpcyBpcyBhIHRlc3QgU3RyaW5nLlRoaXMgaXMg\n"
operator|+
literal|"YSB0ZXN0IFN0cmluZy4="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64('')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64('a')"
argument_list|,
literal|"YQ=="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"to_base64(x'61')"
argument_list|,
literal|"YQ=="
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFromBase64
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|FROM_BASE64
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^from_base64('2fjoeiwjfoj==')^"
argument_list|,
literal|"No match found for function signature FROM_BASE64\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"from_base64('VGhpcyBpcyBhIHRlc3QgU3RyaW5nLg==')"
argument_list|,
literal|"546869732069732061207465737420537472696e672e"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"from_base64('VGhpcyBpcyBhIHRlc\t3QgU3RyaW5nLg==')"
argument_list|,
literal|"546869732069732061207465737420537472696e672e"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"from_base64('VGhpcyBpcyBhIHRlc\t3QgU3\nRyaW5nLg==')"
argument_list|,
literal|"546869732069732061207465737420537472696e672e"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"from_base64('VGhpcyB  pcyBhIHRlc3Qg\tU3Ry\naW5nLg==')"
argument_list|,
literal|"546869732069732061207465737420537472696e672e"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"from_base64('-1')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"from_base64('-100')"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMd5
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|MD5
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^md5(x'')^"
argument_list|,
literal|"No match found for function signature MD5\\(<BINARY>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|libraries
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"md5(x'')"
argument_list|,
literal|"d41d8cd98f00b204e9800998ecf8427e"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"md5('')"
argument_list|,
literal|"d41d8cd98f00b204e9800998ecf8427e"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"md5('ABC')"
argument_list|,
literal|"902fbdd2b1df0c4f70b4a5d23525e932"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"md5(x'414243')"
argument_list|,
literal|"902fbdd2b1df0c4f70b4a5d23525e932"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|libraries
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSha1
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SHA1
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^sha1(x'')^"
argument_list|,
literal|"No match found for function signature SHA1\\(<BINARY>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|libraries
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"sha1(x'')"
argument_list|,
literal|"da39a3ee5e6b4b0d3255bfef95601890afd80709"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"sha1('')"
argument_list|,
literal|"da39a3ee5e6b4b0d3255bfef95601890afd80709"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"sha1('ABC')"
argument_list|,
literal|"3c01bdbb26f358bab27f267924aa2c9a03fcfdb8"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"sha1(x'414243')"
argument_list|,
literal|"3c01bdbb26f358bab27f267924aa2c9a03fcfdb8"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|libraries
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRepeatFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REPEAT
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^repeat('a', -100)^"
argument_list|,
literal|"No match found for function signature REPEAT\\(<CHARACTER>,<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"REPEAT('a', -100)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPEAT('a', -1)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPEAT('a', 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPEAT('a', 2)"
argument_list|,
literal|"aa"
argument_list|,
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"REPEAT('abc', 3)"
argument_list|,
literal|"abcabcabc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), -1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), 2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPEAT('abc', cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), cast(null as integer))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSpaceFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SPACE
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SPACE(-100)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SPACE(-1)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SPACE(0)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SPACE(2)"
argument_list|,
literal|"  "
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SPACE(5)"
argument_list|,
literal|"     "
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"SPACE(cast(null as integer))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStrcmpFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|STRCMP
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"STRCMP('mytesttext', 'mytesttext')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"STRCMP('mytesttext', 'mytest_text')"
argument_list|,
literal|"-1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"STRCMP('mytest_text', 'mytesttext')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRCMP('mytesttext', cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRCMP(cast(null as varchar(1)), 'mytesttext')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSoundexFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SOUNDEX
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^soundex('tech on the net')^"
argument_list|,
literal|"No match found for function signature SOUNDEX\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|libraries
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('TECH ON THE NET')"
argument_list|,
literal|"T253"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('Miller')"
argument_list|,
literal|"M460"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('miler')"
argument_list|,
literal|"M460"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('myller')"
argument_list|,
literal|"M460"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('muller')"
argument_list|,
literal|"M460"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('m')"
argument_list|,
literal|"M000"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('mu')"
argument_list|,
literal|"M000"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SOUNDEX('mile')"
argument_list|,
literal|"M400"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"SOUNDEX(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"SOUNDEX(_UTF8'\u5B57\u5B57')"
argument_list|,
literal|"The character is not mapped.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|libraries
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDifferenceFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DIFFERENCE
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('Miller', 'miller')"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('Miller', 'myller')"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'miller')"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'miller')"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'milk')"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'mile')"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'm')"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'lee')"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"DIFFERENCE('muller', cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"DIFFERENCE(cast(null as varchar(1)), 'muller')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testReverseFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REVERSE
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^reverse('abc')^"
argument_list|,
literal|"No match found for function signature REVERSE\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse('')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse('123')"
argument_list|,
literal|"321"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse('abc')"
argument_list|,
literal|"cba"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse('ABC')"
argument_list|,
literal|"CBA"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse('Hello World')"
argument_list|,
literal|"dlroW olleH"
argument_list|,
literal|"VARCHAR(11) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"reverse(_UTF8'\u4F60\u597D')"
argument_list|,
literal|"\u597D\u4F60"
argument_list|,
literal|"VARCHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"reverse(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIfFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkIf
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkIf
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|HIVE
argument_list|)
argument_list|)
expr_stmt|;
name|checkIf
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkIf
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|IF
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"if(1 = 2, 1, 2)"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"if('abc'='xyz', 'abc', 'xyz')"
argument_list|,
literal|"xyz"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"if(substring('abc',1,2)='ab', 'abc', 'xyz')"
argument_list|,
literal|"abc"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"if(substring('abc',1,2)='ab', 'abc', 'wxyz')"
argument_list|,
literal|"abc "
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// TRUE yields first arg, FALSE and UNKNOWN yield second arg
name|f
operator|.
name|checkScalar
argument_list|(
literal|"if(nullif(true,false), 5, 10)"
argument_list|,
literal|5
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"if(nullif(true,true), 5, 10)"
argument_list|,
literal|10
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"if(nullif(true,true), 5, 10)"
argument_list|,
literal|10
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpperFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UPPER
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"upper('a')"
argument_list|,
literal|"A"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"upper('A')"
argument_list|,
literal|"A"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"upper('1')"
argument_list|,
literal|"1"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"upper('aa')"
argument_list|,
literal|"AA"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"upper(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeftFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LEFT
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left('abcd', 3)"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left('abcd', 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left('abcd', 5)"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left('abcd', -2)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"left(cast(null as varchar(1)), -2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"left('abcd', cast(null as Integer))"
argument_list|)
expr_stmt|;
comment|// test for ByteString
name|f
operator|.
name|checkString
argument_list|(
literal|"left(x'ABCdef', 1)"
argument_list|,
literal|"ab"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left(x'ABCdef', 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left(x'ABCdef', 4)"
argument_list|,
literal|"abcdef"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"left(x'ABCdef', -2)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"left(cast(null as binary(1)), -2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"left(x'ABCdef', cast(null as Integer))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRightFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right('abcd', 3)"
argument_list|,
literal|"bcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right('abcd', 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right('abcd', 5)"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right('abcd', -2)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"right(cast(null as varchar(1)), -2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"right('abcd', cast(null as Integer))"
argument_list|)
expr_stmt|;
comment|// test for ByteString
name|f
operator|.
name|checkString
argument_list|(
literal|"right(x'ABCdef', 1)"
argument_list|,
literal|"ef"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right(x'ABCdef', 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right(x'ABCdef', 4)"
argument_list|,
literal|"abcdef"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"right(x'ABCdef', -2)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"right(cast(null as binary(1)), -2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"right(x'ABCdef', cast(null as Integer))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRegexpReplaceFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REGEXP_REPLACE
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('a b c', 'b', 'X')"
argument_list|,
literal|"a X c"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def ghi', '[a-z]+', 'X')"
argument_list|,
literal|"X X X"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('100-200', '(\\d+)', 'num')"
argument_list|,
literal|"num-num"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('100-200', '(-)', '###')"
argument_list|,
literal|"100###200"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace(cast(null as varchar), '(-)', '###')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace('100-200', cast(null as varchar), '###')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace('100-200', '(-)', cast(null as varchar))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def ghi', '[a-z]+', 'X', 2)"
argument_list|,
literal|"aX X X"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def ghi', '[a-z]+', 'X', 1, 3)"
argument_list|,
literal|"abc def X"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def GHI', '[a-z]+', 'X', 1, 3, 'c')"
argument_list|,
literal|"abc def GHI"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def GHI', '[a-z]+', 'X', 1, 3, 'i')"
argument_list|,
literal|"abc def X"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc def GHI', '[a-z]+', 'X', 1, 3, 'i')"
argument_list|,
literal|"abc def X"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc\t\ndef\t\nghi', '\t', '+')"
argument_list|,
literal|"abc+\ndef+\nghi"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc\t\ndef\t\nghi', '\t\n', '+')"
argument_list|,
literal|"abc+def+ghi"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"regexp_replace('abc\t\ndef\t\nghi', '\\w+', '+')"
argument_list|,
literal|"+\t\n+\t\n+"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1, 3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1, 3, 'i')"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonExists
parameter_list|()
block|{
comment|// default pathmode the default is: strict mode
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'$.foo')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' false on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' true on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' unknown on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' false on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' true on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' unknown on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{}', "
operator|+
literal|"'invalid $.foo' false on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{}', "
operator|+
literal|"'invalid $.foo' true on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{}', "
operator|+
literal|"'invalid $.foo' unknown on error)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// not exists
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo1' false on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo1' true on error)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo1' unknown on error)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' true on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' false on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' error on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' unknown on error)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_exists(^null^, "
operator|+
literal|"'lax $' unknown on error)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_exists(null, 'lax $' unknown on error)"
argument_list|,
literal|null
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_exists(cast(null as varchar), "
operator|+
literal|"'lax $.foo1' unknown on error)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonInsert
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_insert('10', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"10"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_insert('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"{\"a\":1,\"b\":[2],\"c\":\"[true]\"}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_insert('{ \"a\": 1, \"b\": [2]}', '$', 10, '$', '[true]')"
argument_list|,
literal|"{\"a\":1,\"b\":[2]}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_insert('{ \"a\": 1, \"b\": [2]}', '$.b[1]', 10)"
argument_list|,
literal|"{\"a\":1,\"b\":[2,10]}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_insert('{\"a\": 1, \"b\": [2, 3, [true]]}', '$.b[3]', 'false')"
argument_list|,
literal|"{\"a\":1,\"b\":[2,3,[true],\"false\"]}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_insert('{\"a\": 1, \"b\": [2, 3, [true]]}', 'a', 'false')"
argument_list|,
literal|"(?s).*Invalid input for.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_insert(cast(null as varchar), '$', 10)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonReplace
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_replace('10', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"10"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"{\"a\":10,\"b\":[2]}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2]}', '$', 10, '$.c', '[true]')"
argument_list|,
literal|"10"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2]}', '$.b', 10, '$.c', '[true]')"
argument_list|,
literal|"{\"a\":1,\"b\":10}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2, 3]}', '$.b[1]', 10)"
argument_list|,
literal|"{\"a\":1,\"b\":[2,10]}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_replace('{\"a\": 1, \"b\": [2, 3, [true]]}', 'a', 'false')"
argument_list|,
literal|"(?s).*Invalid input for.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_replace(cast(null as varchar), '$', 10)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonSet
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_set('10', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"10"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_set('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|,
literal|"{\"a\":10,\"b\":[2],\"c\":\"[true]\"}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_set('{ \"a\": 1, \"b\": [2]}', '$', 10, '$.c', '[true]')"
argument_list|,
literal|"10"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_set('{ \"a\": 1, \"b\": [2, 3]}', '$.b[1]', 10, '$.c', '[true]')"
argument_list|,
literal|"{\"a\":1,\"b\":[2,10],\"c\":\"[true]\"}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_set('{\"a\": 1, \"b\": [2, 3, [true]]}', 'a', 'false')"
argument_list|,
literal|"(?s).*Invalid input for.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_set(cast(null as varchar), '$', 10)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonValue
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' error on empty)"
argument_list|,
literal|"(?s).*Empty result of JSON_VALUE function is not allowed.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// default pathmode the default is: strict mode
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', '$.foo')"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// type casting test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo')"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo' returning integer)"
argument_list|,
literal|100
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":\"100\"}', 'strict $.foo' returning boolean)"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' returning integer "
operator|+
literal|"null on empty)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"json_value('{\"foo\":\"100\"}', 'strict $.foo1' returning boolean "
operator|+
literal|"null on error)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// lax test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' null on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' error on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' default 'empty' on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' null on empty)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' error on empty)"
argument_list|,
literal|"(?s).*Empty result of JSON_VALUE function is not allowed.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' default 'empty' on empty)"
argument_list|,
literal|"empty"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":{}}', 'lax $.foo' null on empty)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":{}}', 'lax $.foo' error on empty)"
argument_list|,
literal|"(?s).*Empty result of JSON_VALUE function is not allowed.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":{}}', 'lax $.foo' default 'empty' on empty)"
argument_list|,
literal|"empty"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' null on error)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' error on error)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' default 'empty' on error)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// path error test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'invalid $.foo' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":100}', 'invalid $.foo' error on error)"
argument_list|,
literal|"(?s).*Illegal jsonpath spec.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', "
operator|+
literal|"'invalid $.foo' default 'empty' on error)"
argument_list|,
literal|"empty"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// strict test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo' null on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo' error on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', "
operator|+
literal|"'strict $.foo' default 'empty' on empty)"
argument_list|,
literal|"100"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo1' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":100}', 'strict $.foo1' error on error)"
argument_list|,
literal|"(?s).*No results for path: \\$\\['foo1'\\].*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":100}', "
operator|+
literal|"'strict $.foo1' default 'empty' on error)"
argument_list|,
literal|"empty"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":{}}', 'strict $.foo' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_value('{\"foo\":{}}', 'strict $.foo' error on error)"
argument_list|,
literal|"(?s).*Strict jsonpath mode requires scalar value, "
operator|+
literal|"and the actual value is: '\\{\\}'.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value('{\"foo\":{}}', "
operator|+
literal|"'strict $.foo' default 'empty' on error)"
argument_list|,
literal|"empty"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_value(^null^, 'strict $')"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_value(null, 'strict $')"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_value(cast(null as varchar), 'strict $')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonQuery
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// default pathmode the default is: strict mode
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', '$' null on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// lax test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $' null on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $' error on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $' empty array on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $' empty object on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $.foo' null on empty)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $.foo' error on empty)"
argument_list|,
literal|"(?s).*Empty result of JSON_QUERY function is not allowed.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $.foo' empty array on empty)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'lax $.foo' empty object on empty)"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// path error test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'invalid $.foo' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_query('{\"foo\":100}', 'invalid $.foo' error on error)"
argument_list|,
literal|"(?s).*Illegal jsonpath spec.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', "
operator|+
literal|"'invalid $.foo' empty array on error)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', "
operator|+
literal|"'invalid $.foo' empty object on error)"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// strict test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $' null on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $' error on empty)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $' empty array on error)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $' empty object on error)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo1' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo1' error on error)"
argument_list|,
literal|"(?s).*No results for path: \\$\\['foo1'\\].*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo1' empty array on error)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo1' empty object on error)"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' null on error)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' error on error)"
argument_list|,
literal|"(?s).*Strict jsonpath mode requires array or object value, "
operator|+
literal|"and the actual value is: '100'.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' empty array on error)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' empty object on error)"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// array wrapper test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' without wrapper)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' without array wrapper)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' with wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' "
operator|+
literal|"with unconditional wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":100}', 'strict $.foo' "
operator|+
literal|"with conditional wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":[100]}', 'strict $.foo' without wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":[100]}', 'strict $.foo' without array wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":[100]}', 'strict $.foo' with wrapper)"
argument_list|,
literal|"[[100]]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":[100]}', 'strict $.foo' "
operator|+
literal|"with unconditional wrapper)"
argument_list|,
literal|"[[100]]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query('{\"foo\":[100]}', 'strict $.foo' "
operator|+
literal|"with conditional wrapper)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_query(^null^, 'lax $')"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_query(null, 'lax $')"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_query(cast(null as varchar), 'lax $')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonPretty
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_pretty('{\"foo\":100}')"
argument_list|,
literal|"{\n  \"foo\" : 100\n}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_pretty('[1,2,3]')"
argument_list|,
literal|"[ 1, 2, 3 ]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_pretty('null')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_pretty(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_pretty(null)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_pretty(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonStorageSize
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('[100, \"sakila\", [1, 3, 5], 425.05]')"
argument_list|,
literal|"29"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('{\"a\": 1000,\"b\": \"aa\", \"c\": \"[1, 3, 5]\"}')"
argument_list|,
literal|"35"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('{\"a\": 1000, \"b\": \"wxyz\", \"c\": \"[1, 3]\"}')"
argument_list|,
literal|"34"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('[100, \"json\", [[10, 20, 30], 3, 5], 425.05]')"
argument_list|,
literal|"36"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('12')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('12' format json)"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size('null')"
argument_list|,
literal|"4"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_storage_size(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_storage_size(null)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_storage_size(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonType
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|JSON_TYPE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('\"1\"')"
argument_list|,
literal|"STRING"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('1')"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('11.45')"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('true')"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('null')"
argument_list|,
literal|"NULL"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_type(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('{\"a\": [10, true]}')"
argument_list|,
literal|"OBJECT"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('{}')"
argument_list|,
literal|"OBJECT"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('[10, true]')"
argument_list|,
literal|"ARRAY"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type('\"2019-01-27 21:24:00\"')"
argument_list|,
literal|"STRING"
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_type(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_type(null)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_type(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonDepth
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|JSON_DEPTH
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('1')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('11.45')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('true')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('\"2019-01-27 21:24:00\"')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('{}')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('[]')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('null')"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth(cast(null as varchar(1)))"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('[10, true]')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('[[], {}]')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('{\"a\": [10, true]}')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth('[10, {\"a\": [[1,2]]}]')"
argument_list|,
literal|"5"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_depth(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_depth(null)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_depth(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonLength
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// no path context
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{}')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[]')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"foo\":100}')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"a\": 1, \"b\": {\"c\": 30}}')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[1, 2, {\"a\": 3}]')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// default pathmode the default is: strict mode
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"foo\":100}', '$')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// lax test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{}', 'lax $')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[]', 'lax $')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"foo\":100}', 'lax $')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"a\": 1, \"b\": {\"c\": 30}}', 'lax $')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[1, 2, {\"a\": 3}]', 'lax $')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"a\": 1, \"b\": {\"c\": 30}}', 'lax $.b')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"foo\":100}', 'lax $.foo1')"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// strict test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{}', 'strict $')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[]', 'strict $')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"foo\":100}', 'strict $')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"a\": 1, \"b\": {\"c\": 30}}', 'strict $')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('[1, 2, {\"a\": 3}]', 'strict $')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length('{\"a\": 1, \"b\": {\"c\": 30}}', 'strict $.b')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// catch error test
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_length('{\"foo\":100}', 'invalid $.foo')"
argument_list|,
literal|"(?s).*Illegal jsonpath spec.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_length('{\"foo\":100}', 'strict $.foo1')"
argument_list|,
literal|"(?s).*No results for path.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_length(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_length(null)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_length(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonKeys
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// no path context
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{}')"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[]')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"foo\":100}')"
argument_list|,
literal|"[\"foo\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"a\": 1, \"b\": {\"c\": 30}}')"
argument_list|,
literal|"[\"a\",\"b\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[1, 2, {\"a\": 3}]')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// lax test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{}', 'lax $')"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[]', 'lax $')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"foo\":100}', 'lax $')"
argument_list|,
literal|"[\"foo\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"a\": 1, \"b\": {\"c\": 30}}', 'lax $')"
argument_list|,
literal|"[\"a\",\"b\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[1, 2, {\"a\": 3}]', 'lax $')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"a\": 1, \"b\": {\"c\": 30}}', 'lax $.b')"
argument_list|,
literal|"[\"c\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"foo\":100}', 'lax $.foo1')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// strict test
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{}', 'strict $')"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[]', 'strict $')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"foo\":100}', 'strict $')"
argument_list|,
literal|"[\"foo\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"a\": 1, \"b\": {\"c\": 30}}', 'strict $')"
argument_list|,
literal|"[\"a\",\"b\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('[1, 2, {\"a\": 3}]', 'strict $')"
argument_list|,
literal|"null"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys('{\"a\": 1, \"b\": {\"c\": 30}}', 'strict $.b')"
argument_list|,
literal|"[\"c\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
comment|// catch error test
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_keys('{\"foo\":100}', 'invalid $.foo')"
argument_list|,
literal|"(?s).*Illegal jsonpath spec.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_keys('{\"foo\":100}', 'strict $.foo1')"
argument_list|,
literal|"(?s).*No results for path.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_keys(^null^)"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_keys(null)"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_keys(cast(null as varchar))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonRemove
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove('{\"foo\":100}', '$.foo')"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove('{\"foo\":100, \"foo1\":100}', '$.foo')"
argument_list|,
literal|"{\"foo1\":100}"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$[1][0]')"
argument_list|,
literal|"[\"a\",[\"c\"],\"d\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$[1]')"
argument_list|,
literal|"[\"a\",\"d\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$[0]', '$[0]')"
argument_list|,
literal|"[\"d\"]"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"json_remove('[\"a\", [\"b\", \"c\"], \"d\"]', '$')"
argument_list|,
literal|"(?s).*Invalid input for.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// nulls
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"json_remove(^null^, '$')"
argument_list|,
literal|"(?s).*Illegal use of 'NULL'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_remove(null, '$')"
argument_list|,
literal|null
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"json_remove(cast(null as varchar), '$')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonObject
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object()"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': 'bar')"
argument_list|,
literal|"{\"foo\":\"bar\"}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': 'bar', 'foo2': 'bar2')"
argument_list|,
literal|"{\"foo\":\"bar\",\"foo2\":\"bar2\"}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': null)"
argument_list|,
literal|"{\"foo\":null}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': null null on null)"
argument_list|,
literal|"{\"foo\":null}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': null absent on null)"
argument_list|,
literal|"{}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': 100)"
argument_list|,
literal|"{\"foo\":100}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': json_object('foo': 'bar'))"
argument_list|,
literal|"{\"foo\":{\"foo\":\"bar\"}}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': json_object('foo': 'bar') format json)"
argument_list|,
literal|"{\"foo\":{\"foo\":\"bar\"}}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonObjectAgg
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"json_objectagg('foo': 'bar')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"json_objectagg('foo': null)"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"json_objectagg(100: 'bar')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^json_objectagg(100: 'bar')^"
argument_list|,
literal|"(?s).*Cannot apply.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
index|[]
name|values
init|=
block|{
block|{
literal|"'foo'"
block|,
literal|"'bar'"
block|}
block|,
block|{
literal|"'foo2'"
block|,
literal|"cast(null as varchar(2000))"
block|}
block|,
block|{
literal|"'foo3'"
block|,
literal|"'bar3'"
block|}
block|}
decl_stmt|;
name|f
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"{\"foo\":\"bar\",\"foo2\":null,\"foo3\":\"bar3\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2 null on null)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"{\"foo\":\"bar\",\"foo2\":null,\"foo3\":\"bar3\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2 absent on null)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"{\"foo\":\"bar\",\"foo3\":\"bar3\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonValueExpressionOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"'{}' format json"
argument_list|,
literal|"{}"
argument_list|,
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"'[1, 2, 3]' format json"
argument_list|,
literal|"[1,2,3]"
argument_list|,
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cast(null as varchar) format json"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"'null' format json"
argument_list|,
literal|"null"
argument_list|,
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^null^ format json"
argument_list|,
literal|"(?s).*Illegal use of .NULL.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonArray
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array()"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array('foo')"
argument_list|,
literal|"[\"foo\"]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array('foo', 'bar')"
argument_list|,
literal|"[\"foo\",\"bar\"]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(null)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(null null on null)"
argument_list|,
literal|"[null]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(null absent on null)"
argument_list|,
literal|"[]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(100)"
argument_list|,
literal|"[100]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(json_array('foo'))"
argument_list|,
literal|"[[\"foo\"]]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"json_array(json_array('foo') format json)"
argument_list|,
literal|"[[\"foo\"]]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonArrayAgg
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"json_arrayagg('foo')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"json_arrayagg(null)"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'foo'"
block|,
literal|"cast(null as varchar(2000))"
block|,
literal|"'foo3'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[\"foo\",\"foo3\"]"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x null on null)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[\"foo\",null,\"foo3\"]"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x absent on null)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[\"foo\",\"foo3\"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonPredicate
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json value"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{]' is json value"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json object"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json object"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json array"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json array"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'100' is json scalar"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json scalar"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json value"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{]' is not json value"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json object"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is not json object"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json array"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is not json array"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'100' is not json scalar"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is not json scalar"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCompress
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"COMPRESS(NULL)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"COMPRESS('')"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"COMPRESS(REPEAT('a',1000))"
argument_list|,
literal|"e8030000789c4b4c1c05a360140c770000f9d87af8"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"COMPRESS(REPEAT('a',16))"
argument_list|,
literal|"10000000789c4b4c44050033980611"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"COMPRESS('sample')"
argument_list|,
literal|"06000000789c2b4ecc2dc849050008de0283"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"COMPRESS('example')"
argument_list|,
literal|"07000000789c4bad48cc2dc84905000bc002ed"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractValue
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ExtractValue(NULL, '//b')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ExtractValue('', NULL)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"ExtractValue('<a><b/></a>', '#/a/b')"
argument_list|,
literal|"Invalid input for EXTRACTVALUE: xml: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"ExtractValue('<a><b/></a></a>', '/b')"
argument_list|,
literal|"Invalid input for EXTRACTVALUE: xml: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a>c</a>', '//a')"
argument_list|,
literal|"c"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a>ccc<b>ddd</b></a>', '/a')"
argument_list|,
literal|"ccc"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a>ccc<b>ddd</b></a>', '/a/b')"
argument_list|,
literal|"ddd"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a>ccc<b>ddd</b></a>', '/b')"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a>ccc<b>ddd</b><b>eee</b></a>', '//b')"
argument_list|,
literal|"ddd eee"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ExtractValue('<a><b/></a>', 'count(/a/b)')"
argument_list|,
literal|"1"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testXmlTransform
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"XMLTRANSFORM('', NULL)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"XMLTRANSFORM(NULL,'')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"XMLTRANSFORM('', '<')"
argument_list|,
literal|"Illegal xslt specified : '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"XMLTRANSFORM('<', '<?xml version=\"1.0\"?>\n"
operator|+
literal|"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
operator|+
literal|"</xsl:stylesheet>')"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
name|sql
argument_list|,
literal|"Invalid input for XMLTRANSFORM xml: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"XMLTRANSFORM("
operator|+
literal|"'<?xml version=\"1.0\"?>\n"
operator|+
literal|"<Article>\n"
operator|+
literal|"<Title>My Article</Title>\n"
operator|+
literal|"<Authors>\n"
operator|+
literal|"<Author>Mr. Foo</Author>\n"
operator|+
literal|"<Author>Mr. Bar</Author>\n"
operator|+
literal|"</Authors>\n"
operator|+
literal|"<Body>This is my article text.</Body>\n"
operator|+
literal|"</Article>'"
operator|+
literal|","
operator|+
literal|"'<?xml version=\"1.0\"?>\n"
operator|+
literal|"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3"
operator|+
literal|".org/1999/XSL/Transform\">"
operator|+
literal|"<xsl:output method=\"text\"/>"
operator|+
literal|"<xsl:template match=\"/\">"
operator|+
literal|"    Article -<xsl:value-of select=\"/Article/Title\"/>"
operator|+
literal|"    Authors:<xsl:apply-templates select=\"/Article/Authors/Author\"/>"
operator|+
literal|"</xsl:template>"
operator|+
literal|"<xsl:template match=\"Author\">"
operator|+
literal|"    -<xsl:value-of select=\".\" />"
operator|+
literal|"</xsl:template>"
operator|+
literal|"</xsl:stylesheet>')"
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
name|sql2
argument_list|,
literal|"    Article - My Article    Authors:     - Mr. Foo    - Mr. Bar"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractXml
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"\"EXTRACT\"('', '<','a')"
argument_list|,
literal|"Invalid input for EXTRACT xpath: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"\"EXTRACT\"('', '<')"
argument_list|,
literal|"Invalid input for EXTRACT xpath: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"\"EXTRACT\"('', NULL)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"\"EXTRACT\"(NULL,'')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"("
operator|+
literal|"'<Article>"
operator|+
literal|"<Title>Article1</Title>"
operator|+
literal|"<Authors>"
operator|+
literal|"<Author>Foo</Author>"
operator|+
literal|"<Author>Bar</Author>"
operator|+
literal|"</Authors>"
operator|+
literal|"<Body>article text.</Body>"
operator|+
literal|"</Article>', '/Article/Title')"
argument_list|,
literal|"<Title>Article1</Title>"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"('"
operator|+
literal|"<Article>"
operator|+
literal|"<Title>Article1</Title>"
operator|+
literal|"<Title>Article2</Title>"
operator|+
literal|"<Authors><Author>Foo</Author><Author>Bar</Author></Authors>"
operator|+
literal|"<Body>article text.</Body>"
operator|+
literal|"</Article>', '/Article/Title')"
argument_list|,
literal|"<Title>Article1</Title><Title>Article2</Title>"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso.com/books\">"
operator|+
literal|"<book><title>Title</title>"
operator|+
literal|"<author>Author Name</author>"
operator|+
literal|"<price>5.50</price>"
operator|+
literal|"</book>"
operator|+
literal|"</books>', "
operator|+
literal|"'/books:books/books:book', "
operator|+
literal|"'books=\"http://www.contoso.com/books\"')"
argument_list|,
literal|"<book xmlns=\"http://www.contoso.com/books\"><title>Title</title><author>Author "
operator|+
literal|"Name</author><price>5.50</price></book>"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExistsNode
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"EXISTSNODE('', '<','a')"
argument_list|,
literal|"Invalid input for EXISTSNODE xpath: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"EXISTSNODE('', '<')"
argument_list|,
literal|"Invalid input for EXISTSNODE xpath: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"EXISTSNODE('', NULL)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"EXISTSNODE(NULL,'')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article>"
operator|+
literal|"<Title>Article1</Title>"
operator|+
literal|"<Authors><Author>Foo</Author><Author>Bar</Author></Authors>"
operator|+
literal|"<Body>article text.</Body>"
operator|+
literal|"</Article>', '/Article/Title')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article>"
operator|+
literal|"<Title>Article1</Title>"
operator|+
literal|"<Authors><Author>Foo</Author><Author>Bar</Author></Authors>"
operator|+
literal|"<Body>article text.</Body></Article>', '/Article/Title/Books')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article>"
operator|+
literal|"<Title>Article1</Title>"
operator|+
literal|"<Title>Article2</Title>"
operator|+
literal|"<Authors><Author>Foo</Author><Author>Bar</Author></Authors>"
operator|+
literal|"<Body>article text.</Body></Article>', '/Article/Title')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso.com/books\">"
operator|+
literal|"<book>"
operator|+
literal|"<title>Title</title>"
operator|+
literal|"<author>Author Name</author>"
operator|+
literal|"<price>5.50</price>"
operator|+
literal|"</book>"
operator|+
literal|"</books>', "
operator|+
literal|"'/books:books/books:book', "
operator|+
literal|"'books=\"http://www.contoso.com/books\"')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso.com/books\">"
operator|+
literal|"<book><title>Title</title>"
operator|+
literal|"<author>Author Name</author>"
operator|+
literal|"<price>5.50</price></book></books>', "
operator|+
literal|"'/books:books/books:book/books:title2', "
operator|+
literal|"'books=\"http://www.contoso.com/books\"'"
operator|+
literal|")"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLowerFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOWER
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// SQL:2003 6.29.8 The type of lower is the type of its argument
name|f
operator|.
name|checkString
argument_list|(
literal|"lower('A')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lower('a')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lower('1')"
argument_list|,
literal|"1"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lower('AA')"
argument_list|,
literal|"aa"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"lower(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInitcapFunc
parameter_list|()
block|{
comment|// Note: the initcap function is an Oracle defined function and is not
comment|// defined in the SQL:2003 standard
comment|// todo: implement in fennel
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|INITCAP
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"initcap('aA')"
argument_list|,
literal|"Aa"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"initcap('Aa')"
argument_list|,
literal|"Aa"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"initcap('1a')"
argument_list|,
literal|"1a"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"initcap('ab cd Ef 12')"
argument_list|,
literal|"Ab Cd Ef 12"
argument_list|,
literal|"CHAR(11) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"initcap(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
comment|// dtbug 232
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^initcap(cast(null as date))^"
argument_list|,
literal|"Cannot apply 'INITCAP' to arguments of type "
operator|+
literal|"'INITCAP\\(<DATE>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'INITCAP\\(<CHARACTER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"initcap(cast(null as date))"
argument_list|,
literal|"VARCHAR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPowerFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POWER
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"power(2,-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"0.25"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"power(cast(null as integer),2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"power(2,cast(null as double))"
argument_list|)
expr_stmt|;
comment|// 'pow' is an obsolete form of the 'power' function
name|f
operator|.
name|checkFails
argument_list|(
literal|"^pow(2,-2)^"
argument_list|,
literal|"No match found for function signature POW\\(<NUMERIC>,<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSqrtFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SQRT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sqrt(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sqrt(cast(2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sqrt(case when false then 2 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^sqrt('abc')^"
argument_list|,
literal|"Cannot apply 'SQRT' to arguments of type "
operator|+
literal|"'SQRT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'SQRT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sqrt('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sqrt(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.4142d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sqrt(cast(2 as decimal(2, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.4142d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sqrt(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sqrt(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExpFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXP
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"exp(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|7.389056
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"exp(-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.1353
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"exp(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"exp(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MOD
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(4,2)"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(8,5)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(-12,7)"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(-12,-7)"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(12,-7)"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(cast(12 as tinyint), cast(-7 as tinyint))"
argument_list|,
literal|"TINYINT NOT NULL"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(cast(9 as decimal(2, 0)), 7)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(7, cast(9 as decimal(2, 0)))"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(cast(-9 as decimal(2, 0)), "
operator|+
literal|"cast(7 as decimal(1, 0)))"
argument_list|,
literal|"DECIMAL(1, 0) NOT NULL"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModFuncNull
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"mod(cast(null as integer),2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"mod(4,cast(null as tinyint))"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|DECIMAL
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"mod(4,cast(null as decimal(12,0)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModFuncDivByZero
parameter_list|()
block|{
comment|// The extra CASE expression is to fool Janino.  It does constant
comment|// reduction and will throw the divide by zero exception while
comment|// compiling the expression.  The test frame work would then issue
comment|// unexpected exception occurred during "validation".  You cannot
comment|// submit as non-runtime because the janino exception does not have
comment|// error position information and the framework is unhappy with that.
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"mod(3,case 'a' when 'a' then 0 end)"
argument_list|,
name|DIVISION_BY_ZERO_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLnFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"ln(2.71828)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"ln(2.71828)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.999999327
argument_list|,
literal|0.0000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ln(cast(null as tinyint))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLogFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOG10
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(10)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(100.0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e8 as double))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|9.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|3.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e-3 as real))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
operator|-
literal|2.0
argument_list|,
literal|0.000001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"log10(cast(null as real))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRandFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^rand^"
argument_list|,
literal|"Column 'RAND' not found in any table"
argument_list|,
literal|false
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// Result must always be between 0 and 1, inclusive.
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand()"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.5
argument_list|,
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testRandSeedFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.6016
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.4728
argument_list|,
literal|0.0001
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRandIntegerFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND_INTEGER
argument_list|,
name|VmName
operator|.
name|EXPAND
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// Result must always be between 0 and 10, inclusive.
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand_integer(11)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|5.0
argument_list|,
literal|5.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testRandIntegerSeedFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND_INTEGER
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"rand_integer(1, 11)"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"rand_integer(2, 11)"
argument_list|,
literal|1
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code ARRAY_CONCAT} function from BigQuery. */
annotation|@
name|Test
name|void
name|testArrayConcat
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_CONCAT
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^array_concat()^"
argument_list|,
name|INVALID_ARGUMENTS_NUMBER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_concat(array[1, 2], array[2, 3])"
argument_list|,
literal|"[1, 2, 2, 3]"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_concat(array[1, 2], array[2, null])"
argument_list|,
literal|"[1, 2, 2, null]"
argument_list|,
literal|"INTEGER ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_concat(array['hello', 'world'], array['!'], "
operator|+
literal|"array[cast(null as char)])"
argument_list|,
literal|"[hello, world, !, null]"
argument_list|,
literal|"CHAR(5) ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"array_concat(cast(null as integer array), array[1])"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code ARRAY_REVERSE} function from BigQuery. */
annotation|@
name|Test
name|void
name|testArrayReverseFunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_REVERSE
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_reverse(array[1])"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_reverse(array[1, 2])"
argument_list|,
literal|"[2, 1]"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_reverse(array[null, 1])"
argument_list|,
literal|"[1, null]"
argument_list|,
literal|"INTEGER ARRAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code ARRAY_LENGTH} function from BigQuery. */
annotation|@
name|Test
name|void
name|testArrayLengthFunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_LENGTH
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_length(array[1])"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array_length(array[1, 2, null])"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"array_length(null)"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code UNIX_SECONDS} and other datetime functions from BigQuery. */
annotation|@
name|Test
name|void
name|testUnixSecondsFunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|UNIX_SECONDS
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"unix_seconds(timestamp '1970-01-01 00:00:00')"
argument_list|,
literal|0
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"unix_seconds(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"unix_millis(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"unix_micros(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_seconds(0)"
argument_list|,
literal|"1970-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestamp_seconds(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestamp_millis(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestamp_micros(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_from_unix_date(0)"
argument_list|,
literal|"1970-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// DATE is a reserved keyword, but the parser has special treatment to
comment|// allow it as a function.
name|f
operator|.
name|checkNull
argument_list|(
literal|"DATE(null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"DATE('1985-12-06')"
argument_list|,
literal|"1985-12-06"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATETIME()"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATETIME('America/Los_Angeles')"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATETIME(CAST(NULL AS VARCHAR(20)))"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAbsFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(-1)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(cast(10 as TINYINT))"
argument_list|,
literal|"TINYINT NOT NULL"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(cast(-20 as SMALLINT))"
argument_list|,
literal|"SMALLINT NOT NULL"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(cast(-100 as INT))"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(cast(1000 as BIGINT))"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(54.4)"
argument_list|,
literal|"DECIMAL(3, 1) NOT NULL"
argument_list|,
literal|"54.4"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(-54.4)"
argument_list|,
literal|"DECIMAL(3, 1) NOT NULL"
argument_list|,
literal|"54.4"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(-9.32E-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"0.0932"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(-3.5 as double))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"3.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(-3.5 as float))"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"3.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(3.5 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"3.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"abs(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAbsFuncIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"abs(interval '-2' day)"
argument_list|,
literal|"+2"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"abs(interval '-5-03' year to month)"
argument_list|,
literal|"+5-03"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"abs(cast(null as interval hour))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAcosFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ACOS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"acos(0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"acos(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"acos(case when false then 0.5 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^acos('abc')^"
argument_list|,
literal|"Cannot apply 'ACOS' to arguments of type "
operator|+
literal|"'ACOS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'ACOS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"acos('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"acos(0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.0472d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"acos(cast(0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.0472d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"acos(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"acos(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAsinFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ASIN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"asin(0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"asin(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"asin(case when false then 0.5 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^asin('abc')^"
argument_list|,
literal|"Cannot apply 'ASIN' to arguments of type "
operator|+
literal|"'ASIN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'ASIN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"asin('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"asin(0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.5236d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"asin(cast(0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.5236d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"asin(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"asin(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAtanFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ATAN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan(cast(2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan(case when false then 2 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^atan('abc')^"
argument_list|,
literal|"Cannot apply 'ATAN' to arguments of type "
operator|+
literal|"'ATAN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'ATAN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.1071d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan(cast(2 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.1071d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"atan(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"atan(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAtan2Func
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ATAN2
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan2(2, -2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(cast(1 as float), -1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan2(case when false then 0.5 else null end, -1)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^atan2('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'ATAN2' to arguments of type "
operator|+
literal|"'ATAN2\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'ATAN2\\(<NUMERIC>,<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"atan2('abc', 'def')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(0.5, -0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(cast(0.5 as decimal(1, 1)),"
operator|+
literal|" cast(-0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"atan2(cast(null as integer), -1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"atan2(1, cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCbrtFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CBRT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cbrt(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cbrt(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cbrt(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^cbrt('abc')^"
argument_list|,
literal|"Cannot apply 'CBRT' to arguments of type "
operator|+
literal|"'CBRT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'CBRT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cbrt('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cbrt(8)"
argument_list|,
literal|"2.0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cbrt(-8)"
argument_list|,
literal|"-2.0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cbrt(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"1.0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cbrt(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cbrt(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCosFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cos(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cos(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cos(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^cos('abc')^"
argument_list|,
literal|"Cannot apply 'COS' to arguments of type "
operator|+
literal|"'COS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'COS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cos('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cos(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.5403d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cos(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.5403d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cos(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cos(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCoshFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|COSH
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^cosh(1)^"
argument_list|,
literal|"No match found for function signature COSH\\(<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkType
argument_list|(
literal|"cosh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cosh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cosh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cosh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cosh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.5430d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cosh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.5430d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cosh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cosh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCotFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COT
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cot(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cot(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cot(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^cot('abc')^"
argument_list|,
literal|"Cannot apply 'COT' to arguments of type "
operator|+
literal|"'COT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'COT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"cot('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cot(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.6421d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"cot(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.6421d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cot(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"cot(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDegreesFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DEGREES
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"degrees(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"degrees(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"degrees(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^degrees('abc')^"
argument_list|,
literal|"Cannot apply 'DEGREES' to arguments of type "
operator|+
literal|"'DEGREES\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'DEGREES\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"degrees('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"degrees(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|57.2958d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"degrees(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|57.2958d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"degrees(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"degrees(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPiFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PI
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"PI"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|3.1415d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^PI()^"
argument_list|,
literal|"No match found for function signature PI\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// assert that PI function is not dynamic [CALCITE-2750]
name|assertThat
argument_list|(
literal|"PI operator should not be identified as dynamic function"
argument_list|,
name|PI
operator|.
name|isDynamicFunction
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRadiansFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RADIANS
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"radians(42)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"radians(cast(42 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"radians(case when false then 42 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^radians('abc')^"
argument_list|,
literal|"Cannot apply 'RADIANS' to arguments of type "
operator|+
literal|"'RADIANS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'RADIANS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"radians('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"radians(42)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.7330d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"radians(cast(42 as decimal(2, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.7330d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"radians(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"radians(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPowFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|POW
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"pow(2,3)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|"8.0"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"pow(2, cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"pow(cast(null as integer), 2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRoundFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROUND
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"round(42, -1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"round(cast(42 as float), 1)"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"round(case when false then 42 else null end, -1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^round('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'ROUND' to arguments of type "
operator|+
literal|"'ROUND\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'ROUND\\(<NUMERIC>,<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"round('abc', 'def')"
argument_list|,
literal|"DECIMAL(19, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(42, -1)"
argument_list|,
literal|40
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(cast(42.346 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|4235
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(cast(-42.346 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|-
literal|4235
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as integer), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as double), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"round(43.21, cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as double))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(42)"
argument_list|,
literal|42
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(cast(42.346 as decimal(2, 3)))"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(42.324)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(5, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"round(42.724)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|43
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(5, 3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSignFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIGN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sign(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sign(cast(1 as float))"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sign(case when false then 1 else null end)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^sign('abc')^"
argument_list|,
literal|"Cannot apply 'SIGN' to arguments of type "
operator|+
literal|"'SIGN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'SIGN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sign('abc')"
argument_list|,
literal|"DECIMAL(19, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"sign(1)"
argument_list|,
literal|1
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"sign(cast(-1 as decimal(1, 0)))"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
literal|"DECIMAL(1, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"sign(cast(0 as float))"
argument_list|,
literal|0d
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sign(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sign(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSinFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sin(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sin(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sin(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^sin('abc')^"
argument_list|,
literal|"Cannot apply 'SIN' to arguments of type "
operator|+
literal|"'SIN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'SIN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sin('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sin(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.8415d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sin(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.8415d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sin(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sin(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSinhFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SINH
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^sinh(1)^"
argument_list|,
literal|"No match found for function signature SINH\\(<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkType
argument_list|(
literal|"sinh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sinh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sinh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sinh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sinh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.1752d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"sinh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.1752d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sinh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"sinh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTanFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TAN
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tan(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tan(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tan(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^tan('abc')^"
argument_list|,
literal|"Cannot apply 'TAN' to arguments of type "
operator|+
literal|"'TAN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'TAN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tan('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"tan(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.5574d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"tan(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|1.5574d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"tan(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"tan(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTanhFunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TANH
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^tanh(1)^"
argument_list|,
literal|"No match found for function signature TANH\\(<NUMERIC>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkType
argument_list|(
literal|"tanh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tanh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tanh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"tanh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"tanh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.7615d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"tanh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isWithin
argument_list|(
literal|0.7615d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"tanh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"tanh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTruncFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TRUNC
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"trunc(42, -1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"trunc(cast(42 as float), 1)"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"trunc(case when false then 42 else null end, -1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^trunc('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'TRUNC' to arguments of type "
operator|+
literal|"'TRUNC\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'TRUNC\\(<NUMERIC>,<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"trunc('abc', 'def')"
argument_list|,
literal|"DECIMAL(19, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(42, -1)"
argument_list|,
literal|40
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(cast(42.345 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|4234
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(cast(-42.345 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|-
literal|4234
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trunc(cast(null as integer), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trunc(cast(null as double), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trunc(43.21, cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(42)"
argument_list|,
literal|42
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(42.324)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(5, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(cast(42.324 as float))"
argument_list|,
literal|42F
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"trunc(cast(42.345 as decimal(2, 3)))"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trunc(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trunc(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTruncateFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRUNCATE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"truncate(42, -1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"truncate(cast(42 as float), 1)"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"truncate(case when false then 42 else null end, -1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^truncate('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'TRUNCATE' to arguments of type "
operator|+
literal|"'TRUNCATE\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'TRUNCATE\\(<NUMERIC>,<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"truncate('abc', 'def')"
argument_list|,
literal|"DECIMAL(19, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(42, -1)"
argument_list|,
literal|40
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(cast(42.345 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|4234
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(cast(-42.345 as decimal(2, 3)), 2)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
operator|-
literal|4234
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as integer), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as double), 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"truncate(43.21, cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(42)"
argument_list|,
literal|42
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(42.324)"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(5, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(cast(42.324 as float))"
argument_list|,
literal|42F
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"truncate(cast(42.345 as decimal(2, 3)))"
argument_list|,
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"DECIMAL(2, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNullifFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NULLIF
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"nullif(1,1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(1.5, 13.56)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|,
literal|"1.5"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(13.56, 1.5)"
argument_list|,
literal|"DECIMAL(4, 2)"
argument_list|,
literal|"13.56"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(1.5, 3)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|,
literal|"1.5"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(3, 1.5)"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5e0, 3e0)"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"1.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5, cast(3e0 as REAL))"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|,
name|isExactly
argument_list|(
literal|"1.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(3, 1.5e0)"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(3, cast(1.5e0 as REAL))"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5e0, 3.4)"
argument_list|,
literal|"DOUBLE"
argument_list|,
name|isExactly
argument_list|(
literal|"1.5"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"nullif(3.4, 1.5e0)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|,
literal|"3.4"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nullif('a','bc')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nullif('a',cast(null as varchar(1)))"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"nullif(cast(null as varchar(1)),'a')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"nullif(cast(null as numeric(4,3)), 4.3)"
argument_list|)
expr_stmt|;
comment|// Error message reflects the fact that Nullif is expanded before it is
comment|// validated (like a C macro). Not perfect, but good enough.
name|f
operator|.
name|checkFails
argument_list|(
literal|"1 + ^nullif(1, date '2005-8-4')^ + 2"
argument_list|,
literal|"(?s)Cannot apply '=' to arguments of type '<INTEGER> =<DATE>'\\..*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"1 + ^nullif(1, 2, 3)^ + 2"
argument_list|,
literal|"Invalid number of arguments to function 'NULLIF'\\. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNullIfOperatorIntervals
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"nullif(interval '2' month, interval '3' year)"
argument_list|,
literal|"+2"
argument_list|,
literal|"INTERVAL MONTH"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"nullif(interval '2 5' day to hour,"
operator|+
literal|" interval '5' second)"
argument_list|,
literal|"+2 05"
argument_list|,
literal|"INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"nullif(interval '3' day, interval '3' day)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCoalesceFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COALESCE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"coalesce('a','b')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"coalesce(null,null,3)"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"1 + ^coalesce('a', 'b', 1, null)^ + 2"
argument_list|,
literal|"Illegal mixing of types in CASE or COALESCE statement"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"1 + coalesce('a', 'b', 1, null) + 2"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUserFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|USER
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"USER"
argument_list|,
literal|"sa"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentUserFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_USER
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"CURRENT_USER"
argument_list|,
literal|"sa"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSessionUserFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SESSION_USER
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"SESSION_USER"
argument_list|,
literal|"sa"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSystemUserFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SYSTEM_USER
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
comment|// e.g. "jhyde"
name|f
operator|.
name|checkString
argument_list|(
literal|"SYSTEM_USER"
argument_list|,
name|user
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentPathFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_PATH
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"CURRENT_PATH"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentRoleFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_ROLE
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
comment|// By default, the CURRENT_ROLE function returns
comment|// the empty string because a role has to be set explicitly.
name|f
operator|.
name|checkString
argument_list|(
literal|"CURRENT_ROLE"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentCatalogFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_CATALOG
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
comment|// By default, the CURRENT_CATALOG function returns
comment|// the empty string because a catalog has to be set explicitly.
name|f
operator|.
name|checkString
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testLocalTimeFuncWithCurrentTime
parameter_list|()
block|{
name|testLocalTimeFunc
argument_list|(
name|currentTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLocalTimeFuncWithFixedTime
parameter_list|()
block|{
name|testLocalTimeFunc
argument_list|(
name|fixedTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testLocalTimeFunc
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOCALTIME
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIME"
argument_list|,
name|TIME_PATTERN
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^LOCALTIME()^"
argument_list|,
literal|"No match found for function signature LOCALTIME\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIME(1)"
argument_list|,
name|TIME_PATTERN
argument_list|,
literal|"TIME(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(LOCALTIME AS VARCHAR(30))"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|.
name|substring
argument_list|(
literal|11
argument_list|)
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIME"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|.
name|substring
argument_list|(
literal|11
argument_list|)
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|right
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testLocalTimestampFuncWithCurrentTime
parameter_list|()
block|{
name|testLocalTimestampFunc
argument_list|(
name|currentTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLocalTimestampFuncWithFixedTime
parameter_list|()
block|{
name|testLocalTimestampFunc
argument_list|(
name|fixedTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testLocalTimestampFunc
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOCALTIMESTAMP
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|,
name|TIMESTAMP_PATTERN
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^LOCALTIMESTAMP()^"
argument_list|,
literal|"No match found for function signature LOCALTIMESTAMP\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^LOCALTIMESTAMP(4000000000)^"
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^LOCALTIMESTAMP(9223372036854775807)^"
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIMESTAMP(1)"
argument_list|,
name|TIMESTAMP_PATTERN
argument_list|,
literal|"TIMESTAMP(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Check that timestamp is being generated in the right timezone by
comment|// generating a specific timestamp.
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(LOCALTIMESTAMP AS VARCHAR(30))"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|right
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testCurrentTimeFuncWithCurrentTime
parameter_list|()
block|{
name|testCurrentTimeFunc
argument_list|(
name|currentTimeString
argument_list|(
name|CURRENT_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentTimeFuncWithFixedTime
parameter_list|()
block|{
name|testCurrentTimeFunc
argument_list|(
name|fixedTimeString
argument_list|(
name|CURRENT_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCurrentTimeFunc
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_TIME
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIME"
argument_list|,
name|TIME_PATTERN
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^CURRENT_TIME()^"
argument_list|,
literal|"No match found for function signature CURRENT_TIME\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIME(1)"
argument_list|,
name|TIME_PATTERN
argument_list|,
literal|"TIME(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CURRENT_TIME AS VARCHAR(30))"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|.
name|substring
argument_list|(
literal|11
argument_list|)
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIME"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|.
name|substring
argument_list|(
literal|11
argument_list|)
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|right
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testCurrentTimestampFuncWithCurrentTime
parameter_list|()
block|{
name|testCurrentTimestampFunc
argument_list|(
name|currentTimeString
argument_list|(
name|CURRENT_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentTimestampFuncWithFixedTime
parameter_list|()
block|{
name|testCurrentTimestampFunc
argument_list|(
name|fixedTimeString
argument_list|(
name|CURRENT_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCurrentTimestampFunc
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_TIMESTAMP
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|,
name|TIMESTAMP_PATTERN
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^CURRENT_TIMESTAMP()^"
argument_list|,
literal|"No match found for function signature CURRENT_TIMESTAMP\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^CURRENT_TIMESTAMP(4000000000)^"
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIMESTAMP(1)"
argument_list|,
name|TIMESTAMP_PATTERN
argument_list|,
literal|"TIMESTAMP(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CURRENT_TIMESTAMP AS VARCHAR(30))"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pair
operator|.
name|left
operator|+
literal|"[0-9][0-9]:[0-9][0-9]"
argument_list|)
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|right
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a time string, in GMT, that will be valid for at least 2 minutes.    *    *<p>For example, at "2005-01-01 12:34:56 PST", returns "2005-01-01 20:".    * At "2005-01-01 12:34:59 PST", waits a minute, then returns "2005-01-01    * 21:".    *    * @param tz Time zone    * @return Time string    */
specifier|protected
specifier|static
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|currentTimeString
parameter_list|(
name|TimeZone
name|tz
parameter_list|)
block|{
specifier|final
name|Calendar
name|calendar
init|=
name|getCalendarNotTooNear
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
decl_stmt|;
specifier|final
name|Hook
operator|.
name|Closeable
name|closeable
init|=
parameter_list|()
lambda|->
block|{
block|}
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|toTimeString
argument_list|(
name|tz
argument_list|,
name|calendar
argument_list|)
argument_list|,
name|closeable
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|fixedTimeString
parameter_list|(
name|TimeZone
name|tz
parameter_list|)
block|{
specifier|final
name|Calendar
name|calendar
init|=
name|getFixedCalendar
argument_list|()
decl_stmt|;
specifier|final
name|long
name|timeInMillis
init|=
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|Holder
argument_list|<
name|Long
argument_list|>
argument_list|>
name|consumer
init|=
name|o
lambda|->
name|o
operator|.
name|set
argument_list|(
name|timeInMillis
argument_list|)
decl_stmt|;
specifier|final
name|Hook
operator|.
name|Closeable
name|closeable
init|=
name|Hook
operator|.
name|CURRENT_TIME
operator|.
name|addThread
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|toTimeString
argument_list|(
name|tz
argument_list|,
name|calendar
argument_list|)
argument_list|,
name|closeable
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|toTimeString
parameter_list|(
name|TimeZone
name|tz
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
name|SimpleDateFormat
name|sdf
init|=
name|getDateFormatter
argument_list|(
literal|"yyyy-MM-dd HH:"
argument_list|,
name|tz
argument_list|)
decl_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testCurrentDateFuncWithCurrentTime
parameter_list|()
block|{
name|testCurrentDateFunc
argument_list|(
name|currentTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentDateFuncWithFixedTime
parameter_list|()
block|{
name|testCurrentDateFunc
argument_list|(
name|fixedTimeString
argument_list|(
name|LOCAL_TZ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCurrentDateFunc
parameter_list|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Hook
operator|.
name|Closeable
argument_list|>
name|pair
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_DATE
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
comment|// A tester with a lenient conformance that allows parentheses.
specifier|final
name|SqlOperatorFixture
name|f1
init|=
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
name|DATE_PATTERN
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"(CURRENT_DATE - CURRENT_DATE) DAY"
argument_list|,
literal|"+0"
argument_list|,
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NULL"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NOT NULL"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"NOT (CURRENT_DATE IS NULL)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^CURRENT_DATE()^"
argument_list|,
literal|"No match found for function signature CURRENT_DATE\\(\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE() IS NULL"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NOT NULL"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkBoolean
argument_list|(
literal|"NOT (CURRENT_DATE() IS NULL)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATE()"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_TIMESTAMP()"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_TIME()"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Check the actual value.
specifier|final
name|String
name|dateString
init|=
name|pair
operator|.
name|left
decl_stmt|;
try|try
init|(
name|Hook
operator|.
name|Closeable
name|ignore
init|=
name|pair
operator|.
name|right
init|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CURRENT_DATE AS VARCHAR(30))"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CURRENT_DATE AS VARCHAR(30))"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CURRENT_DATE() AS VARCHAR(30))"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"VARCHAR(30) NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkScalar
argument_list|(
literal|"CURRENT_DATE()"
argument_list|,
name|dateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testLastDayFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LAST_DAY
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-02-10')"
argument_list|,
literal|"2019-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-06-10')"
argument_list|,
literal|"2019-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-07-10')"
argument_list|,
literal|"2019-07-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-09-10')"
argument_list|,
literal|"2019-09-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-12-10')"
argument_list|,
literal|"2019-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '9999-12-10')"
argument_list|,
literal|"9999-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// Edge tests
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '1900-01-01')"
argument_list|,
literal|"1900-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '1935-02-01')"
argument_list|,
literal|"1935-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '1965-09-01')"
argument_list|,
literal|"1965-09-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '1970-01-01')"
argument_list|,
literal|"1970-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-02-28')"
argument_list|,
literal|"2019-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-12-31')"
argument_list|,
literal|"2019-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-01-01')"
argument_list|,
literal|"2019-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2019-06-30')"
argument_list|,
literal|"2019-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2020-02-20')"
argument_list|,
literal|"2020-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '2020-02-29')"
argument_list|,
literal|"2020-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(DATE '9999-12-31')"
argument_list|,
literal|"9999-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"last_day(cast(null as date))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-02-10 02:10:12')"
argument_list|,
literal|"2019-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-06-10 06:10:16')"
argument_list|,
literal|"2019-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-07-10 07:10:17')"
argument_list|,
literal|"2019-07-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-09-10 09:10:19')"
argument_list|,
literal|"2019-09-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-12-10 12:10:22')"
argument_list|,
literal|"2019-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// Edge tests
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '1900-01-01 01:01:02')"
argument_list|,
literal|"1900-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '1935-02-01 02:01:03')"
argument_list|,
literal|"1935-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '1970-01-01 01:01:02')"
argument_list|,
literal|"1970-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-02-28 02:28:30')"
argument_list|,
literal|"2019-02-28"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-12-31 12:31:43')"
argument_list|,
literal|"2019-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-01-01 01:01:02')"
argument_list|,
literal|"2019-01-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2019-06-30 06:30:36')"
argument_list|,
literal|"2019-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2020-02-20 02:20:33')"
argument_list|,
literal|"2020-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '2020-02-29 02:29:31')"
argument_list|,
literal|"2020-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"last_day(TIMESTAMP '9999-12-31 12:31:43')"
argument_list|,
literal|"9999-12-31"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"last_day(cast(null as timestamp))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLpadFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LPAD
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select lpad('12345', 8, 'a')"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|,
literal|"aaa12345"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad('12345', 8)"
argument_list|,
literal|"   12345"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad('12345', 8, 'ab')"
argument_list|,
literal|"aba12345"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad('12345', 3, 'a')"
argument_list|,
literal|"123"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad('12345', -3, 'a')"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad('12345', -3)"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad('12345', 3, '')"
argument_list|,
literal|"Third argument (pad pattern) for LPAD/RPAD must not be empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad(x'aa', 4, x'bb')"
argument_list|,
literal|"bbbbbbaa"
argument_list|,
literal|"VARBINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad(x'aa', 4)"
argument_list|,
literal|"202020aa"
argument_list|,
literal|"VARBINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad(x'aaaaaa', 2)"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"lpad(x'aaaaaa', 2, x'bb')"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad(x'aa', -3, x'bb')"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad(x'aa', -3)"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"lpad(x'aa', 3, x'')"
argument_list|,
literal|"Third argument (pad pattern) for LPAD/RPAD must not be empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRpadFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RPAD
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select rpad('12345', 8, 'a')"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|,
literal|"12345aaa"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad('12345', 8)"
argument_list|,
literal|"12345   "
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad('12345', 8, 'ab')"
argument_list|,
literal|"12345aba"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad('12345', 3, 'a')"
argument_list|,
literal|"123"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad('12345', -3, 'a')"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad('12345', -3)"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad('12345', 3, '')"
argument_list|,
literal|"Third argument (pad pattern) for LPAD/RPAD must not be empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad(x'aa', 4, x'bb')"
argument_list|,
literal|"aabbbbbb"
argument_list|,
literal|"VARBINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad(x'aa', 4)"
argument_list|,
literal|"aa202020"
argument_list|,
literal|"VARBINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad(x'aaaaaa', 2)"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"rpad(x'aaaaaa', 2, x'bb')"
argument_list|,
literal|"aaaa"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad(x'aa', -3, x'bb')"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad(x'aa', -3)"
argument_list|,
literal|"Second argument for LPAD/RPAD must not be negative"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"rpad(x'aa', 3, x'')"
argument_list|,
literal|"Third argument (pad pattern) for LPAD/RPAD must not be empty"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStrposFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|STRPOS
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^strpos('abc', 'a')^"
argument_list|,
literal|"No match found for function signature STRPOS\\(<CHARACTER>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS('abc', 'a')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS('abcabc', 'bc')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS('abcabc', 'd')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS('abc', '')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS('', 'a')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRPOS(null, 'a')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRPOS('a', null)"
argument_list|)
expr_stmt|;
comment|// test for BINARY
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'2212', x'12')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'2122', x'12')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'1222', x'12')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'1111', x'22')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'2122', x'')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"STRPOS(x'', x'12')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRPOS(null, x'')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"STRPOS(x'', null)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStartsWithFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|STARTS_WITH
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with('12345', '123')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with('12345', '1243')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with(x'11', x'11')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with(x'112211', x'33')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^starts_with('aabbcc', x'aa')^"
argument_list|,
literal|"Cannot apply 'STARTS_WITH' to arguments of type "
operator|+
literal|"'STARTS_WITH\\(<CHAR\\(6\\)>,<BINARY\\(1\\)>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'STARTS_WITH\\(<STRING>,<STRING>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"starts_with(null, null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"starts_with('12345', null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"starts_with(null, '123')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with('', '123')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with('', '')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"starts_with(x'aa', null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"starts_with(null, x'aa')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with(x'1234', x'')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with(x'', x'123456')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"starts_with(x'', x'')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEndsWithFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ENDS_WITH
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with('12345', '345')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with('12345', '123')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with(x'11', x'11')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with(x'112211', x'33')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ends_with('aabbcc', x'aa')^"
argument_list|,
literal|"Cannot apply 'ENDS_WITH' to arguments of type "
operator|+
literal|"'ENDS_WITH\\(<CHAR\\(6\\)>,<BINARY\\(1\\)>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'ENDS_WITH\\(<STRING>,<STRING>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ends_with(null, null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ends_with('12345', null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ends_with(null, '123')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with('', '123')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with('', '')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ends_with(x'aa', null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ends_with(null, x'aa')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with(x'1234', x'')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with(x'', x'123456')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"ends_with(x'', x'')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the {@code SPLIT} operator. */
annotation|@
name|Test
name|void
name|testSplitFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SPLIT
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^split('hello')^"
argument_list|,
literal|"No match found for function signature SPLIT\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT('h,e,l,l,o')"
argument_list|,
literal|"[h, e, l, l, o]"
argument_list|,
literal|"CHAR(9) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT('h-e-l-l-o', '-')"
argument_list|,
literal|"[h, e, l, l, o]"
argument_list|,
literal|"CHAR(9) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT('hello', '-')"
argument_list|,
literal|"[hello]"
argument_list|,
literal|"CHAR(5) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT('')"
argument_list|,
literal|"[]"
argument_list|,
literal|"CHAR(0) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT('', '-')"
argument_list|,
literal|"[]"
argument_list|,
literal|"CHAR(0) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"SPLIT(null)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"SPLIT('hello', null)"
argument_list|)
expr_stmt|;
comment|// In ASCII, x'41' = 'A', x'42' = 'B', x'43' = 'C'
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT(x'414243', x'ff')"
argument_list|,
literal|"[ABC]"
argument_list|,
literal|"BINARY(3) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT(x'414243', x'41')"
argument_list|,
literal|"[, BC]"
argument_list|,
literal|"BINARY(3) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT(x'414243', x'42')"
argument_list|,
literal|"[A, C]"
argument_list|,
literal|"BINARY(3) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"SPLIT(x'414243', x'43')"
argument_list|,
literal|"[AB, ]"
argument_list|,
literal|"BINARY(3) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^SPLIT(x'aabbcc')^"
argument_list|,
literal|"Call to function 'SPLIT' with argument of type 'BINARY\\(3\\)' "
operator|+
literal|"requires extra delimiter argument"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the {@code SUBSTRING} operator. Many test cases that used to be    * have been moved to {@link SubFunChecker#assertSubFunReturns}, and are    * called for both {@code SUBSTRING} and {@code SUBSTR}. */
annotation|@
name|Test
name|void
name|testSubstringFunction
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkSubstringFunction
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|checkSubstringFunction
argument_list|(
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkSubstringFunction
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 1 for 2)"
argument_list|,
literal|"ab"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 1 for 2)"
argument_list|,
literal|"aabb"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|f
operator|.
name|conformance
argument_list|()
operator|.
name|semantics
argument_list|()
condition|)
block|{
case|case
name|BIG_QUERY
case|:
name|f
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 1 for -1)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 1 for -1)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|f
operator|.
name|checkFails
argument_list|(
literal|"substring('abc' from 1 for -1)"
argument_list|,
literal|"Substring error: negative substring length not allowed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"substring(x'aabbcc' from 1 for -1)"
argument_list|,
literal|"Substring error: negative substring length not allowed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Bug
operator|.
name|FRG296_FIXED
condition|)
block|{
comment|// substring regexp not supported yet
name|f
operator|.
name|checkString
argument_list|(
literal|"substring('foobar' from '%#\"o_b#\"%' for'#')"
argument_list|,
literal|"oob"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkNull
argument_list|(
literal|"substring(cast(null as varchar(1)),1,2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"substring(cast(null as varchar(1)) FROM 1 FOR 2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"substring('abc' FROM cast(null as integer) FOR 2)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"substring('abc' FROM cast(null as integer))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"substring('abc' FROM 2 FOR cast(null as integer))"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the non-standard SUBSTR function, that has syntax    * "SUBSTR(value, start [, length ])", as used in BigQuery. */
annotation|@
name|Test
name|void
name|testBigQuerySubstrFunction
parameter_list|()
block|{
name|substrChecker
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibraryOperators
operator|.
name|SUBSTR_BIG_QUERY
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the non-standard SUBSTR function, that has syntax    * "SUBSTR(value, start [, length ])", as used in Oracle. */
annotation|@
name|Test
name|void
name|testMysqlSubstrFunction
parameter_list|()
block|{
name|substrChecker
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibraryOperators
operator|.
name|SUBSTR_MYSQL
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the non-standard SUBSTR function, that has syntax    * "SUBSTR(value, start [, length ])", as used in Oracle. */
annotation|@
name|Test
name|void
name|testOracleSubstrFunction
parameter_list|()
block|{
name|substrChecker
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|,
name|SqlLibraryOperators
operator|.
name|SUBSTR_ORACLE
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the non-standard SUBSTR function, that has syntax    * "SUBSTR(value, start [, length ])", as used in PostgreSQL. */
annotation|@
name|Test
name|void
name|testPostgresqlSubstrFunction
parameter_list|()
block|{
name|substrChecker
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|,
name|SqlLibraryOperators
operator|.
name|SUBSTR_POSTGRESQL
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the standard {@code SUBSTRING} function in the mode that has    * BigQuery's non-standard semantics. */
annotation|@
name|Test
name|void
name|testBigQuerySubstringFunction
parameter_list|()
block|{
name|substringChecker
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests the standard {@code SUBSTRING} function in ISO standard    * semantics. */
annotation|@
name|Test
name|void
name|testStandardSubstringFunction
parameter_list|()
block|{
name|substringChecker
argument_list|(
name|SqlConformanceEnum
operator|.
name|STRICT_2003
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
name|SubFunChecker
name|substringChecker
parameter_list|(
name|SqlConformanceEnum
name|conformance
parameter_list|,
name|SqlLibrary
name|library
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
return|return
operator|new
name|SubFunChecker
argument_list|(
name|f
operator|.
name|withConnectionFactory
argument_list|(
name|cf
lambda|->
name|cf
operator|.
name|with
argument_list|(
name|ConnectionFactories
operator|.
name|add
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|conformance
argument_list|)
argument_list|)
argument_list|,
name|library
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|)
return|;
block|}
name|SubFunChecker
name|substrChecker
parameter_list|(
name|SqlLibrary
name|library
parameter_list|,
name|SqlFunction
name|function
parameter_list|)
block|{
return|return
operator|new
name|SubFunChecker
argument_list|(
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|library
argument_list|)
argument_list|,
name|library
argument_list|,
name|function
argument_list|)
return|;
block|}
comment|/** Tests various configurations of {@code SUBSTR} and {@code SUBSTRING}    * functions. */
specifier|static
class|class
name|SubFunChecker
block|{
specifier|final
name|SqlOperatorFixture
name|f
decl_stmt|;
specifier|final
name|SqlLibrary
name|library
decl_stmt|;
specifier|final
name|SqlFunction
name|function
decl_stmt|;
name|SubFunChecker
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|,
name|SqlLibrary
name|library
parameter_list|,
name|SqlFunction
name|function
parameter_list|)
block|{
name|this
operator|.
name|f
operator|=
name|f
expr_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|function
argument_list|)
expr_stmt|;
name|this
operator|.
name|library
operator|=
name|library
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
name|void
name|check
parameter_list|()
block|{
comment|// The following tests have been checked on Oracle 11g R2, PostgreSQL 9.6,
comment|// MySQL 5.6, Google BigQuery.
comment|//
comment|// PostgreSQL and MySQL have a standard SUBSTRING(x FROM s [FOR l])
comment|// operator, and its behavior is identical to their SUBSTRING(x, s [, l]).
comment|// Oracle and BigQuery do not have SUBSTRING.
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|2
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|3
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|library
condition|)
block|{
case|case
name|BIG_QUERY
case|:
case|case
name|ORACLE
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
break|break;
case|case
name|POSTGRESQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MYSQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|library
argument_list|)
throw|;
block|}
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|8
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|library
condition|)
block|{
case|case
name|POSTGRESQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
default|default:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// For negative start, BigQuery matches Oracle.
switch|switch
condition|(
name|library
condition|)
block|{
case|case
name|BIG_QUERY
case|:
case|case
name|MYSQL
case|:
case|case
name|ORACLE
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|1
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|3
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|4
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|5
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|6
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|7
argument_list|,
literal|"bc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abcde"
argument_list|,
operator|-
literal|3
argument_list|,
literal|2
argument_list|,
literal|"cd"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|3
argument_list|,
literal|3
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|3
argument_list|,
literal|8
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
break|break;
case|case
name|POSTGRESQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|4
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|5
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|6
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|2
argument_list|,
literal|7
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abcde"
argument_list|,
operator|-
literal|3
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|3
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|3
argument_list|,
literal|8
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|library
argument_list|)
throw|;
block|}
comment|// For negative start and start + length between 0 and actual-length,
comment|// confusion reigns.
switch|switch
condition|(
name|library
condition|)
block|{
case|case
name|BIG_QUERY
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|4
argument_list|,
literal|6
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MYSQL
case|:
case|case
name|ORACLE
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|4
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
case|case
name|POSTGRESQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|4
argument_list|,
literal|6
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|library
argument_list|)
throw|;
block|}
comment|// For very negative start, BigQuery differs from Oracle and PostgreSQL.
switch|switch
condition|(
name|library
condition|)
block|{
case|case
name|BIG_QUERY
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|4
argument_list|,
literal|3
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|5
argument_list|,
literal|1
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|10
argument_list|,
literal|2
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|500
argument_list|,
literal|1
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MYSQL
case|:
case|case
name|ORACLE
case|:
case|case
name|POSTGRESQL
case|:
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|4
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|5
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|10
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertReturns
argument_list|(
literal|"abc"
argument_list|,
operator|-
literal|500
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|library
argument_list|)
throw|;
block|}
block|}
name|void
name|assertReturns
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertSubFunReturns
argument_list|(
literal|false
argument_list|,
name|s
argument_list|,
name|start
argument_list|,
literal|null
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertSubFunReturns
argument_list|(
literal|true
argument_list|,
name|s
argument_list|,
name|start
argument_list|,
literal|null
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
name|void
name|assertReturns
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
annotation|@
name|Nullable
name|Integer
name|end
parameter_list|,
annotation|@
name|Nullable
name|String
name|expected
parameter_list|)
block|{
name|assertSubFunReturns
argument_list|(
literal|false
argument_list|,
name|s
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertSubFunReturns
argument_list|(
literal|true
argument_list|,
name|s
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
name|void
name|assertSubFunReturns
parameter_list|(
name|boolean
name|binary
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
annotation|@
name|Nullable
name|Integer
name|end
parameter_list|,
annotation|@
name|Nullable
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|String
name|v
init|=
name|binary
condition|?
literal|"x'"
operator|+
name|DOUBLER
operator|.
name|apply
argument_list|(
name|s
argument_list|)
operator|+
literal|"'"
else|:
literal|"'"
operator|+
name|s
operator|+
literal|"'"
decl_stmt|;
specifier|final
name|String
name|type
init|=
operator|(
name|binary
condition|?
literal|"VARBINARY"
else|:
literal|"VARCHAR"
operator|)
operator|+
literal|"("
operator|+
name|s
operator|.
name|length
argument_list|()
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|value
init|=
literal|"CAST("
operator|+
name|v
operator|+
literal|" AS "
operator|+
name|type
operator|+
literal|")"
decl_stmt|;
specifier|final
name|String
name|expression
decl_stmt|;
if|if
condition|(
name|function
operator|==
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
condition|)
block|{
name|expression
operator|=
literal|"substring("
operator|+
name|value
operator|+
literal|" FROM "
operator|+
name|start
operator|+
operator|(
name|end
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
literal|" FOR "
operator|+
name|end
operator|)
operator|)
operator|+
literal|")"
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
literal|"substr("
operator|+
name|value
operator|+
literal|", "
operator|+
name|start
operator|+
operator|(
name|end
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
literal|", "
operator|+
name|end
operator|)
operator|)
operator|+
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
name|expression
argument_list|,
literal|"Substring error: negative substring length not allowed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|binary
condition|)
block|{
name|expected
operator|=
name|DOUBLER
operator|.
name|apply
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkString
argument_list|(
name|expression
argument_list|,
name|expected
argument_list|,
name|type
operator|+
name|NON_NULLABLE_SUFFIX
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
name|void
name|testTrimFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
comment|// SQL:2003 6.29.11 Trimming a CHAR yields a VARCHAR
name|f
operator|.
name|checkString
argument_list|(
literal|"trim('a' from 'aAa')"
argument_list|,
literal|"A"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"trim(both 'a' from 'aAa')"
argument_list|,
literal|"A"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"trim(leading 'a' from 'aAa')"
argument_list|,
literal|"Aa"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"trim(trailing 'a' from 'aAa')"
argument_list|,
literal|"aA"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trim(cast(null as varchar(1)) from 'a')"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"trim('a' from cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
comment|// SQL:2003 6.29.9: trim string must have length=1. Failure occurs
comment|// at runtime.
comment|//
comment|// TODO: Change message to "Invalid argument\(s\) for
comment|// 'TRIM' function".
comment|// The message should come from a resource file, and should still
comment|// have the SQL error code 22027.
name|f
operator|.
name|checkFails
argument_list|(
literal|"trim('xy' from 'abcde')"
argument_list|,
literal|"Trim error: trim character must be exactly 1 character"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"trim('' from 'abcde')"
argument_list|,
literal|"Trim error: trim character must be exactly 1 character"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f1
init|=
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|f1
operator|.
name|checkString
argument_list|(
literal|"trim(leading 'eh' from 'hehe__hehe')"
argument_list|,
literal|"__hehe"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkString
argument_list|(
literal|"trim(trailing 'eh' from 'hehe__hehe')"
argument_list|,
literal|"hehe__"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|f1
operator|.
name|checkString
argument_list|(
literal|"trim('eh' from 'hehe__hehe')"
argument_list|,
literal|"__"
argument_list|,
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRtrimFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RTRIM
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^rtrim(' aaa')^"
argument_list|,
literal|"No match found for function signature RTRIM\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"rtrim(' aAa  ')"
argument_list|,
literal|" aAa"
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"rtrim(CAST(NULL AS VARCHAR(6)))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLtrimFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LTRIM
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^ltrim('  aa')^"
argument_list|,
literal|"No match found for function signature LTRIM\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"ltrim(' aAa  ')"
argument_list|,
literal|"aAa  "
argument_list|,
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ltrim(CAST(NULL AS VARCHAR(6)))"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreatestFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|GREATEST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^greatest('on', 'earth')^"
argument_list|,
literal|"No match found for function signature GREATEST\\(<CHARACTER>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"greatest('on', 'earth')"
argument_list|,
literal|"on   "
argument_list|,
literal|"CHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"greatest('show', 'on', 'earth')"
argument_list|,
literal|"show "
argument_list|,
literal|"CHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"greatest(12, CAST(NULL AS INTEGER), 3)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"greatest(false, true)"
argument_list|,
literal|true
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f12
init|=
name|f
operator|.
name|forOracle
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"greatest('on', 'earth')"
argument_list|,
literal|"on"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"greatest('show', 'on', 'earth')"
argument_list|,
literal|"show"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LEAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^least('on', 'earth')^"
argument_list|,
literal|"No match found for function signature LEAST\\(<CHARACTER>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkString
argument_list|(
literal|"least('on', 'earth')"
argument_list|,
literal|"earth"
argument_list|,
literal|"CHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"least('show', 'on', 'earth')"
argument_list|,
literal|"earth"
argument_list|,
literal|"CHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"least(12, CAST(NULL AS INTEGER), 3)"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"least(false, true)"
argument_list|,
literal|false
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f12
init|=
name|f0
operator|.
name|forOracle
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"least('on', 'earth')"
argument_list|,
literal|"earth"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"least('show', 'on', 'earth')"
argument_list|,
literal|"earth"
argument_list|,
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|f0
operator|.
name|forEachLibrary
argument_list|(
name|list
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNvlFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|NVL
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"nvl(1, 2)"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^nvl(1, true)^"
argument_list|,
literal|"Parameters must be of the same type"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"nvl(true, false)"
argument_list|,
literal|true
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"nvl(false, true)"
argument_list|,
literal|false
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', 'de')"
argument_list|,
literal|"abc"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', 'defg')"
argument_list|,
literal|"abc "
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', CAST(NULL AS VARCHAR(20)))"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(20)), 'abc')"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(6)), cast(NULL AS VARCHAR(4)))"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f12
init|=
name|f
operator|.
name|forOracle
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', 'de')"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', 'defg')"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"nvl('abc', CAST(NULL AS VARCHAR(20)))"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkString
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(20)), 'abc')"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
name|f12
operator|.
name|checkNull
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(6)), cast(NULL AS VARCHAR(4)))"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code IFNULL}, which is a synonym for {@code NVL}, and is related to    * {@code COALESCE} but requires precisely two arguments. */
annotation|@
name|Test
name|void
name|testIfnullFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|IFNULL
argument_list|,
name|VM_EXPAND
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ifnull('a','b')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"ifnull(null,'b')"
argument_list|,
literal|"b"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ifnull(4,3)"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ifnull(null, 4)"
argument_list|,
literal|4
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"1 + ifnull('a', 1) + 2"
argument_list|,
literal|"Cannot infer return type for IFNULL; operand types: \\[CHAR\\(1\\), INTEGER\\]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"1 + ifnull(1, null) + 2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ifnull(1,2,3)^"
argument_list|,
literal|"Invalid number of arguments to function 'IFNULL'. Was expecting 2 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ifnull(1)^"
argument_list|,
literal|"Invalid number of arguments to function 'IFNULL'. Was expecting 2 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDecodeFunc
parameter_list|()
block|{
name|checkDecodeFunc
argument_list|(
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkDecodeFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DECODE
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(0, 0, 'a', 1, 'b', 2, 'c')"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(1, 0, 'a', 1, 'b', 2, 'c')"
argument_list|,
literal|"b"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// if there are duplicates, take the first match
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(1, 0, 'a', 1, 'b', 1, 'z', 2, 'c')"
argument_list|,
literal|"b"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// if there's no match, and no "else", return null
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(3, 0, 'a', 1, 'b', 2, 'c')"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// if there's no match, return the "else" value
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(3, 0, 'a', 1, 'b', 2, 'c', 'd')"
argument_list|,
literal|"d"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(1, 0, 'a', 1, 'b', 2, 'c', 'd')"
argument_list|,
literal|"b"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// nulls match
name|f
operator|.
name|checkScalar
argument_list|(
literal|"decode(cast(null as integer), 0, 'a',\n"
operator|+
literal|" cast(null as integer), 'b', 2, 'c', 'd')"
argument_list|,
literal|"b"
argument_list|,
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWindow
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select sum(1) over (order by x)\n"
operator|+
literal|"from (select 1 as x, 2 as y\n"
operator|+
literal|"  from (values (true)))"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testElementFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ELEMENT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkString
argument_list|(
literal|"element(multiset['abc'])"
argument_list|,
literal|"abc"
argument_list|,
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"element(multiset[cast(null as integer)])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCardinalityFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CARDINALITY
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(multiset[cast(null as integer),2])"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// applied to array
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(array['foo', 'bar'])"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// applied to map
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(map['foo', 1, 'bar', 2])"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMemberOfOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MEMBER_OF
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1 member of multiset[1]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"'2' member of multiset['1']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as double) member of"
operator|+
literal|" multiset[cast(null as double)]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as double) member of multiset[1.1]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"1.1 member of multiset[cast(null as double)]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetUnionOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTISET_UNION_DISTINCT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1,2] submultiset of "
operator|+
literal|"(multiset[2] multiset union multiset[1])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8])"
argument_list|,
literal|"7"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8])"
argument_list|,
literal|"7"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
argument_list|,
literal|"5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
argument_list|,
literal|"5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
operator|+
literal|" submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
operator|+
literal|" submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as double)] "
operator|+
literal|"multiset union multiset[cast(null as double)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"DOUBLE MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as boolean)] "
operator|+
literal|"multiset union multiset[cast(null as boolean)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"BOOLEAN MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetUnionAllOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTISET_UNION
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union all multiset[1, 4, 5, 7, 8])"
argument_list|,
literal|"10"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union all multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union all multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 1, 2, 2, 3, 4, 4, 5, 7, 8]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union all multiset['c', 'd', 'e'])"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union all multiset['c', 'd', 'e']) "
operator|+
literal|"submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e']) "
operator|+
literal|"submultiset of multiset['a', 'b', 'c', 'd', 'e', 'c']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as double)] "
operator|+
literal|"multiset union all multiset[cast(null as double)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"DOUBLE MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as boolean)] "
operator|+
literal|"multiset union all multiset[cast(null as boolean)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"BOOLEAN MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubMultisetOfOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBMULTISET_OF
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[2] submultiset of multiset[1]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] submultiset of multiset[1]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] submultiset of multiset[1]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] submultiset of multiset[1, 2]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] submultiset of multiset[1, 2]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] submultiset of "
operator|+
literal|"multiset['c', 'd', 's', 'a']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'd'] submultiset of "
operator|+
literal|"multiset['c', 's', 'a', 'w', 'd']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['q', 'a'] submultiset of multiset['a', 'q']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotSubMultisetOfOperator
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_SUBMULTISET_OF
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[2] not submultiset of multiset[1]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] not submultiset of multiset[1]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] not submultiset of multiset[1]"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] not submultiset of multiset[1, 2]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] not submultiset of multiset[1, 2]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] not submultiset of "
operator|+
literal|"multiset['c', 'd', 's', 'a']"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'd'] not submultiset of "
operator|+
literal|"multiset['c', 's', 'a', 'w', 'd']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['q', 'a'] not submultiset of "
operator|+
literal|"multiset['a', 'q']"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COLLECT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"collect(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"collect(1)"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"collect(1.2)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"collect(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^collect()^"
argument_list|,
literal|"Invalid number of arguments to function 'COLLECT'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^collect(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'COLLECT'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"collect(x)"
argument_list|,
name|values
argument_list|,
name|isSet
argument_list|(
literal|"[0, 2, 2]"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"collect(x) within group(order by x desc)"
argument_list|,
name|values
argument_list|,
name|isSet
argument_list|(
literal|"[2, 2, 0]"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"collect(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"collect(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"collect(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testListAggFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LISTAGG
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"listagg(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"listagg(12)"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^listagg(12)^"
argument_list|,
literal|"Cannot apply 'LISTAGG' to arguments of type .*'\n.*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"listagg(cast(12 as double))"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^listagg(cast(12 as double))^"
argument_list|,
literal|"Cannot apply 'LISTAGG' to arguments of type .*'\n.*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^listagg()^"
argument_list|,
literal|"Invalid number of arguments to function 'LISTAGG'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^listagg('1', '2', '3')^"
argument_list|,
literal|"Invalid number of arguments to function 'LISTAGG'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"listagg('test')"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"listagg('test', ', ')"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"'hello'"
block|,
literal|"CAST(null AS CHAR)"
block|,
literal|"'world'"
block|,
literal|"'!'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"listagg(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|"hello,world,!"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"0"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"listagg(cast(x as CHAR))"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|"0,1,2,3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringAggFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkStringAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkStringAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkStringAggFuncFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkStringAggFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"null"
block|,
literal|"'yz'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"string_agg(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x,yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"string_agg(x,':')"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x:yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"string_agg(x,':' order by x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x:yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"string_agg(x order by char_length(x) desc)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"yz,x"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^string_agg(x respect nulls order by x desc)^"
argument_list|,
name|values
argument_list|,
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'STRING_AGG'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^string_agg(x order by x desc)^ respect nulls"
argument_list|,
name|values
argument_list|,
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'STRING_AGG'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkStringAggFuncFails
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"'y'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^string_agg(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature STRING_AGG\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^string_agg(x, ',')^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature STRING_AGG\\(<CHARACTER>, "
operator|+
literal|"<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^string_agg(x, ',' order by x desc)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature STRING_AGG\\(<CHARACTER>, "
operator|+
literal|"<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGroupConcatFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkGroupConcatFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkGroupConcatFuncFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkGroupConcatFuncFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkGroupConcatFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"null"
block|,
literal|"'yz'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"group_concat(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x,yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"group_concat(x,':')"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x:yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"group_concat(x,':' order by x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x:yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"group_concat(x order by x separator '|')"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"x|yz"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"group_concat(x order by char_length(x) desc)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"yz,x"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^group_concat(x respect nulls order by x desc)^"
argument_list|,
name|values
argument_list|,
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'GROUP_CONCAT'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^group_concat(x order by x desc)^ respect nulls"
argument_list|,
name|values
argument_list|,
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'GROUP_CONCAT'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkGroupConcatFuncFails
parameter_list|(
name|SqlOperatorFixture
name|t
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"'y'"
block|}
decl_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^group_concat(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature GROUP_CONCAT\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^group_concat(x, ',')^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature GROUP_CONCAT\\(<CHARACTER>, "
operator|+
literal|"<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^group_concat(x, ',' order by x desc)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature GROUP_CONCAT\\(<CHARACTER>, "
operator|+
literal|"<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayAggFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkArrayAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkArrayAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkArrayAggFuncFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkArrayAggFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_CONCAT_AGG
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"null"
block|,
literal|"'yz'"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"array_agg(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[x, yz]"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"array_agg(x ignore nulls)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[x, yz]"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"array_agg(x respect nulls)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[x, yz]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"Invalid number of arguments "
operator|+
literal|"to function 'ARRAY_AGG'. Was expecting 1 arguments"
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^array_agg(x,':')^"
argument_list|,
name|values
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^array_agg(x,':' order by x)^"
argument_list|,
name|values
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"array_agg(x order by char_length(x) desc)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"[yz, x]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkArrayAggFuncFails
parameter_list|(
name|SqlOperatorFixture
name|t
parameter_list|)
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_CONCAT_AGG
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"'y'"
block|}
decl_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"No match found for function signature "
operator|+
literal|"ARRAY_AGG\\(<CHARACTER>\\)"
decl_stmt|;
specifier|final
name|String
name|expectedError2
init|=
literal|"No match found for function signature "
operator|+
literal|"ARRAY_AGG\\(<CHARACTER>,<CHARACTER>\\)"
decl_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_agg(x)^"
argument_list|,
name|values
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_agg(x, ',')^"
argument_list|,
name|values
argument_list|,
name|expectedError2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_agg(x, ',' order by x desc)^"
argument_list|,
name|values
argument_list|,
name|expectedError2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayConcatAggFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|checkArrayConcatAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
name|checkArrayConcatAggFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
name|checkArrayConcatAggFuncFails
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkArrayConcatAggFunc
parameter_list|(
name|SqlOperatorFixture
name|t
parameter_list|)
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_CONCAT_AGG
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkFails
argument_list|(
literal|"array_concat_agg(^*^)"
argument_list|,
literal|"(?s)Encountered \"\\*\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggType
argument_list|(
literal|"array_concat_agg(ARRAY[1,2,3])"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"Cannot apply 'ARRAY_CONCAT_AGG' to arguments "
operator|+
literal|"of type 'ARRAY_CONCAT_AGG\\(<INTEGER MULTISET>\\)'. Supported "
operator|+
literal|"form\\(s\\): 'ARRAY_CONCAT_AGG\\(<ARRAY>\\)'"
decl_stmt|;
name|t
operator|.
name|checkFails
argument_list|(
literal|"^array_concat_agg(multiset[1,2])^"
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedError1
init|=
literal|"Cannot apply 'ARRAY_CONCAT_AGG' to "
operator|+
literal|"arguments of type 'ARRAY_CONCAT_AGG\\(<INTEGER>\\)'\\. Supported "
operator|+
literal|"form\\(s\\): 'ARRAY_CONCAT_AGG\\(<ARRAY>\\)'"
decl_stmt|;
name|t
operator|.
name|checkFails
argument_list|(
literal|"^array_concat_agg(12)^"
argument_list|,
name|expectedError1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"ARRAY[0]"
block|,
literal|"ARRAY[1]"
block|,
literal|"ARRAY[2]"
block|,
literal|"ARRAY[3]"
block|}
decl_stmt|;
name|t
operator|.
name|checkAgg
argument_list|(
literal|"array_concat_agg(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|"[0, 1, 2, 3]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"ARRAY[0,1]"
block|,
literal|"ARRAY[1, 2]"
block|}
decl_stmt|;
name|t
operator|.
name|checkAgg
argument_list|(
literal|"array_concat_agg(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|"[0, 1, 1, 2]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkArrayConcatAggFuncFails
parameter_list|(
name|SqlOperatorFixture
name|t
parameter_list|)
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|ARRAY_CONCAT_AGG
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"'x'"
block|,
literal|"'y'"
block|}
decl_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"No match found for function signature "
operator|+
literal|"ARRAY_CONCAT_AGG\\(<CHARACTER>\\)"
decl_stmt|;
specifier|final
name|String
name|expectedError2
init|=
literal|"No match found for function signature "
operator|+
literal|"ARRAY_CONCAT_AGG\\(<CHARACTER>,<CHARACTER>\\)"
decl_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_concat_agg(x)^"
argument_list|,
name|values
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_concat_agg(x, ',')^"
argument_list|,
name|values
argument_list|,
name|expectedError2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkAggFails
argument_list|(
literal|"^array_concat_agg(x, ',' order by x desc)^"
argument_list|,
name|values
argument_list|,
name|expectedError2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFusionFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|FUSION
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"fusion(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"fusion(MULTISET[1,2,3])"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^fusion(12)^"
argument_list|,
literal|"Cannot apply 'FUSION' to arguments of type .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"MULTISET[0]"
block|,
literal|"MULTISET[1]"
block|,
literal|"MULTISET[2]"
block|,
literal|"MULTISET[3]"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"fusion(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|"[0, 1, 2, 3]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"MULTISET[0,1]"
block|,
literal|"MULTISET[1, 2]"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"fusion(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|"[0, 1, 1, 2]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectionFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|INTERSECTION
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"intersection(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"intersection(MULTISET[1,2,3])"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^intersection(12)^"
argument_list|,
literal|"Cannot apply 'INTERSECTION' to arguments of type .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"MULTISET[0]"
block|,
literal|"MULTISET[1]"
block|,
literal|"MULTISET[2]"
block|,
literal|"MULTISET[3]"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|"[]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"MULTISET[0, 1]"
block|,
literal|"MULTISET[1, 2]"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|"[1]"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values3
init|=
block|{
literal|"MULTISET[0, 1, 1]"
block|,
literal|"MULTISET[0, 1, 2]"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values3
argument_list|,
name|isSingle
argument_list|(
literal|"[0, 1, 1]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModeFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MODE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"mode(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^mode()^"
argument_list|,
literal|"Invalid number of arguments to function 'MODE'. "
operator|+
literal|"Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^mode(1,2)^"
argument_list|,
literal|"Invalid number of arguments to function 'MODE'. "
operator|+
literal|"Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"mode(^null^)"
argument_list|,
literal|"Illegal use of 'NULL'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"mode('name')"
argument_list|,
literal|"CHAR(4)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"mode(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"mode(1.2)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"mode(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"mode(cast(null as varchar(2)))"
argument_list|,
literal|"VARCHAR(2)"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"0"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values3
init|=
block|{}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(x)"
argument_list|,
name|values3
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"mode(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testYear
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|YEAR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"year(date '2008-1-23')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"year(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQuarter
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|QUARTER
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-1-23')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-2-23')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-3-23')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-4-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-5-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-6-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-7-23')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-8-23')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-9-23')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-10-23')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-11-23')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"quarter(date '2008-12-23')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"quarter(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMonth
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MONTH
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"month(date '2008-1-23')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"month(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testWeek
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|WEEK
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"week(date '2008-1-23')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"week(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDayOfYear
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DAYOFYEAR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"dayofyear(date '2008-01-23')"
argument_list|,
literal|"23"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"dayofyear(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDayOfMonth
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DAYOFMONTH
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"dayofmonth(date '2008-1-23')"
argument_list|,
literal|"23"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"dayofmonth(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDayOfWeek
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DAYOFWEEK
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"dayofweek(date '2008-1-23')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"dayofweek(cast(null as date))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testHour
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|HOUR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"hour(timestamp '2008-1-23 12:34:56')"
argument_list|,
literal|"12"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"hour(cast(null as timestamp))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinute
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUTE
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"minute(timestamp '2008-1-23 12:34:56')"
argument_list|,
literal|"34"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"minute(cast(null as timestamp))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSecond
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SECOND
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"second(timestamp '2008-1-23 12:34:56')"
argument_list|,
literal|"56"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"second(cast(null as timestamp))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractIntervalYearMonth
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
comment|// Not supported, fails in type validation because the extract
comment|// unit is not YearMonth interval type.
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(epoch from interval '4-2' year to month)"
argument_list|,
comment|// number of seconds elapsed since timestamp
comment|// '1970-01-01 00:00:00' + input interval
literal|"131328000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from "
operator|+
literal|"interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond "
operator|+
literal|"from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from "
operator|+
literal|"interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|// Postgres doesn't support DOW, ISODOW, DOY and WEEK on INTERVAL YEAR MONTH type.
comment|// SQL standard doesn't have extract units for DOW, ISODOW, DOY and WEEK.
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(doy from interval '4-2' year to month)"
argument_list|,
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(dow from interval '4-2' year to month)^"
argument_list|,
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(week from interval '4-2' year to month)^"
argument_list|,
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(isodow from interval '4-2' year to month)^"
argument_list|,
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from interval '4-2' year to month)"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(quarter from interval '4-2' year to month)"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from interval '4-2' year to month)"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(decade from "
operator|+
literal|"interval '426-3' year(3) to month)"
argument_list|,
literal|"42"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from "
operator|+
literal|"interval '426-3' year(3) to month)"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from "
operator|+
literal|"interval '2005-3' year(4) to month)"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractIntervalDayTime
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
comment|// Not implemented in operator test
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(epoch from "
operator|+
literal|"interval '2 3:4:5.678' day to second)"
argument_list|,
comment|// number of seconds elapsed since timestamp
comment|// '1970-01-01 00:00:00' + input interval
literal|"183845.678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from "
operator|+
literal|"interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from "
operator|+
literal|"interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from "
operator|+
literal|"interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
comment|// Postgres doesn't support DOW, ISODOW, DOY and WEEK on INTERVAL DAY TIME type.
comment|// SQL standard doesn't have extract units for DOW, ISODOW, DOY and WEEK.
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(doy from interval '2 3:4:5.678' day to second)"
argument_list|,
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(dow from interval '2 3:4:5.678' day to second)"
argument_list|,
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(week from interval '2 3:4:5.678' day to second)"
argument_list|,
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(isodow from interval '2 3:4:5.678' day to second)"
argument_list|,
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(month from interval '2 3:4:5.678' day to second)^"
argument_list|,
literal|"(?s)Cannot apply 'EXTRACT' to arguments of type 'EXTRACT\\(<INTERVAL "
operator|+
literal|"MONTH> FROM<INTERVAL DAY TO SECOND>\\)'\\. Supported "
operator|+
literal|"form\\(s\\):.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(quarter from interval '2 3:4:5.678' day to second)^"
argument_list|,
literal|"(?s)Cannot apply 'EXTRACT' to arguments of type 'EXTRACT\\(<INTERVAL "
operator|+
literal|"QUARTER> FROM<INTERVAL DAY TO SECOND>\\)'\\. Supported "
operator|+
literal|"form\\(s\\):.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(year from interval '2 3:4:5.678' day to second)^"
argument_list|,
literal|"(?s)Cannot apply 'EXTRACT' to arguments of type 'EXTRACT\\(<INTERVAL "
operator|+
literal|"YEAR> FROM<INTERVAL DAY TO SECOND>\\)'\\. Supported "
operator|+
literal|"form\\(s\\):.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(isoyear from interval '2 3:4:5.678' day to second)^"
argument_list|,
literal|"(?s)Cannot apply 'EXTRACT' to arguments of type 'EXTRACT\\(<INTERVAL "
operator|+
literal|"ISOYEAR> FROM<INTERVAL DAY TO SECOND>\\)'\\. Supported "
operator|+
literal|"form\\(s\\):.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^extract(century from interval '2 3:4:5.678' day to second)^"
argument_list|,
literal|"(?s)Cannot apply 'EXTRACT' to arguments of type 'EXTRACT\\(<INTERVAL "
operator|+
literal|"CENTURY> FROM<INTERVAL DAY TO SECOND>\\)'\\. Supported "
operator|+
literal|"form\\(s\\):.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractDate
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(^a^ from date '2008-2-23')"
argument_list|,
literal|"'A' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(epoch from date '2008-2-23')"
argument_list|,
literal|"1203724800"
argument_list|,
comment|// number of seconds elapsed since timestamp
comment|// '1970-01-01 00:00:00' for given date
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from date '9999-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from date '0001-1-1')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from date '2008-2-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from date '2008-2-23')"
argument_list|,
literal|"23"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from date '2008-2-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(quarter from date '2008-4-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from date '2008-2-23')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(isoyear from date '2008-2-23')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(doy from date '2008-2-23')"
argument_list|,
literal|"54"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dayofyear from date '2008-2-23')"
argument_list|,
literal|"54"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dow from date '2008-2-23')"
argument_list|,
literal|"7"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dow from date '2008-2-24')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dayofweek from date '2008-2-23')"
argument_list|,
literal|"7"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(isodow from date '2008-2-23')"
argument_list|,
literal|"6"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(isodow from date '2008-2-24')"
argument_list|,
literal|"7"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from date '2008-2-23')"
argument_list|,
literal|"8"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from timestamp '2008-2-23 01:23:45')"
argument_list|,
literal|"8"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from cast(null as date))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(decade from date '2008-2-23')"
argument_list|,
literal|"200"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from date '2008-2-23')"
argument_list|,
literal|"21"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from date '2001-01-01')"
argument_list|,
literal|"21"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from date '2000-12-31')"
argument_list|,
literal|"20"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from date '1852-06-07')"
argument_list|,
literal|"19"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from date '0001-02-01')"
argument_list|,
literal|"1"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from date '2000-2-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from date '1969-2-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from date '2000-12-31')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from date '2001-01-01')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractTimestamp
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(^a^ from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"'A' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(epoch from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"1203770096"
argument_list|,
comment|// number of seconds elapsed since timestamp
comment|// '1970-01-01 00:00:00' for given date
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"56"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"56000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"56000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"56000000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"34"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"12"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"23"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(quarter from timestamp '2008-7-23 12:34:56')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(isoyear from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(doy from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"54"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dow from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"7"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"8"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(decade from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"200"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"21"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from timestamp '2001-01-01 12:34:56')"
argument_list|,
literal|"21"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from timestamp '2000-12-31 12:34:56')"
argument_list|,
literal|"20"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from timestamp '2000-2-23 12:34:56')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"extract(^a^ from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"'A' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from interval '23456 3:4:5.678' day(5) to second)"
argument_list|,
literal|"23456"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"3"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
comment|// TODO: Seconds should include precision
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from"
operator|+
literal|" interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from"
operator|+
literal|" interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from"
operator|+
literal|" interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(month from cast(null as interval year))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractFuncFromDateTime
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXTRACT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from date '2008-2-23')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(isoyear from date '2008-2-23')"
argument_list|,
literal|"2008"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from date '2008-2-23')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"34"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from time '12:23:34')"
argument_list|,
literal|"23"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(month from cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(month from cast(null as date))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(second from cast(null as time))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(millisecond from cast(null as time))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(microsecond from cast(null as time))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"extract(nanosecond from cast(null as time))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExtractWithDatesBeforeUnixEpoch
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from"
operator|+
literal|" TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17357"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from TIMESTAMP '1970-01-01 00:00:00')"
argument_list|,
literal|"1970"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(year from TIMESTAMP '1969-12-31 10:13:17')"
argument_list|,
literal|"1969"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(quarter from TIMESTAMP '1969-12-31 08:13:17')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(quarter from TIMESTAMP '1969-5-31 21:13:17')"
argument_list|,
literal|"2"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(month from TIMESTAMP '1969-12-31 00:13:17')"
argument_list|,
literal|"12"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(day from TIMESTAMP '1969-12-31 12:13:17')"
argument_list|,
literal|"31"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from TIMESTAMP '1969-2-23 01:23:45')"
argument_list|,
literal|"8"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(doy from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"365"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(dow from TIMESTAMP '1969-12-31 01:13:17.357')"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(decade from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"196"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"20"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(hour from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"21"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(minute from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"13"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(second from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from"
operator|+
literal|" TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17357"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from"
operator|+
literal|" TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17357000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArrayValueConstructor
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ARRAY_VALUE_CONSTRUCTOR
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"Array['foo', 'bar']"
argument_list|,
literal|"[foo, bar]"
argument_list|,
literal|"CHAR(3) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
comment|// empty array is illegal per SQL spec. presumably because one can't
comment|// infer type
name|f
operator|.
name|checkFails
argument_list|(
literal|"^Array[]^"
argument_list|,
literal|"Require at least 1 argument"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4999">[CALCITE-4999]    * ARRAY, MULTISET functions should return an collection of scalars    * if a sub-query returns 1 column</a>.    */
annotation|@
name|Test
name|void
name|testArrayQueryConstructor
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ARRAY_QUERY
argument_list|,
name|SqlOperatorFixture
operator|.
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"array(select 1)"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select array(select ROW(1,2))"
argument_list|,
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER NOT NULL EXPR$1) NOT NULL ARRAY NOT NULL"
argument_list|,
literal|"[{1, 2}]"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4999">[CALCITE-4999]    * ARRAY, MULTISET functions should return an collection of scalars    * if a sub-query returns 1 column</a>.    */
annotation|@
name|Test
name|void
name|testMultisetQueryConstructor
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTISET_QUERY
argument_list|,
name|SqlOperatorFixture
operator|.
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"multiset(select 1)"
argument_list|,
literal|"[1]"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select multiset(select ROW(1,2))"
argument_list|,
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER NOT NULL EXPR$1) NOT NULL MULTISET NOT NULL"
argument_list|,
literal|"[{1, 2}]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testItemOp
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ITEM
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][1]"
argument_list|,
literal|"foo"
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][0]"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][2]"
argument_list|,
literal|"bar"
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][3]"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ARRAY ['foo', 'bar'][1 + CAST(NULL AS INTEGER)]"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ARRAY ['foo', 'bar']['baz']^"
argument_list|,
literal|"Cannot apply 'ITEM' to arguments of type 'ITEM\\(<CHAR\\(3\\) ARRAY>, "
operator|+
literal|"<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\):<ARRAY>\\[<INTEGER>\\]\n"
operator|+
literal|"<MAP>\\[<ANY>\\]\n"
operator|+
literal|"<ROW>\\[<CHARACTER>\\|<INTEGER>\\]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Array of INTEGER NOT NULL is interesting because we might be tempted
comment|// to represent the result as Java "int".
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY [2, 4, 6][2]"
argument_list|,
literal|"4"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY [2, 4, 6][4]"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// Map item
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"map['foo', 3, 'bar', 7]['bar']"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"map['foo', CAST(NULL AS INTEGER), 'bar', 7]"
operator|+
literal|"['bar']"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"map['foo', CAST(NULL AS INTEGER), 'bar', 7]['baz']"
argument_list|,
literal|"INTEGER"
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkColumnType
argument_list|(
literal|"select cast(null as any)['x'] from (values(1))"
argument_list|,
literal|"ANY"
argument_list|)
expr_stmt|;
comment|// Row item
specifier|final
name|String
name|intStructQuery
init|=
literal|"select \"T\".\"X\"[1] "
operator|+
literal|"from (VALUES (ROW(ROW(3, 7), ROW(4, 8)))) as T(x, y)"
decl_stmt|;
name|f
operator|.
name|check
argument_list|(
name|intStructQuery
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkColumnType
argument_list|(
name|intStructQuery
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select \"T\".\"X\"[1] "
operator|+
literal|"from (VALUES (ROW(ROW(3, CAST(NULL AS INTEGER)), ROW(4, 8)))) as T(x, y)"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"select \"T\".\"X\"[2] "
operator|+
literal|"from (VALUES (ROW(ROW(3, CAST(NULL AS INTEGER)), ROW(4, 8)))) as T(x, y)"
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"select \"T\".\"X\"[1 + CAST(NULL AS INTEGER)] "
operator|+
literal|"from (VALUES (ROW(ROW(3, CAST(NULL AS INTEGER)), ROW(4, 8)))) as T(x, y)"
argument_list|,
literal|"Cannot infer type of field at position null within ROW type: "
operator|+
literal|"RecordType\\(INTEGER EXPR\\$0, INTEGER EXPR\\$1\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMapValueConstructor
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MAP_VALUE_CONSTRUCTOR
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^Map[]^"
argument_list|,
literal|"Map requires at least 2 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^Map[1, 'x', 2]^"
argument_list|,
literal|"Map requires an even number of arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^map[1, 1, 2, 'x']^"
argument_list|,
literal|"Parameters must be of the same type"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"map['washington', 1, 'obama', 44]"
argument_list|,
literal|"{washington=1, obama=44}"
argument_list|,
literal|"(CHAR(10) NOT NULL, INTEGER NOT NULL) MAP NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f1
init|=
name|f
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|f1
operator|.
name|checkScalar
argument_list|(
literal|"map['washington', 1, 'obama', 44]"
argument_list|,
literal|"{washington=1, obama=44}"
argument_list|,
literal|"(VARCHAR(10) NOT NULL, INTEGER NOT NULL) MAP NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCeilFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CEIL
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"ceil(10.1e0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"ceil(cast(-11.2e0 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
operator|-
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ceil(100)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ceil(1.3)"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"ceil(-1.7)"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceiling(cast(null as decimal(2,0)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceiling(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCeilFuncInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(interval '3:4:5' hour to second)"
argument_list|,
literal|"+4:00:00.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(interval '-6.3' second)"
argument_list|,
literal|"-6.000000"
argument_list|,
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(interval '5-1' year to month)"
argument_list|,
literal|"+6-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(interval '-5-1' year to month)"
argument_list|,
literal|"-5-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceil(cast(null as interval year))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFloorFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|FLOOR
argument_list|,
name|VM_FENNEL
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"floor(2.5e0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
name|isExactly
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarApprox
argument_list|(
literal|"floor(cast(-1.2e0 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
name|isExactly
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"floor(100)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"floor(1.7)"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalarExact
argument_list|(
literal|"floor(-1.7)"
argument_list|,
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as decimal(2,0)))"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as real))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFloorFuncDateTime
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^floor('12:34:56')^"
argument_list|,
literal|"Cannot apply 'FLOOR' to arguments of type "
operator|+
literal|"'FLOOR\\(<CHAR\\(8\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'FLOOR\\(<NUMERIC>\\)'\n"
operator|+
literal|"'FLOOR\\(<DATETIME_INTERVAL>\\)'\n"
operator|+
literal|"'FLOOR\\(<DATE> TO<TIME_UNIT>\\)'\n"
operator|+
literal|"'FLOOR\\(<TIME> TO<TIME_UNIT>\\)'\n"
operator|+
literal|"'FLOOR\\(<TIMESTAMP> TO<TIME_UNIT>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"floor('12:34:56')"
argument_list|,
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^floor(time '12:34:56')^"
argument_list|,
literal|"(?s)Cannot apply 'FLOOR' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^floor(123.45 to minute)^"
argument_list|,
literal|"(?s)Cannot apply 'FLOOR' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^floor('abcde' to minute)^"
argument_list|,
literal|"(?s)Cannot apply 'FLOOR' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(time '12:34:56' to minute)"
argument_list|,
literal|"12:34:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56.78' to second)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56.78' to millisecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56.78' to microsecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56.78' to nanosecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56' to minute)"
argument_list|,
literal|"2015-02-19 12:34:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56' to year)"
argument_list|,
literal|"2015-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(date '2015-02-19' to year)"
argument_list|,
literal|"2015-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2015-02-19 12:34:56' to month)"
argument_list|,
literal|"2015-02-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(date '2015-02-19' to month)"
argument_list|,
literal|"2015-02-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as date) to month)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCeilFuncDateTime
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^ceil('12:34:56')^"
argument_list|,
literal|"Cannot apply 'CEIL' to arguments of type "
operator|+
literal|"'CEIL\\(<CHAR\\(8\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'CEIL\\(<NUMERIC>\\)'\n"
operator|+
literal|"'CEIL\\(<DATETIME_INTERVAL>\\)'\n"
operator|+
literal|"'CEIL\\(<DATE> TO<TIME_UNIT>\\)'\n"
operator|+
literal|"'CEIL\\(<TIME> TO<TIME_UNIT>\\)'\n"
operator|+
literal|"'CEIL\\(<TIMESTAMP> TO<TIME_UNIT>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"ceil('12:34:56')"
argument_list|,
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ceil(time '12:34:56')^"
argument_list|,
literal|"(?s)Cannot apply 'CEIL' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ceil(123.45 to minute)^"
argument_list|,
literal|"(?s)Cannot apply 'CEIL' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^ceil('abcde' to minute)^"
argument_list|,
literal|"(?s)Cannot apply 'CEIL' to arguments .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(time '12:34:56' to minute)"
argument_list|,
literal|"12:35:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(time '12:59:56' to minute)"
argument_list|,
literal|"13:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56.78' to second)"
argument_list|,
literal|"2015-02-19 12:34:57"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56.78' to millisecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56.78' to microsecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56.78' to nanosecond)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56.00' to second)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56' to minute)"
argument_list|,
literal|"2015-02-19 12:35:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56' to year)"
argument_list|,
literal|"2016-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(date '2015-02-19' to year)"
argument_list|,
literal|"2016-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2015-02-19 12:34:56' to month)"
argument_list|,
literal|"2015-03-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(date '2015-02-19' to month)"
argument_list|,
literal|"2015-03-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceil(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceil(cast(null as date) to month)"
argument_list|)
expr_stmt|;
comment|// ceiling alias
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceiling(timestamp '2015-02-19 12:34:56' to month)"
argument_list|,
literal|"2015-03-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceiling(date '2015-02-19' to month)"
argument_list|,
literal|"2015-03-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"ceiling(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@code FLOOR}, {@code CEIL}, {@code TIMESTAMPADD},    * {@code TIMESTAMPDIFF} functions with custom time frames. */
annotation|@
name|Test
name|void
name|testCustomTimeFrame
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withFactory
argument_list|(
name|tf
lambda|->
name|tf
operator|.
name|withTypeSystem
argument_list|(
name|typeSystem
lambda|->
operator|new
name|DelegatingTypeSystem
argument_list|(
name|typeSystem
argument_list|)
block|{
block_content|@Override public TimeFrameSet deriveTimeFrameSet(                       TimeFrameSet frameSet
argument_list|)
block|{
return|return
name|TimeFrameSet
operator|.
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|frameSet
argument_list|)
operator|.
name|addDivision
argument_list|(
literal|"minute15"
argument_list|,
literal|4
argument_list|,
literal|"HOUR"
argument_list|)
operator|.
name|addMultiple
argument_list|(
literal|"month4"
argument_list|,
literal|4
argument_list|,
literal|"MONTH"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2020-06-27 12:34:56' to \"minute15\")"
argument_list|,
literal|"2020-06-27 12:30:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(timestamp '2020-06-27 12:34:56' to \"month4\")"
argument_list|,
literal|"2020-05-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(date '2020-06-27' to \"month4\")"
argument_list|,
literal|"2020-05-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"ceil(timestamp '2020-06-27 12:34:56' to \"minute15\")"
argument_list|,
literal|"2020-06-27 12:45:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkFails
argument_list|(
literal|"ceil(timestamp '2020-06-27 12:34:56' to ^\"minute25\"^)"
argument_list|,
literal|"'minute25' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(\"minute15\", 7, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 14:27:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(\"month4\", 7, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2018-06-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(\"month4\", 7, date '2016-02-24')"
argument_list|,
literal|"2018-06-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"minute15\", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25')"
argument_list|,
literal|"12"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2018-02-24 15:42:25')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2018-02-23 15:42:25')"
argument_list|,
literal|"5"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", date '2016-02-24', "
operator|+
literal|"date '2020-03-24')"
argument_list|,
literal|"12"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", date '2016-02-24', "
operator|+
literal|"date '2016-06-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", date '2016-02-24', "
operator|+
literal|"date '2016-06-24')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", date '2016-02-24', "
operator|+
literal|"date '2015-10-24')"
argument_list|,
literal|"-1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(\"month4\", date '2016-02-24', "
operator|+
literal|"date '2016-02-23')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_DIFF3
argument_list|)
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"timestamp '2008-12-25 16:30:00', \"minute15\")"
argument_list|,
literal|"-4"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    @
name|Test
name|void
name|testFloorFuncInterval
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '3:4:5' hour to second)"
argument_list|,
literal|"+3:00:00.000000"
argument_list|,
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '-6.3' second)"
argument_list|,
literal|"-7.000000"
argument_list|,
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '5-1' year to month)"
argument_list|,
literal|"+5-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '-5-1' year to month)"
argument_list|,
literal|"-6-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '-6.3' second to second)"
argument_list|,
literal|"-7.000000"
argument_list|,
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '6-3' minute to second to minute)"
argument_list|,
literal|"-7-0"
argument_list|,
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '6-3' hour to minute to hour)"
argument_list|,
literal|"7-0"
argument_list|,
literal|"INTERVAL HOUR TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '6 3' day to hour to day)"
argument_list|,
literal|"7 00"
argument_list|,
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '102-7' year to month to month)"
argument_list|,
literal|"102-07"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '102-7' year to month to quarter)"
argument_list|,
literal|"102-10"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '102-1' year to month to century)"
argument_list|,
literal|"201"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"floor(interval '1004-1' year to month to millennium)"
argument_list|,
literal|"2001-00"
argument_list|,
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as interval year))"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimestampAdd
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_ADD
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|MICROSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 2000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 2, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|NANOSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 3000000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:28"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|NANOSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 2000000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 2, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:44:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -2000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", CAST(NULL AS INTEGER),"
operator|+
literal|" timestamp '2016-02-24 12:42:25')"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -200, CAST(NULL AS TIMESTAMP))"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 3, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-05-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 3, cast(null as timestamp))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with DATE; returns a TIMESTAMP value for sub-day intervals.
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, date '2016-06-15')"
argument_list|,
literal|"2016-07-15"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-16"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, date '2016-06-15')"
argument_list|,
literal|"2016-06-14 23:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-15 00:01:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, date '2016-06-15')"
argument_list|,
literal|"2016-06-14 23:59:59"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-15 00:00:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, cast(null as date))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, cast(null as date))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"DATE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Round to the last day of previous month
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, date '2016-05-31')"
argument_list|,
literal|"2016-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 5, date '2016-01-31')"
argument_list|,
literal|"2016-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, date '2016-03-31')"
argument_list|,
literal|"2016-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with time; returns a time value.The interval is positive.
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, time '23:59:59')"
argument_list|,
literal|"00:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, time '00:00:00')"
argument_list|,
literal|"00:01:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, time '23:59:59')"
argument_list|,
literal|"00:00:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, time '23:59:59')"
argument_list|,
literal|"00:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 15, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 3, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 6, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|YEAR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", 10, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with time; returns a time value .The interval is negative.
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '00:00:00')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '00:00:00')"
argument_list|,
literal|"23:59:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '00:00:00')"
argument_list|,
literal|"23:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|YEAR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd("
operator|+
name|s
operator|+
literal|", -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimestampAddFractionalSeconds
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_ADD
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"timestampadd(SQL_TSI_FRAC_SECOND, 2, timestamp '2016-02-24 12:42:25.000000')"
argument_list|,
comment|// "2016-02-24 12:42:25.000002",
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
comment|// The following test would correctly return "TIMESTAMP(6) NOT NULL" if max
comment|// precision were 6 or higher
name|assumeTrue
argument_list|(
name|f
operator|.
name|getFactory
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|getMaxPrecision
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|==
literal|3
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"timestampadd(MICROSECOND, 2, timestamp '2016-02-24 12:42:25.000000')"
argument_list|,
comment|// "2016-02-24 12:42:25.000002",
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests {@code TIMESTAMP_ADD}, BigQuery's 2-argument variant of the    * 3-argument {@code TIMESTAMPADD} function. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testTimestampAdd2
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_ADD2
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 5 minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIMESTAMP_ADD\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000000 microsecond)"
argument_list|,
literal|"2008-12-26 19:16:40"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000 millisecond)"
argument_list|,
literal|"2008-12-26 19:16:40"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval 2 second)"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval 2 minute)"
argument_list|,
literal|"2016-02-24 12:44:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval -2000 hour)"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval 1 day)"
argument_list|,
literal|"2016-02-25 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval 1 month)"
argument_list|,
literal|"2016-03-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_add(timestamp '2016-02-24 12:42:25', interval 1 year)"
argument_list|,
literal|"2017-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestamp_add(CAST(NULL AS TIMESTAMP), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests BigQuery's {@code DATETIME_ADD(timestamp, interval)} function.    * When Calcite runs in BigQuery mode, {@code DATETIME} is a type alias for    * {@code TIMESTAMP} and this function follows the same behavior as    * {@code TIMESTAMP_ADD(timestamp, interval)}. The tests below use    * {@code TIMESTAMP} values rather than the {@code DATETIME} alias because the    * operator fixture does not currently support type aliases. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDatetimeAdd
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_ADD
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^datetime_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 5 minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"DATETIME_ADD\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000000 microsecond)"
argument_list|,
literal|"2008-12-26 19:16:40"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000 millisecond)"
argument_list|,
literal|"2008-12-26 19:16:40"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval 2 second)"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval 2 minute)"
argument_list|,
literal|"2016-02-24 12:44:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval -2000 hour)"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval 1 day)"
argument_list|,
literal|"2016-02-25 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval 1 month)"
argument_list|,
literal|"2016-03-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_add(timestamp '2016-02-24 12:42:25', interval 1 year)"
argument_list|,
literal|"2017-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"datetime_add(CAST(NULL AS TIMESTAMP), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests {@code TIMESTAMP_DIFF}, BigQuery's variant of the    * {@code TIMESTAMPDIFF} function, which differs in the ordering    * of the parameters and the ordering of the subtraction between    * the two timestamps. In {@code TIMESTAMPDIFF} it is (t2 - t1)    * while for {@code TIMESTAMP_DIFF} is is (t1 - t2). */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testTimestampDiff3
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_DIFF3
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_diff(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"timestamp '2008-12-25 16:30:00', "
operator|+
literal|"minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIMESTAMP_DIFF\\(<TIMESTAMP>,<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_DIFF3
argument_list|)
decl_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MICROSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"5000000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|YEAR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-104"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-19 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-105"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2019-09-01 12:42:25', "
operator|+
literal|"timestamp '2020-03-01 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2019-09-01 12:42:25', "
operator|+
literal|"timestamp '2016-08-01 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-8"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2614-02-24 12:42:25', "
operator|+
literal|"CENTURY)"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"cast(null as timestamp), "
operator|+
name|s
operator|+
literal|")"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
comment|// timestamp_diff with date
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-03-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2019-09-01', "
operator|+
literal|"date '2020-03-01', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2019-09-01', "
operator|+
literal|"date '2016-08-01', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-06-15',  "
operator|+
literal|"date '2016-06-15', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"1440"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_diff(date '2016-06-15', "
operator|+
literal|"cast(null as date), "
operator|+
name|s
operator|+
literal|")"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests BigQuery's {@code DATETIME_DIFF(timestamp, timestamp2, timeUnit)}    * function. When Calcite runs in BigQuery mode, {@code DATETIME} is a type    * alias for {@code TIMESTAMP} and this function follows the same behavior as    * {@code TIMESTAMP_DIFF(timestamp, timestamp2, timeUnit)}. The tests below    * use {@code TIMESTAMP} values rather than the {@code DATETIME} alias because    * the operator fixture does not currently support type aliases. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDatetimeDiff
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_DIFF
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^datetime_diff(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"timestamp '2008-12-25 16:30:00', "
operator|+
literal|"minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"DATETIME_DIFF\\(<TIMESTAMP>,<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_DIFF
argument_list|)
decl_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MICROSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"5000000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|YEAR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-104"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-19 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-105"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2019-09-01 12:42:25', "
operator|+
literal|"timestamp '2020-03-01 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2019-09-01 12:42:25', "
operator|+
literal|"timestamp '2016-08-01 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-8"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2614-02-24 12:42:25', "
operator|+
literal|"CENTURY)"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(timestamp '2016-02-24 12:42:25', "
operator|+
literal|"cast(null as timestamp), "
operator|+
name|s
operator|+
literal|")"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
comment|// datetime_diff with date
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-03-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2019-09-01', "
operator|+
literal|"date '2020-03-01', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2019-09-01', "
operator|+
literal|"date '2016-08-01', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-06-15',  "
operator|+
literal|"date '2016-06-15', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-06-15', "
operator|+
literal|"date '2016-06-14', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"1440"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_diff(date '2016-06-15', "
operator|+
literal|"cast(null as date), "
operator|+
name|s
operator|+
literal|")"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|ValueSource
argument_list|(
name|booleans
operator|=
block|{
literal|true
block|,
literal|false
block|}
argument_list|)
annotation|@
name|ParameterizedTest
argument_list|(
name|name
operator|=
literal|"CoercionEnabled: {0}"
argument_list|)
name|void
name|testTimestampDiff
parameter_list|(
name|boolean
name|coercionEnabled
parameter_list|)
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withTypeCoercionEnabled
argument_list|(
name|coercionEnabled
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_DIFF
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MICROSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20')"
argument_list|,
literal|"-5000000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|NANOSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20')"
argument_list|,
literal|"-5000000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|YEAR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"104"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|WEEK_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-19 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"105"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2019-09-01 00:00:00', "
operator|+
literal|"timestamp '2020-03-01 00:00:00')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2019-09-01 00:00:00', "
operator|+
literal|"timestamp '2016-08-01 00:00:00')"
argument_list|,
literal|"-37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"8"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Until 1.33, CENTURY was an invalid time frame for TIMESTAMPDIFF
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(CENTURY, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2614-02-24 12:42:25')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"cast(null as timestamp))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
name|QUARTER_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"cast(null as timestamp), "
operator|+
literal|"timestamp '2014-02-24 12:42:25')"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
comment|// timestampdiff with date
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-03-15', date '2016-06-14')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2019-09-01', date '2020-03-01')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2019-09-01', date '2016-08-01')"
argument_list|,
literal|"-37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"time '12:42:25', time '12:42:25')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"time '12:42:25', date '2016-06-14')"
argument_list|,
literal|"-1502389"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MONTH_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-14', time '12:42:25')"
argument_list|,
literal|"1502389"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-15', date '2016-06-14')"
argument_list|,
literal|"-1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-15', date '2016-06-14')"
argument_list|,
literal|"-24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-15',  date '2016-06-15')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-15', date '2016-06-14')"
argument_list|,
literal|"-1440"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"cast(null as date), date '2016-06-15')"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
name|DAY_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff("
operator|+
name|s
operator|+
literal|", "
operator|+
literal|"date '2016-06-15', cast(null as date))"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimestampSub
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_SUB
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 5 minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIMESTAMP_SUB\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000000 microsecond)"
argument_list|,
literal|"2008-12-24 11:44:20"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000 millisecond)"
argument_list|,
literal|"2008-12-24 11:44:20"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 2 second)"
argument_list|,
literal|"2016-02-24 12:42:23"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 2 minute)"
argument_list|,
literal|"2016-02-24 12:40:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 2000 hour)"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 1 day)"
argument_list|,
literal|"2016-02-23 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 2 week)"
argument_list|,
literal|"2016-02-10 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 2 weeks)"
argument_list|,
literal|"2016-02-10 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 1 month)"
argument_list|,
literal|"2016-01-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 1 quarter)"
argument_list|,
literal|"2015-11-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 1 quarters)"
argument_list|,
literal|"2015-11-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_sub(timestamp '2016-02-24 12:42:25', interval 1 year)"
argument_list|,
literal|"2015-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"timestamp_sub(CAST(NULL AS TIMESTAMP), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimeSub
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIME_SUB
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^time_sub(time '15:30:00', "
operator|+
literal|"interval 5 minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIME_SUB\\(<TIME>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '15:30:00', "
operator|+
literal|"interval 100000000000 microsecond)"
argument_list|,
literal|"11:44:20"
argument_list|,
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '15:30:00', "
operator|+
literal|"interval 100000000 millisecond)"
argument_list|,
literal|"11:44:20"
argument_list|,
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '12:42:25', interval 2 second)"
argument_list|,
literal|"12:42:23"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '12:42:25', interval 2 minute)"
argument_list|,
literal|"12:40:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '12:42:25', interval 0 minute)"
argument_list|,
literal|"12:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '12:42:25', interval 20 hour)"
argument_list|,
literal|"16:42:25"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_sub(time '12:34:45', interval -5 second)"
argument_list|,
literal|"12:34:50"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"time_sub(CAST(NULL AS TIME), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDateSub
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATE_SUB
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^date_sub(date '2008-12-25', "
operator|+
literal|"interval 5 day)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"DATE_SUB\\(<DATE>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 2 day)"
argument_list|,
literal|"2016-02-22"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 1 week)"
argument_list|,
literal|"2016-02-17"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 2 weeks)"
argument_list|,
literal|"2016-02-10"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2020-10-17', interval 0 week)"
argument_list|,
literal|"2020-10-17"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 3 month)"
argument_list|,
literal|"2015-11-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 1 quarter)"
argument_list|,
literal|"2015-11-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 2 quarters)"
argument_list|,
literal|"2015-08-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_sub(date '2016-02-24', interval 5 year)"
argument_list|,
literal|"2011-02-24"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"date_sub(CAST(NULL AS DATE), interval 5 day)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests for BigQuery's DATETIME_SUB() function. Because the operator    * fixture does not currently support type aliases, TIMESTAMPs are used    * in place of DATETIMEs (a Calcite alias of TIMESTAMP) for the function's    * first argument. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDatetimeSub
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_SUB
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^datetime_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 5 minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"DATETIME_SUB\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000000 microsecond)"
argument_list|,
literal|"2008-12-24 11:44:20"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2008-12-25 15:30:00', "
operator|+
literal|"interval 100000000 millisecond)"
argument_list|,
literal|"2008-12-24 11:44:20"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 2 second)"
argument_list|,
literal|"2016-02-24 12:42:23"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 2 minute)"
argument_list|,
literal|"2016-02-24 12:40:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 2000 hour)"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 1 day)"
argument_list|,
literal|"2016-02-23 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 1 month)"
argument_list|,
literal|"2016-01-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_sub(timestamp '2016-02-24 12:42:25', interval 1 year)"
argument_list|,
literal|"2015-02-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"datetime_sub(CAST(NULL AS TIMESTAMP), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** The {@code DATEDIFF} function is implemented in the Babel parser but not    * the Core parser, and therefore gives validation errors. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDateDiff
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATEDIFF
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"datediff(^\"MONTH\"^, '2019-09-14',  '2019-09-15')"
argument_list|,
literal|"(?s)Column 'MONTH' not found in any table"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests BigQuery's {@code TIME_ADD}, which adds an interval to a time    * expression. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testTimeAdd
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIME_ADD
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^time_add(time '15:30:00', interval 5 minute)^"
argument_list|,
literal|"No match found for function signature TIME_ADD\\(<TIME>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_5422_FIXED
condition|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 5000000 millisecond)"
argument_list|,
literal|"15:30:05"
argument_list|,
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 5000000000 microsecond)"
argument_list|,
literal|"15:30:05"
argument_list|,
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '23:59:59', interval 2 second)"
argument_list|,
literal|"00:00:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '23:59:59', interval 86402 second)"
argument_list|,
literal|"00:00:01"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 5 minute)"
argument_list|,
literal|"15:35:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 1445 minute)"
argument_list|,
literal|"15:35:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 3 hour)"
argument_list|,
literal|"18:30:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_add(time '15:30:00', interval 27 hour)"
argument_list|,
literal|"18:30:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"time_add(cast(null as time), interval 5 minute)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimeDiff
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIME_DIFF
argument_list|)
decl_stmt|;
name|f0
operator|.
name|checkFails
argument_list|(
literal|"^time_diff(time '15:30:00', "
operator|+
literal|"time '16:30:00', "
operator|+
literal|"minute)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIME_DIFF\\(<TIME>,<TIME>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"time '15:30:05', "
operator|+
literal|"millisecond)"
argument_list|,
literal|"-5000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|MICROSECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"time '15:30:05', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-5000000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SECOND_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"time '15:29:00', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"60"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"time '15:29:00', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|HOUR_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"time '16:30:00', "
operator|+
name|s
operator|+
literal|")"
argument_list|,
literal|"-1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|MINUTE_VARIANTS
operator|.
name|forEach
argument_list|(
name|s
lambda|->
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_diff(time '15:30:00', "
operator|+
literal|"cast(null as time), "
operator|+
name|s
operator|+
literal|")"
argument_list|,
name|isNullValue
argument_list|()
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimeTrunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|nonBigQuery
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIME_TRUNC
argument_list|)
decl_stmt|;
name|nonBigQuery
operator|.
name|checkFails
argument_list|(
literal|"^time_trunc(time '15:30:00', hour)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIME_TRUNC\\(<TIME>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIME_TRUNC
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"time_trunc(time '12:34:56', ^year^)"
argument_list|,
literal|"'YEAR' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^time_trunc(123.45, minute)^"
argument_list|,
literal|"Cannot apply 'TIME_TRUNC' to arguments of type "
operator|+
literal|"'TIME_TRUNC\\(<DECIMAL\\(5, 2\\)>,<INTERVAL MINUTE>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'TIME_TRUNC\\(<TIME>,<DATETIME_INTERVAL>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_trunc(time '12:34:56', second)"
argument_list|,
literal|"12:34:56"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_trunc(time '12:34:56', minute)"
argument_list|,
literal|"12:34:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"time_trunc(time '12:34:56', hour)"
argument_list|,
literal|"12:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
literal|"time_trunc(cast(null as time), second)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTimestampTrunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|nonBigQuery
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_TRUNC
argument_list|)
decl_stmt|;
name|nonBigQuery
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_trunc(timestamp '2012-05-02 15:30:00', hour)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"TIMESTAMP_TRUNC\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TIMESTAMP_TRUNC
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_trunc(100, hour)^"
argument_list|,
literal|"Cannot apply 'TIMESTAMP_TRUNC' to arguments of type "
operator|+
literal|"'TIMESTAMP_TRUNC\\(<INTEGER>,<INTERVAL HOUR>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'TIMESTAMP_TRUNC\\(<TIMESTAMP>,<DATETIME_INTERVAL>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^timestamp_trunc(100, foo)^"
argument_list|,
literal|"Cannot apply 'TIMESTAMP_TRUNC' to arguments of type "
operator|+
literal|"'TIMESTAMP_TRUNC\\(<INTEGER>,<INTERVAL `FOO`>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'TIMESTAMP_TRUNC\\(<TIMESTAMP>,<DATETIME_INTERVAL>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56.78', ^microsecond^)"
argument_list|,
literal|"'MICROSECOND' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56.78', ^nanosecond^)"
argument_list|,
literal|"'NANOSECOND' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56.78', ^millisecond^)"
argument_list|,
literal|"'MILLISECOND' is not a valid time frame"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56.78', second)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', minute)"
argument_list|,
literal|"2015-02-19 12:34:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', hour)"
argument_list|,
literal|"2015-02-19 12:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', day)"
argument_list|,
literal|"2015-02-19 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', week)"
argument_list|,
literal|"2015-02-15 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', month)"
argument_list|,
literal|"2015-02-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"timestamp_trunc(timestamp '2015-02-19 12:34:56', year)"
argument_list|,
literal|"2015-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDatetimeTrunc
parameter_list|()
block|{
name|SqlOperatorFixture
name|nonBigQuery
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_TRUNC
argument_list|)
decl_stmt|;
name|nonBigQuery
operator|.
name|checkFails
argument_list|(
literal|"^datetime_trunc(timestamp '2012-05-02 15:30:00', hour)^"
argument_list|,
literal|"No match found for function signature "
operator|+
literal|"DATETIME_TRUNC\\(<TIMESTAMP>,<INTERVAL_DAY_TIME>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATETIME_TRUNC
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^datetime_trunc(100, hour)^"
argument_list|,
literal|"Cannot apply 'DATETIME_TRUNC' to arguments of type "
operator|+
literal|"'DATETIME_TRUNC\\(<INTEGER>,<INTERVAL HOUR>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'DATETIME_TRUNC\\(<TIMESTAMP>,<DATETIME_INTERVAL>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^datetime_trunc(100, foo)^"
argument_list|,
literal|"Cannot apply 'DATETIME_TRUNC' to arguments of type "
operator|+
literal|"'DATETIME_TRUNC\\(<INTEGER>,<INTERVAL `FOO`>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'DATETIME_TRUNC\\(<TIMESTAMP>,<DATETIME_INTERVAL>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56.78', second)"
argument_list|,
literal|"2015-02-19 12:34:56"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', minute)"
argument_list|,
literal|"2015-02-19 12:34:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', hour)"
argument_list|,
literal|"2015-02-19 12:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', day)"
argument_list|,
literal|"2015-02-19 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', week)"
argument_list|,
literal|"2015-02-15 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', month)"
argument_list|,
literal|"2015-02-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"datetime_trunc(timestamp '2015-02-19 12:34:56', year)"
argument_list|,
literal|"2015-01-01 00:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDateTrunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DATE_TRUNC
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"date_trunc(date '2015-02-19', ^foo^)"
argument_list|,
literal|"Column 'FOO' not found in any table"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', day)"
argument_list|,
literal|"2015-02-19"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week)"
argument_list|,
literal|"2015-02-15"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', isoweek)"
argument_list|,
literal|"2015-02-16"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(sunday))"
argument_list|,
literal|"2015-02-15"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(monday))"
argument_list|,
literal|"2015-02-16"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(tuesday))"
argument_list|,
literal|"2015-02-17"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(wednesday))"
argument_list|,
literal|"2015-02-18"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(thursday))"
argument_list|,
literal|"2015-02-19"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(friday))"
argument_list|,
literal|"2015-02-13"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', week(saturday))"
argument_list|,
literal|"2015-02-14"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', month)"
argument_list|,
literal|"2015-02-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', quarter)"
argument_list|,
literal|"2015-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', year)"
argument_list|,
literal|"2015-01-01"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"date_trunc(date '2015-02-19', isoyear)"
argument_list|,
literal|"2014-12-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFormatTime
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|FORMAT_TIME
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^FORMAT_TIME('%x', timestamp '2008-12-25 15:30:00')^"
argument_list|,
literal|"Cannot apply 'FORMAT_TIME' to arguments of type "
operator|+
literal|"'FORMAT_TIME\\(<CHAR\\(2\\)>,<TIMESTAMP\\(0\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'FORMAT_TIME\\(<CHARACTER>,<TIME>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIME('%H', TIME '12:34:33')"
argument_list|,
literal|"12"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIME('%R', TIME '12:34:33')"
argument_list|,
literal|"12:34"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIME('The time is %M-%S', TIME '12:34:33')"
argument_list|,
literal|"The time is 34-33"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFormatDate
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|FORMAT_DATE
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^FORMAT_DATE('%x', 123)^"
argument_list|,
literal|"Cannot apply 'FORMAT_DATE' to arguments of type "
operator|+
literal|"'FORMAT_DATE\\(<CHAR\\(2\\)>,<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'FORMAT_DATE\\(<CHARACTER>,<DATE>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Can implicitly cast TIMESTAMP to DATE
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_DATE('%x', timestamp '2008-12-25 15:30:00')"
argument_list|,
literal|"12/25/08"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_DATE('%b-%d-%Y', DATE '2008-12-25')"
argument_list|,
literal|"Dec-25-2008"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_DATE('%b %Y', DATE '2008-12-25')"
argument_list|,
literal|"Dec 2008"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_DATE('%x', DATE '2008-12-25')"
argument_list|,
literal|"12/25/08"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_DATE('The date is: %x', DATE '2008-12-25')"
argument_list|,
literal|"The date is: 12/25/08"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFormatTimestamp
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|FORMAT_TIMESTAMP
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^FORMAT_TIMESTAMP('%x', 123)^"
argument_list|,
literal|"Cannot apply 'FORMAT_TIMESTAMP' to arguments of type "
operator|+
literal|"'FORMAT_TIMESTAMP\\(<CHAR\\(2\\)>,<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"FORMAT_TIMESTAMP\\(<CHARACTER>, "
operator|+
literal|"<TIMESTAMP WITH LOCAL TIME ZONE>\\)\n"
operator|+
literal|"FORMAT_TIMESTAMP\\(<CHARACTER>, "
operator|+
literal|"<TIMESTAMP WITH LOCAL TIME ZONE>,<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('%c',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00')"
argument_list|,
literal|"Thu Dec 25 15:30:00 2008"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('%b-%d-%Y',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00')"
argument_list|,
literal|"Dec-25-2008"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('%b %Y',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00')"
argument_list|,
literal|"Dec 2008"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('%x',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00')"
argument_list|,
literal|"12/25/08"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('The time is: %R',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00')"
argument_list|,
literal|"The time is: 15:30"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"FORMAT_TIMESTAMP('The time is: %R.%E2S',"
operator|+
literal|" TIMESTAMP WITH LOCAL TIME ZONE '2008-12-25 15:30:00.1235456')"
argument_list|,
literal|"The time is: 15:30.123"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDenseRankFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DENSE_RANK
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentRankFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENT_RANK
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRankFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RANK
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCumeDistFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CUME_DIST
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRowNumberFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROW_NUMBER
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileContFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENTILE_CONT
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"percentile_cont(0.25) within group (order by 1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"percentile_cont(0.25) within group (^order by 'a'^)"
argument_list|,
literal|"Invalid type 'CHAR' in ORDER BY clause of 'PERCENTILE_CONT' function. "
operator|+
literal|"Only NUMERIC types are supported"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"percentile_cont(0.25) within group (^order by 1, 2^)"
argument_list|,
literal|"'PERCENTILE_CONT' requires precisely one ORDER BY key"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|" ^percentile_cont(2 + 3)^ within group (order by 1)"
argument_list|,
literal|"Argument to function 'PERCENTILE_CONT' must be a literal"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|" ^percentile_cont(2)^ within group (order by 1)"
argument_list|,
literal|"Argument to function 'PERCENTILE_CONT' must be a numeric literal "
operator|+
literal|"between 0 and 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileDiscFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENTILE_DISC
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"percentile_disc(0.25) within group (order by 1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"percentile_disc(0.25) within group (^order by 'a'^)"
argument_list|,
literal|"Invalid type 'CHAR' in ORDER BY clause of 'PERCENTILE_DISC' function. "
operator|+
literal|"Only NUMERIC types are supported"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"percentile_disc(0.25) within group (^order by 1, 2^)"
argument_list|,
literal|"'PERCENTILE_DISC' requires precisely one ORDER BY key"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|" ^percentile_disc(2 + 3)^ within group (order by 1)"
argument_list|,
literal|"Argument to function 'PERCENTILE_DISC' must be a literal"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|" ^percentile_disc(2)^ within group (order by 1)"
argument_list|,
literal|"Argument to function 'PERCENTILE_DISC' must be a numeric literal "
operator|+
literal|"between 0 and 1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCountFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count(*)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count('name')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count(1)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count(1.2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"COUNT(DISTINCT 'x')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^COUNT()^"
argument_list|,
literal|"Invalid number of arguments to function 'COUNT'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count(1, 2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"count(1, 2, 'x', 'y')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"1"
block|,
literal|"0"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// string values -- note that empty string is not null
specifier|final
name|String
index|[]
name|stringValues
init|=
block|{
literal|"'a'"
block|,
literal|"CAST(NULL AS VARCHAR(1))"
block|,
literal|"''"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(*)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(x)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT x)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT 123)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCountifFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|COUNTIF
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"countif(true)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"countif(nullif(true,true))"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"countif(false) filter (where true)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"Invalid number of arguments to function "
operator|+
literal|"'COUNTIF'. Was expecting 1 arguments"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^COUNTIF()^"
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^COUNTIF(true, false)^"
argument_list|,
name|expectedError
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedError2
init|=
literal|"Cannot apply 'COUNTIF' to arguments of "
operator|+
literal|"type 'COUNTIF\\(<INTEGER>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'COUNTIF\\(<BOOLEAN>\\)'"
decl_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^COUNTIF(1)^"
argument_list|,
name|expectedError2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"CAST(NULL AS INTEGER)"
block|,
literal|"1"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x> 0)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x< 2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x is not null) filter (where x< 2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x< 2) filter (where x is not null)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x between 1 and 2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"countif(x< 0)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testApproxCountDistinctFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"approx_count_distinct(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct('name')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1.2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT 'x')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^APPROX_COUNT_DISTINCT()^"
argument_list|,
literal|"Invalid number of arguments to function 'APPROX_COUNT_DISTINCT'. "
operator|+
literal|"Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1, 2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1, 2, 'x', 'y')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"1"
block|,
literal|"0"
block|}
decl_stmt|;
comment|// currently APPROX_COUNT_DISTINCT(x) returns the same as COUNT(DISTINCT x)
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// DISTINCT keyword is allowed but has no effect
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// string values -- note that empty string is not null
specifier|final
name|String
index|[]
name|stringValues
init|=
block|{
literal|"'a'"
block|,
literal|"CAST(NULL AS VARCHAR(1))"
block|,
literal|"''"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(x)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT x)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT 123)"
argument_list|,
name|stringValues
argument_list|,
name|isSingle
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSumFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"sum(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^sum('name')^"
argument_list|,
literal|"(?s)Cannot apply 'SUM' to arguments of type "
operator|+
literal|"'SUM\\(<CHAR\\(4\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'SUM\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sum('name')"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"sum(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"sum(1.2)"
argument_list|,
literal|"DECIMAL(19, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"sum(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(19, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^sum()^"
argument_list|,
literal|"Invalid number of arguments to function 'SUM'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^sum(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'SUM'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^sum(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'SUM' to arguments of type "
operator|+
literal|"'SUM\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'SUM\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"sum(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"sum(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"sum(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"sum(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"sum(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAvgFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"avg(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^avg(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'AVG' to arguments of type "
operator|+
literal|"'AVG\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'AVG\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"avg(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"AVG(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"AVG(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"avg(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"avg(1.2)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"avg(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"AVG(x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|2d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"AVG(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|1.5d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"avg(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCovarPopFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COVAR_POP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"covar_pop(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^covar_pop(cast(null as varchar(2)),"
operator|+
literal|" cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'COVAR_POP' to arguments of type "
operator|+
literal|"'COVAR_POP\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'COVAR_POP\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"covar_pop(cast(null as varchar(2)),cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"covar_pop(CAST(NULL AS INTEGER),CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"covar_pop(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"covar_pop(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCovarSampFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COVAR_SAMP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"covar_samp(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^covar_samp(cast(null as varchar(2)),"
operator|+
literal|" cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'COVAR_SAMP' to arguments of type "
operator|+
literal|"'COVAR_SAMP\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'COVAR_SAMP\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"covar_samp(cast(null as varchar(2)),cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"covar_samp(CAST(NULL AS INTEGER),CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"covar_samp(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"covar_samp(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRegrSxxFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|REGR_SXX
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"regr_sxx(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^regr_sxx(cast(null as varchar(2)),"
operator|+
literal|" cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'REGR_SXX' to arguments of type "
operator|+
literal|"'REGR_SXX\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'REGR_SXX\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"regr_sxx(cast(null as varchar(2)), cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"regr_sxx(CAST(NULL AS INTEGER), CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"regr_sxx(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"regr_sxx(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRegrSyyFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|REGR_SYY
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"regr_syy(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^regr_syy(cast(null as varchar(2)),"
operator|+
literal|" cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'REGR_SYY' to arguments of type "
operator|+
literal|"'REGR_SYY\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'REGR_SYY\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"regr_syy(cast(null as varchar(2)), cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"regr_syy(CAST(NULL AS INTEGER), CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"regr_syy(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"regr_syy(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStddevPopFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|STDDEV_POP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"stddev_pop(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^stddev_pop(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV_POP' to arguments of type "
operator|+
literal|"'STDDEV_POP\\(<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'STDDEV_POP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev_pop(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev_pop(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"stddev_pop(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
comment|// verified on Oracle 10g
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(x)"
argument_list|,
name|values
argument_list|,
name|isWithin
argument_list|(
literal|1.414213562373095d
argument_list|,
literal|0.000000000000001d
argument_list|)
argument_list|)
expr_stmt|;
comment|// Oracle does not allow distinct
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|1.5d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isSingle
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStddevSampFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|STDDEV_SAMP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"stddev_samp(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^stddev_samp(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV_SAMP' to arguments of type "
operator|+
literal|"'STDDEV_SAMP\\(<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'STDDEV_SAMP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev_samp(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev_samp(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"stddev_samp(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
comment|// verified on Oracle 10g
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(x)"
argument_list|,
name|values
argument_list|,
name|isWithin
argument_list|(
literal|1.732050807568877d
argument_list|,
literal|0.000000000000001d
argument_list|)
argument_list|)
expr_stmt|;
comment|// Oracle does not allow distinct
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isWithin
argument_list|(
literal|2.121320343559642d
argument_list|,
literal|0.000000000000001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStddevFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|STDDEV
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"stddev(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^stddev(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV' to arguments of type "
operator|+
literal|"'STDDEV\\(<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'STDDEV\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"stddev(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"stddev(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"stddev(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testVarPopFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|VAR_POP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"var_pop(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^var_pop(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VAR_POP' to arguments of type "
operator|+
literal|"'VAR_POP\\(<VARCHAR\\(2\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'VAR_POP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"var_pop(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"var_pop(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"var_pop(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|2d
argument_list|)
argument_list|)
expr_stmt|;
comment|// verified on Oracle 10g
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
name|isWithin
argument_list|(
literal|2.25d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isExactly
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testVarSampFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|VAR_SAMP
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"var_samp(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^var_samp(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VAR_SAMP' to arguments of type "
operator|+
literal|"'VAR_SAMP\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'VAR_SAMP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"var_samp(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"var_samp(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"var_samp(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|3d
argument_list|)
argument_list|)
expr_stmt|;
comment|// verified on Oracle 10g
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
name|isWithin
argument_list|(
literal|4.5d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testVarFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|VARIANCE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"variance(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|checkFails
argument_list|(
literal|"^variance(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VARIANCE' to arguments of type "
operator|+
literal|"'VARIANCE\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'VARIANCE\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"variance(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 9)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"variance(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggType
argument_list|(
literal|"variance(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS FLOAT)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"variance(x)"
argument_list|,
name|values
argument_list|,
name|isExactly
argument_list|(
literal|3d
argument_list|)
argument_list|)
expr_stmt|;
comment|// verified on Oracle 10g
name|f
operator|.
name|checkAgg
argument_list|(
literal|"variance(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
name|isWithin
argument_list|(
literal|4.5d
argument_list|,
literal|0.0001d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"variance(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with one value
name|f
operator|.
name|checkAgg
argument_list|(
literal|"variance(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"5"
block|}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// with zero values
name|f
operator|.
name|checkAgg
argument_list|(
literal|"variance(x)"
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMinFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"min(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"min(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"min(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"min(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^min()^"
argument_list|,
literal|"Invalid number of arguments to function 'MIN'. "
operator|+
literal|"Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^min(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'MIN'. "
operator|+
literal|"Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"min(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"min(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"min(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"min(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMaxFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"max(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"max(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"max(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"max(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^max()^"
argument_list|,
literal|"Invalid number of arguments to function 'MAX'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^max(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'MAX'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"max(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"max(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"max(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"max(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLastValueFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LAST_VALUE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"last_value(x)"
argument_list|,
name|values
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"INTEGER"
argument_list|,
name|isSet
argument_list|(
literal|"3"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"1.6"
block|,
literal|"1.2"
block|}
decl_stmt|;
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"last_value(x)"
argument_list|,
name|values2
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
name|isSet
argument_list|(
literal|"1.6"
argument_list|,
literal|"1.2"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values3
init|=
block|{
literal|"'foo'"
block|,
literal|"'bar'"
block|,
literal|"'name'"
block|}
decl_stmt|;
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"last_value(x)"
argument_list|,
name|values3
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|,
name|isSet
argument_list|(
literal|"foo "
argument_list|,
literal|"bar "
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFirstValueFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|FIRST_VALUE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"3"
block|,
literal|"3"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"first_value(x)"
argument_list|,
name|values
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"INTEGER"
argument_list|,
name|isSet
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values2
init|=
block|{
literal|"1.6"
block|,
literal|"1.2"
block|}
decl_stmt|;
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"first_value(x)"
argument_list|,
name|values2
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|,
name|isSet
argument_list|(
literal|"1.6"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values3
init|=
block|{
literal|"'foo'"
block|,
literal|"'bar'"
block|,
literal|"'name'"
block|}
decl_stmt|;
name|f
operator|.
name|checkWinAgg
argument_list|(
literal|"first_value(x)"
argument_list|,
name|values3
argument_list|,
literal|"ROWS 3 PRECEDING"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|,
name|isSet
argument_list|(
literal|"foo "
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testEveryFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EVERY
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"every(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"every(1 = 1)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"every(1.2 = 1.2)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"every(1.5 = 1.4)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^every()^"
argument_list|,
literal|"Invalid number of arguments to function 'EVERY'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^every(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'EVERY'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"every(x = 2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSomeAggFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SOME
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"some(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"some(1 = 1)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"some(1.2 = 1.2)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"some(1.5 = 1.4)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^some()^"
argument_list|,
literal|"Invalid number of arguments to function 'SOME'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^some(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'SOME'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"some(x = 2)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5160">[CALCITE-5160]    * ANY/SOME, ALL operators should support collection expressions</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testQuantifyCollectionOperators
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|QUANTIFY_OPERATORS
operator|.
name|forEach
argument_list|(
name|operator
lambda|->
name|f
operator|.
name|setFor
argument_list|(
name|operator
argument_list|,
name|SqlOperatorFixture
operator|.
name|VmName
operator|.
name|EXPAND
argument_list|)
argument_list|)
expr_stmt|;
name|Function2
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|,
name|Void
argument_list|>
name|checkBoolean
init|=
parameter_list|(
name|sql
parameter_list|,
name|result
parameter_list|)
lambda|->
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"COLLECTION"
argument_list|,
literal|"ARRAY"
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"COLLECTION"
argument_list|,
literal|"MULTISET"
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|checkNull
init|=
name|sql
lambda|->
block|{
name|f
operator|.
name|checkNull
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"COLLECTION"
argument_list|,
literal|"ARRAY"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkNull
argument_list|(
name|sql
operator|.
name|replace
argument_list|(
literal|"COLLECTION"
argument_list|,
literal|"MULTISET"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"1 = some (COLLECTION[2,3,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = some (COLLECTION[1,2,3])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = some (COLLECTION[1,2,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"1 = some (COLLECTION[null,null,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = some (COLLECTION[null,null,null])"
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1 = some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3 = some (COLLECTION[1,2])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1<> some (COLLECTION[1])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2<> some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3<> some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1< some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"0< some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2< some (COLLECTION[1,2])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2<= some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"0<= some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3<= some (COLLECTION[1,2])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2> some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3> some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1> some (COLLECTION[1,2])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2>= some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3>= some (COLLECTION[1,2,null])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"0>= some (COLLECTION[1,2])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 3 = some(x.t) FROM (SELECT ARRAY[1,2,3,null] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 4 = some(x.t) FROM (SELECT ARRAY[1,2,3] as t) as x"
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 4 = some(x.t) FROM (SELECT ARRAY[1,2,3,null] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT (SELECT * FROM UNNEST(ARRAY[3]) LIMIT 1) = "
operator|+
literal|"some(x.t) FROM (SELECT ARRAY[1,2,3,null] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"1 = all (COLLECTION[1,1,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = all (COLLECTION[1,2,3])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = all (COLLECTION[1,2,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"1 = all (COLLECTION[null,null,null])"
argument_list|)
expr_stmt|;
name|checkNull
operator|.
name|apply
argument_list|(
literal|"null = all (COLLECTION[null,null,null])"
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1 = all (COLLECTION[1,1])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3 = all (COLLECTION[1,3,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1<> all (COLLECTION[2,3,4])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2<> all (COLLECTION[2,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1< all (COLLECTION[2,3,4])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2< all (COLLECTION[1,2,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2<= all (COLLECTION[2,3,4])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"1<= all (COLLECTION[0,1,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2> all (COLLECTION[0,1])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3> all (COLLECTION[1,3,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"2>= all (COLLECTION[0,1,2])"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkBoolean
operator|.
name|apply
argument_list|(
literal|"3>= all (COLLECTION[3,4,null])"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 3>= all(x.t) FROM (SELECT ARRAY[1,2,3] as t) as x"
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 4 = all(x.t) FROM (SELECT ARRAY[1,2,3,null] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT 4 = all(x.t) FROM (SELECT ARRAY[4,4,null] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|check
argument_list|(
literal|"SELECT (SELECT * FROM UNNEST(ARRAY[3]) LIMIT 1) = "
operator|+
literal|"all(x.t) FROM (SELECT ARRAY[3,3] as t) as x"
argument_list|,
literal|"BOOLEAN"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAnyValueFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ANY_VALUE
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"any_value(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"any_value(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"any_value(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"any_value(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^any_value()^"
argument_list|,
literal|"Invalid number of arguments to function 'ANY_VALUE'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^any_value(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'ANY_VALUE'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"0"
block|,
literal|"CAST(null AS INTEGER)"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkAgg
argument_list|(
literal|"any_value(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"any_value(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"any_value(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"any_value(DISTINCT x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBoolAndFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// not in standard dialect
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^bool_and(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature BOOL_AND\\(<BOOLEAN>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolAndFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|void
name|checkBoolAndFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|BOOL_AND
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"bool_and(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bool_and(true)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_and(1)^"
argument_list|,
literal|"Cannot apply 'BOOL_AND' to arguments of type 'BOOL_AND\\(<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'BOOL_AND\\(<BOOLEAN>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_and()^"
argument_list|,
literal|"Invalid number of arguments to function 'BOOL_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_and(true, true)^"
argument_list|,
literal|"Invalid number of arguments to function 'BOOL_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_and(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values2
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_and(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values3
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"false"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_and(x)"
argument_list|,
name|values3
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values4
init|=
block|{
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_and(x)"
argument_list|,
name|values4
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBoolOrFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// not in standard dialect
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^bool_or(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature BOOL_OR\\(<BOOLEAN>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkBoolOrFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|void
name|checkBoolOrFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|BOOL_OR
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"bool_or(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bool_or(true)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_or(1)^"
argument_list|,
literal|"Cannot apply 'BOOL_OR' to arguments of type 'BOOL_OR\\(<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'BOOL_OR\\(<BOOLEAN>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_or()^"
argument_list|,
literal|"Invalid number of arguments to function 'BOOL_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bool_or(true, true)^"
argument_list|,
literal|"Invalid number of arguments to function 'BOOL_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_or(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values2
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_or(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values3
init|=
block|{
literal|"false"
block|,
literal|"false"
block|,
literal|"false"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_or(x)"
argument_list|,
name|values3
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values4
init|=
block|{
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bool_or(x)"
argument_list|,
name|values4
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLogicalAndFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// not in standard dialect
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^logical_and(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature LOGICAL_AND\\(<BOOLEAN>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkLogicalAndFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|void
name|checkLogicalAndFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LOGICAL_AND
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"logical_and(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"logical_and(true)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_and(1)^"
argument_list|,
literal|"Cannot apply 'LOGICAL_AND' to arguments of type 'LOGICAL_AND\\(<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'LOGICAL_AND\\(<BOOLEAN>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_and()^"
argument_list|,
literal|"Invalid number of arguments to function 'LOGICAL_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_and(true, true)^"
argument_list|,
literal|"Invalid number of arguments to function 'LOGICAL_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_and(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values2
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_and(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values3
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"false"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_and(x)"
argument_list|,
name|values3
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values4
init|=
block|{
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_and(x)"
argument_list|,
name|values4
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLogicalOrFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
comment|// not in standard dialect
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"^logical_or(x)^"
argument_list|,
name|values
argument_list|,
literal|"No match found for function signature LOGICAL_OR\\(<BOOLEAN>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkLogicalOrFunc
argument_list|(
name|f
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|void
name|checkLogicalOrFunc
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LOGICAL_OR
argument_list|,
name|VM_EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"logical_or(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"logical_or(true)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_or(1)^"
argument_list|,
literal|"Cannot apply 'LOGICAL_OR' to arguments of type 'LOGICAL_OR\\(<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'LOGICAL_OR\\(<BOOLEAN>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_or()^"
argument_list|,
literal|"Invalid number of arguments to function 'LOGICAL_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^logical_or(true, true)^"
argument_list|,
literal|"Invalid number of arguments to function 'LOGICAL_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values1
init|=
block|{
literal|"true"
block|,
literal|"true"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_or(x)"
argument_list|,
name|values1
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values2
init|=
block|{
literal|"true"
block|,
literal|"false"
block|,
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_or(x)"
argument_list|,
name|values2
argument_list|,
name|isSingle
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values3
init|=
block|{
literal|"false"
block|,
literal|"false"
block|,
literal|"false"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_or(x)"
argument_list|,
name|values3
argument_list|,
name|isSingle
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values4
init|=
block|{
literal|"null"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"logical_or(x)"
argument_list|,
name|values4
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBitAndFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BIT_AND
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"bit_and(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_and(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_and(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_and(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_and(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_and(CAST(x'02' AS BINARY(1)))"
argument_list|,
literal|"BINARY(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_and(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_AND' to arguments of type 'BIT_AND\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): 'BIT_AND\\(<INTEGER>\\)'\n"
operator|+
literal|"'BIT_AND\\(<BINARY>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_and()^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_and(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_AND'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"3"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_and(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|binaryValues
init|=
block|{
literal|"CAST(x'03' AS BINARY)"
block|,
literal|"cast(x'02' as BINARY)"
block|,
literal|"cast(x'02' AS BINARY)"
block|,
literal|"cast(null AS BINARY)"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_and(x)"
argument_list|,
name|binaryValues
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_and(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"CAST(x'02' AS BINARY)"
block|}
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAggFails
argument_list|(
literal|"bit_and(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"CAST(x'0201' AS VARBINARY)"
block|,
literal|"CAST(x'02' AS VARBINARY)"
block|}
argument_list|,
literal|"Error while executing SQL"
operator|+
literal|" \"SELECT bit_and\\(x\\)"
operator|+
literal|" FROM \\(SELECT CAST\\(x'0201' AS VARBINARY\\) AS x FROM \\(VALUES \\(1\\)\\)"
operator|+
literal|" UNION ALL SELECT CAST\\(x'02' AS VARBINARY\\) AS x FROM \\(VALUES \\(1\\)\\)\\)\":"
operator|+
literal|" Different length for bitwise operands: the first: 2, the second: 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBitOrFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BIT_OR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"bit_or(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_or(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_or(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_or(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_or(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_or(CAST(x'02' AS BINARY(1)))"
argument_list|,
literal|"BINARY(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_or(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_OR' to arguments of type "
operator|+
literal|"'BIT_OR\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'BIT_OR\\(<INTEGER>\\)'\n"
operator|+
literal|"'BIT_OR\\(<BINARY>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_or()^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_or(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_OR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"2"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_or(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|binaryValues
init|=
block|{
literal|"CAST(x'01' AS BINARY)"
block|,
literal|"cast(x'02' as BINARY)"
block|,
literal|"cast(x'02' AS BINARY)"
block|,
literal|"cast(null AS BINARY)"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_or(x)"
argument_list|,
name|binaryValues
argument_list|,
name|isSingle
argument_list|(
literal|"03"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_or(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"CAST(x'02' AS BINARY)"
block|}
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBitXorFunc
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BIT_XOR
argument_list|,
name|VM_FENNEL
argument_list|,
name|VM_JAVA
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"bit_xor(^*^)"
argument_list|,
literal|"Unknown identifier '\\*'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_xor(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_xor(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_xor(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_xor(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkType
argument_list|(
literal|"bit_xor(CAST(x'02' AS BINARY(1)))"
argument_list|,
literal|"BINARY(1)"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_xor(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_XOR' to arguments of type "
operator|+
literal|"'BIT_XOR\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'BIT_XOR\\(<INTEGER>\\)'\n"
operator|+
literal|"'BIT_XOR\\(<BINARY>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_xor()^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_XOR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkFails
argument_list|(
literal|"^bit_xor(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'BIT_XOR'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"1"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_xor(x)"
argument_list|,
name|values
argument_list|,
name|isSingle
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|binaryValues
init|=
block|{
literal|"CAST(x'01' AS BINARY)"
block|,
literal|"cast(x'02' as BINARY)"
block|,
literal|"cast(x'01' AS BINARY)"
block|,
literal|"cast(null AS BINARY)"
block|}
decl_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_xor(x)"
argument_list|,
name|binaryValues
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_xor(x)"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"CAST(x'02' AS BINARY)"
block|}
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"bit_xor(distinct(x))"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"CAST(x'02' AS BINARY)"
block|,
literal|"CAST(x'02' AS BINARY)"
block|}
argument_list|,
name|isSingle
argument_list|(
literal|"02"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testArgMin
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f0
init|=
name|fixture
argument_list|()
operator|.
name|withTester
argument_list|(
name|t
lambda|->
name|TESTER
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|xValues
init|=
block|{
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"4"
block|,
literal|"5"
block|,
literal|"7"
block|}
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkAgg
argument_list|(
literal|"arg_min(mod(x, 3), x)"
argument_list|,
name|xValues
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"arg_max(mod(x, 3), x)"
argument_list|,
name|xValues
argument_list|,
name|isSingle
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|SqlOperatorFixture
argument_list|>
name|consumer2
init|=
name|f
lambda|->
block|{
name|f
operator|.
name|checkAgg
argument_list|(
literal|"min_by(mod(x, 3), x)"
argument_list|,
name|xValues
argument_list|,
name|isSingle
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkAgg
argument_list|(
literal|"max_by(mod(x, 3), x)"
argument_list|,
name|xValues
argument_list|,
name|isSingle
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|f0
argument_list|)
expr_stmt|;
name|consumer2
operator|.
name|accept
argument_list|(
name|f0
operator|.
name|withLibrary
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests that CAST fails when given a value just outside the valid range for    * that type. For example,    *    *<ul>    *<li>CAST(-200 AS TINYINT) fails because the value is less than -128;    *<li>CAST(1E-999 AS FLOAT) fails because the value underflows;    *<li>CAST(123.4567891234567 AS FLOAT) fails because the value loses    * precision.    *</ul>    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLiteralAtLimit
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
name|SqlTests
operator|.
name|getTypes
argument_list|(
name|f
operator|.
name|getFactory
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataType
name|type
range|:
name|types
control|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|getValues
argument_list|(
operator|(
name|BasicSqlType
operator|)
name|type
argument_list|,
literal|true
argument_list|)
control|)
block|{
name|SqlLiteral
name|literal
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|createLiteral
argument_list|(
name|o
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|SqlString
name|literalString
init|=
name|literal
operator|.
name|toSqlString
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expr
init|=
literal|"CAST("
operator|+
name|literalString
operator|+
literal|" AS "
operator|+
name|type
operator|+
literal|")"
decl_stmt|;
try|try
block|{
name|f
operator|.
name|checkType
argument_list|(
name|expr
argument_list|,
name|type
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BINARY
condition|)
block|{
comment|// Casting a string/binary values may change the value.
comment|// For example, CAST(X'AB' AS BINARY(2)) yields
comment|// X'AB00'.
block|}
else|else
block|{
name|f
operator|.
name|checkScalar
argument_list|(
name|expr
operator|+
literal|" = "
operator|+
name|literalString
argument_list|,
literal|true
argument_list|,
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Error
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed for expr=["
operator|+
name|expr
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/**    * Tests that CAST fails when given a value just outside the valid range for    * that type. For example,    *    *<ul>    *<li>CAST(-200 AS TINYINT) fails because the value is less than -128;    *<li>CAST(1E-999 AS FLOAT) fails because the value underflows;    *<li>CAST(123.4567891234567 AS FLOAT) fails because the value loses    * precision.    *</ul>    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLiteralBeyondLimit
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
name|SqlTests
operator|.
name|getTypes
argument_list|(
name|f
operator|.
name|getFactory
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataType
name|type
range|:
name|types
control|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|getValues
argument_list|(
operator|(
name|BasicSqlType
operator|)
name|type
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|SqlLiteral
name|literal
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|createLiteral
argument_list|(
name|o
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|SqlString
name|literalString
init|=
name|literal
operator|.
name|toSqlString
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BIGINT
operator|)
operator|||
operator|(
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|DECIMAL
operator|)
operator|&&
operator|(
name|type
operator|.
name|getPrecision
argument_list|()
operator|==
literal|19
operator|)
operator|)
condition|)
block|{
comment|// Values which are too large to be literals fail at
comment|// validate time.
name|f
operator|.
name|checkFails
argument_list|(
literal|"CAST(^"
operator|+
name|literalString
operator|+
literal|"^ AS "
operator|+
name|type
operator|+
literal|")"
argument_list|,
literal|"Numeric literal '.*' out of range"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|CHAR
operator|)
operator|||
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|VARCHAR
operator|)
operator|||
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BINARY
operator|)
operator|||
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|VARBINARY
operator|)
condition|)
block|{
comment|// Casting overlarge string/binary values do not fail -
comment|// they are truncated. See testCastTruncates().
block|}
else|else
block|{
comment|// Value outside legal bound should fail at runtime (not
comment|// validate time).
comment|//
comment|// NOTE: Because Java and Fennel calcs give
comment|// different errors, the pattern hedges its bets.
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
literal|"CAST("
operator|+
name|literalString
operator|+
literal|" AS "
operator|+
name|type
operator|+
literal|")"
argument_list|,
literal|"(?s).*(Overflow during calculation or cast\\.|Code=22003).*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_function

begin_function
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"safeParameters"
argument_list|)
name|void
name|testCastTruncates
parameter_list|(
name|boolean
name|safe
parameter_list|,
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|f
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST('ABCD' AS CHAR(2))"
argument_list|,
literal|"AB"
argument_list|,
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST('ABCD' AS VARCHAR(2))"
argument_list|,
literal|"AB"
argument_list|,
literal|"VARCHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST('ABCD' AS VARCHAR)"
argument_list|,
literal|"ABCD"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CAST('ABCD' AS VARCHAR) AS VARCHAR(3))"
argument_list|,
literal|"ABC"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(x'ABCDEF12' AS BINARY(2))"
argument_list|,
literal|"abcd"
argument_list|,
literal|"BINARY(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(x'ABCDEF12' AS VARBINARY(2))"
argument_list|,
literal|"abcd"
argument_list|,
literal|"VARBINARY(2) NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(x'ABCDEF12' AS VARBINARY)"
argument_list|,
literal|"abcdef12"
argument_list|,
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkScalar
argument_list|(
literal|"CAST(CAST(x'ABCDEF12' AS VARBINARY) AS VARBINARY(3))"
argument_list|,
literal|"abcdef"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|brokenTestsEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"CAST(X'' AS BINARY(3)) = X'000000'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|checkBoolean
argument_list|(
literal|"CAST(X'' AS BINARY(3)) = X''"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test that calls all operators with all possible argument types, and for    * each type, with a set of tricky values.    *    *<p>This is not really a unit test since there are no assertions;    * it either succeeds or fails in the preparation of the operator case    * and not when actually testing (validating/executing) the call.    *    *<p>Nevertheless the log messages conceal many problems which potentially    * need to be fixed especially cases where the query passes from the    * validation stage and fails at runtime. */
end_comment

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"Too slow and not really a unit test"
argument_list|)
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
name|void
name|testArgumentBounds
parameter_list|()
block|{
specifier|final
name|SqlOperatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorImpl
name|validator
init|=
operator|(
name|SqlValidatorImpl
operator|)
name|f
operator|.
name|getFactory
argument_list|()
operator|.
name|createValidator
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorScope
name|scope
init|=
name|validator
operator|.
name|getEmptyScope
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
operator|-
literal|3
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|,
name|Byte
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
operator|-
literal|4
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|,
name|Short
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
operator|-
literal|2
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
operator|-
literal|5
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add1
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|11
argument_list|,
literal|""
argument_list|,
literal|" "
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add1
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
literal|"e"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add0
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
literal|0L
argument_list|,
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|SqlOperator
argument_list|>
name|operatorsToSkip
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_3243_FIXED
condition|)
block|{
comment|// TODO: Remove entirely the if block when the bug is fixed
comment|// REVIEW zabetak 12-August-2019: It may still make sense to avoid the
comment|// JSON functions since for most of the values above they are expected
comment|// to raise an error and due to the big number of operands they accept
comment|// they increase significantly the running time of the method.
name|operatorsToSkip
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|JSON_VALUE
argument_list|)
expr_stmt|;
name|operatorsToSkip
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|JSON_QUERY
argument_list|)
expr_stmt|;
block|}
comment|// Skip since ClassCastException is raised in SqlOperator#unparse
comment|// since the operands of the call do not have the expected type.
comment|// Moreover, the values above do not make much sense for this operator.
name|operatorsToSkip
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|WITHIN_GROUP
argument_list|)
expr_stmt|;
name|operatorsToSkip
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|)
expr_stmt|;
comment|// can't handle the flag argument
name|operatorsToSkip
operator|.
name|add
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EXISTS
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlOperator
name|op
range|:
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
operator|.
name|getOperatorList
argument_list|()
control|)
block|{
if|if
condition|(
name|operatorsToSkip
operator|.
name|contains
argument_list|(
name|op
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|op
operator|.
name|getSyntax
argument_list|()
operator|==
name|SqlSyntax
operator|.
name|SPECIAL
condition|)
block|{
continue|continue;
block|}
specifier|final
name|SqlOperandTypeChecker
name|typeChecker
init|=
name|op
operator|.
name|getOperandTypeChecker
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeChecker
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
name|SqlOperandCountRange
name|range
init|=
name|typeChecker
operator|.
name|getOperandCountRange
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|range
operator|.
name|getMin
argument_list|()
init|,
name|max
init|=
name|range
operator|.
name|getMax
argument_list|()
init|;
name|n
operator|<=
name|max
condition|;
name|n
operator|++
control|)
block|{
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|ValueType
argument_list|>
argument_list|>
name|argValues
init|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|n
argument_list|,
name|builder
operator|.
name|values
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|List
argument_list|<
name|ValueType
argument_list|>
name|args
range|:
name|Linq4j
operator|.
name|product
argument_list|(
name|argValues
argument_list|)
control|)
block|{
name|SqlNodeList
name|nodeList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ValueType
name|arg
range|:
name|args
control|)
block|{
if|if
condition|(
name|arg
operator|.
name|value
operator|==
literal|null
condition|)
block|{
operator|++
name|nullCount
expr_stmt|;
block|}
name|nodeList
operator|.
name|add
argument_list|(
name|arg
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SqlCall
name|call
init|=
name|op
operator|.
name|createCall
argument_list|(
name|nodeList
argument_list|)
decl_stmt|;
specifier|final
name|SqlCallBinding
name|binding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|typeChecker
operator|.
name|checkOperandTypes
argument_list|(
name|binding
argument_list|,
literal|false
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|SqlPrettyWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|()
decl_stmt|;
name|op
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|writer
operator|.
name|toSqlString
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"OVERLAY("
argument_list|)
operator|||
name|s
operator|.
name|contains
argument_list|(
literal|" / 0"
argument_list|)
operator|||
name|s
operator|.
name|matches
argument_list|(
literal|"MOD\\(.*, 0\\)"
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|Strong
operator|.
name|Policy
name|policy
init|=
name|Strong
operator|.
name|policy
argument_list|(
name|op
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|nullCount
operator|>
literal|0
operator|&&
name|policy
operator|==
name|Strong
operator|.
name|Policy
operator|.
name|ANY
condition|)
block|{
name|f
operator|.
name|checkNull
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|query
decl_stmt|;
if|if
condition|(
name|op
operator|instanceof
name|SqlAggFunction
condition|)
block|{
if|if
condition|(
name|op
operator|.
name|requiresOrder
argument_list|()
condition|)
block|{
name|query
operator|=
literal|"SELECT "
operator|+
name|s
operator|+
literal|" OVER () FROM (VALUES (1))"
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
literal|"SELECT "
operator|+
name|s
operator|+
literal|" FROM (VALUES (1))"
expr_stmt|;
block|}
block|}
else|else
block|{
name|query
operator|=
name|AbstractSqlTester
operator|.
name|buildQuery
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
name|result
lambda|->
block|{
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Logging the top-level throwable directly makes the message
comment|// difficult to read since it either contains too much information
comment|// or very few details.
name|Throwable
name|cause
init|=
name|findMostDescriptiveCause
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Failed: "
operator|+
name|s
operator|+
literal|": "
operator|+
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_function

begin_function
specifier|private
name|Throwable
name|findMostDescriptiveCause
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|CalciteException
operator|||
name|ex
operator|instanceof
name|CalciteContextException
operator|||
name|ex
operator|instanceof
name|SqlParseException
condition|)
block|{
return|return
name|ex
return|;
block|}
name|Throwable
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
return|return
name|findMostDescriptiveCause
argument_list|(
name|cause
argument_list|)
return|;
block|}
return|return
name|ex
return|;
block|}
end_function

begin_function
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|getValues
parameter_list|(
name|BasicSqlType
name|type
parameter_list|,
name|boolean
name|inBound
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|boolean
name|sign
range|:
name|FALSE_TRUE
control|)
block|{
for|for
control|(
name|SqlTypeName
operator|.
name|Limit
name|limit
range|:
name|SqlTypeName
operator|.
name|Limit
operator|.
name|values
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|type
operator|.
name|getLimit
argument_list|(
name|sign
argument_list|,
name|limit
argument_list|,
operator|!
name|inBound
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|values
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
end_function

begin_comment
comment|/**    * Result checker that considers a test to have succeeded if it returns a    * particular value or throws an exception that matches one of a list of    * patterns.    *    *<p>Sounds peculiar, but is necessary when eager and lazy behaviors are    * both valid.    */
end_comment

begin_class
specifier|private
specifier|static
class|class
name|ValueOrExceptionResultChecker
implements|implements
name|SqlTester
operator|.
name|ResultChecker
block|{
specifier|private
specifier|final
name|Object
name|expected
decl_stmt|;
specifier|private
specifier|final
name|Pattern
index|[]
name|patterns
decl_stmt|;
name|ValueOrExceptionResultChecker
parameter_list|(
name|Object
name|expected
parameter_list|,
name|Pattern
modifier|...
name|patterns
parameter_list|)
block|{
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
name|this
operator|.
name|patterns
operator|=
name|patterns
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkResult
parameter_list|(
name|ResultSet
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|result
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// empty result is OK
return|return;
block|}
specifier|final
name|Object
name|actual
init|=
name|result
operator|.
name|getObject
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|thrown
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|stack
init|=
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|thrown
argument_list|)
decl_stmt|;
for|for
control|(
name|Pattern
name|pattern
range|:
name|patterns
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|stack
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
name|fail
argument_list|(
literal|"Stack did not match any pattern; "
operator|+
name|stack
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|/**    * Implementation of {@link org.apache.calcite.sql.test.SqlTester} based on a    * JDBC connection.    */
end_comment

begin_class
specifier|protected
specifier|static
class|class
name|TesterImpl
extends|extends
name|SqlRuntimeTester
block|{
comment|/** Assign a type system object to this thread-local, pass the name of the      * field as the {@link CalciteConnectionProperty#TYPE_SYSTEM} property,      * and any connection you make in the same thread will use your type      * system. */
specifier|public
specifier|static
specifier|final
name|TryThreadLocal
argument_list|<
name|RelDataTypeSystem
argument_list|>
name|THREAD_TYPE_SYSTEM
init|=
name|TryThreadLocal
operator|.
name|of
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Field
name|FIELD
init|=
name|Types
operator|.
name|lookupField
argument_list|(
name|TesterImpl
operator|.
name|class
argument_list|,
literal|"THREAD_TYPE_SYSTEM"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|uri
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'#'
operator|+
name|field
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|TesterImpl
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|check
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|String
name|query
parameter_list|,
name|SqlTester
operator|.
name|TypeChecker
name|typeChecker
parameter_list|,
name|SqlTester
operator|.
name|ParameterChecker
name|parameterChecker
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|super
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|query
argument_list|,
name|typeChecker
argument_list|,
name|parameterChecker
argument_list|,
name|resultChecker
argument_list|)
expr_stmt|;
specifier|final
name|RelDataTypeSystem
name|typeSystem
init|=
name|factory
operator|.
name|typeSystemTransform
operator|.
name|apply
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|ConnectionFactory
name|connectionFactory
init|=
name|factory
operator|.
name|connectionFactory
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|TYPE_SYSTEM
argument_list|,
name|uri
argument_list|(
name|FIELD
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignore
init|=
name|THREAD_TYPE_SYSTEM
operator|.
name|push
argument_list|(
name|typeSystem
argument_list|)
init|;
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
init|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|)
block|{
specifier|final
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|resultChecker
operator|.
name|checkResult
argument_list|(
name|resultSet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
end_class

begin_comment
comment|/** A type, a value, and its {@link SqlNode} representation. */
end_comment

begin_class
specifier|static
class|class
name|ValueType
block|{
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|final
name|Object
name|value
decl_stmt|;
specifier|final
name|SqlNode
name|node
decl_stmt|;
name|ValueType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|literal
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SqlNode
name|literal
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|CAST
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|SqlLiteral
operator|.
name|createNull
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlTypeUtil
operator|.
name|convertTypeToSpec
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
return|return
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|value
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
case|case
name|TINYINT
case|:
case|case
name|SMALLINT
case|:
case|case
name|INTEGER
case|:
case|case
name|BIGINT
case|:
return|return
name|SqlLiteral
operator|.
name|createExactNumeric
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
case|case
name|CHAR
case|:
case|case
name|VARCHAR
case|:
return|return
name|SqlLiteral
operator|.
name|createCharString
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
name|TimestampString
name|ts
init|=
name|TimestampString
operator|.
name|fromMillisSinceEpoch
argument_list|(
operator|(
name|Long
operator|)
name|value
argument_list|)
decl_stmt|;
return|return
name|SqlLiteral
operator|.
name|createTimestamp
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|ts
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|/** Builds lists of types and sample values. */
end_comment

begin_class
specifier|static
class|class
name|Builder
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ValueType
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Builder
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|void
name|add0
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
name|add
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add1
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
name|add
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|)
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|add
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|Object
index|[]
name|values
parameter_list|)
block|{
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|this
operator|.
name|values
operator|.
name|add
argument_list|(
operator|new
name|ValueType
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|values
operator|.
name|add
argument_list|(
operator|new
name|ValueType
argument_list|(
name|type
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/** Runs an OVERLAPS test with a given set of literal values. */
end_comment

begin_class
specifier|static
class|class
name|OverlapChecker
block|{
specifier|final
name|SqlOperatorFixture
name|f
decl_stmt|;
specifier|final
name|String
index|[]
name|values
decl_stmt|;
name|OverlapChecker
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|this
operator|.
name|f
operator|=
name|f
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|void
name|isTrue
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
name|sub
argument_list|(
name|s
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|isFalse
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
name|sub
argument_list|(
name|s
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|sub
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
literal|"$0"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$1"
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$2"
argument_list|,
name|values
index|[
literal|2
index|]
argument_list|)
operator|.
name|replace
argument_list|(
literal|"$3"
argument_list|,
name|values
index|[
literal|3
index|]
argument_list|)
return|;
block|}
block|}
end_class

unit|}
end_unit

