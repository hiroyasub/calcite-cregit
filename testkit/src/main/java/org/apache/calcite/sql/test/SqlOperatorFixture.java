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
name|SqlOperatorTable
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
name|parser
operator|.
name|SqlParser
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
name|StringAndPos
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
operator|.
name|ResultChecker
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
operator|.
name|TypeChecker
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
name|SqlValidator
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
name|ConnectionFactories
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
name|ConnectionFactory
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
name|Matchers
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
name|test
operator|.
name|ResultCheckers
operator|.
name|isSingle
import|;
end_import

begin_comment
comment|/**  * A fixture for testing the SQL operators.  *  *<p>It provides a fluent API so that you can write tests by chaining method  * calls.  *  *<p>It is immutable. If you have two test cases that require a similar set up  * (for example, the same SQL expression and parser configuration), it is safe  * to use the same fixture object as a starting point for both tests.  *  *<p>The idea is that when you define an operator (or another piece of SQL  * functionality), you can define the logical behavior of that operator once, as  * part of that operator. Later you can define one or more physical  * implementations of that operator, and test them all using the same set of  * tests.  *  *<p>Depending on the implementation of {@link SqlTester} used  * (see {@link #withTester(UnaryOperator)}), the fixture may or may not  * evaluate expressions and check their results.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperatorFixture
extends|extends
name|AutoCloseable
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|// TODO: Change message when Fnl3Fixed to something like
comment|// "Invalid character for cast: PC=0 Code=22018"
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
name|String
name|INVALID_EXTRACT_UNIT_CONVERTLET_ERROR
init|=
literal|"Extract.*from.*type data is not supported"
decl_stmt|;
name|String
name|INVALID_EXTRACT_UNIT_VALIDATION_ERROR
init|=
literal|"Cannot apply 'EXTRACT' to arguments of type .*'\n.*"
decl_stmt|;
name|String
name|LITERAL_OUT_OF_RANGE_MESSAGE
init|=
literal|"(?s).*Numeric literal.*out of range.*"
decl_stmt|;
name|String
name|INVALID_ARGUMENTS_NUMBER
init|=
literal|"Invalid number of arguments to function .* Was expecting .* arguments"
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Name of a virtual machine that can potentially implement an operator.    */
enum|enum
name|VmName
block|{
name|FENNEL
block|,
name|JAVA
block|,
name|EXPAND
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Returns the test factory. */
name|SqlTestFactory
name|getFactory
parameter_list|()
function_decl|;
comment|/** Creates a copy of this fixture with a new test factory. */
name|SqlOperatorFixture
name|withFactory
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTestFactory
argument_list|>
name|transform
parameter_list|)
function_decl|;
comment|/** Returns the tester. */
name|SqlTester
name|getTester
parameter_list|()
function_decl|;
comment|/** Creates a copy of this fixture with a new tester. */
name|SqlOperatorFixture
name|withTester
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTester
argument_list|>
name|transform
parameter_list|)
function_decl|;
comment|/** Creates a copy of this fixture with a new parser configuration. */
specifier|default
name|SqlOperatorFixture
name|withParserConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withParserConfig
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that tests a given SQL quoting style. */
specifier|default
name|SqlOperatorFixture
name|withQuoting
parameter_list|(
name|Quoting
name|quoting
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withQuoting
argument_list|(
name|quoting
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that applies a given casing policy to quoted    * identifiers. */
specifier|default
name|SqlOperatorFixture
name|withQuotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withQuotedCasing
argument_list|(
name|casing
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that applies a given casing policy to unquoted    * identifiers. */
specifier|default
name|SqlOperatorFixture
name|withUnquotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withUnquotedCasing
argument_list|(
name|casing
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that matches identifiers by case-sensitive or    * case-insensitive. */
specifier|default
name|SqlOperatorFixture
name|withCaseSensitive
parameter_list|(
name|boolean
name|sensitive
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withCaseSensitive
argument_list|(
name|sensitive
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that follows a given lexical policy. */
specifier|default
name|SqlOperatorFixture
name|withLex
parameter_list|(
name|Lex
name|lex
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withLex
argument_list|(
name|lex
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that tests conformance to a particular SQL language    * version. */
specifier|default
name|SqlOperatorFixture
name|withConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
operator|.
name|withConnectionFactory
argument_list|(
name|cf
lambda|->
name|cf
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
comment|/** Returns the conformance. */
specifier|default
name|SqlConformance
name|conformance
parameter_list|()
block|{
return|return
name|getFactory
argument_list|()
operator|.
name|parserConfig
argument_list|()
operator|.
name|conformance
argument_list|()
return|;
block|}
comment|/** Returns a fixture with a given validator configuration. */
specifier|default
name|SqlOperatorFixture
name|withValidatorConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlValidator
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withValidatorConfig
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that tests with implicit type coercion on/off. */
specifier|default
name|SqlOperatorFixture
name|enableTypeCoercion
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
return|return
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withTypeCoercionEnabled
argument_list|(
name|enabled
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that does not fail validation if it encounters an    * unknown function. */
specifier|default
name|SqlOperatorFixture
name|withLenientOperatorLookup
parameter_list|(
name|boolean
name|lenient
parameter_list|)
block|{
return|return
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withLenientOperatorLookup
argument_list|(
name|lenient
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that gets connections from a given factory. */
specifier|default
name|SqlOperatorFixture
name|withConnectionFactory
parameter_list|(
name|UnaryOperator
argument_list|<
name|ConnectionFactory
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withConnectionFactory
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a fixture that uses a given operator table. */
specifier|default
name|SqlOperatorFixture
name|withOperatorTable
parameter_list|(
name|SqlOperatorTable
name|operatorTable
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withOperatorTable
argument_list|(
name|o
lambda|->
name|operatorTable
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns whether to run tests that are considered 'broken'.    * Returns false by default, but it is useful to temporarily enable the    * 'broken' tests to see whether they are still broken. */
name|boolean
name|brokenTestsEnabled
parameter_list|()
function_decl|;
comment|/** Sets {@link #brokenTestsEnabled()}. */
name|SqlOperatorFixture
name|withBrokenTestsEnabled
parameter_list|(
name|boolean
name|enableBrokenTests
parameter_list|)
function_decl|;
name|void
name|checkScalar
parameter_list|(
name|String
name|expression
parameter_list|,
name|TypeChecker
name|typeChecker
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected result and the    * expected type. For example,    *    *<blockquote>    *<pre>checkScalar("1.1 + 2.9", "4.0", "DECIMAL(2, 1) NOT NULL");</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result    * @param resultType Expected result type    */
specifier|default
name|void
name|checkScalar
parameter_list|(
name|String
name|expression
parameter_list|,
name|Object
name|result
parameter_list|,
name|String
name|resultType
parameter_list|)
block|{
name|checkType
argument_list|(
name|expression
argument_list|,
name|resultType
argument_list|)
expr_stmt|;
name|checkScalar
argument_list|(
name|expression
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|ResultCheckers
operator|.
name|createChecker
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a scalar SQL expression returns the expected exact numeric    * result as an integer. For example,    *    *<blockquote>    *<pre>checkScalarExact("1 + 2", 3);</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result    */
specifier|default
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|int
name|result
parameter_list|)
block|{
name|checkScalar
argument_list|(
name|expression
argument_list|,
name|SqlTests
operator|.
name|INTEGER_TYPE_CHECKER
argument_list|,
name|isSingle
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a scalar SQL expression returns the expected exact numeric    * result. For example,    *    *<blockquote>    *<pre>checkScalarExact("1 + 2", "3");</pre>    *</blockquote>    *    * @param expression   Scalar expression    * @param expectedType Type we expect the result to have, including    *                     nullability, precision and scale, for example    *<code>DECIMAL(2, 1) NOT NULL</code>.    * @param result       Expected result    */
specifier|default
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|String
name|result
parameter_list|)
block|{
name|checkScalarExact
argument_list|(
name|expression
argument_list|,
name|expectedType
argument_list|,
name|isSingle
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns expected approximate numeric    * result. For example,    *    *<blockquote>    *<pre>checkScalarApprox("1.0 + 2.1", "3.1");</pre>    *</blockquote>    *    * @param expression     Scalar expression    * @param expectedType   Type we expect the result to have, including    *                       nullability, precision and scale, for example    *<code>DECIMAL(2, 1) NOT NULL</code>.    * @param result         Expected result, or a matcher    *    * @see Matchers#within(Number, double)    */
name|void
name|checkScalarApprox
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|Object
name|result
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected boolean result.    * For example,    *    *<blockquote>    *<pre>checkScalarExact("TRUE AND FALSE", Boolean.TRUE);</pre>    *</blockquote>    *    *<p>The expected result can be null:    *    *<blockquote>    *<pre>checkScalarExact("NOT UNKNOWN", null);</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result (null signifies NULL).    */
name|void
name|checkBoolean
parameter_list|(
name|String
name|expression
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|result
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected string result.    * For example,    *    *<blockquote>    *<pre>checkScalarExact("'ab' || 'c'", "abc");</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result    * @param resultType Expected result type    */
name|void
name|checkString
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|result
parameter_list|,
name|String
name|resultType
parameter_list|)
function_decl|;
comment|/**    * Tests that a SQL expression returns the SQL NULL value. For example,    *    *<blockquote>    *<pre>checkNull("CHAR_LENGTH(CAST(NULL AS VARCHAR(3))");</pre>    *</blockquote>    *    * @param expression Scalar expression    */
name|void
name|checkNull
parameter_list|(
name|String
name|expression
parameter_list|)
function_decl|;
comment|/**    * Tests that a SQL expression has a given type. For example,    *    *<blockquote>    *<code>checkType("SUBSTR('hello' FROM 1 FOR 3)",    * "VARCHAR(3) NOT NULL");</code>    *</blockquote>    *    *<p>This method checks length/precision, scale, and whether the type allows    * NULL values, so is more precise than the type-checking done by methods    * such as {@link #checkScalarExact}.    *    * @param expression Scalar expression    * @param type       Type string    */
name|void
name|checkType
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|type
parameter_list|)
function_decl|;
comment|/** Very similar to {@link #checkType}, but generates inside a SELECT    * with a non-empty GROUP BY. Aggregate functions may be nullable if executed    * in a SELECT with an empty GROUP BY.    *    *<p>Viz: {@code SELECT sum(1) FROM emp} has type "INTEGER",    * {@code SELECT sum(1) FROM emp GROUP BY deptno} has type "INTEGER NOT NULL",    */
specifier|default
name|SqlOperatorFixture
name|checkAggType
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
name|type
parameter_list|)
block|{
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
return|return
name|this
return|;
block|}
comment|/**    * Checks that a query returns one column of an expected type. For example,    *<code>checkType("VALUES (1 + 2)", "INTEGER NOT NULL")</code>.    *    * @param sql  Query expression    * @param type Type string    */
name|void
name|checkColumnType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|type
parameter_list|)
function_decl|;
comment|/**    * Tests that a SQL query returns a single column with the given type. For    * example,    *    *<blockquote>    *<pre>check("VALUES (1 + 2)", "3", SqlTypeName.Integer);</pre>    *</blockquote>    *    *<p>If<code>result</code> is null, the expression must yield the SQL NULL    * value. If<code>result</code> is a {@link java.util.regex.Pattern}, the    * result must match that pattern.    *    * @param query       SQL query    * @param typeChecker Checks whether the result is the expected type; must    *                    not be null    * @param result      Expected result, or matcher    */
specifier|default
name|void
name|check
parameter_list|(
name|String
name|query
parameter_list|,
name|TypeChecker
name|typeChecker
parameter_list|,
name|Object
name|result
parameter_list|)
block|{
name|check
argument_list|(
name|query
argument_list|,
name|typeChecker
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
name|ResultCheckers
operator|.
name|createChecker
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|default
name|void
name|check
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|Object
name|result
parameter_list|)
block|{
name|check
argument_list|(
name|query
argument_list|,
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
name|expectedType
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a SQL query returns a result of expected type and value.    * Checking of type and value are abstracted using {@link TypeChecker}    * and {@link ResultChecker} functors.    *    * @param query         SQL query    * @param typeChecker   Checks whether the result is the expected type    * @param parameterChecker Checks whether the parameters are of expected    *                      types    * @param resultChecker Checks whether the result has the expected value    */
specifier|default
name|void
name|check
parameter_list|(
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
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|getTester
argument_list|()
operator|.
name|check
argument_list|(
name|getFactory
argument_list|()
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
block|}
comment|/**    * Declares that this test is for a given operator. So we can check that all    * operators are tested.    *    * @param operator             Operator    * @param unimplementedVmNames Names of virtual machines for which this    */
name|SqlOperatorFixture
name|setFor
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|VmName
modifier|...
name|unimplementedVmNames
parameter_list|)
function_decl|;
comment|/**    * Checks that an aggregate expression returns the expected result.    *    *<p>For example,<code>checkAgg("AVG(DISTINCT x)", new String[] {"2", "3",    * null, "3" }, new Double(2.5), 0);</code>    *    * @param expr        Aggregate expression, e.g.<code>SUM(DISTINCT x)</code>    * @param inputValues Array of input values, e.g.<code>["1", null,    *                    "2"]</code>.    * @param checker     Result checker    */
name|void
name|checkAgg
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|ResultChecker
name|checker
parameter_list|)
function_decl|;
comment|/**    * Checks that an aggregate expression with multiple args returns the expected    * result.    *    * @param expr        Aggregate expression, e.g.<code>AGG_FUNC(x, x2, x3)</code>    * @param inputValues Nested array of input values, e.g.<code>[    *                    ["1", null, "2"]    *                    ["3", "4", null]    *                    ]</code>    * @param resultChecker Checks whether the result has the expected value    */
name|void
name|checkAggWithMultipleArgs
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
index|[]
name|inputValues
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
function_decl|;
comment|/**    * Checks that a windowed aggregate expression returns the expected result.    *    *<p>For example,<code>checkWinAgg("FIRST_VALUE(x)", new String[] {"2",    * "3", null, "3" }, "INTEGER NOT NULL", 2, 0d);</code>    *    * @param expr          Aggregate expression, e.g. {@code SUM(DISTINCT x)}    * @param inputValues   Array of input values, e.g. {@code ["1", null, "2"]}    * @param type          Expected result type    * @param resultChecker Checks whether the result has the expected value    */
name|void
name|checkWinAgg
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|String
name|windowSpec
parameter_list|,
name|String
name|type
parameter_list|,
name|ResultChecker
name|resultChecker
parameter_list|)
function_decl|;
comment|/**    * Tests that an aggregate expression fails at run time.    * @param expr An aggregate expression    * @param inputValues Array of input values    * @param expectedError Pattern for expected error    * @param runtime       If true, must fail at runtime; if false, must fail at    *                      validate time    */
name|void
name|checkAggFails
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression fails at run time.    *    * @param expression    SQL scalar expression    * @param expectedError Pattern for expected error. If !runtime, must    *                      include an error location.    * @param runtime       If true, must fail at runtime; if false, must fail at    *                      validate time    */
name|void
name|checkFails
parameter_list|(
name|StringAndPos
name|expression
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
function_decl|;
comment|/** As {@link #checkFails(StringAndPos, String, boolean)}, but with a string    * that contains carets. */
specifier|default
name|void
name|checkFails
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
name|checkFails
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|expression
argument_list|)
argument_list|,
name|expectedError
argument_list|,
name|runtime
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a SQL query fails at prepare time.    *    * @param sap           SQL query and error position    * @param expectedError Pattern for expected error. Must    *                      include an error location.    */
name|void
name|checkQueryFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|String
name|expectedError
parameter_list|)
function_decl|;
comment|/**    * Tests that a SQL query succeeds at prepare time.    *    * @param sql           SQL query    */
name|void
name|checkQuery
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
specifier|default
name|SqlOperatorFixture
name|withLibrary
parameter_list|(
name|SqlLibrary
name|library
parameter_list|)
block|{
return|return
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
name|FUN
argument_list|,
name|library
operator|.
name|fun
argument_list|)
argument_list|)
return|;
block|}
specifier|default
name|SqlOperatorFixture
name|forOracle
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
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
literal|"fun"
argument_list|,
literal|"oracle"
argument_list|)
argument_list|)
return|;
block|}
specifier|default
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
specifier|default
name|void
name|checkCastToApproxOkay
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|targetType
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
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
name|NON_NULLABLE_SUFFIX
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|default
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
name|NON_NULLABLE_SUFFIX
argument_list|)
expr_stmt|;
block|}
specifier|default
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
name|NON_NULLABLE_SUFFIX
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|default
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
specifier|default
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
specifier|default
name|void
name|checkCastToString
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
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
block|}
end_interface

end_unit

