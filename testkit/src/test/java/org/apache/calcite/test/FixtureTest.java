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
name|rel
operator|.
name|rules
operator|.
name|CoreRules
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
name|SqlParserFixture
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
name|opentest4j
operator|.
name|AssertionFailedError
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
name|atomic
operator|.
name|AtomicInteger
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
name|Predicate
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
name|fail
import|;
end_import

begin_comment
comment|/** Tests test fixtures.  *  *<p>The key feature of fixtures is that they work outside of Calcite core  * tests, and of course this test cannot verify that. So, additional tests will  * be needed elsewhere. The code might look similar in these additional tests,  * but the most likely breakages will be due to classes not being on the path.  *  * @see Fixtures */
end_comment

begin_class
specifier|public
class|class
name|FixtureTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DIFF_REPOS_MESSAGE
init|=
literal|"diffRepos is null; if you require a "
operator|+
literal|"DiffRepository, set it in "
operator|+
literal|"your test's fixture() method"
decl_stmt|;
comment|/** Tests that you can write parser tests via {@link Fixtures#forParser()}. */
annotation|@
name|Test
name|void
name|testParserFixture
parameter_list|()
block|{
comment|// 'as' as identifier is invalid with Core parser
specifier|final
name|SqlParserFixture
name|f
init|=
name|Fixtures
operator|.
name|forParser
argument_list|()
decl_stmt|;
name|f
operator|.
name|sql
argument_list|(
literal|"select ^as^ from t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"as\".*"
argument_list|)
expr_stmt|;
comment|// Postgres cast is invalid with core parser
name|f
operator|.
name|sql
argument_list|(
literal|"select 1 ^:^: integer as x"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \":\" at .*"
argument_list|)
expr_stmt|;
comment|// Backtick fails
name|f
operator|.
name|sql
argument_list|(
literal|"select ^`^foo` from `bar``"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Lexical error at line 1, column 8.  "
operator|+
literal|"Encountered: \"`\" \\(96\\), .*"
argument_list|)
expr_stmt|;
comment|// After changing config, backtick succeeds
name|f
operator|.
name|sql
argument_list|(
literal|"select `foo` from `bar`"
argument_list|)
operator|.
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BACK_TICK
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT `foo`\n"
operator|+
literal|"FROM `bar`"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that you can run validator tests via    * {@link Fixtures#forValidator()}. */
annotation|@
name|Test
name|void
name|testValidatorFixture
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|f
init|=
name|Fixtures
operator|.
name|forValidator
argument_list|()
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select ^1 + date '2002-03-04'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\+' to arguments of"
operator|+
literal|" type '<INTEGER> \\+<DATE>'.*"
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select 1 + 2 as three"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL THREE) NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that you can run operator tests via    * {@link Fixtures#forValidator()}. */
annotation|@
name|Test
name|void
name|testOperatorFixture
parameter_list|()
block|{
comment|// The first fixture only validates, does not execute.
specifier|final
name|SqlOperatorFixture
name|validateFixture
init|=
name|Fixtures
operator|.
name|forOperators
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|SqlOperatorFixture
name|executeFixture
init|=
name|Fixtures
operator|.
name|forOperators
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Passes with and without execution
name|validateFixture
operator|.
name|checkBoolean
argument_list|(
literal|"1< 5"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|executeFixture
operator|.
name|checkBoolean
argument_list|(
literal|"1< 5"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The fixture that executes fails, because the result value is incorrect.
name|validateFixture
operator|.
name|checkBoolean
argument_list|(
literal|"1< 5"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
parameter_list|()
lambda|->
name|executeFixture
operator|.
name|checkBoolean
argument_list|(
literal|"1< 5"
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"<false>"
argument_list|,
literal|"<true>"
argument_list|)
expr_stmt|;
comment|// The fixture that executes fails, because the result value is incorrect.
name|validateFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
parameter_list|()
lambda|->
name|validateFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"DATE"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
literal|"\"DATE\""
argument_list|,
literal|"\"INTEGER NOT NULL\""
argument_list|)
expr_stmt|;
comment|// Both fixtures pass.
name|validateFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|executeFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"INTEGER NOT NULL"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
comment|// Both fixtures fail, because the type is incorrect.
name|assertFails
argument_list|(
parameter_list|()
lambda|->
name|validateFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"DATE"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
literal|"\"DATE\""
argument_list|,
literal|"\"INTEGER NOT NULL\""
argument_list|)
expr_stmt|;
name|assertFails
argument_list|(
parameter_list|()
lambda|->
name|executeFixture
operator|.
name|checkScalarExact
argument_list|(
literal|"1 + 2"
argument_list|,
literal|"DATE"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
literal|"\"DATE\""
argument_list|,
literal|"\"INTEGER NOT NULL\""
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|assertFails
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|actual
parameter_list|)
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|String
name|expectedMessage
init|=
literal|"\n"
operator|+
literal|"Expected: is "
operator|+
name|expected
operator|+
literal|"\n"
operator|+
literal|"     but: was "
operator|+
name|actual
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests that you can run SQL-to-Rel tests via    * {@link Fixtures#forSqlToRel()}. */
annotation|@
name|Test
name|void
name|testSqlToRelFixture
parameter_list|()
block|{
specifier|final
name|SqlToRelFixture
name|f
init|=
name|Fixtures
operator|.
name|forSqlToRel
argument_list|()
operator|.
name|withDiffRepos
argument_list|(
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|FixtureTest
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select 1 from emp"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|/** Tests that we get a good error message if a test needs a diff repository.    *    * @see DiffRepository#castNonNull(DiffRepository) */
annotation|@
name|Test
name|void
name|testSqlToRelFixtureNeedsDiffRepos
parameter_list|()
block|{
try|try
block|{
specifier|final
name|SqlToRelFixture
name|f
init|=
name|Fixtures
operator|.
name|forSqlToRel
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select 1 from emp"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|DIFF_REPOS_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests the {@link SqlToRelFixture#ensuring(Predicate, UnaryOperator)}    * test infrastructure. */
annotation|@
name|Test
name|void
name|testSqlToRelFixtureEnsure
parameter_list|()
block|{
specifier|final
name|SqlToRelFixture
name|f
init|=
name|Fixtures
operator|.
name|forSqlToRel
argument_list|()
decl_stmt|;
comment|// Case 1. Predicate is true at first, remedy not needed
name|f
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
literal|true
argument_list|,
name|f2
lambda|->
block|{
throw|throw
argument_list|new
name|AssertionError
argument_list|(
literal|"remedy not needed"
argument_list|)
argument_list|;
block|}
block|)
class|;
end_class

begin_comment
comment|// Case 2. Predicate is false at first, true after we invoke the remedy.
end_comment

begin_decl_stmt
specifier|final
name|AtomicInteger
name|b
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|assertThat
argument_list|(
name|b
operator|.
name|intValue
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|f
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
name|b
operator|.
name|intValue
argument_list|()
operator|>
literal|0
argument_list|,
name|f2
lambda|->
block|{
name|b
operator|.
name|incrementAndGet
argument_list|()
argument_list|;       return
name|f2
argument_list|;
end_expr_stmt

begin_empty_stmt
unit|})
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertThat
argument_list|(
name|b
operator|.
name|intValue
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Case 3. Predicate is false at first, remains false after the "remedy" is
end_comment

begin_comment
comment|// invoked.
end_comment

begin_try
try|try
block|{
name|f
operator|.
name|ensuring
argument_list|(
name|f2
lambda|->
name|b
operator|.
name|intValue
argument_list|()
operator|<
literal|0
argument_list|,
name|f2
lambda|->
block|{
name|b
operator|.
name|incrementAndGet
argument_list|()
argument_list|;         return
name|f2
argument_list|;
block|}
end_try

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_throw
throw|throw
operator|new
name|AssertionFailedError
argument_list|(
literal|"expected AssertionError"
argument_list|)
throw|;
end_throw

begin_expr_stmt
unit|} catch
operator|(
name|AssertionError
name|e
operator|)
block|{
name|String
name|expectedMessage
operator|=
literal|"remedy failed\n"
operator|+
literal|"Expected: is<true>\n"
operator|+
literal|"     but: was<false>"
block|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedMessage
argument_list|)
argument_list|)
block|;     }
name|assertThat
argument_list|(
literal|"Remedy should be called, even though it is unsuccessful"
argument_list|,
name|b
operator|.
name|intValue
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** Tests that you can run RelRule tests via    * {@link Fixtures#forValidator()}. */
end_comment

begin_function
unit|@
name|Test
name|void
name|testRuleFixture
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from dept"
decl_stmt|;
specifier|final
name|RelOptFixture
name|f
init|=
name|Fixtures
operator|.
name|forRules
argument_list|()
operator|.
name|withDiffRepos
argument_list|(
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|FixtureTest
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withRule
argument_list|(
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** As {@link #testSqlToRelFixtureNeedsDiffRepos} but for    * {@link Fixtures#forRules()}. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testRuleFixtureNeedsDiffRepos
parameter_list|()
block|{
try|try
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from dept\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from dept"
decl_stmt|;
specifier|final
name|RelOptFixture
name|f
init|=
name|Fixtures
operator|.
name|forRules
argument_list|()
decl_stmt|;
name|f
operator|.
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withRule
argument_list|(
name|CoreRules
operator|.
name|UNION_TO_DISTINCT
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|DIFF_REPOS_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/** Tests metadata. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testMetadata
parameter_list|()
block|{
specifier|final
name|RelMetadataFixture
name|f
init|=
name|Fixtures
operator|.
name|forMetadata
argument_list|()
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select name as dname from dept"
argument_list|)
operator|.
name|assertColumnOriginSingle
argument_list|(
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select upper(name) as dname from dept"
argument_list|)
operator|.
name|assertColumnOriginSingle
argument_list|(
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select name||ename from dept,emp"
argument_list|)
operator|.
name|assertColumnOriginDouble
argument_list|(
literal|"DEPT"
argument_list|,
literal|"NAME"
argument_list|,
literal|"EMP"
argument_list|,
literal|"ENAME"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select 'Minstrelsy' as dname from dept"
argument_list|)
operator|.
name|assertColumnOriginIsEmpty
argument_list|()
expr_stmt|;
block|}
end_function

unit|}
end_unit

