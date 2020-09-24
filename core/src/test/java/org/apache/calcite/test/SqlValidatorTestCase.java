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
name|SqlCollation
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
name|SqlValidatorTester
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
name|catalog
operator|.
name|MockCatalogReaderExtended
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
name|testlib
operator|.
name|annotations
operator|.
name|WithLex
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
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|BeforeEachCallback
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
name|extension
operator|.
name|ExtensionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|platform
operator|.
name|commons
operator|.
name|support
operator|.
name|AnnotationSupport
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
name|Charset
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
name|function
operator|.
name|UnaryOperator
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
comment|/**  * An abstract base class for implementing tests against {@link SqlValidator}.  *  *<p>A derived class can refine this test in two ways. First, it can add<code>  * testXxx()</code> methods, to test more functionality.  *  *<p>Second, it can override the {@link #getTester} method to return a  * different implementation of the {@link Tester} object. This encapsulates the  * differences between test environments, for example, which SQL parser or  * validator to use.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorTestCase
block|{
specifier|private
specifier|static
specifier|final
name|SqlTestFactory
name|EXTENDED_TEST_FACTORY
init|=
name|SqlTestFactory
operator|.
name|INSTANCE
operator|.
name|withCatalogReader
argument_list|(
name|MockCatalogReaderExtended
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTester
name|EXTENDED_CATALOG_TESTER
init|=
operator|new
name|SqlValidatorTester
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTester
name|EXTENDED_CATALOG_TESTER_2003
init|=
operator|new
name|SqlValidatorTester
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTester
name|EXTENDED_CATALOG_TESTER_LENIENT
init|=
operator|new
name|SqlValidatorTester
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
specifier|protected
name|SqlTester
name|tester
decl_stmt|;
comment|/**    * Creates a test case.    */
specifier|public
name|SqlValidatorTestCase
parameter_list|()
block|{
name|this
operator|.
name|tester
operator|=
name|getTester
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns a tester. Derived classes should override this method to run the    * same set of tests in a different testing environment.    */
specifier|public
name|SqlTester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|SqlValidatorTester
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/** Creates a test context with a SQL query. */
specifier|public
specifier|final
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
name|tester
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Creates a test context with a SQL expression. */
specifier|public
specifier|final
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
name|tester
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Creates a test context with a SQL expression.    * If an error occurs, the error is expected to span the entire expression. */
specifier|public
specifier|final
name|Sql
name|wholeExpr
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Sql
name|winSql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|sql
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Sql
name|win
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|sql
argument_list|(
literal|"select * from emp "
operator|+
name|sql
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|winExp
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|winSql
argument_list|(
literal|"select "
operator|+
name|sql
operator|+
literal|" from emp window w as (order by deptno)"
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|winExp2
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|winSql
argument_list|(
literal|"select "
operator|+
name|sql
operator|+
literal|" from emp"
argument_list|)
return|;
block|}
comment|/**    * Encapsulates differences between test environments, for example, which    * SQL parser or validator to use.    *    *<p>It contains a mock schema with<code>EMP</code> and<code>DEPT</code>    * tables, which can run without having to start up Farrago.    */
specifier|public
interface|interface
name|Tester
block|{
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
function_decl|;
name|SqlNode
name|parseAndValidate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
comment|/**      * Checks that a query is valid, or, if invalid, throws the right      * message at the right location.      *      *<p>If<code>expectedMsgPattern</code> is null, the query must      * succeed.      *      *<p>If<code>expectedMsgPattern</code> is not null, the query must      * fail, and give an error location of (expectedLine, expectedColumn)      * through (expectedEndLine, expectedEndColumn).      *  @param sap                SQL statement      * @param expectedMsgPattern If this parameter is null the query must be      *                           valid for the test to pass; If this parameter      *                           is not null the query must be malformed and the      */
name|void
name|assertExceptionIsThrown
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
comment|/**      * Returns the data type of the sole column of a SQL query.      *      *<p>For example,<code>getResultType("VALUES (1")</code> returns      *<code>INTEGER</code>.      *      *<p>Fails if query returns more than one column.      *      * @see #getResultType(String)      */
name|RelDataType
name|getColumnType
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
comment|/**      * Returns the data type of the row returned by a SQL query.      *      *<p>For example,<code>getResultType("VALUES (1, 'foo')")</code>      * returns<code>RecordType(INTEGER EXPR$0, CHAR(3) EXPR#1)</code>.      */
name|RelDataType
name|getResultType
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
name|void
name|checkCollation
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedCollationName
parameter_list|,
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
parameter_list|)
function_decl|;
name|void
name|checkCharset
parameter_list|(
name|String
name|sql
parameter_list|,
name|Charset
name|expectedCharset
parameter_list|)
function_decl|;
comment|/**      * Checks that a query returns one column of an expected type. For      * example,<code>checkType("VALUES (1 + 2)", "INTEGER NOT      * NULL")</code>.      */
name|void
name|checkColumnType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Given a SQL query, returns a list of the origins of each result      * field.      *      * @param sql             SQL query      * @param fieldOriginList Field origin list, e.g.      *                        "{(CATALOG.SALES.EMP.EMPNO, null)}"      */
name|void
name|checkFieldOrigin
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|fieldOriginList
parameter_list|)
function_decl|;
comment|/**      * Checks that a query gets rewritten to an expected form.      *      * @param query           query to test      * @param expectedRewrite expected SQL text after rewrite and unparse      */
name|void
name|checkRewrite
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|expectedRewrite
parameter_list|)
function_decl|;
comment|/**      * Checks that a query returns one column of an expected type. For      * example,<code>checkType("select empno, name from emp""{EMPNO INTEGER      * NOT NULL, NAME VARCHAR(10) NOT NULL}")</code>.      */
name|void
name|checkResultType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Checks if the interval value conversion to milliseconds is valid. For      * example,<code>checkIntervalConv(VALUES (INTERVAL '1' Minute),      * "60000")</code>.      */
name|void
name|checkIntervalConv
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Given a SQL query, returns the monotonicity of the first item in the      * SELECT clause.      *      * @param sql SQL query      * @return Monotonicity      */
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlConformance
name|getConformance
parameter_list|()
function_decl|;
block|}
comment|/** Fluent testing API. */
specifier|static
class|class
name|Sql
block|{
specifier|private
specifier|final
name|SqlTester
name|tester
decl_stmt|;
specifier|private
specifier|final
name|StringAndPos
name|sap
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|query
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|whole
decl_stmt|;
comment|/** Creates a Sql.      *      * @param tester Tester      * @param sap SQL query or expression      * @param query True if {@code sql} is a query, false if it is an expression      * @param whole Whether the failure location is the whole query or      *              expression      */
name|Sql
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|StringAndPos
name|sap
parameter_list|,
name|boolean
name|query
parameter_list|,
name|boolean
name|whole
parameter_list|)
block|{
name|this
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|sap
operator|=
name|sap
expr_stmt|;
name|this
operator|.
name|whole
operator|=
name|whole
expr_stmt|;
block|}
name|Sql
name|withTester
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTester
argument_list|>
name|transform
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|transform
operator|.
name|apply
argument_list|(
name|tester
argument_list|)
argument_list|,
name|sap
argument_list|,
name|query
argument_list|,
name|whole
argument_list|)
return|;
block|}
specifier|public
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
name|tester
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
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
name|tester
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|StringAndPos
name|toSql
parameter_list|(
name|boolean
name|withCaret
parameter_list|)
block|{
specifier|final
name|String
name|sql2
init|=
name|withCaret
operator|&&
name|sap
operator|.
name|cursor
operator|>=
literal|0
condition|?
name|sap
operator|.
name|sql
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sap
operator|.
name|cursor
argument_list|)
operator|+
literal|"^"
operator|+
name|sap
operator|.
name|sql
operator|.
name|substring
argument_list|(
name|sap
operator|.
name|cursor
argument_list|)
else|:
name|sap
operator|.
name|sql
decl_stmt|;
return|return
name|query
condition|?
name|sap
else|:
name|StringAndPos
operator|.
name|of
argument_list|(
name|AbstractSqlTester
operator|.
name|buildQuery
argument_list|(
name|sap
operator|.
name|addCarets
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withExtendedCatalog
parameter_list|()
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|EXTENDED_CATALOG_TESTER
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withQuoting
parameter_list|(
name|Quoting
name|quoting
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withQuoting
argument_list|(
name|quoting
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withLex
parameter_list|(
name|Lex
name|lex
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withLex
argument_list|(
name|lex
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withTypeCoercion
parameter_list|(
name|boolean
name|typeCoercion
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|enableTypeCoercion
argument_list|(
name|typeCoercion
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|withWhole
parameter_list|(
name|boolean
name|whole
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|sap
operator|.
name|cursor
operator|<
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
literal|"^"
operator|+
name|sap
operator|.
name|sql
operator|+
literal|"^"
argument_list|)
argument_list|,
name|query
argument_list|,
name|whole
argument_list|)
return|;
block|}
name|Sql
name|ok
parameter_list|()
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|toSql
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Checks that a SQL expression gives a particular error.      */
name|Sql
name|fails
parameter_list|(
annotation|@
name|Nonnull
name|String
name|expected
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|toSql
argument_list|(
literal|true
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Checks that a SQL expression fails, giving an {@code expected} error,      * if {@code b} is true, otherwise succeeds.      */
name|Sql
name|failsIf
parameter_list|(
name|boolean
name|b
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
if|if
condition|(
name|b
condition|)
block|{
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ok
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Checks that a query returns a row of the expected type. For example,      *      *<blockquote>      *<code>sql("select empno, name from emp")<br>      *     .type("{EMPNO INTEGER NOT NULL, NAME VARCHAR(10) NOT NULL}");</code>      *</blockquote>      *      * @param expectedType Expected row type      */
specifier|public
name|Sql
name|type
parameter_list|(
name|String
name|expectedType
parameter_list|)
block|{
name|tester
operator|.
name|checkResultType
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|expectedType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Checks that a query returns a single column, and that the column has the      * expected type. For example,      *      *<blockquote>      *<code>sql("SELECT empno FROM Emp").columnType("INTEGER NOT NULL");</code>      *</blockquote>      *      * @param expectedType Expected type, including nullability      */
specifier|public
name|Sql
name|columnType
parameter_list|(
name|String
name|expectedType
parameter_list|)
block|{
name|tester
operator|.
name|checkColumnType
argument_list|(
name|toSql
argument_list|(
literal|false
argument_list|)
operator|.
name|sql
argument_list|,
name|expectedType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|monotonic
parameter_list|(
name|SqlMonotonicity
name|expectedMonotonicity
parameter_list|)
block|{
name|tester
operator|.
name|checkMonotonic
argument_list|(
name|toSql
argument_list|(
literal|false
argument_list|)
operator|.
name|sql
argument_list|,
name|expectedMonotonicity
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|bindType
parameter_list|(
specifier|final
name|String
name|bindType
parameter_list|)
block|{
name|tester
operator|.
name|check
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
literal|null
argument_list|,
name|parameterRowType
lambda|->
name|assertThat
argument_list|(
name|parameterRowType
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|bindType
argument_list|)
argument_list|)
argument_list|,
name|result
lambda|->
block|{ }
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|void
name|charset
parameter_list|(
name|Charset
name|expectedCharset
parameter_list|)
block|{
name|tester
operator|.
name|checkCharset
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|expectedCharset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|collation
parameter_list|(
name|String
name|expectedCollationName
parameter_list|,
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
parameter_list|)
block|{
name|tester
operator|.
name|checkCollation
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|expectedCollationName
argument_list|,
name|expectedCoercibility
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the interval value conversion to milliseconds is valid. For      * example,      *      *<blockquote>      *<code>sql("VALUES (INTERVAL '1' Minute)").intervalConv("60000");</code>      *</blockquote>      */
specifier|public
name|void
name|intervalConv
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkIntervalConv
argument_list|(
name|toSql
argument_list|(
literal|false
argument_list|)
operator|.
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sql
name|withCaseSensitive
parameter_list|(
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withCaseSensitive
argument_list|(
name|caseSensitive
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withOperatorTable
parameter_list|(
name|SqlOperatorTable
name|operatorTable
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withUnquotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
block|{
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|tester
operator|.
name|withUnquotedCasing
argument_list|(
name|casing
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|SqlTester
name|addTransform
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlValidator
argument_list|>
name|after
parameter_list|)
block|{
return|return
name|this
operator|.
name|tester
operator|.
name|withValidatorTransform
argument_list|(
name|transform
lambda|->
name|validator
lambda|->
name|after
operator|.
name|apply
argument_list|(
name|transform
operator|.
name|apply
argument_list|(
name|validator
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withValidatorIdentifierExpansion
parameter_list|(
name|boolean
name|expansion
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlValidator
argument_list|>
name|after
init|=
name|sqlValidator
lambda|->
name|sqlValidator
operator|.
name|transform
argument_list|(
name|config
lambda|->
name|config
operator|.
name|withIdentifierExpansion
argument_list|(
name|expansion
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|addTransform
argument_list|(
name|tester
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withValidatorCallRewrite
parameter_list|(
name|boolean
name|rewrite
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlValidator
argument_list|>
name|after
init|=
name|sqlValidator
lambda|->
name|sqlValidator
operator|.
name|transform
argument_list|(
name|config
lambda|->
name|config
operator|.
name|withCallRewrite
argument_list|(
name|rewrite
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|addTransform
argument_list|(
name|tester
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|withValidatorColumnReferenceExpansion
parameter_list|(
name|boolean
name|expansion
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|SqlValidator
argument_list|>
name|after
init|=
name|sqlValidator
lambda|->
name|sqlValidator
operator|.
name|transform
argument_list|(
name|config
lambda|->
name|config
operator|.
name|withColumnReferenceExpansion
argument_list|(
name|expansion
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|withTester
argument_list|(
name|tester
lambda|->
name|addTransform
argument_list|(
name|tester
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|rewritesTo
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkRewrite
argument_list|(
name|toSql
argument_list|(
literal|false
argument_list|)
operator|.
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**    * Enables to configure {@link #tester} behavior on a per-test basis.    * {@code tester} object is created in the test object constructor, and    * there's no trivial way to override its features.    *    *<p>This JUnit rule enables post-process test object on a per test method    * basis.    */
specifier|public
specifier|static
class|class
name|LexConfiguration
implements|implements
name|BeforeEachCallback
block|{
annotation|@
name|Override
specifier|public
name|void
name|beforeEach
parameter_list|(
name|ExtensionContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|getElement
argument_list|()
operator|.
name|flatMap
argument_list|(
name|element
lambda|->
name|AnnotationSupport
operator|.
name|findAnnotation
argument_list|(
name|element
argument_list|,
name|WithLex
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|lex
lambda|->
block|{
name|SqlValidatorTestCase
name|tc
operator|=
operator|(
name|SqlValidatorTestCase
operator|)
name|context
operator|.
name|getTestInstance
argument_list|()
operator|.
name|get
argument_list|()
argument_list|;
name|SqlTester
name|tester
operator|=
name|tc
operator|.
name|tester
argument_list|;
name|tester
operator|=
name|tester
operator|.
name|withLex
argument_list|(
name|lex
operator|.
name|value
argument_list|()
argument_list|)
argument_list|;
name|tc
operator|.
name|tester
operator|=
name|tester
argument_list|;
block|}
block|)
class|;
block|}
end_class

unit|} }
end_unit

