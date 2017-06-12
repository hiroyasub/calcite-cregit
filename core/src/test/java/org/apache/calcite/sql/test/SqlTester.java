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
name|SqlMonotonicity
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
name|SqlValidatorTestCase
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

begin_comment
comment|/**  * SqlTester defines a callback for testing SQL queries and expressions.  *  *<p>The idea is that when you define an operator (or another piece of SQL  * functionality), you can define the logical behavior of that operator once, as  * part of that operator. Later you can define one or more physical  * implementations of that operator, and test them all using the same set of  * tests.  *  *<p>Specific implementations of<code>SqlTester</code> might evaluate the  * queries in different ways, for example, using a C++ versus Java calculator.  * An implementation might even ignore certain calls altogether.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlTester
extends|extends
name|AutoCloseable
extends|,
name|SqlValidatorTestCase
operator|.
name|Tester
block|{
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
name|SqlTestFactory
name|getFactory
parameter_list|()
function_decl|;
comment|/** Returns a tester that tests a given SQL quoting style. */
name|SqlTester
name|withQuoting
parameter_list|(
name|Quoting
name|quoting
parameter_list|)
function_decl|;
comment|/** Returns a tester that applies a given casing policy to quoted    * identifiers. */
name|SqlTester
name|withQuotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
function_decl|;
comment|/** Returns a tester that applies a given casing policy to unquoted    * identifiers. */
name|SqlTester
name|withUnquotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
function_decl|;
comment|/** Returns a tester that matches identifiers by case-sensitive or    * case-insensitive. */
name|SqlTester
name|withCaseSensitive
parameter_list|(
name|boolean
name|sensitive
parameter_list|)
function_decl|;
comment|/** Returns a tester that follows a lex policy. */
name|SqlTester
name|withLex
parameter_list|(
name|Lex
name|lex
parameter_list|)
function_decl|;
comment|/** Returns a tester that tests conformance to a particular SQL language    * version. */
name|SqlTester
name|withConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
function_decl|;
comment|/** Returns a tester that gets connections from a given factory. */
name|SqlTester
name|withConnectionFactory
parameter_list|(
name|CalciteAssert
operator|.
name|ConnectionFactory
name|connectionFactory
parameter_list|)
function_decl|;
comment|/** Returns a tester that uses a given operator table. */
name|SqlTester
name|withOperatorTable
parameter_list|(
name|SqlOperatorTable
name|operatorTable
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected result and the    * expected type. For example,    *    *<blockquote>    *<pre>checkScalar("1.1 + 2.9", "4.0", "DECIMAL(2, 1) NOT NULL");</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result    * @param resultType Expected result type    */
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
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected exact numeric    * result as an integer. For example,    *    *<blockquote>    *<pre>checkScalarExact("1 + 2", "3");</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result    */
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|result
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected exact numeric    * result. For example,    *    *<blockquote>    *<pre>checkScalarExact("1 + 2", "3");</pre>    *</blockquote>    *    * @param expression   Scalar expression    * @param expectedType Type we expect the result to have, including    *                     nullability, precision and scale, for example    *<code>DECIMAL(2, 1) NOT NULL</code>.    * @param result       Expected result    */
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
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns expected appoximate numeric    * result. For example,    *    *<blockquote>    *<pre>checkScalarApprox("1.0 + 2.1", "3.1");</pre>    *</blockquote>    *    * @param expression     Scalar expression    * @param expectedType   Type we expect the result to have, including    *                       nullability, precision and scale, for example    *<code>DECIMAL(2, 1) NOT NULL</code>.    * @param expectedResult Expected result    * @param delta          Allowed margin of error between expected and actual    *                       result    */
name|void
name|checkScalarApprox
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|double
name|expectedResult
parameter_list|,
name|double
name|delta
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression returns the expected boolean result.    * For example,    *    *<blockquote>    *<pre>checkScalarExact("TRUE AND FALSE", Boolean.TRUE);</pre>    *</blockquote>    *    *<p>The expected result can be null:    *    *<blockquote>    *<pre>checkScalarExact("NOT UNKNOWN", null);</pre>    *</blockquote>    *    * @param expression Scalar expression    * @param result     Expected result (null signifies NULL).    */
name|void
name|checkBoolean
parameter_list|(
name|String
name|expression
parameter_list|,
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
comment|/**    * Tests that a SQL query returns a single column with the given type. For    * example,    *    *<blockquote>    *<pre>check("VALUES (1 + 2)", "3", SqlTypeName.Integer);</pre>    *</blockquote>    *    *<p>If<code>result</code> is null, the expression must yield the SQL NULL    * value. If<code>result</code> is a {@link java.util.regex.Pattern}, the    * result must match that pattern.    *    * @param query       SQL query    * @param typeChecker Checks whether the result is the expected type; must    *                    not be null    * @param result      Expected result    * @param delta       The acceptable tolerance between the expected and actual    */
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
parameter_list|,
name|double
name|delta
parameter_list|)
function_decl|;
comment|/**    * Tests that a SQL query returns a result of expected type and value.    * Checking of type and value are abstracted using {@link TypeChecker}    * and {@link ResultChecker} functors.    *    * @param query         SQL query    * @param typeChecker   Checks whether the result is the expected type; must    *                      not be null    * @param parameterChecker Checks whether the parameters are of expected    *                      types    * @param resultChecker Checks whether the result has the expected value;    *                      must not be null    */
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
function_decl|;
comment|/**    * Tests that the first column of a SQL query has a given monotonicity.    *    * @param expectedMonotonicity Expected monotonicity    * @param query                SQL query    */
name|void
name|checkMonotonic
parameter_list|(
name|String
name|query
parameter_list|,
name|SqlMonotonicity
name|expectedMonotonicity
parameter_list|)
function_decl|;
comment|/**    * Declares that this test is for a given operator. So we can check that all    * operators are tested.    *    * @param operator             Operator    * @param unimplementedVmNames Names of virtual machines for which this    */
name|void
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
comment|/**    * Checks that an aggregate expression returns the expected result.    *    *<p>For example,<code>checkAgg("AVG(DISTINCT x)", new String[] {"2", "3",    * null, "3" }, new Double(2.5), 0);</code>    *    * @param expr        Aggregate expression, e.g.<code>SUM(DISTINCT x)</code>    * @param inputValues Array of input values, e.g.<code>["1", null,    *                    "2"]</code>.    * @param result      Expected result    * @param delta       Allowable variance from expected result    */
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
name|Object
name|result
parameter_list|,
name|double
name|delta
parameter_list|)
function_decl|;
comment|/**    * Checks that a windowed aggregate expression returns the expected result.    *    *<p>For example,<code>checkWinAgg("FIRST_VALUE(x)", new String[] {"2",    * "3", null, "3" }, "INTEGER NOT NULL", 2, 0d);</code>    *    * @param expr        Aggregate expression, e.g.<code>SUM(DISTINCT x)</code>    * @param inputValues Array of input values, e.g.<code>["1", null,    *                    "2"]</code>.    * @param type        Expected result type    * @param result      Expected result    * @param delta       Allowable variance from expected result    */
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
name|Object
name|result
parameter_list|,
name|double
name|delta
parameter_list|)
function_decl|;
comment|/**    * Tests that a scalar SQL expression fails at run time.    *    * @param expression    SQL scalar expression    * @param expectedError Pattern for expected error. If !runtime, must    *                      include an error location.    * @param runtime       If true, must fail at runtime; if false, must fail at    *                      validate time    */
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
function_decl|;
comment|/**    * Tests that a SQL query fails at prepare time.    *    * @param sql           SQL query    * @param expectedError Pattern for expected error. Must    *                      include an error location.    */
name|void
name|checkQueryFails
parameter_list|(
name|String
name|sql
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
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/** Type checker. */
interface|interface
name|TypeChecker
block|{
name|void
name|checkType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
block|}
comment|/** Parameter checker. */
interface|interface
name|ParameterChecker
block|{
name|void
name|checkParameters
parameter_list|(
name|RelDataType
name|parameterRowType
parameter_list|)
function_decl|;
block|}
comment|/** Result checker. */
interface|interface
name|ResultChecker
block|{
name|void
name|checkResult
parameter_list|(
name|ResultSet
name|result
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End SqlTester.java
end_comment

end_unit

