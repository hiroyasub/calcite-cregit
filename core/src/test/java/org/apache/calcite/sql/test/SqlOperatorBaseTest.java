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
name|SqlLibraryOperatorTableFactory
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
name|test
operator|.
name|SqlLimitsTest
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
name|slf4j
operator|.
name|Logger
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
name|assertFalse
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
comment|/**  * Contains unit tests for all operators. Each of the methods is named after an  * operator.  *  *<p>The class is abstract. It contains a test for every operator, but does not  * provide a mechanism to execute the tests: parse, validate, and execute  * expressions on the operators. This is left to a {@link SqlTester} object  * which the derived class must provide.</p>  *  *<p>Different implementations of {@link SqlTester} are possible, such as:</p>  *  *<ul>  *<li>Execute against a real farrago database  *<li>Execute in pure java (parsing and validation can be done, but expression  * evaluation is not possible)  *<li>Generate a SQL script.  *<li>Analyze which operators are adequately tested.  *</ul>  *  *<p>A typical method will be named after the operator it is testing (say  *<code>testSubstringFunc</code>). It first calls  * {@link SqlTester#setFor(org.apache.calcite.sql.SqlOperator, org.apache.calcite.sql.test.SqlTester.VmName...)}  * to declare which operator it is testing.  *  *<blockquote>  *<pre><code>  * public void testSubstringFunc() {  *     tester.setFor(SqlStdOperatorTable.substringFunc);  *     tester.checkScalar("sin(0)", "0");  *     tester.checkScalar("sin(1.5707)", "1");  * }</code></pre>  *</blockquote>  *  *<p>The rest of the method contains calls to the various {@code checkXxx}  * methods in the {@link SqlTester} interface. For an operator  * to be adequately tested, there need to be tests for:  *  *<ul>  *<li>Parsing all of its the syntactic variants.  *<li>Deriving the type of in all combinations of arguments.  *  *<ul>  *<li>Pay particular attention to nullability. For example, the result of the  * "+" operator is NOT NULL if and only if both of its arguments are NOT  * NULL.</li>  *<li>Also pay attention to precision/scale/length. For example, the maximum  * length of the "||" operator is the sum of the maximum lengths of its  * arguments.</li>  *</ul>  *</li>  *<li>Executing the function. Pay particular attention to corner cases such as  * null arguments or null results.</li>  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlOperatorBaseTest
block|{
comment|//~ Static fields/initializers ---------------------------------------------
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
name|SqlOperatorBaseTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "Invalid character for cast: PC=0 Code=22018"
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_CHAR_MESSAGE
init|=
name|Bug
operator|.
name|FNL3_FIXED
condition|?
literal|null
else|:
literal|"(?s).*"
decl_stmt|;
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "Overflow during calculation or cast: PC=0 Code=22003"
specifier|public
specifier|static
specifier|final
name|String
name|OUT_OF_RANGE_MESSAGE
init|=
name|Bug
operator|.
name|FNL3_FIXED
condition|?
literal|null
else|:
literal|"(?s).*"
decl_stmt|;
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "Division by zero: PC=0 Code=22012"
specifier|public
specifier|static
specifier|final
name|String
name|DIVISION_BY_ZERO_MESSAGE
init|=
name|Bug
operator|.
name|FNL3_FIXED
condition|?
literal|null
else|:
literal|"(?s).*"
decl_stmt|;
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "String right truncation: PC=0 Code=22001"
specifier|public
specifier|static
specifier|final
name|String
name|STRING_TRUNC_MESSAGE
init|=
name|Bug
operator|.
name|FNL3_FIXED
condition|?
literal|null
else|:
literal|"(?s).*"
decl_stmt|;
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "Invalid datetime format: PC=0 Code=22007"
specifier|public
specifier|static
specifier|final
name|String
name|BAD_DATETIME_MESSAGE
init|=
name|Bug
operator|.
name|FNL3_FIXED
condition|?
literal|null
else|:
literal|"(?s).*"
decl_stmt|;
comment|// Error messages when an invalid time unit is given as
comment|// input to extract for a particular input type.
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
init|=
literal|"Extract.*from.*type data is not supported"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
init|=
literal|"Cannot apply 'EXTRACT' to arguments of type .*'\n.*"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LITERAL_OUT_OF_RANGE_MESSAGE
init|=
literal|"(?s).*Numeric literal.*out of range.*"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INVALID_ARGUMENTS_NUMBER
init|=
literal|"Invalid number of arguments to function .* Was expecting .* arguments"
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
name|String
index|[]
name|NUMERIC_TYPE_NAMES
init|=
block|{
literal|"TINYINT"
block|,
literal|"SMALLINT"
block|,
literal|"INTEGER"
block|,
literal|"BIGINT"
block|,
literal|"DECIMAL(5, 2)"
block|,
literal|"REAL"
block|,
literal|"FLOAT"
block|,
literal|"DOUBLE"
block|}
decl_stmt|;
comment|// REVIEW jvs 27-Apr-2006:  for Float and Double, MIN_VALUE
comment|// is the smallest positive value, not the smallest negative value
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|MIN_NUMERIC_STRINGS
init|=
block|{
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MIN_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MIN_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
block|,
literal|"-999.99"
block|,
comment|// NOTE jvs 26-Apr-2006:  Win32 takes smaller values from win32_values.h
literal|"1E-37"
block|,
comment|/*Float.toString(Float.MIN_VALUE)*/
literal|"2E-307"
block|,
comment|/*Double.toString(Double.MIN_VALUE)*/
literal|"2E-307"
comment|/*Double.toString(Double.MIN_VALUE)*/
block|,   }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|MIN_OVERFLOW_NUMERIC_STRINGS
init|=
block|{
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
block|,
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
block|,
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
block|,
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
block|,
literal|"-1000.00"
block|,
literal|"1e-46"
block|,
literal|"1e-324"
block|,
literal|"1e-324"
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|MAX_NUMERIC_STRINGS
init|=
block|{
name|Long
operator|.
name|toString
argument_list|(
name|Byte
operator|.
name|MAX_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
block|,
literal|"999.99"
block|,
comment|// NOTE jvs 26-Apr-2006:  use something slightly less than MAX_VALUE
comment|// because roundtripping string to approx to string doesn't preserve
comment|// MAX_VALUE on win32
literal|"3.4028234E38"
block|,
comment|/*Float.toString(Float.MAX_VALUE)*/
literal|"1.79769313486231E308"
block|,
comment|/*Double.toString(Double.MAX_VALUE)*/
literal|"1.79769313486231E308"
comment|/*Double.toString(Double.MAX_VALUE)*/
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|MAX_OVERFLOW_NUMERIC_STRINGS
init|=
block|{
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
block|,
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
block|,
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
block|,
operator|(
operator|new
name|BigDecimal
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|)
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
block|,
literal|"1000.00"
block|,
literal|"1e39"
block|,
literal|"-1e309"
block|,
literal|"1e309"
block|}
decl_stmt|;
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
name|SqlTester
operator|.
name|VmName
name|VM_FENNEL
init|=
name|SqlTester
operator|.
name|VmName
operator|.
name|FENNEL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlTester
operator|.
name|VmName
name|VM_JAVA
init|=
name|SqlTester
operator|.
name|VmName
operator|.
name|JAVA
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlTester
operator|.
name|VmName
name|VM_EXPAND
init|=
name|SqlTester
operator|.
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
literal|"(?s).*could not calculate results for the following row.*PC=5 Code=2201F.*"
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
specifier|private
specifier|final
name|boolean
name|enable
decl_stmt|;
specifier|protected
specifier|final
name|SqlTester
name|tester
decl_stmt|;
comment|// same with tester but without implicit type coercion.
specifier|protected
specifier|final
name|SqlTester
name|strictTester
decl_stmt|;
comment|/**    * Creates a SqlOperatorBaseTest.    *    * @param enable Whether to run "failing" tests.    * @param tester Means to validate, execute various statements.    */
specifier|protected
name|SqlOperatorBaseTest
parameter_list|(
name|boolean
name|enable
parameter_list|,
name|SqlTester
name|tester
parameter_list|)
block|{
name|this
operator|.
name|enable
operator|=
name|enable
expr_stmt|;
name|this
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
assert|assert
name|tester
operator|!=
literal|null
assert|;
name|this
operator|.
name|strictTester
operator|=
name|tester
operator|.
name|enableTypeCoercion
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeEach
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|tester
operator|.
name|setFor
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SqlTester
name|oracleTester
parameter_list|()
block|{
return|return
name|libraryTester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
return|;
block|}
specifier|private
name|SqlTester
name|bigQueryTester
parameter_list|()
block|{
return|return
name|libraryTester
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
return|;
block|}
specifier|protected
name|SqlTester
name|libraryTester
parameter_list|(
name|SqlLibrary
name|library
parameter_list|)
block|{
return|return
name|tester
operator|.
name|withOperatorTable
argument_list|(
name|SqlLibraryOperatorTableFactory
operator|.
name|INSTANCE
operator|.
name|getOperatorTable
argument_list|(
name|SqlLibrary
operator|.
name|STANDARD
argument_list|,
name|library
argument_list|)
argument_list|)
operator|.
name|withConnectionFactory
argument_list|(
name|CalciteAssert
operator|.
name|EMPTY_CONNECTION_FACTORY
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|AddSchemaSpecPostProcessor
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
name|FUN
argument_list|,
name|library
operator|.
name|fun
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|SqlTester
name|oracleTester
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
if|if
condition|(
name|conformance
operator|==
literal|null
condition|)
block|{
name|conformance
operator|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
expr_stmt|;
block|}
return|return
name|tester
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|SqlLibraryOperatorTableFactory
operator|.
name|INSTANCE
operator|.
name|getOperatorTable
argument_list|(
name|SqlLibrary
operator|.
name|STANDARD
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
argument_list|)
operator|.
name|withConnectionFactory
argument_list|(
name|CalciteAssert
operator|.
name|EMPTY_CONNECTION_FACTORY
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|AddSchemaSpecPostProcessor
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
literal|"fun"
argument_list|,
literal|"oracle"
argument_list|)
operator|.
name|with
argument_list|(
literal|"conformance"
argument_list|,
name|conformance
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates a tester with special sql library. */
specifier|protected
name|SqlTester
name|tester
parameter_list|(
name|SqlLibrary
name|library
parameter_list|)
block|{
return|return
name|tester
operator|.
name|withOperatorTable
argument_list|(
name|SqlLibraryOperatorTableFactory
operator|.
name|INSTANCE
operator|.
name|getOperatorTable
argument_list|(
name|SqlLibrary
operator|.
name|STANDARD
argument_list|,
name|library
argument_list|)
argument_list|)
operator|.
name|withConnectionFactory
argument_list|(
name|CalciteAssert
operator|.
name|EMPTY_CONNECTION_FACTORY
operator|.
name|with
argument_list|(
operator|new
name|CalciteAssert
operator|.
name|AddSchemaSpecPostProcessor
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
literal|"fun"
argument_list|,
name|library
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|BETWEEN
argument_list|,
name|SqlTester
operator|.
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2 between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2 between 3 and 2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2 between symmetric 3 and 2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"3 between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"4 between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 between 4 and -3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 between -1 and -3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 between -1 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 between 1 and 1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2 between 1.1 and 1.3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 2 and 3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5 between 1.6 and 1.7"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1 between 1.1 and 1.3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0 between 1.1 and 1.3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 between 2 and 3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 between 2e0 and 3e0"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e1 between 1.6e1 and 1.7e1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'' between x'' and x''"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer) between -1 and 2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"1 between -1 and cast(null as integer)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"1 between cast(null as integer) and cast(null as integer)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"1 between cast(null as integer) and 1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' between x'0A000130' and x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' between x'0A0001A0' and x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotBetween
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2 not between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"3 not between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"4 not between 1 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0 not between 1.1 and 1.3"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1 not between 1.1 and 1.3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 not between 2 and 3"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.5e0 not between 2e0 and 3e0"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' not between x'0A000130' and x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A00015A' not between x'0A0001A0' and x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getCastString
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|boolean
name|errorLoc
parameter_list|)
block|{
if|if
condition|(
name|errorLoc
condition|)
block|{
name|value
operator|=
literal|"^"
operator|+
name|value
operator|+
literal|"^"
expr_stmt|;
block|}
return|return
literal|"cast("
operator|+
name|value
operator|+
literal|" as "
operator|+
name|targetType
operator|+
literal|")"
return|;
block|}
specifier|private
name|void
name|checkCastToApproxOkay
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|double
name|expected
parameter_list|,
name|double
name|delta
parameter_list|)
block|{
name|tester
operator|.
name|checkScalarApprox
argument_list|(
name|getCastString
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|,
literal|false
argument_list|)
argument_list|,
name|targetType
operator|+
literal|" NOT NULL"
argument_list|,
name|expected
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCastToStringOkay
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkString
argument_list|(
name|getCastString
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|,
literal|false
argument_list|)
argument_list|,
name|expected
argument_list|,
name|targetType
operator|+
literal|" NOT NULL"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCastToScalarOkay
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkScalarExact
argument_list|(
name|getCastString
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|,
literal|false
argument_list|)
argument_list|,
name|targetType
operator|+
literal|" NOT NULL"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCastToScalarOkay
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|)
block|{
name|checkCastToScalarOkay
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCastFails
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
name|getCastString
argument_list|(
name|value
argument_list|,
name|targetType
argument_list|,
operator|!
name|runtime
argument_list|)
argument_list|,
name|expectedError
argument_list|,
name|runtime
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCastToString
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|String
name|spaces
init|=
literal|"     "
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|expected
operator|=
name|value
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|int
name|len
init|=
name|expected
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|getCastString
argument_list|(
name|value
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// currently no exception thrown for truncation
if|if
condition|(
name|Bug
operator|.
name|DT239_FIXED
condition|)
block|{
name|checkCastFails
argument_list|(
name|value
argument_list|,
literal|"VARCHAR("
operator|+
operator|(
name|len
operator|-
literal|1
operator|)
operator|+
literal|")"
argument_list|,
name|STRING_TRUNC_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|checkCastToStringOkay
argument_list|(
name|value
argument_list|,
literal|"VARCHAR("
operator|+
name|len
operator|+
literal|")"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|checkCastToStringOkay
argument_list|(
name|value
argument_list|,
literal|"VARCHAR("
operator|+
operator|(
name|len
operator|+
literal|5
operator|)
operator|+
literal|")"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|// currently no exception thrown for truncation
if|if
condition|(
name|Bug
operator|.
name|DT239_FIXED
condition|)
block|{
name|checkCastFails
argument_list|(
name|value
argument_list|,
literal|"CHAR("
operator|+
operator|(
name|len
operator|-
literal|1
operator|)
operator|+
literal|")"
argument_list|,
name|STRING_TRUNC_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|checkCastToStringOkay
argument_list|(
name|value
argument_list|,
literal|"CHAR("
operator|+
name|len
operator|+
literal|")"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|checkCastToStringOkay
argument_list|(
name|value
argument_list|,
literal|"CHAR("
operator|+
operator|(
name|len
operator|+
literal|5
operator|)
operator|+
literal|")"
argument_list|,
name|expected
operator|+
name|spaces
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastToString
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"cast(cast('abc' as char(4)) as varchar(6))"
argument_list|,
literal|null
argument_list|,
literal|"abc "
argument_list|)
expr_stmt|;
comment|// integer
name|checkCastToString
argument_list|(
literal|"123"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"0"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"-123"
argument_list|,
literal|"CHAR(4)"
argument_list|,
literal|"-123"
argument_list|)
expr_stmt|;
comment|// decimal
name|checkCastToString
argument_list|(
literal|"123.4"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"123.4"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"-0.0"
argument_list|,
literal|"CHAR(2)"
argument_list|,
literal|".0"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"-123.4"
argument_list|,
literal|"CHAR(6)"
argument_list|,
literal|"-123.4"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(2.523 as char(2))"
argument_list|,
name|STRING_TRUNC_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
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
name|checkCastToString
argument_list|(
literal|"1.23E45"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"1.23E45"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"CAST(0 AS DOUBLE)"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"0E0"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"-1.20e-07"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"-1.2E-7"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"cast(0e0 as varchar(5))"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"0E0"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|checkCastToString
argument_list|(
literal|"cast(-45e-2 as varchar(17))"
argument_list|,
literal|"CHAR(7)"
argument_list|,
literal|"-4.5E-1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|TODO
condition|)
block|{
name|checkCastToString
argument_list|(
literal|"cast(4683442.3432498375e0 as varchar(20))"
argument_list|,
literal|"CHAR(19)"
argument_list|,
literal|"4.683442343249838E6"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|TODO
condition|)
block|{
name|checkCastToString
argument_list|(
literal|"cast(-0.1 as real)"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"-1E-1"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(1.3243232e0 as varchar(4))"
argument_list|,
name|STRING_TRUNC_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(1.9e5 as char(4))"
argument_list|,
name|STRING_TRUNC_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// string
name|checkCastToString
argument_list|(
literal|"'abc'"
argument_list|,
literal|"CHAR(1)"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"'abc'"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"cast('abc' as varchar(6))"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"cast(' abc  ' as varchar(10))"
argument_list|,
literal|null
argument_list|,
literal|" abc  "
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"cast(cast('abc' as char(4)) as varchar(6))"
argument_list|,
literal|null
argument_list|,
literal|"abc "
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|checkCastToString
argument_list|(
literal|"date '2008-01-01'"
argument_list|,
literal|"CHAR(10)"
argument_list|,
literal|"2008-01-01"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"time '1:2:3'"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"01:02:03"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"timestamp '2008-1-1 1:2:3'"
argument_list|,
literal|"CHAR(19)"
argument_list|,
literal|"2008-01-01 01:02:03"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"timestamp '2008-1-1 1:2:3'"
argument_list|,
literal|"VARCHAR(30)"
argument_list|,
literal|"2008-01-01 01:02:03"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"interval '3-2' year to month"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"+3-02"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"interval '32' month"
argument_list|,
literal|"CHAR(3)"
argument_list|,
literal|"+32"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"interval '1 2:3:4' day to second"
argument_list|,
literal|"CHAR(11)"
argument_list|,
literal|"+1 02:03:04"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"interval '1234.56' second(4,2)"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"+1234.56"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"interval '60' day"
argument_list|,
literal|"CHAR(8)"
argument_list|,
literal|"+60     "
argument_list|)
expr_stmt|;
comment|// boolean
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"CHAR(4)"
argument_list|,
literal|"TRUE"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"CHAR(6)"
argument_list|,
literal|"TRUE  "
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"True"
argument_list|,
literal|"VARCHAR(6)"
argument_list|,
literal|"TRUE"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"False"
argument_list|,
literal|"CHAR(5)"
argument_list|,
literal|"FALSE"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(true as char(3))"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(false as char(4))"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(true as varchar(3))"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(false as varchar(4))"
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testCastExactNumericLimits
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
comment|// Test casting for min,max, out of range for exact numeric types
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMERIC_TYPE_NAMES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|type
init|=
name|NUMERIC_TYPE_NAMES
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"DOUBLE"
argument_list|)
operator|||
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"FLOAT"
argument_list|)
operator|||
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"REAL"
argument_list|)
condition|)
block|{
comment|// Skip approx types
continue|continue;
block|}
comment|// Convert from literal to type
name|checkCastToScalarOkay
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|)
expr_stmt|;
comment|// Overflow test
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"BIGINT"
argument_list|)
condition|)
block|{
comment|// Literal of range
name|checkCastFails
argument_list|(
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkCastFails
argument_list|(
name|MIN_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
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
name|checkCastFails
argument_list|(
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkCastFails
argument_list|(
name|MIN_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Convert from string to type
name|checkCastToScalarOkay
argument_list|(
literal|"'"
operator|+
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"'"
operator|+
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|MIN_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// Convert from type to string
name|checkCastToString
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|checkCastFails
argument_list|(
literal|"'notnumeric'"
argument_list|,
name|type
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
name|void
name|testCastToExactNumeric
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1"
argument_list|,
literal|"DECIMAL(4, 0)"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1"
argument_list|,
literal|"DECIMAL(4, 0)"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.234E3"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-9.99E2"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-999"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"'1'"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"' 01 '"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"'-1'"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"' -00 '"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// string to integer
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast('6543' as integer)"
argument_list|,
literal|"6543"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast(' -123 ' as int)"
argument_list|,
literal|"-123"
argument_list|)
expr_stmt|;
name|tester
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
name|Test
name|void
name|testCastStringToDecimal
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Test
name|void
name|testCastIntervalToNumeric
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
comment|// interval to decimal
if|if
condition|(
name|DECIMAL
condition|)
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Test
name|void
name|testCastToInterval
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Test
name|void
name|testCastIntervalToInterval
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Test
name|void
name|testCastWithRoundingToScalar
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.25"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.25E0"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|checkCastToScalarOkay
argument_list|(
literal|"1.5"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"5E-1"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.75"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.75E0"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.25"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.25E0"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.5"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-5E-1"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.75"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.75E0"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.23454"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"1.2345"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.23454E0"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"1.2345"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.23455"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"1.2346"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"5E-5"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"0.0001"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.99995"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"2.0000"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"1.99995E0"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"2.0000"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.23454"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-1.2345"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.23454E0"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-1.2345"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.23455"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-1.2346"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-5E-5"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-0.0001"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.99995"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-2.0000"
argument_list|)
expr_stmt|;
name|checkCastToScalarOkay
argument_list|(
literal|"-1.99995E0"
argument_list|,
literal|"DECIMAL(8, 4)"
argument_list|,
literal|"-2.0000"
argument_list|)
expr_stmt|;
comment|// 9.99 round to 10.0, should give out of range error
name|tester
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
name|Test
name|void
name|testCastDecimalToDoubleToInteger
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(1.25 as double) as integer)"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(-1.25 as double) as integer)"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(1.75 as double) as integer)"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(-1.75 as double) as integer)"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(1.5 as double) as integer)"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cast( cast(-1.5 as double) as integer)"
argument_list|,
literal|"-2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastApproxNumericLimits
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
comment|// Test casting for min,max, out of range for approx numeric types
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMERIC_TYPE_NAMES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|type
init|=
name|NUMERIC_TYPE_NAMES
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|isFloat
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"DOUBLE"
argument_list|)
operator|||
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"FLOAT"
argument_list|)
condition|)
block|{
name|isFloat
operator|=
literal|false
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"REAL"
argument_list|)
condition|)
block|{
name|isFloat
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// Skip non-approx types
continue|continue;
block|}
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// Convert from literal to type
name|checkCastToApproxOkay
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
argument_list|,
name|isFloat
condition|?
literal|1E32
else|:
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFloat
condition|)
block|{
name|checkCastFails
argument_list|(
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Double: Literal out of range
name|checkCastFails
argument_list|(
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|LITERAL_OUT_OF_RANGE_MESSAGE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Underflow: goes to 0
name|checkCastToApproxOkay
argument_list|(
name|MIN_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Convert from string to type
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
argument_list|,
name|isFloat
condition|?
literal|1E32
else|:
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastFails
argument_list|(
literal|"'"
operator|+
name|MAX_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Underflow: goes to 0
name|checkCastToApproxOkay
argument_list|(
literal|"'"
operator|+
name|MIN_OVERFLOW_NUMERIC_STRINGS
index|[
name|i
index|]
operator|+
literal|"'"
argument_list|,
name|type
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Convert from type to string
comment|// Treated as DOUBLE
name|checkCastToString
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
name|isFloat
condition|?
literal|null
else|:
literal|"1.79769313486231E308"
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
name|checkCastToString
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
comment|// Treated as DOUBLE
name|isFloat
condition|?
literal|"3.402824E38"
else|:
literal|"1.797693134862316E308"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
comment|// Treated as FLOAT or DOUBLE
name|isFloat
condition|?
literal|null
else|:
literal|"4.940656458412465E-324"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
literal|"1.401299E-45"
else|:
literal|"4.940656458412465E-324"
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
name|checkCastToString
argument_list|(
name|MAX_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
comment|// Treated as DOUBLE
name|isFloat
condition|?
literal|"3.402823E38"
else|:
literal|"1.797693134862316E308"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
name|isFloat
condition|?
literal|null
else|:
literal|null
argument_list|)
expr_stmt|;
comment|// Treated as FLOAT or DOUBLE
name|checkCastToString
argument_list|(
name|MIN_NUMERIC_STRINGS
index|[
name|i
index|]
argument_list|,
name|type
argument_list|,
name|isFloat
condition|?
literal|"1.401298E-45"
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
name|checkCastFails
argument_list|(
literal|"'notnumeric'"
argument_list|,
name|type
argument_list|,
name|INVALID_CHAR_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testCastToApproxNumeric
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"1"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"1.0"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"-2.3"
argument_list|,
literal|"FLOAT"
argument_list|,
operator|-
literal|2.3
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"'1'"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"'  -1e-37  '"
argument_list|,
literal|"DOUBLE"
argument_list|,
operator|-
literal|1e-37
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"1e0"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkCastToApproxOkay
argument_list|(
literal|"0e0"
argument_list|,
literal|"REAL"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastNull
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
comment|// null
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as decimal(4,3))"
argument_list|)
expr_stmt|;
block|}
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as double)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as varchar(10))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as char(10))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as date)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as time)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as timestamp)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval year to month)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval day to second(3))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean)"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1439">[CALCITE-1439]    * Handling errors during constant reduction</a>. */
annotation|@
name|Test
name|void
name|testCastInvalid
parameter_list|()
block|{
comment|// Before CALCITE-1439 was fixed, constant reduction would kick in and
comment|// generate Java constants that throw when the class is loaded, thus
comment|// ExceptionInInitializerError.
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
annotation|@
name|Test
name|void
name|testCastDateTime
parameter_list|()
block|{
comment|// Test cast for date/time/timestamp
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|enable
condition|)
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|checkCastToString
argument_list|(
literal|"TIME '12:42:25'"
argument_list|,
literal|null
argument_list|,
literal|"12:42:25"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|checkCastToString
argument_list|(
literal|"TIME '12:42:25.34'"
argument_list|,
literal|null
argument_list|,
literal|"12:42:25.34"
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Test
name|void
name|testCastStringToDateTime
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1241241' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('12:54:78' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('12:34:5' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('12:3:45' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1:23:45' as TIME)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// timestamp<-> string
name|checkCastToString
argument_list|(
literal|"TIMESTAMP '1945-02-24 12:42:25'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24 12:42:25"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
comment|// TODO: casting allows one to discard precision without error
name|checkCastToString
argument_list|(
literal|"TIMESTAMP '1945-02-24 12:42:25.34'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24 12:42:25.34"
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1241241' as TIMESTAMP)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1945-20-24 12:42:25.34' as TIMESTAMP)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1945-01-24 25:42:25.34' as TIMESTAMP)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1945-1-24 12:23:34.454' as TIMESTAMP)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// date<-> string
name|checkCastToString
argument_list|(
literal|"DATE '1945-02-24'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24"
argument_list|)
expr_stmt|;
name|checkCastToString
argument_list|(
literal|"DATE '1945-2-24'"
argument_list|,
literal|null
argument_list|,
literal|"1945-02-24"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('52534253' as DATE)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast('1945-30-24' as DATE)"
argument_list|,
name|BAD_DATETIME_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// cast null
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as date)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as timestamp)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as time)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as time)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as date)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as varchar(10)) as timestamp)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as date) as timestamp)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as time) as timestamp)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as timestamp) as date)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(cast(null as timestamp) as time)"
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
name|Test
name|void
name|testCastToBoolean
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
comment|// string to boolean
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('true' as boolean)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('false' as boolean)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  trUe' as boolean)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  tr' || 'Ue' as boolean)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('  fALse' as boolean)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(cast('true' as varchar(10))  as boolean)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(cast('false' as varchar(10)) as boolean)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
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
annotation|@
name|Test
name|void
name|testCase
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'='a' then 1 end"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case 2 when 1 then 11.2 when 2 then 4.543 else null end"
argument_list|,
literal|"DECIMAL(5, 3)"
argument_list|,
literal|"4.543"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case 1 when 1 then 11.2 when 2 then 4.543 else null end"
argument_list|,
literal|"DECIMAL(5, 3)"
argument_list|,
literal|"11.200"
argument_list|)
expr_stmt|;
block|}
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case 'a' when 'a' then 1 end"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 when 2 then cast(4 as bigint) else 3 end"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|11.2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 when 2 then 4 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|11.2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 2 when 1 then 11.2e0 when 2 then 4 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|4
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 1 when 1 then 11.2e0 when 2 then 4.543 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|11.2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"case 2 when 1 then 11.2e0 when 2 then 4.543 else null end"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|4.543
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"case cast(null as int) when cast(null as int) then 'nulls match' else 'nulls do not match' end"
argument_list|,
literal|"nulls do not match"
argument_list|,
literal|"CHAR(18) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'=cast(null as varchar(1)) then 1 else 2 end"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// equivalent to "nullif('a',cast(null as varchar(1)))"
name|tester
operator|.
name|checkString
argument_list|(
literal|"case when 'a' = cast(null as varchar(1)) then null else 'a' end"
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"case 1 when 1 then row('a','b') when 2 then row('ab','cd') end"
argument_list|,
literal|"ROW(CHAR(2) NOT NULL, CHAR(2) NOT NULL)"
argument_list|,
literal|"row('a ','b ')"
argument_list|)
expr_stmt|;
block|}
comment|// multiple values in some cases (introduced in SQL:2011)
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|SqlTester
name|tester2
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|tester2
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
name|tester2
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
name|tester2
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
name|tester2
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
name|tester2
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 'a'=cast(null as varchar(1)) then 1 else 2 end"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// equivalent to "nullif('a',cast(null as varchar(1)))"
name|tester2
operator|.
name|checkString
argument_list|(
literal|"case when 'a' = cast(null as varchar(1)) then null else 'a' end"
argument_list|,
literal|"a"
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// multiple values in some cases (introduced in SQL:2011)
name|tester2
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
name|tester2
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
name|tester2
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
name|tester2
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"case when 1 = 1 then 10 else null end"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"case 1 when 1 then current_timestamp else null end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"case 1 when 1 then current_timestamp else current_timestamp end"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"case when true then current_timestamp else null end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"case when true then current_timestamp end"
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
operator|new
name|SqlJdbcFunctionCall
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
comment|// There follows one test for each function in appendix C of the JDBC
comment|// 3.0 specification. The test is 'if-false'd out if the function is
comment|// not implemented or is broken.
comment|// Numeric Functions
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ACOS(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.36943
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ASIN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.20135
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ATAN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.19739
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn ATAN2(-2, 2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
operator|-
literal|0.78539
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn COS(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.98007
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn COT(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|4.93315
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn DEGREES(-1)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
operator|-
literal|57.29578
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn EXP(2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|7.389
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn LOG(10)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.30258
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn LOG10(100)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn PI()}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|3.14159
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn POWER(2, 3)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|8.0
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn RADIANS(90)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.57080
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn RAND(42)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.63708
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn SIN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.19867
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn SQRT(4.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.04939
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"{fn TAN(0.2)}"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.20271
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn ASCII(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
condition|)
block|{
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"{fn CHAR(code)}"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn DIFFERENCE('muller', cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REVERSE(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn LEFT(cast(null as varchar(1)), 3)}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn RIGHT(cast(null as varchar(1)), 3)}"
argument_list|)
expr_stmt|;
comment|// REVIEW: is this result correct? I think it should be "abcCdef"
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REPEAT('abc', cast(null as integer))}"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REPEAT(cast(null as varchar(1)), cast(null as integer))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE(cast(null as varchar(5)), 'ciao', '')}"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE('ciao', cast(null as varchar(3)), 'zz')}"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn REPLACE('ciao', 'bella', cast(null as varchar(3)))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn SOUNDEX(cast(null as varchar(1)))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"{fn SPACE(cast(null as integer))}"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"{fn CURDATE()}"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"{fn CURTIME()}"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"{fn DAYOFWEEK(DATE '2014-12-10')}"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"{fn DAYOFYEAR(DATE '2014-12-10')}"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"{fn NOW()}"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"{fn WEEK(DATE '2014-12-10')}"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"{fn DATABASE()}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|testChr
parameter_list|()
block|{
name|tester
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
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester
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
block|}
annotation|@
name|Test
name|void
name|testSelect
parameter_list|()
block|{
name|tester
operator|.
name|check
argument_list|(
literal|"select * from (values(1))"
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
literal|"1"
argument_list|,
literal|0
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
operator|||
operator|(
name|getClass
argument_list|()
operator|!=
name|SqlOperatorTest
operator|.
name|class
operator|)
operator|&&
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
name|tester
operator|.
name|checkType
argument_list|(
literal|"SELECT *,(SELECT * FROM (VALUES(1))) FROM (VALUES(2))"
argument_list|,
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"SELECT *,(SELECT * FROM (VALUES(CAST(10 as BIGINT)))) "
operator|+
literal|"FROM (VALUES(CAST(10 as bigint)))"
argument_list|,
literal|"RecordType(BIGINT NOT NULL EXPR$0, BIGINT EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|" SELECT *,(SELECT * FROM (VALUES(10.5))) FROM (VALUES(10.5))"
argument_list|,
literal|"RecordType(DECIMAL(3, 1) NOT NULL EXPR$0, DECIMAL(3, 1) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"SELECT *,(SELECT * FROM (VALUES('this is a char'))) "
operator|+
literal|"FROM (VALUES('this is a char too'))"
argument_list|,
literal|"RecordType(CHAR(18) NOT NULL EXPR$0, CHAR(14) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"SELECT *,(SELECT * FROM (VALUES(true))) FROM (values(false))"
argument_list|,
literal|"RecordType(BOOLEAN NOT NULL EXPR$0, BOOLEAN EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|" SELECT *,(SELECT * FROM (VALUES(cast('abcd' as varchar(10))))) "
operator|+
literal|"FROM (VALUES(CAST('abcd' as varchar(10))))"
argument_list|,
literal|"RecordType(VARCHAR(10) NOT NULL EXPR$0, VARCHAR(10) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"SELECT *,"
operator|+
literal|"  (SELECT * FROM (VALUES(TIMESTAMP '2006-01-01 12:00:05'))) "
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
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"'buttered'\n' toast'"
argument_list|,
literal|"buttered toast"
argument_list|,
literal|"CHAR(14) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"'corned'\n' beef'\n' on'\n' rye'"
argument_list|,
literal|"corned beef on rye"
argument_list|,
literal|"CHAR(18) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"_latin1'Spaghetti'\n' all''Amatriciana'"
argument_list|,
literal|"Spaghetti all'Amatriciana"
argument_list|,
literal|"CHAR(25) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'1234'\n'abcd' = x'1234abcd'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'1234'\n'' = x'1234'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x''\n'ab' = x'ab'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testComplexLiteral
parameter_list|()
block|{
name|tester
operator|.
name|check
argument_list|(
literal|"select 2 * 2 * x from (select 2 as x)"
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|,
literal|"8"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|check
argument_list|(
literal|"select 1 * 2 * 3 * x from (select 2 as x)"
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|,
literal|"12"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|check
argument_list|(
literal|"select 1 + 2 + 3 + 4 + x from (select 2 as x)"
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
argument_list|,
literal|"12"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRow
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true and false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true and true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) and false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false and cast(null as boolean)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean) and true"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true and (not false)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOperator2
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then unknown else true end and true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then cast(null as boolean) else true end and true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"case when false then null else true end and true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOperatorLazy
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|)
expr_stmt|;
comment|// lazy eval returns FALSE;
comment|// eager eval executes RHS of AND and throws;
comment|// both are valid
name|tester
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
name|Boolean
operator|.
name|FALSE
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|" 'a' || cast(null as char(2)) "
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|" cast(null as char(2)) || 'b' "
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|" cast(null as char(1)) || cast(null as char(2)) "
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"x'ff' || cast(null as varbinary)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|" cast(null as ANY) || cast(null as ANY) "
argument_list|)
expr_stmt|;
name|tester
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
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
specifier|final
name|SqlTester
name|tester2
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
specifier|final
name|SqlTester
name|tester3
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlTester
name|sqlTester
range|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|tester1
argument_list|,
name|tester2
argument_list|)
control|)
block|{
name|sqlTester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CONCAT_FUNCTION
argument_list|)
expr_stmt|;
name|sqlTester
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
name|sqlTester
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
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"concat('a', 'b', cast(null as char(2)))"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"concat(cast(null as ANY), 'b', cast(null as char(2)))"
argument_list|)
expr_stmt|;
name|sqlTester
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
name|sqlTester
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
name|sqlTester
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
name|tester3
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|CONCAT2
argument_list|)
expr_stmt|;
name|tester3
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
name|tester3
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
name|tester3
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
name|tester3
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
name|tester3
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
name|tester3
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
name|tester3
operator|.
name|checkNull
argument_list|(
literal|"concat('a', cast(null as varchar))"
argument_list|)
expr_stmt|;
name|tester3
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
name|tester3
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
comment|// "%" is allowed under MYSQL_5 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENT_REMAINDER
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"4%2"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"8%5"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"-12%7"
argument_list|,
literal|"-5"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"-12%-7"
argument_list|,
literal|"-5"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"12%-7"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
annotation|@
name|Test
name|void
name|testModPrecedence
parameter_list|()
block|{
comment|// "%" is allowed under MYSQL_5 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PERCENT_REMAINDER
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 5 % 3 % 4 * 14 % 17"
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"(1 + 5 % 3) % 4 + 14 % 17"
argument_list|,
literal|"17"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModOperatorNull
parameter_list|()
block|{
comment|// "%" is allowed under MYSQL_5 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer) % 2"
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"4 % cast(null as decimal(12,0))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testModOperatorDivByZero
parameter_list|()
block|{
comment|// "%" is allowed under MYSQL_5 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
comment|// The extra CASE expression is to fool Janino.  It does constant
comment|// reduction and will throw the divide by zero exception while
comment|// compiling the expression.  The test frame work would then issue
comment|// unexpected exception occurred during "validation".  You cannot
comment|// submit as non-runtime because the janino exception does not have
comment|// error position information and the framework is unhappy with that.
name|tester1
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DIVIDE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|" cast(10.0 as double) / 5"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|" cast(10.0 as real) / 4"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
literal|2.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|" 6.0 / cast(10.0 as real) "
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.6
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"1e1 / cast(null as float)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
operator|.
name|checkFails
argument_list|(
literal|"100.1 / 0.00000000000000001"
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
name|testDivideOperatorIntervals
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"interval '2' day / cast(null as bigint)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval month) / 2"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1=1.0"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.34=1.34"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1=1.34"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1e2=100e0"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1e2=101"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1e2 as real)=cast(101 as bigint)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a'='b'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true = true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true = false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false = true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false = false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('a' as varchar(30))"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a ' as varchar(30))=cast('a' as varchar(30))"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(' a' as varchar(30))=cast(' a' as varchar(30))"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a ' as varchar(15))=cast('a ' as varchar(30))"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(' ' as varchar(3))=cast(' ' as varchar(2))"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('abcd' as varchar(2))='ab'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('b' as varchar(30))"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast('a' as varchar(30))=cast('a' as varchar(15))"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as boolean)=cast(null as boolean)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)=1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day = interval '1' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day = interval '2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2:2:2' hour to second = interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1>2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(-1 as TINYINT)>cast(1 as TINYINT)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1 as SMALLINT)>cast(1 as SMALLINT)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2>1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>1.2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1>-1.2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>1.1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1e1>1.2e1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(-1.1 as real)> cast(-1.2 as real)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1e2>1.1e2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e0>1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.2e0 as real)>1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true>false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true>true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false>false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false>true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"3.0>cast(null as double)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"DATE '2013-02-23'> DATE '1945-02-24'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"DATE '2013-02-23'> CAST(NULL AS DATE)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'>x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOperatorIntervals
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '1' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '5' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second> interval '2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '-2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day> interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute> interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second> interval '2' minute"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)> interval '2' minute"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 1.0"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is distinct from 2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is distinct from 2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is distinct from cast(null as integer)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is distinct from 1.23"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is distinct from 5.23"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-23e0 is distinct from -2.3e1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// IS DISTINCT FROM not implemented for ROW yet
if|if
condition|(
literal|false
condition|)
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is distinct from row(1,1)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day is distinct from interval '1' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '10' hour is distinct from interval '10' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotDistinctFromOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 1.0"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 is not distinct from 2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is not distinct from 2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) is not distinct from cast(null as integer)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is not distinct from 1.23"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.23 is not distinct from 5.23"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-23e0 is not distinct from -2.3e1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// IS NOT DISTINCT FROM not implemented for ROW yet
if|if
condition|(
literal|false
condition|)
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"row(1,1) is not distinct from row(1,1)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day is not distinct from interval '1' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '10' hour is not distinct from interval '10' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOrEqualOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1>=2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1>=1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1>=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2>=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>=1.2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1>=-1.2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1>=1.1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e4>=1e5"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e4>=cast(1e5 as real)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2>=cast(1e5 as double)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"120000>=cast(1e5 as real)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true>=false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true>=true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false>=false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false>=true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as real)>=999"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'>=x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A0001B0'>=x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreaterThanOrEqualOperatorIntervals
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '1' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '5' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second>= interval '2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '-2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day>= interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute>= interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second>= interval '2' minute"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)>= interval '2' minute"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 in (0, 1, 2)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"3 in (0, 1, 2)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) in (0, 1, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
comment|// AND has lower precedence than IN
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 not in (0, 1, 2)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) not in (0, 1, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as integer) not in (0, null, 2)"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '1-2-3') overlaps (date '1-2-3', interval '1' year)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '1-2-3') overlaps (date '4-5-6', interval '1' year)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1-2-3', date '4-5-6') overlaps (date '2-2-3', date '3-4-5')"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(cast(null as date), date '1-2-3') overlaps (date '1-2-3', interval '1' year)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(date '1-2-3', date '1-2-3') overlaps (date '1-2-3', cast(null as date))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) overlaps (time '23:59:59', time '1:2:3')"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) overlaps (time '23:59:59', time '1:2:2')"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(time '1:2:3', interval '1' second) overlaps (time '23:59:59', interval '2' hour)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(time '1:2:3', cast(null as time)) overlaps (time '23:59:59', time '1:2:3')"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(time '1:2:3', interval '1' second) overlaps (time '23:59:59', cast(null as interval hour))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) overlaps (timestamp '1-2-3 4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) overlaps (timestamp '2-2-3 4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', cast(null as interval day) ) overlaps (timestamp '1-2-3 4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) overlaps (cast(null as timestamp), interval '1 2:3:4.5' day to second)"
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
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|times
argument_list|)
argument_list|)
expr_stmt|;
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|dates
argument_list|)
argument_list|)
expr_stmt|;
name|checkOverlaps
argument_list|(
operator|new
name|OverlapChecker
argument_list|(
name|timestamps
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1<1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2<1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<1.2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1<-1.2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<1.1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<1.1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<cast(1.2 as real)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1e-1<-1.2e-1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(1.1 as real)<cast(1.1 as double)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true<false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true<true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false<false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false<true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"123<cast(null as bigint)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint)<123"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<1.32"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'<x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '1' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '5' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second< interval '2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '-2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day< interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute< interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second< interval '2' minute"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)< interval '2' minute"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute< cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOrEqualOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<=2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1<=1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"2<=1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<=1.2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"-1.1<=-1.2"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1<=1.1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2<=1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<=cast(1e2 as real)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1000<=cast(1e2 as real)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1<=1e2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.2e1<=cast(1e2 as real)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true<=false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true<=true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false<=false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false<=true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as real)<=cast(1 as real)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<=3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"3<=cast(null as smallint)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as integer)<=1.32"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A000130'<=x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"x'0A0001B0'<=x'0A0001B0'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLessThanOrEqualOperatorInterval
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '1' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '5' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2 2:2:2' day to second<= interval '2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '2' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '-2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<= interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' minute<= interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' second<= interval '2' minute"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval hour)<= interval '2' minute"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"interval '2:2' hour to minute<= cast(null as interval second)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinusOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"-2-1"
argument_list|,
literal|"-3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"-2-1-5"
argument_list|,
literal|"-8"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"2-1"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as double) -1"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(1 as smallint)-cast(2.0 as real)"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"2.4-cast(2.0 as real)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.4
argument_list|,
literal|0.00000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"1-2"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"1e1-cast(null as double)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as interval day) + interval '2' hour"
argument_list|)
expr_stmt|;
comment|// Datetime minus interval
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01' - interval '-4 2:4' day to minute"
argument_list|,
literal|"2003-08-06 14:58:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Datetime minus year-month interval
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS_DATE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"(timestamp '2004-05-01 12:03:34' - timestamp '2004-04-29 11:57:23') day to second"
argument_list|,
literal|"+2 00:06:11.000000"
argument_list|,
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"(timestamp '2004-05-01 12:03:34' - timestamp '2004-04-29 11:57:23') day to hour"
argument_list|,
literal|"+2 00"
argument_list|,
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"(cast(null as date) - date '2003-12-01') day"
argument_list|)
expr_stmt|;
comment|// combine '<datetime> +<interval>' with '<datetime> -<datetime>'
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(date '1969-04-29' +"
operator|+
literal|" (CURRENT_DATE - "
operator|+
literal|"  date '1969-04-29') day / 2) is not null"
argument_list|,
name|Boolean
operator|.
name|TRUE
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"2*3"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"2*-3"
argument_list|,
literal|"-6"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"+2*3"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"2*0"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as float)*3"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"3*cast(2.0 as real)"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
literal|6
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cast(2.0 as real)*3.2"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|6.4
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(1 as real)*cast(null as real)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"2e-3*cast(null as integer)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"interval '2' day * cast(null as bigint)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|">"
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|"<"
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|"<="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|">="
argument_list|)
expr_stmt|;
name|checkNullOperand
argument_list|(
name|tester
argument_list|,
literal|"<>"
argument_list|)
expr_stmt|;
comment|// "!=" is allowed under ORACLE_10 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
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
name|tester1
argument_list|,
literal|"<>"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkNullOperand
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|String
name|op
parameter_list|)
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1<>1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a'<>'A'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1e0<>1e1"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"'a'<>cast(null as varchar(1))"
argument_list|)
expr_stmt|;
comment|// "!=" is not an acceptable alternative to "<>" under default SQL conformance level
name|tester
operator|.
name|checkFails
argument_list|(
literal|"1 != 1"
argument_list|,
literal|"Bang equal '!=' is not allowed under the current SQL conformance level"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// "!=" is allowed under ORACLE_10 SQL conformance level
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"1<> 1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"1 != 1"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"1 != null"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotEqualsOperatorIntervals
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<> interval '1' day"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2' day<> interval '2' day"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"interval '2:2:2' hour to second<> interval '2' hour"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true or false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false or false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true or cast(null as boolean)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|)
expr_stmt|;
comment|// need to evaluate 2nd argument if first evaluates to null, therefore
comment|// get error
name|tester
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
name|tester
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
name|Boolean
operator|.
name|TRUE
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1< cast(null as integer) or sqrt(4) = 2"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPlusOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"1+2"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"-1+2"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"1+2+3"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"1+cast(2.0 as double)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"1+cast(2.0 as double)+cast(6.0 as float)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|9
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"19.68 + cast(4.2 as float)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|23.88
argument_list|,
literal|0.02
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as tinyint)+1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkFails
argument_list|(
literal|"cast(-5e18 as decimal(19,0)) + cast(-5e18 as decimal(19,0))"
argument_list|,
name|OUT_OF_RANGE_MESSAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"interval '2' day + interval '5' minute + interval '-3' second"
argument_list|,
literal|"+2 00:04:57.000000"
argument_list|,
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"interval '2' year + cast(null as interval month)"
argument_list|)
expr_stmt|;
comment|// Datetime plus interval
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01' + interval '-4 2:4' day to minute"
argument_list|,
literal|"2003-07-29 10:50:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Datetime plus year-to-month interval
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestamp '2003-08-02 12:54:01' + interval '5-3' year to month"
argument_list|,
literal|"2008-11-02 12:54:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"interval '5-3' year to month + timestamp '2003-08-02 12:54:01'"
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is not null"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not null"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNullOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is null"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is null"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotTrueOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is not true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is not true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"select ^'a string' is not true^ from (values (1))"
argument_list|,
literal|"(?s)Cannot apply 'IS NOT TRUE' to arguments of type '<CHAR\\(8\\)> IS NOT TRUE'. Supported form\\(s\\): '<BOOLEAN> IS NOT TRUE'.*"
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is true"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotFalseOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is not false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is not false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsFalseOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is false"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotUnknownOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is not unknown"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is not unknown"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is not unknown"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"unknown is not unknown"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"false is unknown"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"true is unknown"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as boolean) is unknown"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"unknown is unknown"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 1] is a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean), cast(null as boolean)] is a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean)] is a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a'] is a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] is a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b', 'a'] is a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotASetOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is not a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 1] is not a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean), cast(null as boolean)] is not a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[cast(null as boolean)] is not a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a'] is not a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] is not a set"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b', 'a'] is not a set"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectOperator
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1,2,3,2] multiset except distinct multiset[1])"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"cardinality(multiset[1,2,3,2] multiset except all multiset[1])"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except distinct multiset[1]) submultiset of multiset[2, 3]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except distinct multiset[1]) submultiset of multiset[2, 3]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3,2] multiset except all multiset[1]) submultiset of multiset[2, 2, 3]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1,2,3] multiset except multiset[1]) is empty"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1] multiset except multiset[1]) is empty"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsEmptyOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is empty"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsNotEmptyOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] is not empty"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testExistsOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"not true"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"not false"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"not unknown"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"'a' + - 'b' + 'c'"
argument_list|,
literal|"DECIMAL(19, 19) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"-1"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"-1.0e0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"-cast(null as integer)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"+1"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"+1.0e0"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"+cast(null as integer)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|check
argument_list|(
literal|"select 'abc' from (values(true))"
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
argument_list|,
literal|"abc"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotLikeOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' not like '_b_'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' not like 'ab%'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' not like '%'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' not like '%cd%'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' not like '%cde%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeEscape
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' like 'a#_c' escape '#'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' like 'a#_c' escape '#'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a_c' like 'a\\_c' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'axc' like 'a\\_c' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a%c' like 'a\\%c' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a%cde' like 'a\\%c_e' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' like 'a%c' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbc' like 'a\\%c' escape '\\'"
argument_list|,
name|Boolean
operator|.
name|FALSE
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'x' not like 'x' escape 'x'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xyz' not like 'xyz' escape 'xyz'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeOperator
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"''  like ''"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'b'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'A'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a_'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '_a'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '%a'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like '%a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' like 'a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   like 'a_'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc'  like 'a_'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like 'a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   like '_b'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like '_d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like '%d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' like 'ab%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' like 'ab%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' like '%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' like '%cd%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' like '%cde%'"
argument_list|,
name|Boolean
operator|.
name|FALSE
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' like 'a.c'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcde' like '%c.e'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc.e' like '%c.e'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotSimilarToOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'a_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc' not similar to 'ab*c+d'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'a' || '_'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' not similar to 'ba_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as varchar(2)) not similar to 'a_'"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as varchar(3)) not similar to cast(null as char(2))"
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIMILAR_TO
argument_list|)
expr_stmt|;
comment|// like LIKE
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"''  similar to ''"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'b'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'A'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a_'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '_a'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '%a'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to '%a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   similar to 'a_'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc'  similar to 'a_'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab'   similar to '_b'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to '_d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to '%d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd' similar to 'ab%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc\ncd' similar to 'ab%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'123\n\n45\n' similar to '%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' similar to '%cd%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab\ncd\nef' similar to '%cde%'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// simple regular expressions
comment|// ab*c+d matches acd, abcd, acccd, abcccd but not abd, aabc
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'acd'    similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd'   similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'acccd'  similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcccd' similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abd'    similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc'   similar to 'ab*c+d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// compound regular expressions
comment|// x(ab|c)*y matches xy, xccy, xababcy but not xbcy
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xy'      similar to 'x(ab|c)*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xccy'    similar to 'x(ab|c)*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xababcy' similar to 'x(ab|c)*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xbcy'    similar to 'x(ab|c)*y'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// x(ab|c)+y matches xccy, xababcy but not xy, xbcy
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xy'      similar to 'x(ab|c)+y'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xccy'    similar to 'x(ab|c)+y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xababcy' similar to 'x(ab|c)+y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xbcy'    similar to 'x(ab|c)+y'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a%' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a%' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a_' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a%' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'1a' similar to '_a' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'123aXYZ' similar to '%a%'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'123aXYZ' similar to '_%_a%_' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xy' similar to '(xy)' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to '[ab][bcde]d' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'bdd' similar to '[ab][bcde]d' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to '[ab]d' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '[a-e]d' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'amy' similar to 'amy|fred' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'fred' similar to 'amy|fred' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'mike' similar to 'amy|fred' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'acd' similar to 'ab*c+d' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'accccd' similar to 'ab*c+d' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abd' similar to 'ab*c+d' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'aabc' similar to 'ab*c+d' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abb' similar to 'a(b{3})' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbb' similar to 'a(b{3})' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbb' similar to 'a(b{3})' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbb' similar to 'ab{3,6}' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abbbbbbbb' similar to 'ab{3,6}' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'' similar to 'ab?' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'ab?' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a' similar to 'a(b?)' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'ab?' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a(b?)' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abb' similar to 'ab?' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a\\_' ESCAPE '\\' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'ab' similar to 'a\\%' ESCAPE '\\' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a_' similar to 'a\\_' ESCAPE '\\' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a%' similar to 'a\\%' ESCAPE '\\' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a(b{3})' similar to 'a(b{3})' "
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a(b{3})' similar to 'a\\(b\\{3\\}\\)' ESCAPE '\\' "
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[a-ey]d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ey]d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ex-z]d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[a-ex-z]d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[x-za-e]d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to '[^a-ey]?d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yyyd' similar to '[a-ey]*d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// range must be specified in []
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd' similar to 'x-zd'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x-z'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '([a-e])d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'xy' similar to 'x*?y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x*?y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to '(x?)*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x+?y'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x?+y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x*+y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// dot is a wildcard for SIMILAR TO but not LIKE
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' similar to 'a.c'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a.c' similar to 'a.c'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' similar to 'a.*d'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abc' like 'a.c'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'a.c' like 'a.c'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'abcd' like 'a.*d'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// The following two tests throws exception(They probably should).
comment|// "Dangling meta character '*' near index 2"
if|if
condition|(
name|enable
condition|)
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x+*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to 'x?*y'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
comment|// some negative tests
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'cd' similar to '[a-e^c]d' "
argument_list|,
name|Boolean
operator|.
name|FALSE
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'y' similar to '[:ALPHA:]*'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:LOWER:]{2}[:DIGIT:]*'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]*'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]*[:DIGIT:]?'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd32' similar to '[:ALNUM:]?[:DIGIT:]*'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '([:LOWER:]{2})[:DIGIT:]{2,5}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}[:DIGIT:]{2,}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}||[:DIGIT:]{4}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd3223' similar to '[:LOWER:]{2}[:DIGIT:]{3}'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'yd  3223' similar to '[:UPPER:]{2}  [:DIGIT:]{3}'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'YD  3223' similar to '[:UPPER:]{2}  [:DIGIT:]{3}'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'YD  3223' similar to "
operator|+
literal|"'[:UPPER:]{2}||[:WHITESPACE:]*[:DIGIT:]{4}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t3223' similar to '[:UPPER:]{2}[:SPACE:]*[:DIGIT:]{4}'"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t3223' similar to "
operator|+
literal|"'[:UPPER:]{2}[:WHITESPACE:]*[:DIGIT:]{4}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'YD\t\t3223' similar to "
operator|+
literal|"'([:UPPER:]{2}[:WHITESPACE:]+)||[:DIGIT:]{4}'"
argument_list|,
name|Boolean
operator|.
name|TRUE
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
name|tester
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
name|tester
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
name|tester
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
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TRANSLATE3
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"translate(cast(null as varchar(7)), 'ab', '+-')"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"translate('aabbcc', cast(null as varchar(2)), '+-')"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"translate('aabbcc', 'ab', cast(null as varchar(2)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testOverlayFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OVERLAY
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|enable
condition|)
block|{
name|tester
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
name|enable
condition|)
block|{
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"overlay(cast(null as varchar(1)) placing 'abc' from 1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|enable
condition|)
block|{
name|tester
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
name|enable
condition|)
block|{
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"overlay(x'ABCdef' placing x'abcd' from 1 for cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"overlay(cast(null as varbinary(1)) placing x'abcd' from 1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POSITION
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abc')"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc')"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 3)"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 5)"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM 6)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('b' in 'abcabc' FROM -5)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc' FROM 3)"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('' in 'abc' FROM 10)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbcc')"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc')"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 3)"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 5)"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM 6)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'bb' in x'aabbccaabbcc' FROM -5)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'cc' in x'aabbccdd' FROM 2)"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc' FROM 3)"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position(x'' in x'aabbcc' FROM 10)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// FRG-211
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"position('tra' in 'fdgjklewrtra')"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"position(cast(null as varchar(1)) in '0010')"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"position('a' in cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|REPLACE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"REPLACE(cast(null as varchar(5)), 'ciao', '')"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"REPLACE('ciao', cast(null as varchar(3)), 'zz')"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHAR_LENGTH
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"char_length('abc')"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CHARACTER_LENGTH
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"CHARACTER_LENGTH('abc')"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
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
name|testAsciiFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ASCII
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('')"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('a')"
argument_list|,
literal|"97"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('1')"
argument_list|,
literal|"49"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('abc')"
argument_list|,
literal|"97"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII('ABC')"
argument_list|,
literal|"65"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u0082')"
argument_list|,
literal|"130"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u5B57')"
argument_list|,
literal|"23383"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"ASCII(_UTF8'\u03a9')"
argument_list|,
literal|"937"
argument_list|)
expr_stmt|;
comment|// omega
name|tester
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
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|TO_BASE64
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|FROM_BASE64
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"from_base64('-1')"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"from_base64('-100')"
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
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|MD5
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
annotation|@
name|Test
name|void
name|testSha1
parameter_list|()
block|{
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SHA1
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
annotation|@
name|Test
name|void
name|testRepeatFunc
parameter_list|()
block|{
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REPEAT
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), -1)"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), 2)"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"REPEAT('abc', cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"REPEAT(cast(null as varchar(1)), cast(null as integer))"
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
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SPACE
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|STRCMP
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"STRCMP('mytesttext', cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|tester1
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
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|SOUNDEX
argument_list|)
expr_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"SOUNDEX(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|tester1
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
annotation|@
name|Test
name|void
name|testDifferenceFunc
parameter_list|()
block|{
specifier|final
name|SqlTester
name|tester1
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|tester1
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DIFFERENCE
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('Miller', 'miller')"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('Miller', 'myller')"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'miller')"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'miller')"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'milk')"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'mile')"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'm')"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkScalarExact
argument_list|(
literal|"DIFFERENCE('muller', 'lee')"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"DIFFERENCE('muller', cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|tester1
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
name|SqlTester
name|testerMysql
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|testerMysql
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REVERSE
argument_list|)
expr_stmt|;
name|testerMysql
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
name|testerMysql
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
name|testerMysql
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
name|testerMysql
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
name|testerMysql
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
name|testerMysql
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
name|testerMysql
operator|.
name|checkNull
argument_list|(
literal|"reverse(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpperFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UPPER
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|Stream
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|tester
argument_list|)
operator|.
name|forEach
argument_list|(
name|t
lambda|->
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LEFT
argument_list|)
expr_stmt|;
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkNull
argument_list|(
literal|"left(cast(null as varchar(1)), -2)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"left('abcd', cast(null as Integer))"
argument_list|)
expr_stmt|;
comment|// test for ByteString
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkNull
argument_list|(
literal|"left(cast(null as binary(1)), -2)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"left(x'ABCdef', cast(null as Integer))"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRightFunc
parameter_list|()
block|{
name|Stream
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|tester
argument_list|)
operator|.
name|forEach
argument_list|(
name|t
lambda|->
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkNull
argument_list|(
literal|"right(cast(null as varchar(1)), -2)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"right('abcd', cast(null as Integer))"
argument_list|)
expr_stmt|;
comment|// test for ByteString
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkNull
argument_list|(
literal|"right(cast(null as binary(1)), -2)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"right(x'ABCdef', cast(null as Integer))"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRegexpReplaceFunc
parameter_list|()
block|{
name|Stream
operator|.
name|of
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|,
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|tester
argument_list|)
operator|.
name|forEach
argument_list|(
name|t
lambda|->
block|{
name|t
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|REGEXP_REPLACE
argument_list|)
expr_stmt|;
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace(cast(null as varchar), '(-)', '###')"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace('100-200', cast(null as varchar), '###')"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkNull
argument_list|(
literal|"regexp_replace('100-200', '(-)', cast(null as varchar))"
argument_list|)
expr_stmt|;
name|t
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
name|t
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
name|t
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
name|t
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
name|t
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
name|t
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
name|t
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
name|t
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
name|t
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X')"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1, 3)"
argument_list|)
expr_stmt|;
name|t
operator|.
name|checkQuery
argument_list|(
literal|"select regexp_replace('a b c', 'b', 'X', 1, 3, 'i')"
argument_list|)
expr_stmt|;
block|}
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'$.foo')"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' false on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' true on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo' unknown on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' false on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' true on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo' unknown on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{}', "
operator|+
literal|"'invalid $.foo' false on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{}', "
operator|+
literal|"'invalid $.foo' true on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo1' false on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'strict $.foo1' true on error)"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' true on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' false on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' error on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"json_exists('{\"foo\":\"bar\"}', "
operator|+
literal|"'lax $.foo1' unknown on error)"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
comment|// nulls
name|strictTester
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
name|tester
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
name|tester
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
name|void
name|testJsonValue
parameter_list|()
block|{
if|if
condition|(
literal|false
condition|)
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo1' returning integer "
operator|+
literal|"null on empty)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"json_value('{\"foo\":\"100\"}', 'strict $.foo1' returning boolean "
operator|+
literal|"null on error)"
argument_list|,
literal|null
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// lax test
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
comment|// default pathmode the default is: strict mode
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|JSON_TYPE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"json_type(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|JSON_DEPTH
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
comment|// no path context
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
comment|// no path context
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"json_object('foo': json_object('foo': 'bar'))"
argument_list|,
literal|"{\"foo\":\"{\\\"foo\\\":\\\"bar\\\"}\"}"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"json_objectagg('foo': 'bar')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"json_objectagg('foo': null)"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"json_objectagg(100: 'bar')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2)"
argument_list|,
name|values
argument_list|,
literal|"{\"foo\":\"bar\",\"foo2\":null,\"foo3\":\"bar3\"}"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2 null on null)"
argument_list|,
name|values
argument_list|,
literal|"{\"foo\":\"bar\",\"foo2\":null,\"foo3\":\"bar3\"}"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAggWithMultipleArgs
argument_list|(
literal|"json_objectagg(x: x2 absent on null)"
argument_list|,
name|values
argument_list|,
literal|"{\"foo\":\"bar\",\"foo3\":\"bar3\"}"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonValueExpressionOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"'[1, 2, 3]' format json"
argument_list|,
literal|"[1, 2, 3]"
argument_list|,
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cast(null as varchar) format json"
argument_list|)
expr_stmt|;
name|tester
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
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"json_array(json_array('foo'))"
argument_list|,
literal|"[\"[\\\"foo\\\"]\"]"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"json_arrayagg('foo')"
argument_list|,
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x)"
argument_list|,
name|values
argument_list|,
literal|"[\"foo\",\"foo3\"]"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x null on null)"
argument_list|,
name|values
argument_list|,
literal|"[\"foo\",null,\"foo3\"]"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"json_arrayagg(x absent on null)"
argument_list|,
name|values
argument_list|,
literal|"[\"foo\",\"foo3\"]"
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testJsonPredicate
parameter_list|()
block|{
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json value"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{]' is json value"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json object"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json object"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is json array"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json array"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'100' is json scalar"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is json scalar"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json value"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{]' is not json value"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json object"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is not json object"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'{}' is not json array"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'[]' is not json array"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'100' is not json scalar"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|SqlTester
name|sqlTester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"COMPRESS(NULL)"
argument_list|)
expr_stmt|;
name|sqlTester
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
name|sqlTester
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
name|sqlTester
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
name|sqlTester
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
name|sqlTester
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
name|SqlTester
name|mySqlTester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|MYSQL
argument_list|)
decl_stmt|;
name|mySqlTester
operator|.
name|checkNull
argument_list|(
literal|"ExtractValue(NULL, '//b')"
argument_list|)
expr_stmt|;
name|mySqlTester
operator|.
name|checkNull
argument_list|(
literal|"ExtractValue('', NULL)"
argument_list|)
expr_stmt|;
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|mySqlTester
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
name|SqlTester
name|sqlTester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"XMLTRANSFORM('', NULL)"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"XMLTRANSFORM(NULL,'')"
argument_list|)
expr_stmt|;
name|sqlTester
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
name|sqlTester
operator|.
name|checkFails
argument_list|(
literal|"XMLTRANSFORM('<', '<?xml version=\"1.0\"?>\n"
operator|+
literal|"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
operator|+
literal|"</xsl:stylesheet>')"
argument_list|,
literal|"Invalid input for XMLTRANSFORM xml: '.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
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
name|SqlTester
name|sqlTester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|sqlTester
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
name|sqlTester
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
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"\"EXTRACT\"('', NULL)"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"\"EXTRACT\"(NULL,'')"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"('<Article><Title>Article1</Title><Authors><Author>Foo</Author><Author>Bar"
operator|+
literal|"</Author></Authors><Body>article text"
operator|+
literal|".</Body></Article>', '/Article/Title')"
argument_list|,
literal|"<Title>Article1</Title>"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"('<Article><Title>Article1</Title><Title>Article2</Title><Authors><Author>Foo"
operator|+
literal|"</Author><Author>Bar</Author></Authors><Body>article text"
operator|+
literal|".</Body></Article>', '/Article/Title')"
argument_list|,
literal|"<Title>Article1</Title><Title>Article2</Title>"
argument_list|,
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"\"EXTRACT\"(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso"
operator|+
literal|".com/books\"><book><title>Title</title><author>Author Name</author><price>5"
operator|+
literal|".50</price></book></books>'"
operator|+
literal|", '/books:books/books:book', 'books=\"http://www.contoso.com/books\"'"
operator|+
literal|")"
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
name|SqlTester
name|sqlTester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|sqlTester
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
name|sqlTester
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
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"EXISTSNODE('', NULL)"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkNull
argument_list|(
literal|"EXISTSNODE(NULL,'')"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article><Title>Article1</Title><Authors><Author>Foo</Author><Author>Bar"
operator|+
literal|"</Author></Authors><Body>article text"
operator|+
literal|".</Body></Article>', '/Article/Title')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article><Title>Article1</Title><Authors><Author>Foo</Author><Author>Bar"
operator|+
literal|"</Author></Authors><Body>article text"
operator|+
literal|".</Body></Article>', '/Article/Title/Books')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE('<Article><Title>Article1</Title><Title>Article2</Title><Authors><Author>Foo"
operator|+
literal|"</Author><Author>Bar</Author></Authors><Body>article text"
operator|+
literal|".</Body></Article>', '/Article/Title')"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso"
operator|+
literal|".com/books\"><book><title>Title</title><author>Author Name</author><price>5"
operator|+
literal|".50</price></book></books>'"
operator|+
literal|", '/books:books/books:book', 'books=\"http://www.contoso.com/books\"'"
operator|+
literal|")"
argument_list|,
literal|"1"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sqlTester
operator|.
name|checkString
argument_list|(
literal|"EXISTSNODE(\n"
operator|+
literal|"'<books xmlns=\"http://www.contoso"
operator|+
literal|".com/books\"><book><title>Title</title><author>Author Name</author><price>5"
operator|+
literal|".50</price></book></books>'"
operator|+
literal|", '/books:books/books:book/books:title2', 'books=\"http://www.contoso.com/books\"'"
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOWER
argument_list|)
expr_stmt|;
comment|// SQL:2003 6.29.8 The type of lower is the type of its argument
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"initcap(cast(null as varchar(1)))"
argument_list|)
expr_stmt|;
comment|// dtbug 232
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^initcap(cast(null as date))^"
argument_list|,
literal|"Cannot apply 'INITCAP' to arguments of type 'INITCAP\\(<DATE>\\)'\\. Supported form\\(s\\): 'INITCAP\\(<CHARACTER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|POWER
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"power(2,-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.25
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"power(cast(null as integer),2)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"power(2,cast(null as double))"
argument_list|)
expr_stmt|;
comment|// 'pow' is an obsolete form of the 'power' function
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SQRT
argument_list|,
name|SqlTester
operator|.
name|VmName
operator|.
name|EXPAND
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sqrt(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sqrt(cast(2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sqrt(case when false then 2 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sqrt('abc')^"
argument_list|,
literal|"Cannot apply 'SQRT' to arguments of type 'SQRT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'SQRT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sqrt('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sqrt(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.4142d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sqrt(cast(2 as decimal(2, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.4142d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"sqrt(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"exp(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|7.389056
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"exp(-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.1353
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"exp(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MOD
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(4,2)"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(8,5)"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(-12,7)"
argument_list|,
literal|"-5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(-12,-7)"
argument_list|,
literal|"-5"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(12,-7)"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"mod(cast(-9 as decimal(2, 0)), cast(7 as decimal(1, 0)))"
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"mod(cast(null as integer),2)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"ln(2.71828)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"ln(2.71828)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.999999327
argument_list|,
literal|0.0000001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOG10
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(10)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(100.0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e8 as double))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|9.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|3.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"log10(cast(10e-3 as real))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
operator|-
literal|2.0
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand()"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.5
argument_list|,
literal|0.5
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.6016
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.4728
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRandIntegerFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND_INTEGER
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"rand_integer(11)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|5.0
argument_list|,
literal|5.0
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RAND_INTEGER
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
comment|/** Tests {@code UNIX_SECONDS} and other datetime functions from BigQuery. */
annotation|@
name|Test
name|void
name|testUnixSecondsFunc
parameter_list|()
block|{
name|SqlTester
name|tester
init|=
name|bigQueryTester
argument_list|()
decl_stmt|;
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|UNIX_SECONDS
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"unix_seconds(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"unix_millis(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"unix_micros(cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"timestamp_seconds(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"timestamp_millis(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"timestamp_micros(cast(null as bigint))"
argument_list|)
expr_stmt|;
name|tester
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
comment|// Have to quote the "DATE" function because we're not using the Babel
comment|// parser. In the regular parser, DATE is a reserved keyword.
name|tester
operator|.
name|checkNull
argument_list|(
literal|"\"DATE\"(null)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"\"DATE\"('1985-12-06')"
argument_list|,
literal|"1985-12-06"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAbsFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"abs(-1)"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(-9.32E-2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.0932
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(-3.5 as double))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|3.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(-3.5 as float))"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|,
literal|3.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"abs(cast(3.5 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
literal|3.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ACOS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"acos(0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"acos(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"acos(case when false then 0.5 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^acos('abc')^"
argument_list|,
literal|"Cannot apply 'ACOS' to arguments of type 'ACOS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'ACOS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"acos('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"acos(0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.0472d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"acos(cast(0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.0472d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"acos(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ASIN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"asin(0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"asin(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"asin(case when false then 0.5 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^asin('abc')^"
argument_list|,
literal|"Cannot apply 'ASIN' to arguments of type 'ASIN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'ASIN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"asin('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"asin(0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.5236d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"asin(cast(0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.5236d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"asin(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ATAN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan(cast(2 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan(case when false then 2 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^atan('abc')^"
argument_list|,
literal|"Cannot apply 'ATAN' to arguments of type 'ATAN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'ATAN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan(2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.1071d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan(cast(2 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.1071d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"atan(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ATAN2
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan2(2, -2)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(cast(1 as float), -1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan2(case when false then 0.5 else null end, -1)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^atan2('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'ATAN2' to arguments of type 'ATAN2\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'ATAN2\\(<NUMERIC>,<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"atan2('abc', 'def')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(0.5, -0.5)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"atan2(cast(0.5 as decimal(1, 1)), cast(-0.5 as decimal(1, 1)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2.3562d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"atan2(cast(null as integer), -1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CBRT
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cbrt(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cbrt(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cbrt(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^cbrt('abc')^"
argument_list|,
literal|"Cannot apply 'CBRT' to arguments of type 'CBRT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'CBRT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cbrt('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cbrt(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cos(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cos(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cos(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^cos('abc')^"
argument_list|,
literal|"Cannot apply 'COS' to arguments of type 'COS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'COS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cos('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cos(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.5403d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cos(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.5403d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cos(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|SqlTester
name|tester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cosh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cosh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cosh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^cosh('abc')^"
argument_list|,
literal|"No match found for function signature COSH\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cosh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cosh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.5430d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cosh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.5430d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cosh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cosh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCotFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COT
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cot(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cot(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cot(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^cot('abc')^"
argument_list|,
literal|"Cannot apply 'COT' to arguments of type 'COT\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'COT\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"cot('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cot(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.6421d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"cot(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.6421d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"cot(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DEGREES
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"degrees(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"degrees(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"degrees(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^degrees('abc')^"
argument_list|,
literal|"Cannot apply 'DEGREES' to arguments of type 'DEGREES\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'DEGREES\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"degrees('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"degrees(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|57.2958d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"degrees(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|57.2958d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"degrees(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PI
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"PI"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|3.1415d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
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
name|assertFalse
argument_list|(
name|PI
operator|.
name|isDynamicFunction
argument_list|()
argument_list|,
literal|"PI operator should not be identified as dynamic function"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRadiansFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|RADIANS
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"radians(42)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"radians(cast(42 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"radians(case when false then 42 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^radians('abc')^"
argument_list|,
literal|"Cannot apply 'RADIANS' to arguments of type 'RADIANS\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'RADIANS\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"radians('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"radians(42)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.7330d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"radians(cast(42 as decimal(2, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.7330d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"radians(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|testRoundFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ROUND
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"round(42, -1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"round(cast(42 as float), 1)"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"round(case when false then 42 else null end, -1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^round('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'ROUND' to arguments of type 'ROUND\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'ROUND\\(<NUMERIC>,<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"round('abc', 'def')"
argument_list|,
literal|"DECIMAL(19, 19) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as integer), 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as double), 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"round(43.21, cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"round(cast(null as double))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIGN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sign(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sign(cast(1 as float))"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sign(case when false then 1 else null end)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sign('abc')^"
argument_list|,
literal|"Cannot apply 'SIGN' to arguments of type 'SIGN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'SIGN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sign('abc')"
argument_list|,
literal|"DECIMAL(19, 19) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"sign(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SIN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sin(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sin(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sin(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sin('abc')^"
argument_list|,
literal|"Cannot apply 'SIN' to arguments of type 'SIN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'SIN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sin('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sin(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.8415d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sin(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.8415d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"sin(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|SqlTester
name|tester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sinh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sinh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sinh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sinh('abc')^"
argument_list|,
literal|"No match found for function signature SINH\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sinh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sinh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.1752d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"sinh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.1752d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"sinh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"sinh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTanFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TAN
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tan(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tan(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tan(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^tan('abc')^"
argument_list|,
literal|"Cannot apply 'TAN' to arguments of type 'TAN\\(<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'TAN\\(<NUMERIC>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tan('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"tan(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.5574d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"tan(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|1.5574d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"tan(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|SqlTester
name|tester
init|=
name|tester
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tanh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tanh(cast(1 as float))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tanh(case when false then 1 else null end)"
argument_list|,
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^tanh('abc')^"
argument_list|,
literal|"No match found for function signature TANH\\(<CHARACTER>\\)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"tanh('abc')"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"tanh(1)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.7615d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"tanh(cast(1 as decimal(1, 0)))"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|0.7615d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"tanh(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"tanh(cast(null as double))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTruncateFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRUNCATE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"truncate(42, -1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"truncate(cast(42 as float), 1)"
argument_list|,
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"truncate(case when false then 42 else null end, -1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^truncate('abc', 'def')^"
argument_list|,
literal|"Cannot apply 'TRUNCATE' to arguments of type 'TRUNCATE\\(<CHAR\\(3\\)>,<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\): 'TRUNCATE\\(<NUMERIC>,<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"truncate('abc', 'def')"
argument_list|,
literal|"DECIMAL(19, 19) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as integer), 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as double), 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"truncate(43.21, cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"truncate(cast(null as integer))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"nullif(1,1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5e0, 3e0)"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5, cast(3e0 as REAL))"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|,
literal|1.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"nullif(1.5e0, 3.4)"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|1.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"nullif(cast(null as varchar(1)),'a')"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"nullif(cast(null as numeric(4,3)), 4.3)"
argument_list|)
expr_stmt|;
comment|// Error message reflects the fact that Nullif is expanded before it is
comment|// validated (like a C macro). Not perfect, but good enough.
name|tester
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
name|tester
operator|.
name|checkFails
argument_list|(
literal|"1 + ^nullif(1, 2, 3)^ + 2"
argument_list|,
literal|"Invalid number of arguments to function 'NULLIF'\\. Was expecting 2 arguments"
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"nullif(interval '2 5' day to hour, interval '5' second)"
argument_list|,
literal|"+2 05"
argument_list|,
literal|"INTERVAL DAY TO HOUR"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"coalesce(null,null,3)"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOCALTIME
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LOCALTIMESTAMP
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_TIME
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CURRENT_TIMESTAMP
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
operator|(
name|Consumer
argument_list|<
name|Holder
argument_list|<
name|Long
argument_list|>
argument_list|>
operator|)
name|o
lambda|->
name|o
operator|.
name|set
argument_list|(
name|timeInMillis
argument_list|)
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
name|tester
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
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NULL"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NOT NULL"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"NOT (CURRENT_DATE IS NULL)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
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
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE() IS NULL"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"CURRENT_DATE IS NOT NULL"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkBoolean
argument_list|(
literal|"NOT (CURRENT_DATE() IS NULL)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_DATE()"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester1
operator|.
name|checkType
argument_list|(
literal|"CURRENT_TIMESTAMP()"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester1
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
name|tester
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
name|tester
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LAST_DAY
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"last_day(cast(null as date))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|testSubstringFunction
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 2 for 8)"
argument_list|,
literal|"bc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 0 for 2)"
argument_list|,
literal|"a"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 0 for 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 8 for 2)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 2)"
argument_list|,
literal|"bc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 0)"
argument_list|,
literal|"abc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from 8)"
argument_list|,
literal|""
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring('abc' from -2)"
argument_list|,
literal|"bc"
argument_list|,
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 2 for 8)"
argument_list|,
literal|"bbcc"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 0 for 2)"
argument_list|,
literal|"aa"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 0 for 0)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 8 for 2)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 2)"
argument_list|,
literal|"bbcc"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 0)"
argument_list|,
literal|"aabbcc"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from 8)"
argument_list|,
literal|""
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkString
argument_list|(
literal|"substring(x'aabbcc' from -2)"
argument_list|,
literal|"bbcc"
argument_list|,
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG296_FIXED
condition|)
block|{
comment|// substring regexp not supported yet
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"substring(cast(null as varchar(1)),1,2)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTrimFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TRIM
argument_list|)
expr_stmt|;
comment|// SQL:2003 6.29.11 Trimming a CHAR yields a VARCHAR
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"trim(cast(null as varchar(1)) from 'a')"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|SqlTester
name|tester1
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|RTRIM
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"rtrim(CAST(NULL AS VARCHAR(6)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLtrimFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LTRIM
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"ltrim(CAST(NULL AS VARCHAR(6)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGreatestFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|GREATEST
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkScalar
argument_list|(
literal|"greatest(12, CAST(NULL AS INTEGER), 3)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester1
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
name|SqlTester
name|tester2
init|=
name|oracleTester
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|tester2
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
name|tester2
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
annotation|@
name|Test
name|void
name|testLeastFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|LEAST
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkScalar
argument_list|(
literal|"least(12, CAST(NULL AS INTEGER), 3)"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester1
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
name|SqlTester
name|tester2
init|=
name|oracleTester
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|tester2
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
name|tester2
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
annotation|@
name|Test
name|void
name|testNvlFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|NVL
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkNull
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(6)), cast(NULL AS VARCHAR(4)))"
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester2
init|=
name|oracleTester
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
decl_stmt|;
name|tester2
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
name|tester2
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
name|tester2
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
name|tester2
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
name|tester2
operator|.
name|checkNull
argument_list|(
literal|"nvl(CAST(NULL AS VARCHAR(6)), cast(NULL AS VARCHAR(4)))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDecodeFunc
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlLibraryOperators
operator|.
name|DECODE
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester1
init|=
name|oracleTester
argument_list|()
decl_stmt|;
name|tester1
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
name|tester1
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
name|tester1
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
name|tester1
operator|.
name|checkScalar
argument_list|(
literal|"decode(3, 0, 'a', 1, 'b', 2, 'c')"
argument_list|,
literal|null
argument_list|,
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
comment|// if there's no match, return the "else" value
name|tester1
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
name|tester1
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
name|tester1
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
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|check
argument_list|(
literal|"select sum(1) over (order by x) from (select 1 as x, 2 as y from (values (true)))"
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
literal|"INTEGER"
argument_list|)
argument_list|,
literal|"1"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testElementFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(multiset[cast(null as integer),2])"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// applied to array
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(array['foo', 'bar'])"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// applied to map
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"cardinality(map['foo', 1, 'bar', 2])"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMemberOfOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1 member of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"'2' member of multiset['1']"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as double) member of multiset[cast(null as double)]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"cast(null as double) member of multiset[1.1]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"1.1 member of multiset[cast(null as double)]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetUnionOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1,2] submultiset of (multiset[2] multiset union multiset[1])"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union distinct multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
operator|+
literal|" submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e'])"
operator|+
literal|" submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as double)] multiset union multiset[cast(null as double)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"DOUBLE MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as boolean)] multiset union multiset[cast(null as boolean)]"
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union all multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 2, 3, 4, 5, 7, 8]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset[1, 2, 3, 4, 2] "
operator|+
literal|"multiset union all multiset[1, 4, 5, 7, 8]) "
operator|+
literal|"submultiset of multiset[1, 1, 2, 2, 3, 4, 4, 5, 7, 8]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union all multiset['c', 'd', 'e']) "
operator|+
literal|"submultiset of multiset['a', 'b', 'c', 'd', 'e']"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"(multiset['a', 'b', 'c'] "
operator|+
literal|"multiset union distinct multiset['c', 'd', 'e']) "
operator|+
literal|"submultiset of multiset['a', 'b', 'c', 'd', 'e', 'c']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as double)] multiset union all multiset[cast(null as double)]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"DOUBLE MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"multiset[cast(null as boolean)] multiset union all multiset[cast(null as boolean)]"
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
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[2] submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] submultiset of multiset[1, 2]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] submultiset of multiset[1, 2]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] submultiset of multiset['c', 'd', 's', 'a']"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'd'] submultiset of multiset['c', 's', 'a', 'w', 'd']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['q', 'a'] submultiset of multiset['a', 'q']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNotSubMultisetOfOperator
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[2] not submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] not submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] not submultiset of multiset[1]"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1] not submultiset of multiset[1, 2]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset[1, 2] not submultiset of multiset[1, 2]"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'b'] not submultiset of multiset['c', 'd', 's', 'a']"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['a', 'd'] not submultiset of multiset['c', 's', 'a', 'w', 'd']"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"multiset['q', 'a'] not submultiset of multiset['a', 'q']"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCollectFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"collect(1)"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"collect(1.2)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"collect(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"collect(x)"
argument_list|,
name|values
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"[0, 2, 2]"
argument_list|)
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"collect(x) within group(order by x desc)"
argument_list|,
name|values
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"[2, 2, 0]"
argument_list|)
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Object
name|result1
init|=
operator|-
literal|3
decl_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"collect(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|result1
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
operator|-
literal|1
decl_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"collect(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|result
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"collect(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testListAggFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"listagg(12)"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|strictTester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"listagg(cast(12 as double))"
argument_list|,
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
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
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"listagg('test')"
argument_list|,
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"listagg(x)"
argument_list|,
name|values1
argument_list|,
literal|"hello,world,!"
argument_list|,
operator|(
name|double
operator|)
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"listagg(cast(x as CHAR))"
argument_list|,
name|values2
argument_list|,
literal|"0,1,2,3"
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFusionFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"fusion(MULTISET[1,2,3])"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"fusion(x)"
argument_list|,
name|values1
argument_list|,
literal|"[0, 1, 2, 3]"
argument_list|,
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"fusion(x)"
argument_list|,
name|values2
argument_list|,
literal|"[0, 1, 1, 2]"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntersectionFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"intersection(MULTISET[1,2,3])"
argument_list|,
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|strictTester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values1
argument_list|,
literal|"[]"
argument_list|,
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values2
argument_list|,
literal|"[1]"
argument_list|,
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"intersection(x)"
argument_list|,
name|values3
argument_list|,
literal|"[0, 1, 1]"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testYear
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"week(date '2008-1-23')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"week(cast(null as date))"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testDayOfYear
parameter_list|()
block|{
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"dayofyear(date '2008-1-23')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"dayofyear(cast(null as date))"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testDayOfMonth
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"dayofweek(date '2008-1-23')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"dayofweek(cast(null as date))"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testHour
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from interval '4-2' year to month)"
argument_list|,
literal|"0"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^extract(doy from interval '4-2' year to month)^"
argument_list|,
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(decade from interval '426-3' year(3) to month)"
argument_list|,
literal|"42"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(century from interval '426-3' year(3) to month)"
argument_list|,
literal|"4"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millennium from interval '2005-3' year(4) to month)"
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(epoch from interval '2 3:4:5.678' day to second)"
argument_list|,
comment|// number of seconds elapsed since timestamp
comment|// '1970-01-01 00:00:00' + input interval
literal|"183845.678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(week from cast(null as date))"
argument_list|,
literal|null
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"extract(doy from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"extract(dow from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// TODO: Not implemented in operator test execution code
name|tester
operator|.
name|checkFails
argument_list|(
literal|"extract(week from timestamp '2008-2-23 12:34:56')"
argument_list|,
literal|"cannot translate call EXTRACT.*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|testExtractFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(nanosecond from interval '2 3:4:5.678' day to second)"
argument_list|,
literal|"5678000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"extract(month from cast(null as timestamp))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"extract(month from cast(null as date))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"extract(second from cast(null as time))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"extract(millisecond from cast(null as time))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"extract(microsecond from cast(null as time))"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17357"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(millisecond from TIMESTAMP '1969-12-31 21:13:17.357')"
argument_list|,
literal|"17357"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"extract(microsecond from TIMESTAMP '1969-12-31 21:13:17.357')"
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
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ARRAY_VALUE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
annotation|@
name|Test
name|void
name|testItemOp
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|ITEM
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][0]"
argument_list|,
literal|null
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY ['foo', 'bar'][3]"
argument_list|,
literal|null
argument_list|,
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"ARRAY ['foo', 'bar'][1 + CAST(NULL AS INTEGER)]"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^ARRAY ['foo', 'bar']['baz']^"
argument_list|,
literal|"Cannot apply 'ITEM' to arguments of type 'ITEM\\(<CHAR\\(3\\) ARRAY>,<CHAR\\(3\\)>\\)'\\. Supported form\\(s\\):<ARRAY>\\[<INTEGER>\\]\n"
operator|+
literal|"<MAP>\\[<VALUE>\\]"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Array of INTEGER NOT NULL is interesting because we might be tempted
comment|// to represent the result as Java "int".
name|tester
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
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"ARRAY [2, 4, 6][4]"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// Map item
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"map['foo', CAST(NULL AS INTEGER), 'bar', 7]['bar']"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"map['foo', CAST(NULL AS INTEGER), 'bar', 7]['baz']"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkColumnType
argument_list|(
literal|"select cast(null as any)['x'] from (values(1))"
argument_list|,
literal|"ANY"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMapValueConstructor
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarExact
argument_list|(
literal|"map['washington', 1, 'obama', 44]"
argument_list|,
literal|"(CHAR(10) NOT NULL, INTEGER NOT NULL) MAP NOT NULL"
argument_list|,
literal|"{washington=1, obama=44}"
argument_list|)
expr_stmt|;
specifier|final
name|SqlTester
name|tester2
init|=
name|tester
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|tester2
operator|.
name|checkScalarExact
argument_list|(
literal|"map['washington', 1, 'obama', 44]"
argument_list|,
literal|"(VARCHAR(10) NOT NULL, INTEGER NOT NULL) MAP NOT NULL"
argument_list|,
literal|"{washington=1, obama=44}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCeilFunc
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"ceil(10.1e0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|11
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"ceil(cast(-11.2e0 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
operator|-
literal|11
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"ceiling(cast(null as decimal(2,0)))"
argument_list|)
expr_stmt|;
name|tester
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
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"floor(2.5e0)"
argument_list|,
literal|"DOUBLE NOT NULL"
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalarApprox
argument_list|(
literal|"floor(cast(-1.2e0 as real))"
argument_list|,
literal|"REAL NOT NULL"
argument_list|,
operator|-
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as decimal(2,0)))"
argument_list|)
expr_stmt|;
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^floor('12:34:56')^"
argument_list|,
literal|"Cannot apply 'FLOOR' to arguments of type 'FLOOR\\(<CHAR\\(8\\)>\\)'\\. Supported form\\(s\\): 'FLOOR\\(<NUMERIC>\\)'\n"
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"floor('12:34:56')"
argument_list|,
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^floor(timestamp '2015-02-19 12:34:56.78' to microsecond)^"
argument_list|,
literal|"(?s)Encountered \"microsecond\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^floor(timestamp '2015-02-19 12:34:56.78' to nanosecond)^"
argument_list|,
literal|"(?s)Encountered \"nanosecond\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^ceil('12:34:56')^"
argument_list|,
literal|"Cannot apply 'CEIL' to arguments of type 'CEIL\\(<CHAR\\(8\\)>\\)'\\. Supported form\\(s\\): 'CEIL\\(<NUMERIC>\\)'\n"
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"ceil('12:34:56')"
argument_list|,
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^ceil(timestamp '2015-02-19 12:34:56.78' to microsecond)^"
argument_list|,
literal|"(?s)Encountered \"microsecond\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^ceil(timestamp '2015-02-19 12:34:56.78' to nanosecond)^"
argument_list|,
literal|"(?s)Encountered \"nanosecond\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"ceil(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"ceil(cast(null as date) to month)"
argument_list|)
expr_stmt|;
comment|// ceiling alias
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"ceiling(cast(null as timestamp) to month)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFloorFuncInterval
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
operator|.
name|checkNull
argument_list|(
literal|"floor(cast(null as interval year))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAdd
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_ADD
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MICROSECOND, 2000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SQL_TSI_SECOND, 2, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(NANOSECOND, 3000000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:28"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SQL_TSI_FRAC_SECOND, 2000000000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:42:27"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MINUTE, 2, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-02-24 12:44:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(HOUR, -2000, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2015-12-03 04:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"timestampadd(HOUR, CAST(NULL AS INTEGER),"
operator|+
literal|" timestamp '2016-02-24 12:42:25')"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkNull
argument_list|(
literal|"timestampadd(HOUR, -200, CAST(NULL AS TIMESTAMP))"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 3, timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2016-05-24 12:42:25"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 3, cast(null as timestamp))"
argument_list|,
literal|null
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with DATE; returns a TIMESTAMP value for sub-day intervals.
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 1, date '2016-06-15')"
argument_list|,
literal|"2016-07-15"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(DAY, 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-16"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(HOUR, -1, date '2016-06-15')"
argument_list|,
literal|"2016-06-14 23:00:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MINUTE, 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-15 00:01:00"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SQL_TSI_SECOND, -1, date '2016-06-15')"
argument_list|,
literal|"2016-06-14 23:59:59"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SECOND, 1, date '2016-06-15')"
argument_list|,
literal|"2016-06-15 00:00:01"
argument_list|,
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SECOND, 1, cast(null as date))"
argument_list|,
literal|null
argument_list|,
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(DAY, 1, cast(null as date))"
argument_list|,
literal|null
argument_list|,
literal|"DATE"
argument_list|)
expr_stmt|;
comment|// Round to the last day of previous month
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 1, date '2016-05-31')"
argument_list|,
literal|"2016-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 5, date '2016-01-31')"
argument_list|,
literal|"2016-06-30"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, -1, date '2016-03-31')"
argument_list|,
literal|"2016-02-29"
argument_list|,
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with time; returns a time value.The interval is positive.
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SECOND, 1, time '23:59:59')"
argument_list|,
literal|"00:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MINUTE, 1, time '00:00:00')"
argument_list|,
literal|"00:01:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MINUTE, 1, time '23:59:59')"
argument_list|,
literal|"00:00:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(HOUR, 1, time '23:59:59')"
argument_list|,
literal|"00:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(DAY, 15, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(WEEK, 3, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, 6, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(QUARTER, 1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(YEAR, 10, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// TIMESTAMPADD with time; returns a time value .The interval is negative.
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(SECOND, -1, time '00:00:00')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MINUTE, -1, time '00:00:00')"
argument_list|,
literal|"23:59:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(HOUR, -1, time '00:00:00')"
argument_list|,
literal|"23:00:00"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(DAY, -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(WEEK, -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(MONTH, -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(QUARTER, -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampadd(YEAR, -1, time '23:59:59')"
argument_list|,
literal|"23:59:59"
argument_list|,
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAddFractionalSeconds
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_ADD
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|getValidator
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
name|tester
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
annotation|@
name|Test
name|void
name|testTimestampDiff
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|TIMESTAMP_DIFF
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(HOUR, "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 15:42:25')"
argument_list|,
literal|"3"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MICROSECOND, "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20')"
argument_list|,
literal|"-5000000"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(SQL_TSI_FRAC_SECOND, "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20')"
argument_list|,
literal|"-5000000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(NANOSECOND, "
operator|+
literal|"timestamp '2016-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:20')"
argument_list|,
literal|"-5000000000"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(YEAR, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(WEEK, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"104"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(WEEK, "
operator|+
literal|"timestamp '2014-02-19 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"105"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, "
operator|+
literal|"timestamp '2019-09-01 00:00:00', "
operator|+
literal|"timestamp '2020-03-01 00:00:00')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, "
operator|+
literal|"timestamp '2019-09-01 00:00:00', "
operator|+
literal|"timestamp '2016-08-01 00:00:00')"
argument_list|,
literal|"-37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(QUARTER, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2016-02-24 12:42:25')"
argument_list|,
literal|"8"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"timestampdiff(CENTURY, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"timestamp '2614-02-24 12:42:25')"
argument_list|,
literal|"(?s)Encountered \"CENTURY\" at .*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(QUARTER, "
operator|+
literal|"timestamp '2014-02-24 12:42:25', "
operator|+
literal|"cast(null as timestamp))"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(QUARTER, "
operator|+
literal|"cast(null as timestamp), "
operator|+
literal|"timestamp '2014-02-24 12:42:25')"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
comment|// timestampdiff with date
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, date '2016-03-15', date '2016-06-14')"
argument_list|,
literal|"2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, date '2019-09-01', date '2020-03-01')"
argument_list|,
literal|"6"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MONTH, date '2019-09-01', date '2016-08-01')"
argument_list|,
literal|"-37"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(DAY, date '2016-06-15', date '2016-06-14')"
argument_list|,
literal|"-1"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(HOUR, date '2016-06-15', date '2016-06-14')"
argument_list|,
literal|"-24"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(MINUTE, date '2016-06-15',  date '2016-06-15')"
argument_list|,
literal|"0"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(SECOND, cast(null as date), date '2016-06-15')"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkScalar
argument_list|(
literal|"timestampdiff(DAY, date '2016-06-15', cast(null as date))"
argument_list|,
literal|null
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDenseRankFunc
parameter_list|()
block|{
name|tester
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
annotation|@
name|Test
name|void
name|testPercentRankFunc
parameter_list|()
block|{
name|tester
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
annotation|@
name|Test
name|void
name|testRankFunc
parameter_list|()
block|{
name|tester
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
annotation|@
name|Test
name|void
name|testCumeDistFunc
parameter_list|()
block|{
name|tester
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
annotation|@
name|Test
name|void
name|testRowNumberFunc
parameter_list|()
block|{
name|tester
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
annotation|@
name|Test
name|void
name|testCountFunc
parameter_list|()
block|{
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"count(*)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"count('name')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"count(1)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"count(1.2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"COUNT(DISTINCT 'x')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"count(1, 2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(x)"
argument_list|,
name|values
argument_list|,
literal|3
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(*)"
argument_list|,
name|stringValues
argument_list|,
literal|3
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(x)"
argument_list|,
name|stringValues
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT x)"
argument_list|,
name|stringValues
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"COUNT(DISTINCT 123)"
argument_list|,
name|stringValues
argument_list|,
literal|1
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testApproxCountDistinctFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct('name')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1.2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT 'x')"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"approx_count_distinct(1, 2)"
argument_list|,
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|1
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// DISTINCT keyword is allowed but has no effect
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(x)"
argument_list|,
name|stringValues
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT x)"
argument_list|,
name|stringValues
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"APPROX_COUNT_DISTINCT(DISTINCT 123)"
argument_list|,
name|stringValues
argument_list|,
literal|1
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSumFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sum('name')^"
argument_list|,
literal|"(?s)Cannot apply 'SUM' to arguments of type 'SUM\\(<CHAR\\(4\\)>\\)'\\. Supported form\\(s\\): 'SUM\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sum('name')"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"sum(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"sum(1.2)"
argument_list|,
literal|"DECIMAL(19, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"sum(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(19, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^sum(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'SUM' to arguments of type 'SUM\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'SUM\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"sum(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"sum(x)"
argument_list|,
name|values
argument_list|,
literal|4
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Object
name|result1
init|=
operator|-
literal|3
decl_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"sum(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|result1
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
operator|-
literal|1
decl_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"sum(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|result
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"sum(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
operator|(
name|double
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Very similar to {@code tester.checkType}, but generates inside a SELECT    * with a non-empty GROUP BY. Aggregate functions may be nullable if executed    * in a SELECT with an empty GROUP BY.    *    *<p>Viz: {@code SELECT sum(1) FROM emp} has type "INTEGER",    * {@code SELECT sum(1) FROM emp GROUP BY deptno} has type "INTEGER NOT NULL",    */
specifier|protected
name|void
name|checkAggType
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|String
name|expr
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|tester
operator|.
name|checkColumnType
argument_list|(
name|AbstractSqlTester
operator|.
name|buildQueryAgg
argument_list|(
name|expr
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAvgFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^avg(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'AVG' to arguments of type 'AVG\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'AVG\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"avg(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"AVG(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"AVG(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"avg(1)"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"avg(1.2)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"avg(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"AVG(x)"
argument_list|,
name|values
argument_list|,
literal|2d
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"AVG(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|1.5d
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
operator|-
literal|1
decl_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"avg(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
name|result
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCovarPopFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^covar_pop(cast(null as varchar(2)),cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'COVAR_POP' to arguments of type 'COVAR_POP\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'COVAR_POP\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"covar_pop(cast(null as varchar(2)),cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"covar_pop(CAST(NULL AS INTEGER),CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"covar_pop(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCovarSampFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^covar_samp(cast(null as varchar(2)),cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'COVAR_SAMP' to arguments of type 'COVAR_SAMP\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'COVAR_SAMP\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"covar_samp(cast(null as varchar(2)),cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"covar_samp(CAST(NULL AS INTEGER),CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"covar_samp(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRegrSxxFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^regr_sxx(cast(null as varchar(2)), cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'REGR_SXX' to arguments of type 'REGR_SXX\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'REGR_SXX\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"regr_sxx(cast(null as varchar(2)), cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"regr_sxx(CAST(NULL AS INTEGER), CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"regr_sxx(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRegrSyyFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^regr_syy(cast(null as varchar(2)), cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'REGR_SYY' to arguments of type 'REGR_SYY\\(<VARCHAR\\(2\\)>,<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'REGR_SYY\\(<NUMERIC>,<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"regr_syy(cast(null as varchar(2)), cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"regr_syy(CAST(NULL AS INTEGER), CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"regr_syy(1.5, 2.5)"
argument_list|,
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStddevPopFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^stddev_pop(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV_POP' to arguments of type 'STDDEV_POP\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'STDDEV_POP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev_pop(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev_pop(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|enable
condition|)
block|{
comment|// verified on Oracle 10g
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(x)"
argument_list|,
name|values
argument_list|,
literal|1.414213562373095d
argument_list|,
literal|0.000000000000001d
argument_list|)
expr_stmt|;
comment|// Oracle does not allow distinct
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|1.5d
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_pop(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
comment|// with one value
name|tester
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
literal|0
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStddevSampFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^stddev_samp(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV_SAMP' to arguments of type 'STDDEV_SAMP\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'STDDEV_SAMP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev_samp(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev_samp(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|enable
condition|)
block|{
comment|// verified on Oracle 10g
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(x)"
argument_list|,
name|values
argument_list|,
literal|1.732050807568877d
argument_list|,
literal|0.000000000000001d
argument_list|)
expr_stmt|;
comment|// Oracle does not allow distinct
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|2.121320343559642d
argument_list|,
literal|0.000000000000001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"stddev_samp(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
comment|// with one value
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStddevFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^stddev(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'STDDEV' to arguments of type 'STDDEV\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'STDDEV\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"stddev(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
literal|"stddev(DISTINCT 1.5)"
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
comment|// with one value
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testVarPopFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^var_pop(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VAR_POP' to arguments of type 'VAR_POP\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'VAR_POP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"var_pop(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"var_pop(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(x)"
argument_list|,
name|values
argument_list|,
literal|2d
argument_list|,
comment|// verified on Oracle 10g
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
literal|2.25d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_pop(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with one value
name|tester
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
literal|0
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testVarSampFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^var_samp(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VAR_SAMP' to arguments of type 'VAR_SAMP\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'VAR_SAMP\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"var_samp(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"var_samp(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(x)"
argument_list|,
name|values
argument_list|,
literal|3d
argument_list|,
comment|// verified on Oracle 10g
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
literal|4.5d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"var_samp(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with one value
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testVarFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|strictTester
operator|.
name|checkFails
argument_list|(
literal|"^variance(cast(null as varchar(2)))^"
argument_list|,
literal|"(?s)Cannot apply 'VARIANCE' to arguments of type 'VARIANCE\\(<VARCHAR\\(2\\)>\\)'\\. Supported form\\(s\\): 'VARIANCE\\(<NUMERIC>\\)'.*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"variance(cast(null as varchar(2)))"
argument_list|,
literal|"DECIMAL(19, 19)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"variance(CAST(NULL AS INTEGER))"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|checkAggType
argument_list|(
name|tester
argument_list|,
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"variance(x)"
argument_list|,
name|values
argument_list|,
literal|3d
argument_list|,
comment|// verified on Oracle 10g
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"variance(DISTINCT x)"
argument_list|,
comment|// Oracle does not allow distinct
name|values
argument_list|,
literal|4.5d
argument_list|,
literal|0.0001d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"variance(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with one value
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
comment|// with zero values
name|tester
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
literal|null
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMinFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"min(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"min(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"min(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^min()^"
argument_list|,
literal|"Invalid number of arguments to function 'MIN'. Was expecting 1 arguments"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^min(1, 2)^"
argument_list|,
literal|"Invalid number of arguments to function 'MIN'. Was expecting 1 arguments"
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"min(x)"
argument_list|,
name|values
argument_list|,
literal|"0"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"min(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"min(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"min(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|"0"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMaxFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"max(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"max(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"max(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"max(x)"
argument_list|,
name|values
argument_list|,
literal|"2"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"max(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"max(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"max(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|"2"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLastValueFunc
parameter_list|()
block|{
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"3"
argument_list|,
literal|"0"
argument_list|)
argument_list|,
literal|0d
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
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1.6"
argument_list|,
literal|"1.2"
argument_list|)
argument_list|,
literal|0d
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
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo "
argument_list|,
literal|"bar "
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFirstValueFunc
parameter_list|()
block|{
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"0"
argument_list|)
argument_list|,
literal|0d
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
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1.6"
argument_list|)
argument_list|,
literal|0d
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
name|tester
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
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo "
argument_list|)
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEveryFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"every(1 = 1)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"every(1.2 = 1.2)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"every(1.5 = 1.4)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"every(x = 2)"
argument_list|,
name|values
argument_list|,
literal|"false"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSomeAggFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"some(1 = 1)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"some(1.2 = 1.2)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"some(1.5 = 1.4)"
argument_list|,
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
if|if
condition|(
operator|!
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"some(x = 2)"
argument_list|,
name|values
argument_list|,
literal|"true"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAnyValueFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"any_value(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"any_value(1.2)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"any_value(DISTINCT 1.5)"
argument_list|,
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"any_value(x)"
argument_list|,
name|values
argument_list|,
literal|"0"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"any_value(CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"any_value(DISTINCT CASE x WHEN 0 THEN NULL ELSE -1 END)"
argument_list|,
name|values
argument_list|,
literal|"-1"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"any_value(DISTINCT x)"
argument_list|,
name|values
argument_list|,
literal|"0"
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBitAndFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_and(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_and(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_and(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_and(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^bit_and(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_AND' to arguments of type 'BIT_AND\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): 'BIT_AND\\(<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"bit_and(x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBitOrFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_or(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_or(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_or(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_or(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^bit_or(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_OR' to arguments of type 'BIT_OR\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): 'BIT_OR\\(<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"bit_or(x)"
argument_list|,
name|values
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBitXorFunc
parameter_list|()
block|{
name|tester
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
name|tester
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
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_xor(1)"
argument_list|,
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_xor(CAST(2 AS TINYINT))"
argument_list|,
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_xor(CAST(2 AS SMALLINT))"
argument_list|,
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkType
argument_list|(
literal|"bit_xor(distinct CAST(2 AS BIGINT))"
argument_list|,
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkFails
argument_list|(
literal|"^bit_xor(1.2)^"
argument_list|,
literal|"Cannot apply 'BIT_XOR' to arguments of type 'BIT_XOR\\(<DECIMAL\\(2, 1\\)>\\)'\\. Supported form\\(s\\): 'BIT_XOR\\(<INTEGER>\\)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
operator|.
name|checkAgg
argument_list|(
literal|"bit_xor(x)"
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that CAST fails when given a value just outside the valid range for    * that type. For example,    *    *<ul>    *<li>CAST(-200 AS TINYINT) fails because the value is less than -128;    *<li>CAST(1E-999 AS FLOAT) fails because the value underflows;    *<li>CAST(123.4567891234567 AS FLOAT) fails because the value loses    * precision.    *</ul>    */
annotation|@
name|Test
name|void
name|testLiteralAtLimit
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|enable
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
name|SqlLimitsTest
operator|.
name|getTypes
argument_list|(
name|tester
operator|.
name|getValidator
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
name|tester
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
name|tester
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
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed for expr=["
operator|+
name|expr
operator|+
literal|"]"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed for expr=["
operator|+
name|expr
operator|+
literal|"]"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Tests that CAST fails when given a value just outside the valid range for    * that type. For example,    *    *<ul>    *<li>CAST(-200 AS TINYINT) fails because the value is less than -128;    *<li>CAST(1E-999 AS FLOAT) fails because the value underflows;    *<li>CAST(123.4567891234567 AS FLOAT) fails because the value loses    * precision.    *</ul>    */
annotation|@
name|Test
name|void
name|testLiteralBeyondLimit
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
name|SqlLimitsTest
operator|.
name|getTypes
argument_list|(
name|tester
operator|.
name|getValidator
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
name|tester
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
if|if
condition|(
name|Bug
operator|.
name|CALCITE_2539_FIXED
condition|)
block|{
comment|// Value outside legal bound should fail at runtime (not
comment|// validate time).
comment|//
comment|// NOTE: Because Java and Fennel calcs give
comment|// different errors, the pattern hedges its bets.
name|tester
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
annotation|@
name|Test
name|void
name|testCastTruncates
parameter_list|()
block|{
name|tester
operator|.
name|setFor
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|)
expr_stmt|;
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|tester
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
name|enable
condition|)
block|{
return|return;
block|}
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"CAST(X'' AS BINARY(3)) = X'000000'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tester
operator|.
name|checkBoolean
argument_list|(
literal|"CAST(X'' AS BINARY(3)) = X''"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Test that calls all operators with all possible argument types, and for    * each type, with a set of tricky values.    *    *<p>This is not really a unit test since there are no assertions;    * it either succeeds or fails in the preparation of the operator case    * and not when actually testing (validating/executing) the call.    *    *<p>Nevertheless the log messages conceal many problems which potentially    * need to be fixed especially cases where the query passes from the    * validation stage and fails at runtime. */
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
name|SqlValidatorImpl
name|validator
init|=
operator|(
name|SqlValidatorImpl
operator|)
name|tester
operator|.
name|getValidator
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
switch|switch
condition|(
name|op
operator|.
name|getSyntax
argument_list|()
condition|)
block|{
case|case
name|SPECIAL
case|:
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
comment|// TODO this part is deactivated because it uses a deprecated method
comment|//          final Strong.Policy policy = Strong.policy(op.kind);
comment|//          try {
comment|//            if (nullCount> 0&& policy == Strong.Policy.ANY) {
comment|//              tester.checkNull(s);
comment|//            } else {
comment|//              final String query;
comment|//              if (op instanceof SqlAggFunction) {
comment|//                if (op.requiresOrder()) {
comment|//                  query = "SELECT " + s + " OVER () FROM (VALUES (1))";
comment|//                } else {
comment|//                  query = "SELECT " + s + " FROM (VALUES (1))";
comment|//                }
comment|//              } else {
comment|//                query = AbstractSqlTester.buildQuery(s);
comment|//              }
comment|//              tester.check(query, SqlTests.ANY_TYPE_CHECKER,
comment|//                  SqlTests.ANY_PARAMETER_CHECKER, result -> { });
comment|//            }
comment|//          } catch (Throwable e) {
comment|//            // Logging the top-level throwable directly makes the message
comment|//            // difficult to read since it either contains too much information
comment|//            // or very few details.
comment|//            Throwable cause = findMostDescriptiveCause(e);
comment|//            LOGGER.info("Failed: " + s + ": " + cause);
comment|//          }
block|}
block|}
block|}
block|}
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
comment|// TODO: Test other stuff
comment|/**    * Result checker that considers a test to have succeeded if it throws an    * exception that matches one of a list of patterns.    */
specifier|private
specifier|static
class|class
name|ExceptionResultChecker
implements|implements
name|SqlTester
operator|.
name|ResultChecker
block|{
specifier|private
specifier|final
name|Pattern
index|[]
name|patterns
decl_stmt|;
name|ExceptionResultChecker
parameter_list|(
name|Pattern
modifier|...
name|patterns
parameter_list|)
block|{
name|this
operator|.
name|patterns
operator|=
name|patterns
expr_stmt|;
block|}
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
name|result
operator|.
name|next
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
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
comment|/**    * Result checker that considers a test to have succeeded if it returns a    * particular value or throws an exception that matches one of a list of    * patterns.    *    *<p>Sounds peculiar, but is necessary when eager and lazy behaviors are    * both valid.    */
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
specifier|public
specifier|static
name|SqlTester
name|tester
parameter_list|()
block|{
return|return
operator|new
name|TesterImpl
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**    * Implementation of {@link org.apache.calcite.sql.test.SqlTester} based on a    * JDBC connection.    */
specifier|protected
specifier|static
class|class
name|TesterImpl
extends|extends
name|SqlRuntimeTester
block|{
specifier|public
name|TesterImpl
parameter_list|(
name|SqlTestFactory
name|testFactory
parameter_list|)
block|{
name|super
argument_list|(
name|testFactory
argument_list|,
name|UnaryOperator
operator|.
name|identity
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
name|String
name|query
parameter_list|,
name|TypeChecker
name|typeChecker
parameter_list|,
name|ParameterChecker
name|parameterChecker
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|super
operator|.
name|check
argument_list|(
name|query
argument_list|,
name|typeChecker
argument_list|,
name|parameterChecker
argument_list|,
name|resultChecker
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
specifier|final
name|CalciteAssert
operator|.
name|ConnectionFactory
name|connectionFactory
init|=
operator|(
name|CalciteAssert
operator|.
name|ConnectionFactory
operator|)
name|getFactory
argument_list|()
operator|.
name|get
argument_list|(
literal|"connectionFactory"
argument_list|)
decl_stmt|;
try|try
init|(
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
annotation|@
name|Override
specifier|protected
name|SqlTester
name|with
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
return|return
operator|new
name|TesterImpl
argument_list|(
name|factory
argument_list|)
return|;
block|}
block|}
comment|/** A type, a value, and its {@link SqlNode} representation. */
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
comment|/** Builds lists of types and sample values. */
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
comment|/** Runs an OVERLAPS test with a given set of literal values. */
class|class
name|OverlapChecker
block|{
specifier|final
name|String
index|[]
name|values
decl_stmt|;
name|OverlapChecker
parameter_list|(
name|String
modifier|...
name|values
parameter_list|)
block|{
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
name|tester
operator|.
name|checkBoolean
argument_list|(
name|sub
argument_list|(
name|s
argument_list|)
argument_list|,
name|Boolean
operator|.
name|TRUE
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
name|tester
operator|.
name|checkBoolean
argument_list|(
name|sub
argument_list|(
name|s
argument_list|)
argument_list|,
name|Boolean
operator|.
name|FALSE
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
block|}
end_class

end_unit

