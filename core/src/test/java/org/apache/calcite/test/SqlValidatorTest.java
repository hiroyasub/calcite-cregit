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
name|avatica
operator|.
name|util
operator|.
name|TimeUnit
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
name|SqlSpecialOperator
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
name|type
operator|.
name|ArraySqlType
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
name|SqlTypeFactoryImpl
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
name|SqlAbstractConformance
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
name|SqlDelegatingConformance
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorCatalogReader
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
name|SqlValidatorUtil
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
name|CountingFactory
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
name|LocaleEnUs
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
name|ValidationException
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
name|ImmutableBitSet
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
name|Ordering
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
name|DisplayName
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|function
operator|.
name|BiConsumer
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|Matchers
operator|.
name|isCharset
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
name|nullValue
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
name|assertNull
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
name|assertTrue
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_comment
comment|/**  * Concrete child class of {@link SqlValidatorTestCase}, containing lots of unit  * tests.  *  *<p>If you want to run these same tests in a different environment, create a  * derived class whose {@link #fixture()} returns a different implementation of  * {@link SqlValidatorFixture}.  */
end_comment

begin_class
annotation|@
name|LocaleEnUs
specifier|public
class|class
name|SqlValidatorTest
extends|extends
name|SqlValidatorTestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// CHECKSTYLE: IGNORE 1
comment|/**    * @deprecated Deprecated so that usages of this constant will show up in    * yellow in Intellij and maybe someone will fix them.    */
specifier|protected
specifier|static
specifier|final
name|boolean
name|TODO
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ANY
init|=
literal|"(?s).*"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SqlValidatorTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERR_IN_VALUES_INCOMPATIBLE
init|=
literal|"Values in expression list must have compatible types"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERR_IN_OPERANDS_INCOMPATIBLE
init|=
literal|"Values passed to IN operator must have compatible types"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERR_AGG_IN_GROUP_BY
init|=
literal|"Aggregate expression is illegal in GROUP BY clause"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERR_AGG_IN_ORDER_BY
init|=
literal|"Aggregate expression is illegal in ORDER BY clause of non-aggregating SELECT"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ERR_NESTED_AGG
init|=
literal|"Aggregate expressions cannot be nested"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMP_RECORD_TYPE
init|=
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER) NOT NULL"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STR_AGG_REQUIRES_MONO
init|=
literal|"Streaming aggregation "
operator|+
literal|"requires at least one monotonic expression in GROUP BY clause"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STR_ORDER_REQUIRES_MONO
init|=
literal|"Streaming ORDER BY must start with monotonic expression"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STR_SET_OP_INCONSISTENT
init|=
literal|"Set operator cannot combine streaming and non-streaming inputs"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
init|=
literal|"ROW/RANGE not allowed with RANK, DENSE_RANK or ROW_NUMBER functions"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RANK_REQUIRES_ORDER_BY
init|=
literal|"RANK or DENSE_RANK "
operator|+
literal|"functions require ORDER BY clause in window specification"
decl_stmt|;
specifier|private
specifier|static
name|String
name|cannotConvertToStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|"Cannot convert table '"
operator|+
name|name
operator|+
literal|"' to stream"
return|;
block|}
specifier|private
specifier|static
name|String
name|cannotConvertToRelation
parameter_list|(
name|String
name|table
parameter_list|)
block|{
return|return
literal|"Cannot convert stream '"
operator|+
name|table
operator|+
literal|"' to relation"
return|;
block|}
specifier|private
specifier|static
name|String
name|cannotStreamResultsForNonStreamingInputs
parameter_list|(
name|String
name|inputs
parameter_list|)
block|{
return|return
literal|"Cannot stream results of a query with no streaming inputs: '"
operator|+
name|inputs
operator|+
literal|"'. At least one input should be convertible to a stream"
return|;
block|}
specifier|static
name|SqlOperatorTable
name|operatorTableFor
parameter_list|(
name|SqlLibrary
name|library
parameter_list|)
block|{
return|return
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
return|;
block|}
annotation|@
name|Test
name|void
name|testMultipleSameAsPass
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 as again,2 as \"again\", 3 as AGAiN from (values (true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultipleDifferentAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 as c1,2 as c2 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTypeOfAs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 as c1 from (values (true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 'hej' as c1 from (values (true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select x'deadbeef' as c1 from (values (true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(4) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(null as boolean) as c1 from (values (true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTypesLiterals
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'abc'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"n'abc'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_UTF16'abc'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'ab '\n"
operator|+
literal|"' cd'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'ab'\n"
operator|+
literal|"'cd'\n"
operator|+
literal|"'ef'\n"
operator|+
literal|"'gh'\n"
operator|+
literal|"'ij'\n"
operator|+
literal|"'kl'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(12) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"n'ab '\n"
operator|+
literal|"' cd'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_UTF16'ab '\n"
operator|+
literal|"' cd'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^x'abc'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Binary literal string must contain an even number of hexits"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'abcd'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'abcd'\n"
operator|+
literal|"'ff001122aabb'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(8) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"x'aaaa'\n"
operator|+
literal|"'bbbb'\n"
operator|+
literal|"'0000'\n"
operator|+
literal|"'1111'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(8) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1234567890"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"123456.7890"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"123456.7890e3"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"unknown"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that date-time literals with invalid strings are considered invalid.    * Originally the parser did that checking, but now the parser creates a    * {@link org.apache.calcite.sql.SqlUnknownLiteral} and the checking is    * deferred to the validator. */
annotation|@
name|Test
name|void
name|testLiteral
parameter_list|()
block|{
name|expr
argument_list|(
literal|"^DATE '12/21/99'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal DATE literal.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^TIME '1230:33'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^TIME '12:00:00 PM'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIME literal.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^TIMESTAMP '12-21-99, 12:30:00'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIMESTAMP literal.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^TIMESTAMP WITH LOCAL TIME ZONE '12-21-99, 12:30:00'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal TIMESTAMP WITH LOCAL TIME ZONE literal.*"
argument_list|)
expr_stmt|;
block|}
comment|/** PostgreSQL and Redshift allow TIMESTAMP literals that contain only a    * date part. */
annotation|@
name|Test
name|void
name|testShortTimestampLiteral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select timestamp '1969-07-20'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// PostgreSQL allows the following. We should too.
name|sql
argument_list|(
literal|"select ^timestamp '1969-07-20 1:2'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal TIMESTAMP literal '1969-07-20 1:2': not in format "
operator|+
literal|"'yyyy-MM-dd HH:mm:ss'"
argument_list|)
expr_stmt|;
comment|// PostgreSQL gives 1969-07-20 01:02:00
name|sql
argument_list|(
literal|"select ^timestamp '1969-07-20:23:'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal TIMESTAMP literal '1969-07-20:23:': not in format "
operator|+
literal|"'yyyy-MM-dd HH:mm:ss'"
argument_list|)
expr_stmt|;
comment|// PostgreSQL gives 1969-07-20 23:00:00
block|}
annotation|@
name|Test
name|void
name|testBooleans
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select TRUE OR unknowN from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select false AND unknown from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select not UNKNOWn from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select not true from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select not false from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAndOrIllegalTypesFails
parameter_list|()
block|{
comment|// TODO need col+line number
name|wholeExpr
argument_list|(
literal|"'abc' AND FaLsE"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*'<CHAR.3.> AND<BOOLEAN>'.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TRUE OR 1"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"unknown OR 1.0"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"true OR 1.0e4"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|wholeExpr
argument_list|(
literal|"TRUE OR (TIME '12:00' AT LOCAL)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testNotIllegalTypeFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^NOT 3.141^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'NOT' to arguments of type "
operator|+
literal|"'NOT<DECIMAL.4, 3.>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^NOT 'abc'^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^NOT 1^ from (values(true))"
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
name|testIs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select TRUE IS FALSE FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select false IS NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select UNKNOWN IS NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select FALSE IS UNKNOWN FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select TRUE IS NOT FALSE FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select TRUE IS NOT NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select false IS NOT NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select UNKNOWN IS NOT NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select FALSE IS NOT UNKNOWN FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 IS NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2 IS NULL FROM (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"^'abc' IS NOT UNKNOWN^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIsFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^1 IS TRUE^ FROM (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*'<INTEGER> IS TRUE'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^1.1 IS NOT FALSE^ FROM (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^1.1e1 IS NOT FALSE^ FROM (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'IS NOT FALSE' to arguments of type "
operator|+
literal|"'<DOUBLE> IS NOT FALSE'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^'abc' IS NOT TRUE^ FROM (values(true))"
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
name|testScalars
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1  + 1 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  + 2.3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2+3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2+3.4 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  - 1 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  - 2.3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2-3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2-3.4 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  * 2 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2* 3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  * 2.3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2* 3.4 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  / 2 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1  / 2.3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2/ 3 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1.2/3.4 from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testScalarsFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^1+TRUE^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\+' to arguments of type "
operator|+
literal|"'<INTEGER> \\+<BOOLEAN>'\\. Supported form\\(s\\):.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNumbers
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1+-2.*-3.e-1/-4>+5 AND true from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPrefix
parameter_list|()
block|{
name|expr
argument_list|(
literal|"+interval '1' second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-interval '1' month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ^-'abc'^ from (values(true))"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '-' to arguments of type '-<CHAR.3.>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT -'abc' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ^+'abc'^ from (values(true))"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\+' to arguments of type '\\+<CHAR.3.>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT +'abc' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNiladicForBigQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select current_time, current_time(), current_date, "
operator|+
literal|"current_date(), current_timestamp, current_timestamp()"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|()
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
literal|"''=''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'=n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"''=_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n''=''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n'abc'=n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n''=_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1''=''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1''=n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1''=_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"''<>''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'<>n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"''<>_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n''<>''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n'abc'<>n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"n''<>_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1''<>''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1'abc'<>n''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"_latin1''<>_latin1''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"true=false"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"unknown<>true"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1=1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1=.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1=1e-1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1=1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1=0.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1=1e1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e1=1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e1=1.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e-1=1e1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"''<>''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1<>1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1<>.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1<>1e-1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1<>1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1<>0.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"0.1<>1e1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e1<>1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e1<>1.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.1e-1<>1e1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualNotEqualFails
parameter_list|()
block|{
comment|// compare CHAR, INTEGER ok; implicitly convert CHAR
name|expr
argument_list|(
literal|"''<>1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'1'>=1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// compare INTEGER, NCHAR ok
name|expr
argument_list|(
literal|"1<>n'abc'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// compare CHAR, DECIMAL ok
name|expr
argument_list|(
literal|"''=.1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"true<>1e-1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"^true<>1e-1^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '<>' to arguments of type '<BOOLEAN><><DOUBLE>'.*"
argument_list|)
expr_stmt|;
comment|// compare BOOLEAN, CHAR ok
name|expr
argument_list|(
literal|"false=''"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"^x'a4'=0.01^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type "
operator|+
literal|"'<BINARY.1.> =<DECIMAL.3, 2.>'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^x'a4'=1^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type '<BINARY.1.> =<INTEGER>'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^x'13'<>0.01^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '<>' to arguments of type "
operator|+
literal|"'<BINARY.1.><><DECIMAL.3, 2.>'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^x'abcd'<>1^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '<>' to arguments of type "
operator|+
literal|"'<BINARY.2.><><INTEGER>'.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBinaryString
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select x'face'=X'' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select x'ff'=X'' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBinaryStringFails
parameter_list|()
block|{
name|expr
argument_list|(
literal|"select x'ffee'='abc' from (values(true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^x'ffee'='abc'^ from (values(true))"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type "
operator|+
literal|"'<BINARY.2.> =<CHAR.3.>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^x'ff'=88^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type "
operator|+
literal|"'<BINARY.1.> =<INTEGER>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^x''<>1.1e-1^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '<>' to arguments of type "
operator|+
literal|"'<BINARY.0.><><DOUBLE>'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^x''<>1.1^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '<>' to arguments of type "
operator|+
literal|"'<BINARY.0.><><DECIMAL.2, 1.>'.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringLiteral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select n''=_iso-8859-1'abc' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select N'f'<>'''' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringLiteralBroken
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 'foo'\n"
operator|+
literal|"'bar' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo'\r'bar' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo'\n\r'bar' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo'\r\n'bar' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo'\n'bar' from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo' /* comment */ ^'bar'^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"String literal continued on same line"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo' -- comment\r from (values(true))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo' ^'bar'^ from (values(true))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"String literal continued on same line"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArithmeticOperators
parameter_list|()
block|{
name|expr
argument_list|(
literal|"power(2,3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"aBs(-2.3e-2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"MOD(5             ,\t\f\r\n2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"ln(5.43  )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"log10(- -.2  )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"mod(5.1, 3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"mod(2,5.1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"5.1 % 3"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"2 % 5.1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"exp(3.67)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testArithmeticOperatorsFails
parameter_list|()
block|{
name|expr
argument_list|(
literal|"^power(2,'abc')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'POWER' to arguments of type "
operator|+
literal|"'POWER.<INTEGER>,<CHAR.3.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"power(2,'abc')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^power(true,1)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'POWER' to arguments of type "
operator|+
literal|"'POWER.<BOOLEAN>,<INTEGER>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^mod(x'1100',1)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'MOD' to arguments of type "
operator|+
literal|"'MOD.<BINARY.2.>,<INTEGER>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^mod(1, x'1100')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'MOD' to arguments of type "
operator|+
literal|"'MOD.<INTEGER>,<BINARY.2.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^abs(x'')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'ABS' to arguments of type 'ABS.<BINARY.0.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^ln(x'face12')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'LN' to arguments of type 'LN.<BINARY.3.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^log10(x'fa')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'LOG10' to arguments of type 'LOG10.<BINARY.1.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^exp('abc')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'EXP' to arguments of type 'EXP.<CHAR.3.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"exp('abc')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCaseExpression
parameter_list|()
block|{
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' else 'more' end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' when 2 then null else 'more' end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"case when TRUE then 'true' else 'false' end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"values case when TRUE then 'true' else 'false' end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1 WHEN 1 THEN cast(null as integer) WHEN 2 THEN null END"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1 WHEN 1 THEN cast(null as integer) WHEN 2 THEN cast(null as "
operator|+
literal|"integer) END"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1 WHEN 1 THEN null WHEN 2 THEN cast(null as integer) END"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1 WHEN 1 THEN cast(null as integer) WHEN 2 THEN cast(cast(null"
operator|+
literal|" as tinyint) as integer) END"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCaseExpressionTypes
parameter_list|()
block|{
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' else 'not one' end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(7) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case when 2<1 then 'impossible' end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(10)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 'one' when 'two' then 2.00 when 'one' then 1.3 else 3.2 end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(3, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 'one' when 'two' then 2 when 'one' then 1.00 else 3 end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(12, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then 'one' when 2 then null else 'more' end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(4)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case when TRUE then 'true' else 'false' end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1 WHEN 1 THEN cast(null as integer) END"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1\n"
operator|+
literal|"WHEN 1 THEN NULL\n"
operator|+
literal|"WHEN 2 THEN cast(cast(null as tinyint) as integer) END"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1\n"
operator|+
literal|"WHEN 1 THEN cast(null as integer)\n"
operator|+
literal|"WHEN 2 THEN cast(null as integer) END"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1\n"
operator|+
literal|"WHEN 1 THEN cast(null as integer)\n"
operator|+
literal|"WHEN 2 THEN cast(cast(null as tinyint) as integer)\n"
operator|+
literal|"END"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CASE 1\n"
operator|+
literal|"WHEN 1 THEN INTERVAL '12 3:4:5.6' DAY TO SECOND(6)\n"
operator|+
literal|"WHEN 2 THEN INTERVAL '12 3:4:5.6' DAY TO SECOND(9)\n"
operator|+
literal|"END"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND(9)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select\n"
operator|+
literal|"CASE WHEN job is not null THEN mgr\n"
operator|+
literal|"ELSE 5 end as mgr\n"
operator|+
literal|"from EMP"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCaseExpressionFails
parameter_list|()
block|{
comment|// varchar not comparable with bit string
name|expr
argument_list|(
literal|"case 'string' when x'01' then 'zero one' else 'something' end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"case 'string' when x'01' then 'zero one' else 'something' end"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type '<CHAR.6.> =<BINARY.1.>'.*"
argument_list|)
expr_stmt|;
comment|// all thens and else return null
name|wholeExpr
argument_list|(
literal|"case 1 when 1 then null else null end"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*ELSE clause or at least one THEN clause must be non-NULL.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then null else null end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
comment|// all thens and else return null
name|wholeExpr
argument_list|(
literal|"case 1 when 1 then null end"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*ELSE clause or at least one THEN clause must be non-NULL.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"case 1 when 1 then null end"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"case when true and true then 1 "
operator|+
literal|"when false then 2 "
operator|+
literal|"when false then true "
operator|+
literal|"else "
operator|+
literal|"case when true then 3 end end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal mixing of types in CASE or COALESCE statement"
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
literal|"nullif(1,2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"nullif(1,2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"nullif('a','b')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"nullif(345.21, 2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"nullif(345.21, 2e0)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 2)"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"nullif(1,2,3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'NULLIF'. Was "
operator|+
literal|"expecting 2 arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCoalesce
parameter_list|()
block|{
name|expr
argument_list|(
literal|"coalesce('a','b')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"coalesce('a','b','c')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select COALESCE(mgr, 12) as m from EMP"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCoalesceFails
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"coalesce('a',1)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal mixing of types in CASE or COALESCE statement"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"coalesce('a',1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"coalesce('a','b',1)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal mixing of types in CASE or COALESCE statement"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"coalesce('a','b',1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringCompare
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a' = 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'<> 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'> 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'< 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'>= 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'<= 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))>cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))<cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))>=cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))<=cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))=cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"cast('' as varchar(1))<>cast('' as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testStringCompareType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a' = 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'<> 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'> 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'< 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'>= 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'<= 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CAST(NULL AS VARCHAR(33))> 'foo'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
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
literal|"'a'||'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"x'12'||x'34'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a'||'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('a' as char(1))||cast('b' as char(2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as char(1))||cast('b' as char(2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'||'b'||'c'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'||'b'||'cde'||'f'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a'||'b'||cast('cde' as VARCHAR(3))|| 'f'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"_UTF16'a'||_UTF16'b'||_UTF16'c'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConcatWithCharset
parameter_list|()
block|{
name|sql
argument_list|(
literal|"_UTF16'a'||_UTF16'b'||_UTF16'c'"
argument_list|)
operator|.
name|assertCharset
argument_list|(
name|isCharset
argument_list|(
literal|"UTF-16LE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConcatFails
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"'a'||x'ff'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\|\\|' to arguments of type "
operator|+
literal|"'<CHAR.1.> \\|\\|<BINARY.1.>'.*Supported form.s.: "
operator|+
literal|"'<STRING> \\|\\|<STRING>.*'"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the CONCAT function, which unlike the concat operator ('||') is not    * standard but only in the ORACLE and POSTGRESQL libraries. */
annotation|@
name|Test
name|void
name|testConcatFunction
parameter_list|()
block|{
comment|// CONCAT is not in the library operator table
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('a', 'b')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(x'12', x'34')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(_UTF16'a', _UTF16'b', _UTF16'c')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('aabbcc', 'ab', '+-')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('aabbcc', CAST(NULL AS VARCHAR(20)), '+-')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(28)"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('aabbcc', 2)"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'CONCAT' to arguments of type "
operator|+
literal|"'CONCAT\\(<CHAR\\(6\\)>,<INTEGER>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('aabbcc', 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('abc', 'ab', 123)"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'CONCAT' to arguments of type "
operator|+
literal|"'CONCAT\\(<CHAR\\(3\\)>,<CHAR\\(2\\)>,<INTEGER>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('abc', 'ab', 123)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(true, false)"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'CONCAT' to arguments of type "
operator|+
literal|"'CONCAT\\(<BOOLEAN>,<BOOLEAN>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(true, false)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(DATE '2020-04-17', TIMESTAMP '2020-04-17 14:17:51')"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'CONCAT' to arguments of type "
operator|+
literal|"'CONCAT\\(<DATE>,<TIMESTAMP\\(0\\)>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat(DATE '2020-04-17', TIMESTAMP '2020-04-17 14:17:51')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testBetween
parameter_list|()
block|{
name|expr
argument_list|(
literal|"1 between 2 and 3"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a' between 'b' and 'c'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// can implicitly convert CHAR to INTEGER
name|expr
argument_list|(
literal|"'' between 2 and 3"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"date '2012-02-03' between 2 and 3"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'BETWEEN ASYMMETRIC' to arguments of type.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCharsetMismatch
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"''=_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply .* to the two different charsets ISO-8859-1 and "
operator|+
literal|"UTF-16LE"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''<>_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply .* to the two different charsets.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''>_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply .* to the two different charsets.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''<_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply .* to the two different charsets.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''<=_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply .* to the two different charsets.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''>=_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply .* to the two different charsets.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"''||_UTF16''"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"'a'||'b'||_UTF16'c'"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
block|}
comment|// FIXME jvs 2-Feb-2005:
annotation|@
name|Disabled
argument_list|(
literal|"all collation-related tests are disabled due to dtbug 280"
argument_list|)
name|void
name|testSimpleCollate
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'s' collate latin1$en$1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'s' collate latin1$en$1"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"'s'"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$en_US$primary"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|COERCIBLE
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"'s' collate latin1$sv$3"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$sv$3"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"all collation-related tests are disabled due to dtbug 280"
argument_list|)
name|void
name|testCharsetAndCollateMismatch
parameter_list|()
block|{
comment|// todo
name|expr
argument_list|(
literal|"_UTF16's' collate latin1$en$1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"all collation-related tests are disabled due to dtbug 280"
argument_list|)
name|void
name|testDyadicCollateCompare
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'s' collate latin1$en$1< 't'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'t'> 's' collate latin1$en$1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'s' collate latin1$en$1<> 't' collate latin1$en$1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"all collation-related tests are disabled due to dtbug 280"
argument_list|)
name|void
name|testDyadicCompareCollateFails
parameter_list|()
block|{
comment|// two different explicit collations. difference in strength
name|expr
argument_list|(
literal|"'s' collate latin1$en$1<= 't' collate latin1$en$2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Two explicit different collations.*are illegal.*"
argument_list|)
expr_stmt|;
comment|// two different explicit collations. difference in language
name|expr
argument_list|(
literal|"'s' collate latin1$sv$1>= 't' collate latin1$en$1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Two explicit different collations.*are illegal.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"all collation-related tests are disabled due to dtbug 280"
argument_list|)
name|void
name|testDyadicCollateOperator
parameter_list|()
block|{
name|sql
argument_list|(
literal|"'a' || 'b'"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$en_US$primary"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|COERCIBLE
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"'a' collate latin1$sv$3 || 'b'"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$sv$3"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"'a' collate latin1$sv$3 || 'b' collate latin1$sv$3"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$sv$3"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
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
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"char_length(_UTF16'string')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"character_length('string')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"char_length('string')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"character_length('string')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUpperLower
parameter_list|()
block|{
name|expr
argument_list|(
literal|"upper(_UTF16'sadf')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"lower(n'sadf')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"lower('sadf')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"upper(123)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'UPPER' to arguments of type 'UPPER.<INTEGER>.'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"upper(123)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
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
literal|"position('mouse' in 'house')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"position(x'11' in x'100110')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"position(x'11' in x'100110' FROM 10)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"position(x'abcd' in x'')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"position('mouse' in 'house')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"position(x'1234' in '110')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"position(x'1234' in '110' from 3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
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
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim(both 'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim(leading 'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim(trailing 'mustache' FROM 'beard')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim('mustache' FROM 'beard')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim('beard  ')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(7) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim('mustache' FROM cast(null as varchar(4)))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(4)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
specifier|final
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
init|=
literal|null
decl_stmt|;
name|sql
argument_list|(
literal|"trim('mustache' FROM 'beard')"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"CHAR(5)"
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedCoercibility
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testTrimFails
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"trim(123 FROM 'beard')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'TRIM' to arguments of type.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim(123 FROM 'beard')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"trim('a' FROM 123)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'TRIM' to arguments of type.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"trim('a' FROM 123)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"trim('a' FROM _UTF16'b')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*not comparable to each other.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
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
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"translate('abc' using translation)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTranslate3
parameter_list|()
block|{
comment|// TRANSLATE3 is not in the standard operator table
name|wholeExpr
argument_list|(
literal|"translate('aabbcc', 'ab', '+-')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"TRANSLATE3\\(<CHARACTER>,<CHARACTER>,<CHARACTER>\\)"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|expr
argument_list|(
literal|"translate('aabbcc', 'ab', '+-')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(6) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"translate('abc', 'ab')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TRANSLATE3'. "
operator|+
literal|"Was expecting 3 arguments"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"translate('abc', 'ab', 123)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'TRANSLATE3' to arguments of type "
operator|+
literal|"'TRANSLATE3\\(<CHAR\\(3\\)>,<CHAR\\(2\\)>,<INTEGER>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"translate('abc', 'ab', 123)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"translate('abc', 'ab', '+-', 'four')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TRANSLATE3'. "
operator|+
literal|"Was expecting 3 arguments"
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
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for 3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from '1' for 3)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*OVERLAY\\(<STRING> PLACING<STRING> FROM<INTEGER>\\).*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from '1' for 3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 1 for 3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing 'abc' from 6 for 3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(9) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"overlay('ABCdef' placing cast(null as char(5)) from 1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(11)"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|sql
argument_list|(
literal|"overlay('ABCdef' placing 'abc' collate latin1$sv from 1 for 3)"
argument_list|)
operator|.
name|assertCollation
argument_list|(
name|is
argument_list|(
literal|"ISO-8859-1$sv"
argument_list|)
argument_list|,
name|is
argument_list|(
name|SqlCollation
operator|.
name|Coercibility
operator|.
name|EXPLICIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testSubstring
parameter_list|()
block|{
name|expr
argument_list|(
literal|"substring('a' FROM 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' FROM 1 FOR 3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' FROM 'reg' FOR '\\')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// binary string
name|expr
argument_list|(
literal|"substring(x'ff' FROM 1  FOR 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring('10' FROM 1  FOR 2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('1000' FROM 2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('1000' FROM '1'  FOR 'w')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring(cast(' 100 ' as CHAR(99)) FROM '1'  FOR 'w')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(99) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring(x'10456b' FROM 1  FOR 2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARBINARY(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"substring('10' FROM 1  FOR 2)"
argument_list|)
operator|.
name|assertCharset
argument_list|(
name|isCharset
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// aka "latin1"
name|sql
argument_list|(
literal|"substring(_UTF16'10' FROM 1  FOR 2)"
argument_list|)
operator|.
name|assertCharset
argument_list|(
name|isCharset
argument_list|(
literal|"UTF-16LE"
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', 1, 3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Implicit type coercion.
name|expr
argument_list|(
literal|"substring(12345, '1')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', '1')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a', 1, '3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testSubstringFails
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"substring('a' from 1 for 'b')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'SUBSTRING' to arguments of type.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"substring('a' from 1 for 'b')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"substring(_UTF16'10' FROM '0' FOR '\\')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).* not comparable to each other.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"substring('10' FROM _UTF16'0' FOR '\\')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).* not comparable to each other.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"substring('10' FROM '0' FOR _UTF16'\\')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).* not comparable to each other.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLikeAndSimilar
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a' like 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a' like 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a' similar to 'b'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'a' similar to 'b' escape 'c'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIlike
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'a' ilike 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'a' ilike cast(null as varchar(99))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"cast(null as varchar(99)) not ilike 'b'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'a' not ilike 'b' || 'c'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// ILIKE is only available in the PostgreSQL function library
name|expr
argument_list|(
literal|"^'a' ilike 'b'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature ILIKE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRlike
parameter_list|()
block|{
comment|// RLIKE is supported for SPARK
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'first_name' rlike '%Ted%'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'first_name' rlike '^M+'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// RLIKE is only supported for Spark and Hive
name|String
name|noMatch
init|=
literal|"(?s).*No match found for function signature RLIKE"
decl_stmt|;
name|expr
argument_list|(
literal|"^'b' rlike '.+@.+\\\\..+'^"
argument_list|)
operator|.
name|fails
argument_list|(
name|noMatch
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
operator|.
name|fails
argument_list|(
name|noMatch
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|SPARK
argument_list|)
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|HIVE
argument_list|)
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
name|void
name|testLikeAndSimilarFails
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'a' like _UTF16'b'  escape 'c'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Operands _ISO-8859-1.a. COLLATE ISO-8859-1.en_US.primary,"
operator|+
literal|" _SHIFT_JIS.b..*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a' similar to _UTF16'b'  escape 'c'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Operands _ISO-8859-1.a. COLLATE ISO-8859-1.en_US.primary,"
operator|+
literal|" _SHIFT_JIS.b..*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'a' similar to 'b' collate UTF16$jp  escape 'c'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Operands _ISO-8859-1.a. COLLATE ISO-8859-1.en_US.primary,"
operator|+
literal|" _ISO-8859-1.b. COLLATE SHIFT_JIS.jp.primary.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNull
parameter_list|()
block|{
name|expr
argument_list|(
literal|"nullif(null, 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"values 1.0 + ^NULL^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1.0 + ^NULL^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"case when 1> 0 then null else 0 end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1> 0 and null"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"position(null in 'abc' from 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring(null from 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim(null from 'ab')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"trim(null from null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"null || 'a'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"not(null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"+null"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"-null"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"upper(null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"lower(null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"initcap(null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"mod(null, 2) + 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"abs(null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"round(null,1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"sign(null) + 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"truncate(null,1) + 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select null as a from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(null) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select bit_and(null) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select bit_or(null) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring(null from 1) + 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"substring(^NULL^ from 1)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal use of .NULL.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"values 1.0 + ^NULL^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal use of .NULL.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"values 1.0 + NULL"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.0 + ^NULL^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal use of .NULL.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.0 + NULL"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 1)"
argument_list|)
expr_stmt|;
comment|// FIXME: SQL:2003 does not allow raw NULL in IN clause
name|expr
argument_list|(
literal|"1 in (1, null, 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (null, 1, null, 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (cast(null as integer), null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (null, null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNullCast
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(null as tinyint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as smallint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"SMALLINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as integer)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as bigint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as float)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as real)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as double)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as boolean)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as varchar(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as char(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as binary(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as date)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as time)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as decimal)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as varbinary(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARBINARY(1)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as integer), cast(null as char(1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastTypeToType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(123 as char)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as varchar)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x'1234' as binary)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x'1234' as varbinary)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARBINARY NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as varchar(3))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as char(3))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('123' as integer)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('123' as double)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('1.0' as real)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as tinyint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as tinyint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as smallint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"SMALLINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as integer)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as integer)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as bigint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as bigint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as float)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as float)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as real)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as real)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0 as double)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as double)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as decimal(6,4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(6, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as decimal(6))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(6, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as decimal)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.234 as decimal(2,5))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 5) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('4.5' as decimal(3,1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(3, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as boolean)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as varchar(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as char(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(x'ff' as binary(1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BINARY(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(multiset[1] as double multiset)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(multiset['abc'] as integer multiset)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as boolean)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1.0e1 as boolean)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(true as numeric)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// It's a runtime error that 'TRUE' cannot fit into CHAR(3), but at
comment|// validate time this expression is OK.
name|expr
argument_list|(
literal|"cast(true as char(3))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
comment|// test cast to time type.
name|expr
argument_list|(
literal|"cast('abc' as time)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as time without time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as time with local time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME_WITH_LOCAL_TIME_ZONE(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as time(3))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as time(3) without time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as time(3) with local time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME_WITH_LOCAL_TIME_ZONE(3) NOT NULL"
argument_list|)
expr_stmt|;
comment|// test cast to timestamp type.
name|expr
argument_list|(
literal|"cast('abc' as timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as timestamp without time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as timestamp with local time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP_WITH_LOCAL_TIME_ZONE(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as timestamp(3))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as timestamp(3) without time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast('abc' as timestamp(3) with local time zone)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP_WITH_LOCAL_TIME_ZONE(3) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastRegisteredType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(123 as ^customBigInt^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'CUSTOMBIGINT'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as sales.customBigInt)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(123 as catalog.sales.customBigInt)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
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
literal|"cast('foo' as ^bar^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'BAR'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(multiset[1] as integer)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cast function cannot convert value of type "
operator|+
literal|"INTEGER MULTISET to type INTEGER"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(x'ff' as decimal(5,2))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cast function cannot convert value of type "
operator|+
literal|"BINARY\\(1\\) to type DECIMAL\\(5, 2\\)"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(DATE '1243-12-01' as TIME)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cast function cannot convert value of type "
operator|+
literal|"DATE to type TIME.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(TIME '12:34:01' as DATE)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cast function cannot convert value of type "
operator|+
literal|"TIME\\(0\\) to type DATE.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCastBinaryLiteral
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(^x'0dd'^ as binary(5))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Binary literal string must contain an even number of hexits"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether the GEOMETRY data type is allowed.    *    * @see SqlConformance#allowGeometry()    */
annotation|@
name|Test
name|void
name|testGeometry
parameter_list|()
block|{
specifier|final
name|String
name|err
init|=
literal|"Geo-spatial extensions and the GEOMETRY data type are not enabled"
decl_stmt|;
name|sql
argument_list|(
literal|"select cast(null as ^geometry^) as g from emp"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|STRICT_2003
argument_list|)
operator|.
name|fails
argument_list|(
name|err
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDateTime
parameter_list|()
block|{
comment|// LOCAL_TIME
name|expr
argument_list|(
literal|"LOCALTIME(3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"LOCALTIME"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// fix sqlcontext later.
name|wholeExpr
argument_list|(
literal|"LOCALTIME(1+2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must be a literal"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME(NULL)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must not be NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME(NULL)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must not be NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME(CAST(NULL AS INTEGER))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must not be NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature LOCALTIME.."
argument_list|)
expr_stmt|;
comment|//  with TZ?
name|expr
argument_list|(
literal|"LOCALTIME"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME(-1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must be a positive integer literal"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^LOCALTIME(100000000000000)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Numeric literal '100000000000000' out of range.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must be a valid precision "
operator|+
literal|"between '0' and '3'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME('foo')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIME('foo')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIME' must be a literal"
argument_list|)
expr_stmt|;
comment|// LOCALTIMESTAMP
name|expr
argument_list|(
literal|"LOCALTIMESTAMP(3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|//    fix sqlcontext later.
name|expr
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP(1+2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIMESTAMP' must be a literal"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature LOCALTIMESTAMP.."
argument_list|)
expr_stmt|;
comment|// with TZ?
name|expr
argument_list|(
literal|"LOCALTIMESTAMP"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP(-1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIMESTAMP' must be a positive "
operator|+
literal|"integer literal"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^LOCALTIMESTAMP(100000000000000)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Numeric literal '100000000000000' out of range.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIMESTAMP' must be a valid "
operator|+
literal|"precision between '0' and '3'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP('foo')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"LOCALTIMESTAMP('foo')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'LOCALTIMESTAMP' must be a literal"
argument_list|)
expr_stmt|;
comment|// CURRENT_DATE
name|wholeExpr
argument_list|(
literal|"CURRENT_DATE(3)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_DATE..NUMERIC.."
argument_list|)
expr_stmt|;
comment|//    fix sqlcontext later.
name|expr
argument_list|(
literal|"CURRENT_DATE"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_DATE(1+2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_DATE..NUMERIC.."
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_DATE()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_DATE.."
argument_list|)
expr_stmt|;
comment|//  with TZ?
name|expr
argument_list|(
literal|"CURRENT_DATE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
comment|// I guess -s1 is an expression?
name|wholeExpr
argument_list|(
literal|"CURRENT_DATE(-1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_DATE..NUMERIC.."
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_DATE('foo')"
argument_list|)
operator|.
name|fails
argument_list|(
name|ANY
argument_list|)
expr_stmt|;
comment|// current_time
name|expr
argument_list|(
literal|"current_time(3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|//    fix sqlcontext later.
name|expr
argument_list|(
literal|"current_time"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"current_time(1+2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIME' must be a literal"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"current_time()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_TIME.."
argument_list|)
expr_stmt|;
comment|// with TZ?
name|expr
argument_list|(
literal|"current_time"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"current_time(-1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIME' must be a positive integer literal"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^CURRENT_TIME(100000000000000)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Numeric literal '100000000000000' out of range.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIME(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIME' must be a valid precision "
operator|+
literal|"between '0' and '3'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"current_time('foo')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"current_time('foo')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIME' must be a literal"
argument_list|)
expr_stmt|;
comment|// current_timestamp
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP(3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|//    fix sqlcontext later.
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT CURRENT_TIMESTAMP AS X FROM (VALUES (1))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP(1+2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIMESTAMP' must be a literal"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature CURRENT_TIMESTAMP.."
argument_list|)
expr_stmt|;
comment|// should type be 'TIMESTAMP with TZ'?
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// should type be 'TIMESTAMP with TZ'?
name|expr
argument_list|(
literal|"CURRENT_TIMESTAMP(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(2) NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP(-1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIMESTAMP' must be a positive "
operator|+
literal|"integer literal"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^CURRENT_TIMESTAMP(100000000000000)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Numeric literal '100000000000000' out of range.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP(4)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIMESTAMP' must be a valid "
operator|+
literal|"precision between '0' and '3'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP('foo')"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CURRENT_TIMESTAMP('foo')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'CURRENT_TIMESTAMP' must be a literal"
argument_list|)
expr_stmt|;
comment|// Date literals
name|expr
argument_list|(
literal|"DATE '2004-12-01'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '11:59:59.99'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '12:01:01.001'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '2004-12-01 12:01:01.001'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// REVIEW: Can't think of any date/time/ts literals that will parse,
comment|// but not validate.
block|}
comment|/**    * Tests casting to/from date/time types.    */
annotation|@
name|Test
name|void
name|testDateTimeCast
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"CAST(1 as DATE)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cast function cannot convert value of type INTEGER to type DATE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"CAST(DATE '2001-12-21' AS VARCHAR(10))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CAST( '2001-12-21' AS DATE)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CAST( TIMESTAMP '2001-12-21 10:12:21' AS VARCHAR(20))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CAST( TIME '10:12:21' AS VARCHAR(20))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CAST( '10:12:21' AS TIME)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"CAST( '2004-12-21 10:12:21' AS TIMESTAMP)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConvertTimezoneFunction
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"CONVERT_TIMEZONE('UTC', 'America/Los_Angeles',"
operator|+
literal|" CAST('2000-01-01' AS TIMESTAMP))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"CONVERT_TIMEZONE\\(<CHARACTER>,<CHARACTER>,<TIMESTAMP>\\)"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|expr
argument_list|(
literal|"CONVERT_TIMEZONE('UTC', 'America/Los_Angeles',\n"
operator|+
literal|"  CAST('2000-01-01' AS TIMESTAMP))"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CONVERT_TIMEZONE('UTC', 'America/Los_Angeles')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'CONVERT_TIMEZONE'. "
operator|+
literal|"Was expecting 3 arguments"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CONVERT_TIMEZONE('UTC', 'America/Los_Angeles', '2000-01-01')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'CONVERT_TIMEZONE' to arguments of type "
operator|+
literal|"'CONVERT_TIMEZONE\\(<CHAR\\(3\\)>,<CHAR\\(19\\)>, "
operator|+
literal|"<CHAR\\(10\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'CONVERT_TIMEZONE\\(<CHARACTER>,<CHARACTER>,<DATETIME>\\)'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"CONVERT_TIMEZONE('UTC', 'America/Los_Angeles', "
operator|+
literal|"'UTC', CAST('2000-01-01' AS TIMESTAMP))"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'CONVERT_TIMEZONE'. "
operator|+
literal|"Was expecting 3 arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testToDateFunction
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"TO_DATE('2000-01-01', 'YYYY-MM-DD')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"TO_DATE\\(<CHARACTER>,<CHARACTER>\\)"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|expr
argument_list|(
literal|"TO_DATE('2000-01-01', 'YYYY-MM-DD')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_DATE('2000-01-01')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TO_DATE'. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TO_DATE(2000, 'YYYY')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_DATE(2000, 'YYYY')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TO_DATE' to arguments of type "
operator|+
literal|"'TO_DATE\\(<INTEGER>,<CHAR\\(4\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'TO_DATE\\(<STRING>,<STRING>\\)'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_DATE('2000-01-01', 'YYYY-MM-DD', 'YYYY-MM-DD')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TO_DATE'. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testToTimestampFunction
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"TO_TIMESTAMP('2000-01-01 01:00:00', 'YYYY-MM-DD HH:MM:SS')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature "
operator|+
literal|"TO_TIMESTAMP\\(<CHARACTER>,<CHARACTER>\\)"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
decl_stmt|;
name|expr
argument_list|(
literal|"TO_TIMESTAMP('2000-01-01 01:00:00', 'YYYY-MM-DD HH:MM:SS')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_TIMESTAMP('2000-01-01 01:00:00')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TO_TIMESTAMP'. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TO_TIMESTAMP(2000, 'YYYY')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DATE NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_TIMESTAMP(2000, 'YYYY')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TO_TIMESTAMP' to arguments of type "
operator|+
literal|"'TO_TIMESTAMP\\(<INTEGER>,<CHAR\\(4\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'TO_TIMESTAMP\\(<STRING>,<STRING>\\)'"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"TO_TIMESTAMP('2000-01-01 01:00:00', 'YYYY-MM-DD HH:MM:SS',"
operator|+
literal|" 'YYYY-MM-DD')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TO_TIMESTAMP'. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCurrentDatetime
parameter_list|()
throws|throws
name|SqlParseException
throws|,
name|ValidationException
block|{
specifier|final
name|String
name|currentDateTimeExpr
init|=
literal|"select ^current_datetime^"
decl_stmt|;
name|SqlValidatorFixture
name|shouldFail
init|=
name|sql
argument_list|(
name|currentDateTimeExpr
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedError
init|=
literal|"query [select CURRENT_DATETIME]; exception "
operator|+
literal|"[Column 'CURRENT_DATETIME' not found in any table]; class "
operator|+
literal|"[class org.apache.calcite.sql.validate.SqlValidatorException]; pos [line 1 col 8 thru line 1 col 8]"
decl_stmt|;
name|shouldFail
operator|.
name|fails
argument_list|(
literal|"Column 'CURRENT_DATETIME' not found in any table"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
decl_stmt|;
name|sql
argument_list|(
literal|"select current_datetime()"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select CURRENT_DATETIME('America/Los_Angeles')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select CURRENT_DATETIME(CAST(NULL AS VARCHAR(20)))"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInvalidFunction
parameter_list|()
block|{
name|wholeExpr
argument_list|(
literal|"foo()"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature FOO.."
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"mod(123)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'MOD'. "
operator|+
literal|"Was expecting 2 arguments"
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
literal|false
argument_list|,
literal|"test case for [CALCITE-3326], disabled til it is fixed"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select foo()"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature FOO.."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInvalidTableFunction
parameter_list|()
block|{
comment|// A table function at most have one input table with row semantics
name|sql
argument_list|(
literal|"select * from table(^invalid(table orders, table emp)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"A table function at most has one input table with row semantics."
operator|+
literal|" Table function 'INVALID' has multiple input tables with row semantics"
argument_list|)
expr_stmt|;
comment|// Only tables with set semantics may be partitioned
name|sql
argument_list|(
literal|"select * from table(^score(table orders partition by productid)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Only tables with set semantics may be partitioned."
operator|+
literal|" Invalid PARTITION BY clause in the 0-th operand of table function 'SCORE'"
argument_list|)
expr_stmt|;
comment|// Only tables with set semantics may be ordered
name|sql
argument_list|(
literal|"select * from table(^score(table orders order by orderId)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Only tables with set semantics may be ordered."
operator|+
literal|" Invalid ORDER BY clause in the 0-th operand of table function 'SCORE'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTableFunctionWithTableParam
parameter_list|()
block|{
comment|// test input table with row semantic
name|sql
argument_list|(
literal|"select * from table(score(table orders))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test no partition by clause and order by clause for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders, 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test one partition key for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders partition by productid, 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test multiple partition keys for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders partition by (orderId, productid), 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test one order key for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders order by orderId, 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test multiple order keys for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders order by (orderId, productid), 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test complex order-by clause for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders order by (orderId desc, productid asc), 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test partition by clause and order by clause for input table with set semantic
name|sql
argument_list|(
literal|"select * from table(topn(table orders partition by productid order by orderId, 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test partition by clause and order by clause for subquery
name|sql
argument_list|(
literal|"select * from table(topn(select * from Orders partition by productid\n "
operator|+
literal|"order by orderId, 3))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test multiple input tables
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"similarlity(\n"
operator|+
literal|"  table emp partition by deptno order by empno,\n"
operator|+
literal|"  table emp_b partition by deptno order by empno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnknownFunctionHandling
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withLenientOperatorLookup
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"concat('a', 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"foo('2001-12-21')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"\"foo\"('b')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"foo()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"'a' || foo(bar('2001-12-21'))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"cast(foo(5, 2) as DECIMAL)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"select ascii('xyz')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"select get_bit(CAST('FFFF' as BINARY), 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"select now()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"^TIMESTAMP_CMP_TIMESTAMPTZ^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"atan(0)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"select row_number() over () from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withExpr
argument_list|(
literal|"select coalesce(1, 2, 3)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select count() from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// too few args
name|s
operator|.
name|withSql
argument_list|(
literal|"select sum(1, 2) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// too many args
block|}
annotation|@
name|Test
name|void
name|testJdbcFunctionCall
parameter_list|()
block|{
name|expr
argument_list|(
literal|"{fn log10(1)}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"{fn locate('','')}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"{fn insert('',1,2,'')}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// 'lower' is a valid SQL function but not valid JDBC fn; the JDBC
comment|// equivalent is 'lcase'
name|wholeExpr
argument_list|(
literal|"{fn lower('Foo' || 'Bar')}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Function '\\{fn LOWER\\}' is not defined"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn lcase('Foo' || 'Bar')}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"{fn power(2, 3)}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn insert('','',1,2)}"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn insert('','',1,2)}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn insert('','',1)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*4.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn locate('','',1)}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn log10('1')}"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*fn LOG10..<CHAR.1.>.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"{fn log10('1')}"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Cannot apply '\\{fn LOG10\\}' to arguments of"
operator|+
literal|" type '\\{fn LOG10\\}\\(<INTEGER>,<INTEGER>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): '\\{fn LOG10\\}\\(<NUMERIC>\\)'"
decl_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn log10(1,1)}"
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn fn(1)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Function '.fn FN.' is not defined.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"{fn hahaha(1)}"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Function '.fn HAHAHA.' is not defined.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testQuotedFunction
parameter_list|()
block|{
if|if
condition|(
literal|false
condition|)
block|{
comment|// REVIEW jvs 2-Feb-2005:  I am disabling this test because I
comment|// removed the corresponding support from the parser.  Where in the
comment|// standard does it state that you're supposed to be able to quote
comment|// keywords for builtin functions?
name|expr
argument_list|(
literal|"\"CAST\"(1 as double)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"\"POSITION\"('b' in 'alphabet')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// convert and translate not yet implemented
comment|//        checkExp("\"CONVERT\"('b' using conversion)");
comment|//        checkExp("\"TRANSLATE\"('b' using translation)");
name|expr
argument_list|(
literal|"\"OVERLAY\"('a' PLAcing 'b' from 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"\"SUBSTRING\"('a' from 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"\"TRIM\"('b')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expr
argument_list|(
literal|"\"TRIM\"('b' ^FROM^ 'a')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"FROM\" at .*"
argument_list|)
expr_stmt|;
comment|// Without the "FROM" noise word, TRIM is parsed as a regular
comment|// function without quoting and built-in function with quoting.
name|expr
argument_list|(
literal|"\"TRIM\"('b', 'FROM', 'a')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TRIM('b')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Not able to parse member function yet.    */
annotation|@
name|Test
name|void
name|testInvalidMemberFunction
parameter_list|()
block|{
name|expr
argument_list|(
literal|"myCol.^func()^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature FUNC().*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"customer.mySubschema.^memberFunc()^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature MEMBERFUNC().*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowtype
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values (1),(2),(1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"values (1),(2),(1)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values (1,'1'),(2,'2')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"values (1,'1'),(2,'2')"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, CHAR(1) NOT NULL EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values true"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(BOOLEAN NOT NULL EXPR$0) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^values ('1'),(2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Values passed to VALUES operator must have compatible types"
argument_list|)
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|sql
argument_list|(
literal|"values (1),(2.0),(3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ROWTYPE(DOUBLE)"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testRow
parameter_list|()
block|{
comment|// double-nested rows can confuse validator namespace resolution
name|sql
argument_list|(
literal|"select t.r.\"EXPR$1\".\"EXPR$2\"\n"
operator|+
literal|"from (select ((1,2),(3,4,5)) r from dept) t"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select row(emp.empno, emp.ename) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, VARCHAR(20) NOT NULL EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select row(emp.empno + 1, emp.ename) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, VARCHAR(20) NOT NULL EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select row((select deptno from dept where dept.deptno = emp.deptno), emp.ename)\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER EXPR$0, VARCHAR(20) NOT NULL EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowWithValidDot
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ((1,2),(3,4,5)).\"EXPR$1\".\"EXPR$2\"\n from dept"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select row(1,2).\"EXPR$1\" from dept"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.a.\"EXPR$1\" from (select row(1,2) as a from (values (1))) as t"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testRowWithInvalidDotOperation
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select t.^s.\"EXPR$1\"^ from (\n"
operator|+
literal|"  select 1 AS s from (values (1))) as t"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Column 'S\\.EXPR\\$1' not found in table 'T'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"select ^array[1, 2, 3]^.\"EXPR$1\" from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Incompatible types.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"select ^'mystr'^.\"EXPR$1\" from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Incompatible types.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDotAfterParenthesizedIdentifier
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select (home_address).city from emp_address"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(20) NOT NULL"
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
name|columnType
argument_list|(
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1, CAST(null AS DOUBLE)]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1.3,2.3]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1,2.3, cast(4 as bigint)]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset['1','22', '333','22']"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^multiset[1, '2']^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2)]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0,"
operator|+
literal|" INTEGER NOT NULL EXPR$1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2),ROW(2,5)]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0,"
operator|+
literal|" INTEGER NOT NULL EXPR$1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2),ROW(3.4,5.4)]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(DECIMAL(11, 1) NOT NULL EXPR$0,"
operator|+
literal|" DECIMAL(11, 1) NOT NULL EXPR$1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset(select*from emp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMultisetSetOperators
parameter_list|()
block|{
name|expr
argument_list|(
literal|"multiset[1] multiset union multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[324.2] multiset union multiset[23.2,2.32]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 2) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset union multiset[1,2.3]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset union all multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset except multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset except all multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset intersect multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] multiset intersect all multiset[1,2.3]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"^multiset[1, '2']^ multiset union multiset[1]"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2)] multiset intersect multiset[row(3,4)]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
if|if
condition|(
name|TODO
condition|)
block|{
name|wholeExpr
argument_list|(
literal|"multiset[ROW(1,'2')] multiset union multiset[ROW(1,2)]"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
name|void
name|testSubMultisetOf
parameter_list|()
block|{
name|expr
argument_list|(
literal|"multiset[1] submultiset of multiset[1,2.3]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[1] submultiset of multiset[1]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^multiset[1, '2']^ submultiset of multiset[1]"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Parameters must be of the same type"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"multiset[ROW(1,2)] submultiset of multiset[row(3,4)]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testElement
parameter_list|()
block|{
name|expr
argument_list|(
literal|"element(multiset[1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.0+element(multiset[1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(12, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"element(multiset['1'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(1) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"element(multiset[1e-2])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"element(multiset[multiset[cast(null as tinyint)]])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT MULTISET NOT NULL"
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
literal|"1 member of multiset[1]"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"1 member of multiset['1']"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot compare values of types 'INTEGER', 'CHAR\\(1\\)'"
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
literal|"multiset[1] is a set"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"multiset['1'] is a set"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"'a' is a set"
argument_list|)
operator|.
name|fails
argument_list|(
literal|".*Cannot apply 'IS A SET' to.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCardinality
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cardinality(multiset[1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cardinality(multiset['1'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cardinality('a')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'CARDINALITY' to arguments of type "
operator|+
literal|"'CARDINALITY\\(<CHAR\\(1\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'CARDINALITY\\(<MULTISET>\\)'\n"
operator|+
literal|"'CARDINALITY\\(<ARRAY>\\)'\n"
operator|+
literal|"'CARDINALITY\\(<MAP>\\)'"
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
literal|"PIVOT (sum(sal) AS ss FOR job in ('CLERK' AS c, 'MANAGER' AS m))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME, INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE, INTEGER NOT NULL COMM,"
operator|+
literal|" INTEGER NOT NULL DEPTNO, BOOLEAN NOT NULL SLACKER,"
operator|+
literal|" INTEGER C_SS, INTEGER M_SS) NOT NULL"
argument_list|)
expr_stmt|;
block|}
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
literal|"FROM   (SELECT deptno, job, sal\n"
operator|+
literal|"        FROM   emp)\n"
operator|+
literal|"PIVOT  (SUM(sal) AS sum_sal, COUNT(*) AS \"COUNT\"\n"
operator|+
literal|"        FOR (job) IN ('CLERK', 'MANAGER' mgr, 'ANALYST' AS \"a\"))\n"
operator|+
literal|"ORDER BY deptno"
decl_stmt|;
specifier|final
name|String
name|type
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"INTEGER 'CLERK'_SUM_SAL, BIGINT NOT NULL 'CLERK'_COUNT, "
operator|+
literal|"INTEGER MGR_SUM_SAL, BIGINT NOT NULL MGR_COUNT, INTEGER a_SUM_SAL, "
operator|+
literal|"BIGINT NOT NULL a_COUNT) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotAliases
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (\n"
operator|+
literal|"    SELECT deptno, job, sal FROM   emp)\n"
operator|+
literal|"PIVOT (SUM(sal) AS ss\n"
operator|+
literal|"    FOR (job, deptno)\n"
operator|+
literal|"    IN (('A B'/*C*/||' D', 10),\n"
operator|+
literal|"        ('MANAGER', null) mgr,\n"
operator|+
literal|"        ('ANALYST', 30) AS \"a\"))"
decl_stmt|;
comment|// Oracle uses parse tree without spaces around '||',
comment|//   'A B'||' D'_10_SUM_SAL
comment|// but close enough.
specifier|final
name|String
name|type
init|=
literal|"RecordType(INTEGER 'A B' || ' D'_10_SS, "
operator|+
literal|"INTEGER MGR_SS, INTEGER a_SS) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotAggAliases
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT deptno, job, sal FROM emp)\n"
operator|+
literal|"PIVOT (SUM(sal) AS ss, MIN(job)\n"
operator|+
literal|"    FOR deptno IN (10 AS ten, 20))"
decl_stmt|;
specifier|final
name|String
name|type
init|=
literal|"RecordType(INTEGER TEN_SS, VARCHAR(10) TEN, "
operator|+
literal|"INTEGER 20_SS, VARCHAR(10) 20) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotNoValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT deptno, sal, job FROM emp)\n"
operator|+
literal|"PIVOT (sum(sal) AS sum_sal FOR job in ())"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO) NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Output only includes columns not referenced in an aggregate or axis. */
annotation|@
name|Test
name|void
name|testPivotRemoveColumns
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sum_sal, count(comm) AS count_comm,\n"
operator|+
literal|"      min(hiredate) AS min_hiredate, max(hiredate) AS max_hiredate\n"
operator|+
literal|"  FOR (job, deptno, slacker, mgr, ename)\n"
operator|+
literal|"  IN (('CLERK', 10, false, null, ename) AS c10))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" INTEGER C10_SUM_SAL, BIGINT NOT NULL C10_COUNT_COMM,"
operator|+
literal|" TIMESTAMP(0) C10_MIN_HIREDATE,"
operator|+
literal|" TIMESTAMP(0) C10_MAX_HIREDATE) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotInvalidCol
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(^invalid^) AS sal FOR job in ('CLERK' AS c, 'MANAGER'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'INVALID' not found in any table"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotInvalidCol2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sum(sal) AS sal FOR (job, ^invalid^) in (('CLERK', 'x') AS c))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'INVALID' not found in any table"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotMeasureMustBeAgg
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (sal ^+^ 1 AS sal1 FOR job in ('CLERK' AS c, 'MANAGER'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\+\" at .*"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (^log10(sal)^ AS logSal FOR job in ('CLERK' AS c, 'MANAGER'))"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Measure expression in PIVOT must use aggregate function"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"PIVOT (^123^ AS logSal FOR job in ('CLERK' AS c, 'MANAGER'))"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"123\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests an expression as argument to an aggregate function in a PIVOT.    * Both of the columns referenced ({@code sum} and {@code deptno}) are removed    * from the implicit GROUP BY. */
annotation|@
name|Test
name|void
name|testPivotAggExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (SELECT sal, deptno, job, mgr FROM Emp)\n"
operator|+
literal|"PIVOT (sum(sal + deptno + 1)\n"
operator|+
literal|"   FOR job in ('CLERK' AS c, 'ANALYST' AS a))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER MGR, INTEGER C, INTEGER A) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testPivotValueMismatch
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM Emp\n"
operator|+
literal|"PIVOT (SUM(sal) FOR job IN (^('A', 'B')^, ('C', 'D')))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Value count in PIVOT \\(2\\) must match number of "
operator|+
literal|"FOR columns \\(1\\)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT * FROM Emp\n"
operator|+
literal|"PIVOT (SUM(sal) FOR job IN (^('A', 'B')^ AS x, ('C', 'D')))"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Value count in PIVOT \\(2\\) must match number of "
operator|+
literal|"FOR columns \\(1\\)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"SELECT * FROM Emp\n"
operator|+
literal|"PIVOT (SUM(sal) FOR (job) IN (^('A', 'B')^))"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Value count in PIVOT \\(2\\) must match number of "
operator|+
literal|"FOR columns \\(1\\)"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"SELECT * FROM Emp\n"
operator|+
literal|"PIVOT (SUM(sal) FOR (job, deptno) IN (^'CLERK'^, 10))"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Value count in PIVOT \\(1\\) must match number of "
operator|+
literal|"FOR columns \\(2\\)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivot
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR remuneration_type IN (comm AS 'commission',\n"
operator|+
literal|"                            sal as 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME, VARCHAR(10) NOT NULL JOB, INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE, INTEGER NOT NULL DEPTNO,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER, CHAR(10) NOT NULL REMUNERATION_TYPE,"
operator|+
literal|" INTEGER NOT NULL REMUNERATION) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotInvalidColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR remuneration_type IN (comm AS 'commission',\n"
operator|+
literal|"                            ^unknownCol^ as 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'UNKNOWNCOL' not found in any table"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotCannotDeriveMeasureType
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR remuneration_type IN (^comm^ AS 'commission',\n"
operator|+
literal|"                            ename as 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"In UNPIVOT, cannot derive type for measure 'REMUNERATION'"
operator|+
literal|" because source columns have different data types"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotValueMismatch
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR remuneration_type IN (comm AS 'commission',\n"
operator|+
literal|"                            sal AS ^('salary', 1)^))"
decl_stmt|;
name|String
name|expected
init|=
literal|"Value count in UNPIVOT \\(2\\) must match "
operator|+
literal|"number of FOR columns \\(1\\)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotDuplicateName
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT ((remuneration, ^remuneration^)\n"
operator|+
literal|"  FOR remuneration_type\n"
operator|+
literal|"  IN ((comm, comm) AS 'commission',\n"
operator|+
literal|"      (sal, sal) AS 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate column name 'REMUNERATION' in UNPIVOT"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotDuplicateName2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR ^remuneration^ IN (comm AS 'commission',\n"
operator|+
literal|"                         sal AS 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate column name 'REMUNERATION' in UNPIVOT"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotDuplicateName3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM emp\n"
operator|+
literal|"UNPIVOT (remuneration\n"
operator|+
literal|"  FOR ^deptno^ IN (comm AS 'commission',\n"
operator|+
literal|"                         sal AS 'salary'))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate column name 'DEPTNO' in UNPIVOT"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotMissingAs
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (\n"
operator|+
literal|"  SELECT *\n"
operator|+
literal|"  FROM (VALUES (0, 1, 2, 3, 4),\n"
operator|+
literal|"               (10, 11, 12, 13, 14))\n"
operator|+
literal|"      AS t (c0, c1, c2, c3, c4))\n"
operator|+
literal|"UNPIVOT ((m0, m1, m2)\n"
operator|+
literal|"    FOR (a0, a1)\n"
operator|+
literal|"     IN ((c1, c2, c3) AS ('col1','col2'),\n"
operator|+
literal|"         (c2, c3, c4)))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C0, VARCHAR(8) NOT NULL A0,"
operator|+
literal|" VARCHAR(8) NOT NULL A1, INTEGER M0, INTEGER M1,"
operator|+
literal|" INTEGER M2) NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testUnpivotMissingAs2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (\n"
operator|+
literal|"  SELECT *\n"
operator|+
literal|"  FROM (VALUES (0, 1, 2, 3, 4),\n"
operator|+
literal|"               (10, 11, 12, 13, 14))\n"
operator|+
literal|"      AS t (c0, c1, c2, c3, c4))\n"
operator|+
literal|"UNPIVOT ((m0, m1, m2)\n"
operator|+
literal|"    FOR (^a0^, a1)\n"
operator|+
literal|"     IN ((c1, c2, c3) AS (6, true),\n"
operator|+
literal|"         (c2, c3, c4)))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"In UNPIVOT, cannot derive type for axis 'A0'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testMatchRecognizeWithDistinctAggregation
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"MATCH_RECOGNIZE (\n"
operator|+
literal|"  ORDER BY ename\n"
operator|+
literal|"  MEASURES\n"
operator|+
literal|"    ^COUNT(DISTINCT A.deptno)^ AS deptno\n"
operator|+
literal|"  PATTERN (A B)\n"
operator|+
literal|"  DEFINE\n"
operator|+
literal|"    A AS A.empno = 123\n"
operator|+
literal|") AS T"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"DISTINCT/ALL not allowed with "
operator|+
literal|"COUNT\\(DISTINCT `A`\\.`DEPTNO`\\) function"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalTimeUnitEnumeration
parameter_list|()
block|{
comment|// Since there is validation code relaying on the fact that the
comment|// enumerated time unit ordinals in SqlIntervalQualifier starts with 0
comment|// and ends with 5, this test is here to make sure that if someone
comment|// changes how the time untis are setup, an early feedback will be
comment|// generated by this test.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|YEAR
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MONTH
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|DAY
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|TimeUnit
operator|.
name|HOUR
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|TimeUnit
operator|.
name|MINUTE
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECOND
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|b
init|=
operator|(
name|TimeUnit
operator|.
name|YEAR
operator|.
name|ordinal
argument_list|()
operator|<
name|TimeUnit
operator|.
name|MONTH
operator|.
name|ordinal
argument_list|()
operator|)
operator|&&
operator|(
name|TimeUnit
operator|.
name|MONTH
operator|.
name|ordinal
argument_list|()
operator|<
name|TimeUnit
operator|.
name|DAY
operator|.
name|ordinal
argument_list|()
operator|)
operator|&&
operator|(
name|TimeUnit
operator|.
name|DAY
operator|.
name|ordinal
argument_list|()
operator|<
name|TimeUnit
operator|.
name|HOUR
operator|.
name|ordinal
argument_list|()
operator|)
operator|&&
operator|(
name|TimeUnit
operator|.
name|HOUR
operator|.
name|ordinal
argument_list|()
operator|<
name|TimeUnit
operator|.
name|MINUTE
operator|.
name|ordinal
argument_list|()
operator|)
operator|&&
operator|(
name|TimeUnit
operator|.
name|MINUTE
operator|.
name|ordinal
argument_list|()
operator|<
name|TimeUnit
operator|.
name|SECOND
operator|.
name|ordinal
argument_list|()
operator|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalMonthsConversion
parameter_list|()
block|{
name|expr
argument_list|(
literal|"INTERVAL '1' YEAR"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|12L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '5' MONTH"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|5L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '3-2' YEAR TO MONTH"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|38L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '-5-4' YEAR TO MONTH"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
operator|-
literal|64L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalMillisConversion
parameter_list|()
block|{
name|expr
argument_list|(
literal|"INTERVAL '1' DAY"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|86_400_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' HOUR"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|3_600_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' MINUTE"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|60_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1' SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|1_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:05' HOUR TO MINUTE"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|3_900_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:05' MINUTE TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|65_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1' DAY TO HOUR"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|90_000_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:05' DAY TO MINUTE"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|90_300_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:05:03' DAY TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|90_303_000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1 1:05:03.12345' DAY TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|90_303_123L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1.12345' SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|1_123L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:05.12345' MINUTE TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|65_123L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:05:03' HOUR TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|3903000L
argument_list|)
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"INTERVAL '1:05:03.12345' HOUR TO SECOND"
argument_list|)
operator|.
name|assertInterval
argument_list|(
name|is
argument_list|(
literal|3_903_123L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDatetimePlusNullInterval
parameter_list|()
block|{
name|expr
argument_list|(
literal|"TIME '8:8:8' + cast(NULL AS interval hour)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '8:8:8' + cast(NULL AS interval YEAR)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '1990-12-12 12:12:12' + cast(NULL AS interval hour)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIMESTAMP '1990-12-12 12:12:12' + cast(NULL AS interval YEAR)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(NULL AS interval hour) + TIME '8:8:8'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(NULL AS interval YEAR) + TIME '8:8:8'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(NULL AS interval hour) + TIMESTAMP '1990-12-12 12:12:12'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(NULL AS interval YEAR) + TIMESTAMP '1990-12-12 12:12:12'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testIntervalLiterals
parameter_list|()
block|{
comment|// First check that min, max, and defaults are what we expect
comment|// (values used in subtests depend on these being true to
comment|// accurately test bounds)
specifier|final
name|RelDataTypeSystem
name|typeSystem
init|=
name|fixture
argument_list|()
operator|.
name|factory
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|getTypeSystem
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeSystem
name|defTypeSystem
init|=
name|RelDataTypeSystem
operator|.
name|DEFAULT
decl_stmt|;
for|for
control|(
name|SqlTypeName
name|typeName
range|:
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
control|)
block|{
name|assertThat
argument_list|(
name|typeName
operator|.
name|getMinPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeSystem
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeSystem
operator|.
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeName
operator|.
name|getMinScale
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeSystem
operator|.
name|getMaxScale
argument_list|(
name|typeName
argument_list|)
argument_list|,
name|is
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|typeName
operator|.
name|getDefaultScale
argument_list|()
argument_list|,
name|is
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SqlValidatorFixture
name|f
init|=
name|fixture
argument_list|()
decl_stmt|;
specifier|final
name|IntervalTest
operator|.
name|Fixture
name|intervalFixture
init|=
operator|new
name|IntervalTest
operator|.
name|Fixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IntervalTest
operator|.
name|Fixture2
name|expr
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|getFixture2
argument_list|(
name|f
operator|.
name|withExpr
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IntervalTest
operator|.
name|Fixture2
name|wholeExpr
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|getFixture2
argument_list|(
name|f
operator|.
name|withExpr
argument_list|(
name|s
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|IntervalTest
operator|.
name|Fixture2
name|getFixture2
parameter_list|(
name|SqlValidatorFixture
name|f2
parameter_list|)
block|{
return|return
operator|new
name|IntervalTest
operator|.
name|Fixture2
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|fails
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|f2
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|columnType
parameter_list|(
name|String
name|expectedType
parameter_list|)
block|{
name|f2
operator|.
name|columnType
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IntervalTest
operator|.
name|Fixture2
name|assertParse
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
return|return
name|this
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
operator|new
name|IntervalTest
argument_list|(
name|intervalFixture
argument_list|)
operator|.
name|testAll
argument_list|()
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
literal|"interval 1 hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (2 + 3) month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (cast(null as integer)) year"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (cast(null as integer)) year(2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR(2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (date '1970-01-01') hour"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'INTERVAL' to arguments of type "
operator|+
literal|"'INTERVAL<DATE><INTERVAL HOUR>'\\. Supported form\\(s\\): "
operator|+
literal|"'INTERVAL<NUMERIC><DATETIME_INTERVAL>'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (nullif(true, true)) hour"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'INTERVAL' to arguments of type "
operator|+
literal|"'INTERVAL<BOOLEAN><INTERVAL HOUR>'\\. Supported form\\(s\\): "
operator|+
literal|"'INTERVAL<NUMERIC><DATETIME_INTERVAL>'"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval (interval '1' day) hour"
argument_list|)
operator|.
name|withWhole
argument_list|(
literal|true
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'INTERVAL' to arguments of type "
operator|+
literal|"'INTERVAL<INTERVAL DAY><INTERVAL HOUR>'\\. "
operator|+
literal|"Supported form\\(s\\): "
operator|+
literal|"'INTERVAL<NUMERIC><DATETIME_INTERVAL>'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select interval empno hour as h from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select interval emp.mgr hour as h from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR"
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
literal|"interval '1' hour + TIME '8:8:8'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '8:8:8' - interval '1' hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"TIME '8:8:8' + interval '1' hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIME(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day + interval '1' DAY(4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day(5) + interval '1' DAY"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY(5) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day + interval '1' HOUR(10)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day + interval '1' MINUTE"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day + interval '1' second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:2' hour to minute + interval '1' second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:3' hour to minute + interval '1 1:2:3.4' day to second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:2' hour to minute + interval '1 1' day to hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1:2' hour to minute + interval '1 1' day to hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO MINUTE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1 2' day to hour + interval '1:1' minute to second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' year + interval '1' month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' day - interval '1' hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' year - interval '1' month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' month - interval '1' year"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' year + interval '1' day"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\+' to arguments of type "
operator|+
literal|"'<INTERVAL YEAR> \\+<INTERVAL DAY>'.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' month + interval '1' second"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '\\+' to arguments of type "
operator|+
literal|"'<INTERVAL MONTH> \\+<INTERVAL SECOND>'.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' year - interval '1' day"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '-' to arguments of type "
operator|+
literal|"'<INTERVAL YEAR> -<INTERVAL DAY>'.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' month - interval '1' second"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '-' to arguments of type "
operator|+
literal|"'<INTERVAL MONTH> -<INTERVAL SECOND>'.*"
argument_list|)
expr_stmt|;
comment|// mixing between datetime and interval todo        checkExpType("date
comment|// '1234-12-12' + INTERVAL '1' month + interval '1' day","DATE"); todo
comment|//      checkExpFails("date '1234-12-12' + (INTERVAL '1' month +
comment|// interval '1' day)","?");
comment|// multiply operator
name|expr
argument_list|(
literal|"interval '1' year * 2"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.234*interval '1 1:2:3' day to second "
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
comment|// division operator
name|expr
argument_list|(
literal|"interval '1' month / 0.1"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1-2' year TO month / 0.1e-9"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"1.234/interval '1 1:2:3' day to second"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '/' to arguments of type "
operator|+
literal|"'<DECIMAL.4, 3.> /<INTERVAL DAY TO SECOND>'.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAddAndDiff
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tsi
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
literal|"FRAC_SECOND"
argument_list|)
operator|.
name|add
argument_list|(
literal|"MICROSECOND"
argument_list|)
operator|.
name|add
argument_list|(
literal|"MINUTE"
argument_list|)
operator|.
name|add
argument_list|(
literal|"HOUR"
argument_list|)
operator|.
name|add
argument_list|(
literal|"DAY"
argument_list|)
operator|.
name|add
argument_list|(
literal|"WEEK"
argument_list|)
operator|.
name|add
argument_list|(
literal|"MONTH"
argument_list|)
operator|.
name|add
argument_list|(
literal|"QUARTER"
argument_list|)
operator|.
name|add
argument_list|(
literal|"YEAR"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_FRAC_SECOND"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_MICROSECOND"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_MINUTE"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_HOUR"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_DAY"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_WEEK"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_MONTH"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_QUARTER"
argument_list|)
operator|.
name|add
argument_list|(
literal|"SQL_TSI_YEAR"
argument_list|)
operator|.
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
literal|"timestampadd(%s, 12, current_timestamp)"
argument_list|)
operator|.
name|add
argument_list|(
literal|"timestampdiff(%s, current_timestamp, current_timestamp)"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|interval
range|:
name|tsi
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
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
block|}
name|expr
argument_list|(
literal|"timestampadd(SQL_TSI_WEEK, 2, current_timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampadd(SQL_TSI_WEEK, 2, cast(null as timestamp))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampdiff(SQL_TSI_WEEK, current_timestamp, current_timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampdiff(SQL_TSI_WEEK, cast(null as timestamp), current_timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampadd(^incorrect^, 1, current_timestamp)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"'INCORRECT' is not a valid time frame"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampdiff(^incorrect^, current_timestamp, current_timestamp)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"'INCORRECT' is not a valid time frame"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTimestampAddNullInterval
parameter_list|()
block|{
name|expr
argument_list|(
literal|"timestampadd(SQL_TSI_SECOND, cast(NULL AS INTEGER),"
operator|+
literal|" current_timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestampadd(SQL_TSI_DAY, cast(NULL AS INTEGER),"
operator|+
literal|" current_timestamp)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TIMESTAMP(0)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testNumericOperators
parameter_list|()
block|{
comment|// unary operator
name|expr
argument_list|(
literal|"- cast(1 as TINYINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+ cast(1 as INT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"- cast(1 as FLOAT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+ cast(1 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-1.643"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(4, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"+1.643"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(4, 3) NOT NULL"
argument_list|)
expr_stmt|;
comment|// addition operator
name|expr
argument_list|(
literal|"cast(1 as TINYINT) + cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as SMALLINT) + cast(5 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as REAL) + cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) + cast(5 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) + cast(5 as REAL)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as REAL)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(5, 2)) + cast(1 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.543 + 2.34"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as NUMERIC(5, 2)) + cast(1 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(13, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(null as SMALLINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(8, 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as TINYINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(6, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as DECIMAL(5, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(6, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) + cast(1 as DECIMAL(6, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(7, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 2)) + cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(7, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(4, 2)) + cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(7, 4)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(19, 2)) + cast(1 as DECIMAL(19, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// subtraction operator
name|expr
argument_list|(
literal|"cast(1 as TINYINT) - cast(5 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as INTEGER) - cast(5 as SMALLINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as INTEGER) - cast(5 as REAL)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) - cast(5 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) - cast(5 as REAL)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(1 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DOUBLE) - cast(1 as DECIMAL)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1.543 - 24"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(14, 3) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5)) - cast(1 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(1 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(13, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(null as SMALLINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(8, 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(1 as TINYINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(6, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(1 as DECIMAL(7))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) - cast(1 as DECIMAL(6, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(7, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 2)) - cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(7, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL) - cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 4)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(19, 2)) - cast(1 as DECIMAL(19, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// multiply operator
name|expr
argument_list|(
literal|"cast(1 as TINYINT) * cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as SMALLINT) * cast(5 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as REAL) * cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) * cast(5 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(7, 3)) * 1.654"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(7, 3)) * cast (1.654 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(5, 2)) * cast(1 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 2)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) * cast(1 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(15, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) * cast(1 as SMALLINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) * cast(1 as TINYINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(8, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) * cast(1 as DECIMAL(5, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) * cast(1 as DECIMAL(6, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 4) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 2)) * cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(4, 2)) * cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 10)) * cast(null as DECIMAL(6, 10))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(10, 19)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(19, 2)) * cast(1 as DECIMAL(19, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 4) NOT NULL"
argument_list|)
expr_stmt|;
comment|// divide operator
name|expr
argument_list|(
literal|"cast(1 as TINYINT) / cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as SMALLINT) / cast(5 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as REAL) / cast(5 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"REAL NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as REAL) / cast(5 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(7, 3)) / 1.654"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(15, 8) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(7, 3)) / cast (1.654 as DOUBLE)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(5, 2)) / cast(1 as BIGINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 16)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) / cast(1 as INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(16, 13) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) / cast(1 as SMALLINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 8) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) / cast(1 as TINYINT)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(9, 6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) / cast(1 as DECIMAL(5, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(13, 8) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(5, 2)) / cast(1 as DECIMAL(6, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(14, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 2)) / cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(15, 9) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as DECIMAL(4, 2)) / cast(1 as DECIMAL(6, 4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(15, 9)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(4, 10)) / cast(null as DECIMAL(6, 19))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 6)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1 as DECIMAL(19, 2)) / cast(1 as DECIMAL(19, 2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(19, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"4/3"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"-4.0/3"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(13, 12) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"4/3.0"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(17, 6) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(2.3 as float)/3"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT NOT NULL"
argument_list|)
expr_stmt|;
comment|// null
name|expr
argument_list|(
literal|"cast(2.3 as float)/null"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"FLOAT"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFloorCeil
parameter_list|()
block|{
name|expr
argument_list|(
literal|"floor(cast(null as tinyint))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"TINYINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(1.2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(1.2e-2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"floor(interval '2' day)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(cast(null as bigint))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(1.2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(2, 0) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(1.2e-2)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"ceil(interval '2' second)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL SECOND NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that EXTRACT, FLOOR, CEIL functions accept abbreviations for    * time units (such as "Y" for "YEAR").    *    *<p>This used to be accomplished via the now deprecated    * {@code timeUnitCodes} method in {@link SqlParser.Config}, and is now    * accomplished via    * {@link RelDataTypeSystem#deriveTimeFrameSet(TimeFrameSet)}. */
annotation|@
name|Test
name|void
name|testTimeUnitCodes
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|simpleCodes
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|TimeUnit
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"Y"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
literal|"M"
argument_list|,
name|TimeUnit
operator|.
name|MONTH
argument_list|)
decl|.
name|put
argument_list|(
literal|"D"
argument_list|,
name|TimeUnit
operator|.
name|DAY
argument_list|)
decl|.
name|put
argument_list|(
literal|"H"
argument_list|,
name|TimeUnit
operator|.
name|HOUR
argument_list|)
decl|.
name|put
argument_list|(
literal|"N"
argument_list|,
name|TimeUnit
operator|.
name|MINUTE
argument_list|)
decl|.
name|put
argument_list|(
literal|"S"
argument_list|,
name|TimeUnit
operator|.
name|SECOND
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|// Time unit abbreviations for Microsoft SQL Server
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|mssqlCodes
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|TimeUnit
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"Y"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
literal|"YY"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
literal|"YYYY"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|)
decl|.
name|put
argument_list|(
literal|"Q"
argument_list|,
name|TimeUnit
operator|.
name|QUARTER
argument_list|)
decl|.
name|put
argument_list|(
literal|"QQ"
argument_list|,
name|TimeUnit
operator|.
name|QUARTER
argument_list|)
decl|.
name|put
argument_list|(
literal|"M"
argument_list|,
name|TimeUnit
operator|.
name|MONTH
argument_list|)
decl|.
name|put
argument_list|(
literal|"MM"
argument_list|,
name|TimeUnit
operator|.
name|MONTH
argument_list|)
decl|.
name|put
argument_list|(
literal|"W"
argument_list|,
name|TimeUnit
operator|.
name|WEEK
argument_list|)
decl|.
name|put
argument_list|(
literal|"WK"
argument_list|,
name|TimeUnit
operator|.
name|WEEK
argument_list|)
decl|.
name|put
argument_list|(
literal|"WW"
argument_list|,
name|TimeUnit
operator|.
name|WEEK
argument_list|)
decl|.
name|put
argument_list|(
literal|"DY"
argument_list|,
name|TimeUnit
operator|.
name|DOY
argument_list|)
decl|.
name|put
argument_list|(
literal|"DW"
argument_list|,
name|TimeUnit
operator|.
name|DOW
argument_list|)
decl|.
name|put
argument_list|(
literal|"D"
argument_list|,
name|TimeUnit
operator|.
name|DAY
argument_list|)
decl|.
name|put
argument_list|(
literal|"DD"
argument_list|,
name|TimeUnit
operator|.
name|DAY
argument_list|)
decl|.
name|put
argument_list|(
literal|"H"
argument_list|,
name|TimeUnit
operator|.
name|HOUR
argument_list|)
decl|.
name|put
argument_list|(
literal|"HH"
argument_list|,
name|TimeUnit
operator|.
name|HOUR
argument_list|)
decl|.
name|put
argument_list|(
literal|"N"
argument_list|,
name|TimeUnit
operator|.
name|MINUTE
argument_list|)
decl|.
name|put
argument_list|(
literal|"MI"
argument_list|,
name|TimeUnit
operator|.
name|MINUTE
argument_list|)
decl|.
name|put
argument_list|(
literal|"S"
argument_list|,
name|TimeUnit
operator|.
name|SECOND
argument_list|)
decl|.
name|put
argument_list|(
literal|"SS"
argument_list|,
name|TimeUnit
operator|.
name|SECOND
argument_list|)
decl|.
name|put
argument_list|(
literal|"MS"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECOND
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|checkTimeUnitCodes
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|checkTimeUnitCodes
argument_list|(
name|simpleCodes
argument_list|)
expr_stmt|;
name|checkTimeUnitCodes
argument_list|(
name|mssqlCodes
argument_list|)
expr_stmt|;
block|}
comment|/** Checks parsing of built-in functions that accept time unit    * abbreviations.    *    *<p>For example, {@code EXTRACT(Y FROM orderDate)} is using    * "Y" as an abbreviation for "YEAR".    *    *<p>Override if your parser supports more such functions. */
specifier|protected
name|void
name|checkTimeUnitCodes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|timeUnitCodes
parameter_list|)
block|{
name|SqlValidatorFixture
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
name|TimeFrameSet
operator|.
name|Builder
name|b
operator|=
name|TimeFrameSet
operator|.
name|builder
argument_list|()
block|;
name|b
operator|.
name|addAll
argument_list|(
name|frameSet
argument_list|)
block|;
name|timeUnitCodes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|name
parameter_list|,
name|unit
parameter_list|)
lambda|->
name|b
operator|.
name|addAlias
argument_list|(
name|name
argument_list|,
name|unit
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
block|;
return|return
name|b
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

begin_decl_stmt
specifier|final
name|String
name|ts
init|=
literal|"TIMESTAMP '2020-08-27 18:16:43'"
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|BiConsumer
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|validConsumer
init|=
parameter_list|(
name|abbrev
parameter_list|,
name|timeUnit
parameter_list|)
lambda|->
block|{
name|f
operator|.
name|withSql
argument_list|(
literal|"select extract("
operator|+
name|abbrev
operator|+
literal|" from "
operator|+
name|ts
operator|+
literal|")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select floor("
operator|+
name|ts
operator|+
literal|" to "
operator|+
name|abbrev
operator|+
literal|")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select ceil("
operator|+
name|ts
operator|+
literal|" to "
operator|+
name|abbrev
operator|+
literal|")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
decl_stmt|;
end_decl_stmt

begin_decl_stmt
name|BiConsumer
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|invalidConsumer
init|=
parameter_list|(
name|abbrev
parameter_list|,
name|timeUnit
parameter_list|)
lambda|->
block|{
specifier|final
name|String
name|upAbbrev
init|=
name|abbrev
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|message
init|=
literal|"'"
operator|+
name|upAbbrev
operator|+
literal|"' is not a valid time frame"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select extract(^"
operator|+
name|abbrev
operator|+
literal|"^ from "
operator|+
name|ts
operator|+
literal|")"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"SELECT FLOOR("
operator|+
name|ts
operator|+
literal|" to ^"
operator|+
name|abbrev
operator|+
literal|"^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"SELECT CEIL("
operator|+
name|ts
operator|+
literal|" to ^"
operator|+
name|abbrev
operator|+
literal|"^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
end_decl_stmt

begin_comment
comment|// Check that each valid code passes each query that it should.
end_comment

begin_expr_stmt
name|timeUnitCodes
operator|.
name|forEach
argument_list|(
name|validConsumer
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// If "M" is a valid code then "m" should be also.
end_comment

begin_expr_stmt
name|timeUnitCodes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|abbrev
parameter_list|,
name|timeUnit
parameter_list|)
lambda|->
name|validConsumer
operator|.
name|accept
argument_list|(
name|abbrev
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|timeUnit
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Check that invalid codes generate the right error messages.
end_comment

begin_decl_stmt
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TimeUnit
argument_list|>
name|invalidCodes
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"A"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|,
literal|"a"
argument_list|,
name|TimeUnit
operator|.
name|YEAR
argument_list|)
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|invalidCodes
operator|.
name|forEach
argument_list|(
name|invalidConsumer
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/** Checks parsing of built-in functions that accept time unit    *  Checks WEEK(WEEKDAY)    *<p>Override if your parser supports more such functions. */
end_comment

begin_function
unit|@
name|Test
name|void
name|checkWeekdayCustomTimeFrames
parameter_list|()
block|{
name|SqlValidatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|BIG_QUERY
argument_list|)
argument_list|)
decl_stmt|;
comment|// Check that each valid code passes each query that it should.
specifier|final
name|String
name|ds
init|=
literal|"DATE '2022-12-25'"
decl_stmt|;
name|Consumer
argument_list|<
name|String
argument_list|>
name|validConsumer
init|=
name|weekday
lambda|->
block|{
name|f
operator|.
name|withSql
argument_list|(
literal|"select date_trunc("
operator|+
name|ds
operator|+
literal|", "
operator|+
name|weekday
operator|+
literal|")"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
decl_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(SUNDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(MONDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(TUESDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(WEDNESDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(THURSDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(FRIDAY)"
argument_list|)
expr_stmt|;
name|validConsumer
operator|.
name|accept
argument_list|(
literal|"WEEK(SUNDAY)"
argument_list|)
expr_stmt|;
comment|// Check that each invalid code fails each query that it should.
name|Consumer
argument_list|<
name|String
argument_list|>
name|invalidConsumer
init|=
name|weekday
lambda|->
block|{
name|String
name|errorMessage
init|=
literal|"Column '"
operator|+
name|weekday
operator|+
literal|"' not found in any table"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select date_trunc("
operator|+
name|ds
operator|+
literal|", ^"
operator|+
name|weekday
operator|+
literal|"^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|invalidConsumer
operator|.
name|accept
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|public
name|void
name|checkWinFuncExpWithWinClause
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|winExp
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|// test window partition clause. See SQL 2003 specification for detail
end_comment

begin_function
annotation|@
name|Disabled
name|void
name|testWinPartClause
parameter_list|()
block|{
name|win
argument_list|(
literal|"window w as (w2 order by deptno), w2 as (^rang^e 100 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Referenced window cannot have framing declarations"
argument_list|)
expr_stmt|;
comment|// Test specified collation, window clause syntax rule 4,5.
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-820">[CALCITE-820]    * Validate that window functions have OVER clause</a>, and    *<a href="https://issues.apache.org/jira/browse/CALCITE-1340">[CALCITE-1340]    * Window aggregates give invalid errors</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWindowFunctionsWithoutOver
parameter_list|()
block|{
name|winSql
argument_list|(
literal|"select sum(empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"order by ^row_number()^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"OVER clause is necessary for window functions"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^rank()^\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"OVER clause is necessary for window functions"
argument_list|)
expr_stmt|;
comment|// With [CALCITE-1340], the validator would see RANK without OVER,
comment|// mistakenly think this is an aggregating query, and wrongly complain
comment|// about the PARTITION BY: "Expression 'DEPTNO' is not being grouped"
name|winSql
argument_list|(
literal|"select cume_dist() over w , ^rank()^\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as (partition by deptno order by deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"OVER clause is necessary for window functions"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^nth_value(sal, 2)^\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"OVER clause is necessary for window functions"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOverInPartitionBy
parameter_list|()
block|{
name|winSql
argument_list|(
literal|"select sum(deptno) over ^(partition by sum(deptno)\n"
operator|+
literal|"over(order by deptno))^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"PARTITION BY expression should not contain OVER clause"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select sum(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as ^(partition by sum(deptno) over(order by deptno))^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"PARTITION BY expression should not contain OVER clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOverInOrderBy
parameter_list|()
block|{
name|winSql
argument_list|(
literal|"select sum(deptno) over ^(order by sum(deptno)\n"
operator|+
literal|"over(order by deptno))^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY expression should not contain OVER clause"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select sum(deptno) over w\n"
operator|+
literal|"from emp\n"
operator|+
literal|"window w as ^(order by sum(deptno) over(order by deptno))^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY expression should not contain OVER clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFunctionInOver
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(deptno) over (order by count(empno))\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|winSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select sum(^empno^) over (order by count(empno))\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|winSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateInsideOverClause
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^empno^,\n"
operator|+
literal|"  sum(empno) over (partition by min(sal)) empno_sum\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select ^empno^,\n"
operator|+
literal|"  sum(empno) over (partition by min(sal)) empno_sum\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by empno"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateInsideOverClause2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^empno^,\n"
operator|+
literal|"  sum(empno) over ()\n"
operator|+
literal|"  + sum(empno) over (partition by min(sal)) empno_sum\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowFunctions
parameter_list|()
block|{
comment|// SQL 03 Section 6.10
comment|// Window functions may only appear in the<select list> of a
comment|//<query specification> or<select statement: single row>,
comment|// or the<order by clause> of a simple table query.
comment|// See 4.15.3 for detail
name|winSql
argument_list|(
literal|"select *\n"
operator|+
literal|" from emp\n"
operator|+
literal|" where ^sum(sal) over (partition by deptno\n"
operator|+
literal|"    order by empno\n"
operator|+
literal|"    rows 3 preceding)^> 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Windowed aggregate expression is illegal in WHERE clause"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select *\n"
operator|+
literal|" from emp\n"
operator|+
literal|" group by ename, ^sum(sal) over (partition by deptno\n"
operator|+
literal|"    order by empno\n"
operator|+
literal|"    rows 3 preceding)^ + 10\n"
operator|+
literal|"order by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Windowed aggregate expression is illegal in GROUP BY clause"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select *\n"
operator|+
literal|" from emp\n"
operator|+
literal|" join dept on emp.deptno = dept.deptno\n"
operator|+
literal|" and ^sum(sal) over (partition by emp.deptno\n"
operator|+
literal|"    order by empno\n"
operator|+
literal|"    rows 3 preceding)^ = dept.deptno + 40\n"
operator|+
literal|"order by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Windowed aggregate expression is illegal in ON clause"
argument_list|)
expr_stmt|;
comment|// rule 3, a)
name|winSql
argument_list|(
literal|"select sal from emp\n"
operator|+
literal|"order by sum(sal) over (partition by deptno order by deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// scope reference
comment|// rule 4,
comment|// valid window functions
name|winExp
argument_list|(
literal|"sum(sal)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowFunctions2
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|defined
init|=
name|asList
argument_list|(
literal|"CUME_DIST"
argument_list|,
literal|"DENSE_RANK"
argument_list|,
literal|"PERCENT_RANK"
argument_list|,
literal|"RANK"
argument_list|,
literal|"ROW_NUMBER"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bug
operator|.
name|TODO_FIXED
condition|)
block|{
name|sql
argument_list|(
literal|"select rank() over (order by deptno) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
name|winSql
argument_list|(
literal|"select rank() over w from emp\n"
operator|+
literal|"window w as ^(partition by sal)^, w2 as (w order by deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select rank() over w2 from emp\n"
operator|+
literal|"window w as (partition by sal), w2 as (w order by deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// row_number function
name|winExp
argument_list|(
literal|"row_number() over (order by deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"row_number() over (partition by deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"row_number() over ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"row_number() over (order by deptno ^rows^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
name|winExp
argument_list|(
literal|"row_number() over (order by deptno ^range^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
comment|// rank function type
if|if
condition|(
name|defined
operator|.
name|contains
argument_list|(
literal|"DENSE_RANK"
argument_list|)
condition|)
block|{
name|winExp
argument_list|(
literal|"^dense_rank()^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"OVER clause is necessary for window functions"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkWinFuncExpWithWinClause
argument_list|(
literal|"^dense_rank()^"
argument_list|,
literal|"Function 'DENSE_RANK\\(\\)' is not defined"
argument_list|)
expr_stmt|;
block|}
name|winExp
argument_list|(
literal|"rank() over (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"percent_rank() over (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"cume_dist() over (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"nth_value(sal, 2) over (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// rule 6a
comment|// ORDER BY required with RANK& DENSE_RANK
name|winSql
argument_list|(
literal|"select rank() over ^(partition by deptno)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select dense_rank() over ^(partition by deptno)^ from emp "
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select rank() over w from emp window w as ^(partition by deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select dense_rank() over w from emp\n"
operator|+
literal|"window w as ^(partition by deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
comment|// rule 6b
comment|// Framing not allowed with RANK& DENSE_RANK functions
comment|// window framing defined in window clause
name|winSql
argument_list|(
literal|"select rank() over w from emp\n"
operator|+
literal|"window w as (order by empno ^rows^ 2 preceding )"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select dense_rank() over w from emp\n"
operator|+
literal|"window w as (order by empno ^rows^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
if|if
condition|(
name|defined
operator|.
name|contains
argument_list|(
literal|"PERCENT_RANK"
argument_list|)
condition|)
block|{
name|winSql
argument_list|(
literal|"select percent_rank() over w from emp\n"
operator|+
literal|"window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select percent_rank() over w from emp\n"
operator|+
literal|"window w as (order by empno ^rows^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select percent_rank() over w from emp\n"
operator|+
literal|"window w as ^(partition by empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkWinFuncExpWithWinClause
argument_list|(
literal|"^percent_rank()^"
argument_list|,
literal|"Function 'PERCENT_RANK\\(\\)' is not defined"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|defined
operator|.
name|contains
argument_list|(
literal|"CUME_DIST"
argument_list|)
condition|)
block|{
name|winSql
argument_list|(
literal|"select cume_dist() over w from emp\n"
operator|+
literal|"window w as ^(rows 2 preceding)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select cume_dist() over w from emp\n"
operator|+
literal|"window w as (order by empno ^rows^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select cume_dist() over w from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select cume_dist() over (order by empno) from emp "
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|checkWinFuncExpWithWinClause
argument_list|(
literal|"^cume_dist()^"
argument_list|,
literal|"Function 'CUME_DIST\\(\\)' is not defined"
argument_list|)
expr_stmt|;
block|}
comment|// window framing defined in in-line window
name|winSql
argument_list|(
literal|"select rank() over (order by empno ^range^ 2 preceding ) from emp "
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select dense_rank() over (order by empno ^rows^ 2 preceding ) from emp "
argument_list|)
operator|.
name|fails
argument_list|(
name|ROW_RANGE_NOT_ALLOWED_WITH_RANK
argument_list|)
expr_stmt|;
if|if
condition|(
name|defined
operator|.
name|contains
argument_list|(
literal|"PERCENT_RANK"
argument_list|)
condition|)
block|{
name|winSql
argument_list|(
literal|"select percent_rank() over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|// invalid column reference
name|checkWinFuncExpWithWinClause
argument_list|(
literal|"sum(^invalidColumn^)"
argument_list|,
literal|"Column 'INVALIDCOLUMN' not found in any table"
argument_list|)
expr_stmt|;
comment|// invalid window functions
name|checkWinFuncExpWithWinClause
argument_list|(
literal|"^invalidFun(sal)^"
argument_list|,
literal|"No match found for function signature INVALIDFUN\\(<NUMERIC>\\)"
argument_list|)
expr_stmt|;
comment|// 6.10 rule 10. no distinct allowed aggregate function
comment|// Fails in parser.
comment|// checkWinFuncExpWithWinClause(" sum(distinct sal) over w ", null);
comment|// 7.11 rule 10c
name|winSql
argument_list|(
literal|"select sum(sal) over (w partition by ^deptno^)\n"
operator|+
literal|" from emp window w as (order by empno rows 2 preceding )"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"PARTITION BY not allowed with existing window reference"
argument_list|)
expr_stmt|;
comment|// 7.11 rule 10d
name|winSql
argument_list|(
literal|"select sum(sal) over (w order by ^empno^)\n"
operator|+
literal|" from emp window w as (order by empno rows 2 preceding )"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY not allowed in both base and referenced windows"
argument_list|)
expr_stmt|;
comment|// 7.11 rule 10e
name|winSql
argument_list|(
literal|"select sum(sal) over (w)\n"
operator|+
literal|" from emp window w as (order by empno ^rows^ 2 preceding )"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Referenced window cannot have framing declarations"
argument_list|)
expr_stmt|;
comment|// Empty window is OK for functions that don't require ordering.
name|winSql
argument_list|(
literal|"select sum(sal) over () from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select sum(sal) over w from emp window w as ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select count(*) over () from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select count(*) over w from emp window w as ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select rank() over ^()^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select rank() over w from emp window w as ^()^"
argument_list|)
operator|.
name|fails
argument_list|(
name|RANK_REQUIRES_ORDER_BY
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-883">[CALCITE-883]    * Give error if the aggregate function don't support null treatment</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWindowFunctionsIgnoreNulls
parameter_list|()
block|{
name|winSql
argument_list|(
literal|"select lead(sal, 4) over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lead(sal, 4) IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lag(sal, 4) over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lag(sal, 4) IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select first_value(sal) over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select first_value(sal) IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select last_value(sal) over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select last_value(sal) IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^sum(sal)^ IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'SUM'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^count(sal)^ IGNORE NULLS over (w)\n"
operator|+
literal|" from emp window w as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'COUNT'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^avg(sal)^ IGNORE NULLS\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'AVG'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^abs(sal)^ IGNORE NULLS\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'ABS'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-883">[CALCITE-883]    * Give error if the aggregate function don't support null treatment</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWindowFunctionsRespectNulls
parameter_list|()
block|{
name|winSql
argument_list|(
literal|"select lead(sal, 4) over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lead(sal, 4) RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lag(sal, 4) over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select lag(sal, 4) RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select first_value(sal) over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select first_value(sal) RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select last_value(sal) over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select last_value(sal) RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^sum(sal)^ RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'SUM'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^count(sal)^ RESPECT NULLS over (w)\n"
operator|+
literal|"from emp window w as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'COUNT'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^avg(sal)^ RESPECT NULLS\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'AVG'"
argument_list|)
expr_stmt|;
name|winSql
argument_list|(
literal|"select ^abs(sal)^ RESPECT NULLS\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify IGNORE NULLS or RESPECT NULLS following 'ABS'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1954">[CALCITE-1954]    * Column from outer join should be null, whether or not it is aliased</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLeftOuterJoinWithAlias
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
literal|"select *\n"
operator|+
literal|"from (select row_number() over (order by sal) from emp) as emp1(r1)\n"
operator|+
literal|"left outer join\n"
operator|+
literal|"(select  dense_rank() over(order by sal) from emp) as emp2(r2)\n"
operator|+
literal|"on (emp1.r1 = emp2.r2)"
decl_stmt|;
comment|// In this case, R2 is nullable in the join since we have a left outer join.
specifier|final
name|String
name|type
init|=
literal|"RecordType(BIGINT NOT NULL R1, BIGINT R2) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// Similar query, without "AS t(c)"
specifier|final
name|String
name|query2
init|=
literal|"select *\n"
operator|+
literal|"from (select row_number() over (order by sal) as r1 from emp) as emp1\n"
operator|+
literal|"left outer join\n"
operator|+
literal|"(select dense_rank() over(order by sal) as r2 from emp) as emp2\n"
operator|+
literal|"on (emp1.r1 = emp2.r2)"
decl_stmt|;
name|sql
argument_list|(
name|query2
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// Similar query, without "AS t"
specifier|final
name|String
name|query3
init|=
literal|"select *\n"
operator|+
literal|"from (select row_number() over (order by sal) as r1 from emp)\n"
operator|+
literal|"left outer join\n"
operator|+
literal|"(select dense_rank() over(order by sal) as r2 from emp)\n"
operator|+
literal|"on r1 = r2"
decl_stmt|;
name|sql
argument_list|(
name|query3
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInvalidWindowFunctionWithGroupBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select max(^empno^) over () from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select max(deptno) over (partition by ^empno^) from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rank() over (order by ^empno^) from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInlineWinDef
parameter_list|()
block|{
comment|// the<window specification> used by windowed agg functions is
comment|// fully defined in SQL 03 Std. section 7.1<window clause>
name|sql
argument_list|(
literal|"select sum(sal) over (partition by deptno order by empno)\n"
operator|+
literal|"from emp order by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) OVER ("
operator|+
literal|"partition by deptno "
operator|+
literal|"order by empno "
operator|+
literal|"rows 2 preceding )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) OVER ("
operator|+
literal|"order by 1 "
operator|+
literal|"rows 2 preceding )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) OVER ("
operator|+
literal|"order by 'b' "
operator|+
literal|"rows 2 preceding )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"partition by deptno "
operator|+
literal|"order by 1+1 rows 26 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over (order by deptno rows unbounded preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over (order by deptno rows current row)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ^("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between unbounded preceding and unbounded following)^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ^("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between CURRENT ROW and unbounded following)^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between unbounded preceding and CURRENT ROW)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// logical current row/current row
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between CURRENT ROW and CURRENT ROW)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// physical current row/current row
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"range between CURRENT ROW and CURRENT ROW)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 preceding and CURRENT ROW)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp
argument_list|(
literal|"sum(sal) OVER (w "
operator|+
literal|"rows 2 preceding )"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over (order by deptno range 2.0 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Failure mode tests
name|winExp2
argument_list|(
literal|"sum(sal) over (order by deptno "
operator|+
literal|"rows between ^UNBOUNDED FOLLOWING^ and unbounded preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"UNBOUNDED FOLLOWING cannot be specified for the lower frame boundary"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 preceding and ^UNBOUNDED PRECEDING^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"UNBOUNDED PRECEDING cannot be specified for the upper frame boundary"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between CURRENT ROW and ^2 preceding^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be PRECEDING when lower boundary is CURRENT ROW"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 following and ^CURRENT ROW^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be CURRENT ROW when lower boundary is FOLLOWING"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 following and ^2 preceding^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be PRECEDING when lower boundary is FOLLOWING"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by deptno "
operator|+
literal|"RANGE BETWEEN ^INTERVAL '1' SECOND^ PRECEDING AND INTERVAL '1' SECOND FOLLOWING)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Data Type mismatch between ORDER BY and RANGE clause"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ("
operator|+
literal|"order by empno "
operator|+
literal|"RANGE BETWEEN ^INTERVAL '1' SECOND^ PRECEDING AND INTERVAL '1' SECOND FOLLOWING)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Data Type mismatch between ORDER BY and RANGE clause"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over (order by deptno, empno ^range^ 2 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"RANGE clause cannot be used with compound ORDER BY clause"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ^(partition by deptno range 5 preceding)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Window specification must contain an ORDER BY clause"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over ^w1^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Window 'W1' not found"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) OVER (^w1^ "
operator|+
literal|"partition by deptno "
operator|+
literal|"order by empno "
operator|+
literal|"rows 2 preceding )"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Window 'W1' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPartitionByExpr
parameter_list|()
block|{
name|winExp2
argument_list|(
literal|"sum(sal) over (partition by empno + deptno order by empno range 5 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|winSql
argument_list|(
literal|"select "
operator|+
literal|"sum(sal) over (partition by ^empno + ename^ order by empno range 5 preceding)"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply '\\+' to arguments of type '<INTEGER> \\+<VARCHAR\\(20\\)>'.*"
argument_list|)
expr_stmt|;
name|winExp2
argument_list|(
literal|"sum(sal) over (partition by empno + ename order by empno range 5 preceding)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowClause
parameter_list|()
block|{
comment|// -----------------------------------
comment|// --   positive testings           --
comment|// -----------------------------------
comment|// correct syntax:
name|winExp
argument_list|(
literal|"sum(sal) as sumsal"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|win
argument_list|(
literal|"window w as (partition by sal order by deptno rows 2 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// define window on an existing window
name|win
argument_list|(
literal|"window w as (order by sal), w1 as (w)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// -----------------------------------
comment|// --   negative testings           --
comment|// -----------------------------------
comment|// Test fails in parser
comment|// checkWinClauseExp("window foo.w as (range 100 preceding) "+
comment|//    "Window name must be a simple identifier\");
comment|// rule 11
comment|// a)
comment|// missing window order clause.
name|win
argument_list|(
literal|"window w as ^(range 100 preceding)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Window specification must contain an ORDER BY clause"
argument_list|)
expr_stmt|;
comment|// order by number
name|win
argument_list|(
literal|"window w as (order by sal range 100 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// order by date
name|win
argument_list|(
literal|"window w as (order by hiredate range ^100^ preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Data Type mismatch between ORDER BY and RANGE clause"
argument_list|)
expr_stmt|;
comment|// order by string, should fail
name|win
argument_list|(
literal|"window w as (order by ename range ^100^ preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Data type of ORDER BY prohibits use of RANGE clause"
argument_list|)
expr_stmt|;
comment|// todo: interval test ???
comment|// b)
comment|// valid
name|win
argument_list|(
literal|"window w as (rows 2 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// invalid tests exact numeric for the unsigned value specification The
comment|// following two test fail as they should but in the parser: JR not
comment|// anymore now the validator kicks out
name|win
argument_list|(
literal|"window w as (rows ^-2.5^ preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ROWS value must be a non-negative integral constant"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as (rows ^-2^ preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ROWS value must be a non-negative integral constant"
argument_list|)
expr_stmt|;
comment|// This test should fail as per 03 Std. but we pass it and plan
comment|// to apply the FLOOR function before window processing
name|win
argument_list|(
literal|"window w as (rows ^2.5^ preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ROWS value must be a non-negative integral constant"
argument_list|)
expr_stmt|;
comment|// -----------------------------------
comment|// --   negative testings           --
comment|// -----------------------------------
comment|// reference undefined xyz column
name|win
argument_list|(
literal|"window w as (partition by ^xyz^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'XYZ' not found in any table"
argument_list|)
expr_stmt|;
comment|// window definition is empty when applied to unsorted table
name|win
argument_list|(
literal|"window w as ^( /* boo! */  )^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// duplicate window name
name|win
argument_list|(
literal|"window w as (order by empno), ^w^ as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate window names not allowed"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window win1 as (order by empno), ^win1^ as (order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate window names not allowed"
argument_list|)
expr_stmt|;
comment|// syntax rule 6
name|sql
argument_list|(
literal|"select min(sal) over (order by deptno) from emp group by deptno,sal"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select min(sal) over (order by ^deptno^) from emp group by sal"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select min(sal) over\n"
operator|+
literal|"(partition by comm order by deptno) from emp group by deptno,sal,comm"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select min(sal) over\n"
operator|+
literal|"(partition by ^comm^ order by deptno) from emp group by deptno,sal"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'COMM' is not being grouped"
argument_list|)
expr_stmt|;
comment|// syntax rule 7
name|win
argument_list|(
literal|"window w as ^(order by rank() over (order by sal))^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY expression should not contain OVER clause"
argument_list|)
expr_stmt|;
comment|// ------------------------------------
comment|// ---- window frame between tests ----
comment|// ------------------------------------
comment|// bound 1 shall not specify UNBOUNDED FOLLOWING
name|win
argument_list|(
literal|"window w as (rows between ^unbounded following^ and 5 following)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"UNBOUNDED FOLLOWING cannot be specified for the lower frame boundary"
argument_list|)
expr_stmt|;
comment|// bound 2 shall not specify UNBOUNDED PRECEDING
name|win
argument_list|(
literal|"window w as ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 preceding and ^UNBOUNDED PRECEDING^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"UNBOUNDED PRECEDING cannot be specified for the upper frame boundary"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 following and ^2 preceding^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be PRECEDING when lower boundary is FOLLOWING"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between CURRENT ROW and ^2 preceding^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be PRECEDING when lower boundary is CURRENT ROW"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as ("
operator|+
literal|"order by deptno "
operator|+
literal|"rows between 2 following and ^CURRENT ROW^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Upper frame boundary cannot be CURRENT ROW when lower boundary is FOLLOWING"
argument_list|)
expr_stmt|;
comment|// Sql '03 rule 10 c) assertExceptionIsThrown("select deptno as d, sal
comment|// as s from emp window w as (partition by deptno order by sal), w2 as
comment|// (w partition by deptno)", null); checkWinClauseExp("window w as
comment|// (partition by sal order by deptno), w2 as (w partition by sal)",
comment|// null); d) valid because existing window does not have an ORDER BY
comment|// clause
name|win
argument_list|(
literal|"window w as (w2 range 2 preceding), w2 as (order by sal)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|win
argument_list|(
literal|"window w as ^(partition by sal)^, w2 as (w order by deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|win
argument_list|(
literal|"window w as (w2 partition by ^sal^), w2 as (order by deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"PARTITION BY not allowed with existing window reference"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as (partition by sal order by deptno), w2 as (w order by ^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ORDER BY not allowed in both base and referenced windows"
argument_list|)
expr_stmt|;
comment|// e)
name|win
argument_list|(
literal|"window w as (w2 order by deptno), w2 as (^range^ 100 preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Referenced window cannot have framing declarations"
argument_list|)
expr_stmt|;
comment|// rule 12, todo: test scope of window assertExceptionIsThrown("select
comment|// deptno as d from emp window d as (partition by deptno)", null);
comment|// rule 13
name|win
argument_list|(
literal|"window w as (order by sal)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|win
argument_list|(
literal|"window w as (order by ^non_exist_col^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'NON_EXIST_COL' not found in any table"
argument_list|)
expr_stmt|;
name|win
argument_list|(
literal|"window w as (partition by ^non_exist_col^ order by sal)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'NON_EXIST_COL' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowClause2
parameter_list|()
block|{
comment|// 7.10 syntax rule 2<new window name> NWN1 shall not be contained in
comment|// the scope of another<new window name> NWN2 such that NWN1 and NWN2
comment|// are equivalent.
name|win
argument_list|(
literal|"window\n"
operator|+
literal|"w  as (partition by deptno order by empno rows 2 preceding),\n"
operator|+
literal|"w2 as ^(partition by deptno order by empno rows 2 preceding)^\n"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate window specification not allowed in the same window clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowClauseWithSubQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from\n"
operator|+
literal|"( select sum(empno) over w, sum(deptno) over w from emp\n"
operator|+
literal|"window w as (order by hiredate range interval '1' minute preceding))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from\n"
operator|+
literal|"( select sum(empno) over w, sum(deptno) over w, hiredate from emp)\n"
operator|+
literal|"window w as (order by hiredate range interval '1' minute preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from\n"
operator|+
literal|"( select sum(empno) over w, sum(deptno) over w from emp)\n"
operator|+
literal|"window w as (order by ^hiredate^ range interval '1' minute preceding)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'HIREDATE' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-754">[CALCITE-754]    * Validator error when resolving OVER clause of JOIN query</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPartitionByColumnInJoinAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(1) over(partition by t1.ename)\n"
operator|+
literal|"from emp t1, emp t2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(1) over(partition by emp.ename)\n"
operator|+
literal|"from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(1) over(partition by ^deptno^)\n"
operator|+
literal|"from emp, dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1535">[CALCITE-1535]    * Give error if column referenced in ORDER BY is ambiguous</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrderByColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select emp.deptno from emp, dept order by emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Not ambiguous. There are two columns which could be referenced as
comment|// "deptno", but the one in the SELECT clause takes priority.
name|sql
argument_list|(
literal|"select emp.deptno from emp, dept order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.deptno as deptno from emp, dept order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as deptno from emp, dept order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.deptno as n, dept.deptno as n from emp, dept order by ^n^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'N' is ambiguous"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as deptno, dept.deptno from emp, dept\n"
operator|+
literal|"order by ^deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as deptno, dept.deptno from emp, dept\n"
operator|+
literal|"order by emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as deptno, dept.deptno from emp, dept order by 1, 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno as \"deptno\", deptno from emp order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno as \"deptno\", deptno from emp order by \"deptno\""
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowNegative
parameter_list|()
block|{
comment|// Do not fail when window has negative size. Allow
specifier|final
name|String
name|negSize
init|=
literal|null
decl_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between 2 preceding and 4 preceding"
argument_list|,
name|negSize
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between 2 preceding and 3 preceding"
argument_list|,
name|negSize
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between 2 preceding and 2 preceding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between unbounded preceding and current row"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Unbounded following IS supported
specifier|final
name|String
name|unboundedFollowing
init|=
literal|null
decl_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between unbounded preceding and unbounded following"
argument_list|,
name|unboundedFollowing
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between current row and unbounded following"
argument_list|,
name|unboundedFollowing
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between current row and 2 following"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"range between 2 preceding and 2 following"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"range between 2 preceding and -2 preceding"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"range between 4 following and 3 following"
argument_list|,
name|negSize
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"range between 4 following and 5 following"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between 1 following and 0 following"
argument_list|,
name|negSize
argument_list|)
expr_stmt|;
name|checkNegWindow
argument_list|(
literal|"rows between 0 following and 0 following"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|void
name|checkNegWindow
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|String
name|sql
init|=
literal|"select sum(deptno) over ^(order by empno "
operator|+
name|s
operator|+
literal|")^ from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|failsIf
argument_list|(
name|msg
operator|!=
literal|null
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWindowPartial
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(deptno) over (\n"
operator|+
literal|"order by deptno, empno rows 2 preceding disallow partial)\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// cannot do partial over logical window
name|sql
argument_list|(
literal|"select sum(deptno) over (\n"
operator|+
literal|"  partition by deptno\n"
operator|+
literal|"  order by empno\n"
operator|+
literal|"  range between 2 preceding and 3 following\n"
operator|+
literal|"  ^disallow partial^)\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot use DISALLOW PARTIAL with window based on RANGE"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOneWinFunc
parameter_list|()
block|{
name|win
argument_list|(
literal|"window w as (partition by sal order by deptno rows 2 preceding)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNameResolutionInValuesClause
parameter_list|()
block|{
specifier|final
name|String
name|emps
init|=
literal|"(select 1 as empno, 'x' as name, 10 as deptno, 'M' as gender, 'San Francisco' as city, 30 as empid, 25 as age from (values (1)))"
decl_stmt|;
specifier|final
name|String
name|depts
init|=
literal|"(select 10 as deptno, 'Sales' as name from (values (1)))"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" join "
operator|+
name|depts
operator|+
literal|"\n"
operator|+
literal|" on ^emps^.deptno = deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMPS' not found"
argument_list|)
expr_stmt|;
comment|// this is ok
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" as e\n"
operator|+
literal|" join "
operator|+
name|depts
operator|+
literal|" as d\n"
operator|+
literal|" on e.deptno = d.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// fail: ambiguous column in WHERE
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" as emps,\n"
operator|+
literal|" "
operator|+
name|depts
operator|+
literal|"\n"
operator|+
literal|"where ^deptno^> 5"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// fail: ambiguous column reference in ON clause
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" as e\n"
operator|+
literal|" join "
operator|+
name|depts
operator|+
literal|" as d\n"
operator|+
literal|" on e.deptno = ^deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// ok: column 'age' is unambiguous
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" as e\n"
operator|+
literal|" join "
operator|+
name|depts
operator|+
literal|" as d\n"
operator|+
literal|" on e.deptno = age"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ok: reference to derived column
name|sql
argument_list|(
literal|"select * from "
operator|+
name|depts
operator|+
literal|"\n"
operator|+
literal|" join (select mod(age, 30) as agemod from "
operator|+
name|emps
operator|+
literal|")\n"
operator|+
literal|"on deptno = agemod"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// fail: deptno is ambiguous
name|sql
argument_list|(
literal|"select name from "
operator|+
name|depts
operator|+
literal|"\n"
operator|+
literal|"join (select mod(age, 30) as agemod, deptno from "
operator|+
name|emps
operator|+
literal|")\n"
operator|+
literal|"on ^deptno^ = agemod"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// fail: lateral reference
name|sql
argument_list|(
literal|"select * from "
operator|+
name|emps
operator|+
literal|" as e,\n"
operator|+
literal|" (select 1, ^e^.deptno from (values(true))) as d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found"
argument_list|)
expr_stmt|;
comment|// fail: deptno is ambiguous
name|sql
argument_list|(
literal|"select ^deptno^, name from "
operator|+
name|depts
operator|+
literal|"as d\n"
operator|+
literal|"join "
operator|+
name|emps
operator|+
literal|"as e\n"
operator|+
literal|"on d.deptno = e.deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedFrom
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values (true)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values(true))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (select * from (values(true)))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (select * from (select * from (values(true))))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ("
operator|+
literal|"  select * from ("
operator|+
literal|"    select * from (values(true))"
operator|+
literal|"    union"
operator|+
literal|"    select * from (values (false)))"
operator|+
literal|"  except"
operator|+
literal|"  select * from (values(true)))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAmbiguousColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp join dept\n"
operator|+
literal|" on emp.deptno = ^deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// this is ok
name|sql
argument_list|(
literal|"select * from emp as e\n"
operator|+
literal|" join dept as d\n"
operator|+
literal|" on e.deptno = d.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// fail: ambiguous column in WHERE
name|sql
argument_list|(
literal|"select * from emp as emps, dept\n"
operator|+
literal|"where ^deptno^> 5"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// fail: alias 'd' obscures original table name 'dept'
name|sql
argument_list|(
literal|"select * from emp as emps, dept as d\n"
operator|+
literal|"where ^dept^.deptno> 5"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'DEPT' not found"
argument_list|)
expr_stmt|;
comment|// fail: ambiguous column reference in ON clause
name|sql
argument_list|(
literal|"select * from emp as e\n"
operator|+
literal|" join dept as d\n"
operator|+
literal|" on e.deptno = ^deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// ok: column 'comm' is unambiguous
name|sql
argument_list|(
literal|"select * from emp as e\n"
operator|+
literal|" join dept as d\n"
operator|+
literal|" on e.deptno = comm"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ok: reference to derived column
name|sql
argument_list|(
literal|"select * from dept\n"
operator|+
literal|" join (select mod(comm, 30) as commmod from emp)\n"
operator|+
literal|"on deptno = commmod"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// fail: deptno is ambiguous
name|sql
argument_list|(
literal|"select name from dept\n"
operator|+
literal|"join (select mod(comm, 30) as commmod, deptno from emp)\n"
operator|+
literal|"on ^deptno^ = commmod"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// fail: lateral reference
name|sql
argument_list|(
literal|"select * from emp as e,\n"
operator|+
literal|" (select 1, ^e^.deptno from (values(true))) as d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testExpandStar
parameter_list|()
block|{
comment|// dtbug 282 -- "select r.* from sales.depts" gives NPE.
comment|// dtbug 318 -- error location should be ^r^ not ^r.*^.
name|sql
argument_list|(
literal|"select ^r^.* from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'R'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select e.* from emp as e"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.* from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Error message could be better (EMPNO does exist, but it's a column).
name|sql
argument_list|(
literal|"select ^empno^ .  * from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Not a record type. The '\\*' operator requires a record"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^emp.empno^ .  * from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Not a record type. The '\\*' operator requires a record"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-546">[CALCITE-546]    * Allow table, column and field called '*'</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testStarIdentifier
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT * FROM (VALUES (0, 0)) AS T(A, \"*\")"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL A, INTEGER NOT NULL *) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarAliasFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select emp.^*^ AS x from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown field '\\*'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNonLocalStar
parameter_list|()
block|{
comment|// MySQL allows this, and now so do we
name|sql
argument_list|(
literal|"select * from emp e where exists (\n"
operator|+
literal|"  select e.* from dept where dept.deptno = e.deptno)"
argument_list|)
operator|.
name|type
argument_list|(
name|EMP_RECORD_TYPE
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Parser does not allow "*" in FROM clause.    * Parser allows "*" in column expressions, but throws if they are outside    * of the SELECT clause.    *    * @see #testStarIdentifier()    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testStarInFromFails
parameter_list|()
block|{
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
literal|"select emp.empno from emp where emp.^*^ is not null"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown field '\\*'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStarDotIdFails
parameter_list|()
block|{
comment|// Fails in parser
name|sql
argument_list|(
literal|"select emp.^*^.\"EXPR$1\" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Unknown field '\\*'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.^*^.foo from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Unknown field '\\*'"
argument_list|)
expr_stmt|;
comment|// Parser does not allow star dot identifier.
name|sql
argument_list|(
literal|"select *^.^foo from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \".\" at .*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAsColumnList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select d.a, b from dept as d(a, b)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select d.^deptno^ from dept as d(a, b)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Column 'DEPTNO' not found in table 'D'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from dept as d(^a, b, c^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*List of column aliases must have same degree as table; "
operator|+
literal|"table has 2 columns \\('DEPTNO', 'NAME'\\), "
operator|+
literal|"whereas alias list has 3 columns.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept as d(a, b)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL A, VARCHAR(10) NOT NULL B) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values ('a', 1), ('bc', 2)) t (a, b)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(CHAR(2) NOT NULL A, INTEGER NOT NULL B) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMeasureRef
parameter_list|()
block|{
comment|// A measure can be used in the SELECT clause of a GROUP BY query even
comment|// though it is not a GROUP BY key.
name|SqlValidatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|CALCITE
argument_list|)
argument_list|)
decl_stmt|;
name|SqlValidatorFixture
name|f2
init|=
name|f
operator|.
name|withValidatorConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withNakedMeasures
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|measureIllegal
init|=
literal|"Measure expressions can only occur within AGGREGATE function"
decl_stmt|;
specifier|final
name|String
name|measureIllegal2
init|=
literal|"Measure expressions can only occur within a GROUP BY query"
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select deptno, ^count_plus_100^\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Same SQL is invalid if naked measures are not enabled
name|f2
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|measureIllegal
argument_list|)
expr_stmt|;
comment|// Similarly, with alias
specifier|final
name|String
name|sql1b
init|=
literal|"select deptno, ^count_plus_100^ as x\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql1b
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|f2
operator|.
name|withSql
argument_list|(
name|sql1b
argument_list|)
operator|.
name|fails
argument_list|(
name|measureIllegal
argument_list|)
expr_stmt|;
comment|// Similarly, in an expression
specifier|final
name|String
name|sql1c
init|=
literal|"select deptno, deptno + ^count_plus_100^ * 2 as x\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql1c
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|f2
operator|.
name|withSql
argument_list|(
name|sql1c
argument_list|)
operator|.
name|fails
argument_list|(
name|measureIllegal
argument_list|)
expr_stmt|;
comment|// Similarly, for a query that is an aggregate query because of another
comment|// aggregate function.
specifier|final
name|String
name|sql1
init|=
literal|"select count(*), ^count_plus_100^\n"
operator|+
literal|"from empm"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|f2
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|measureIllegal
argument_list|)
expr_stmt|;
comment|// A measure in a non-aggregate query.
comment|// Using a measure should not make it an aggregate query.
comment|// The type of the measure should be the result type of the COUNT aggregate
comment|// function (BIGINT), not type of the un-aggregated argument type (VARCHAR).
specifier|final
name|String
name|sql2
init|=
literal|"select deptno, ^count_plus_100^, ename\n"
operator|+
literal|"from empm"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"MEASURE<INTEGER NOT NULL> NOT NULL COUNT_PLUS_100, "
operator|+
literal|"VARCHAR(20) NOT NULL ENAME) NOT NULL"
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|f2
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|measureIllegal2
argument_list|)
expr_stmt|;
comment|// as above, wrapping the measure in AGGREGATE
specifier|final
name|String
name|sql3
init|=
literal|"select deptno, aggregate(count_plus_100) as x, ename\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno, ename"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"MEASURE<INTEGER NOT NULL> NOT NULL X, "
operator|+
literal|"VARCHAR(20) NOT NULL ENAME) NOT NULL"
argument_list|)
expr_stmt|;
comment|// you can apply the AGGREGATE function only to measures
name|f
operator|.
name|withSql
argument_list|(
literal|"select deptno, aggregate(count_plus_100), ^aggregate(ename)^\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno, ename"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'AGGREGATE' must be a measure"
argument_list|)
expr_stmt|;
name|f
operator|.
name|withSql
argument_list|(
literal|"select deptno, ^aggregate(count_plus_100 + 1)^\n"
operator|+
literal|"from empm\n"
operator|+
literal|"group by deptno, ename"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'AGGREGATE' must be a measure"
argument_list|)
expr_stmt|;
comment|// A query with AGGREGATE is an aggregate query, even without GROUP BY,
comment|// and even if it is inside an expression.
name|f
operator|.
name|withSql
argument_list|(
literal|"select aggregate(count_plus_100) + 1\n"
operator|+
literal|"from empm"
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Including a measure in a query does not make it an aggregate query
name|f
operator|.
name|withSql
argument_list|(
literal|"select count_plus_100\n"
operator|+
literal|"from empm"
argument_list|)
operator|.
name|isAggregate
argument_list|(
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAmbiguousColumnInIn
parameter_list|()
block|{
comment|// ok: cyclic reference
name|sql
argument_list|(
literal|"select * from emp as e\n"
operator|+
literal|"where e.deptno in (\n"
operator|+
literal|"  select 1 from (values(true)) where e.empno> 10)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ok: cyclic reference
name|sql
argument_list|(
literal|"select * from emp as e\n"
operator|+
literal|"where e.deptno in (\n"
operator|+
literal|"  select e.deptno from (values(true)))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where empno in (10,20)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// "select * from emp where empno in ()" is invalid -- see parser test
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where empno in (10 + deptno, cast(null as integer))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where empno in (10, '20')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where empno in ^(10, '20')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (2, 3, 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as integer) in (2, 3, 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (2, cast(null as integer) , 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (2.5, 3.14)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true in (false, unknown)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true in (false, false or unknown)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true in (false, true)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(1,2) in ((1,2), (3,4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'medium' in (cast(null as varchar(10)), 'bc')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// nullability depends on nullability of both sides
name|sql
argument_list|(
literal|"select empno in (1, 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select nullif(empno,empno) in (1, 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno in (1, nullif(empno,empno), 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 in (2, 'c')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1 in ^(2, 'c')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 in ^((2), (3,4))^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"false and ^1 in ('b', 'c')^"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"false and ^1 in (date '2012-01-02', date '2012-01-04')^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_OPERANDS_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1> 5 or ^(1, 2) in (3, 4)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_OPERANDS_INCOMPATIBLE
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInSubQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno in (select deptno from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where (empno,deptno)"
operator|+
literal|" in (select deptno,deptno from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// NOTE: jhyde: The closing caret should be one character to the right
comment|// ("dept)^"), but it's difficult to achieve, because parentheses are
comment|// discarded during the parsing process.
name|sql
argument_list|(
literal|"select * from emp where ^deptno in "
operator|+
literal|"(select deptno,deptno from dept^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Values passed to IN operator must have compatible types"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAnyList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where empno = any (10,20)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where empno< any (10 + deptno, cast(null as integer))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where empno< any (10, '20')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where empno< any ^(10, '20')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1< all (2, 3, 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as integer)< all (2, 3, 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1> some (2, cast(null as integer) , 4)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1> any (2.5, 3.14)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true = any (false, unknown)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true = any (false, false or unknown)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true<> any (false, true)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(1,2) = any ((1,2), (3,4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(1,2)< any ((1,2), (3,4))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'abc'< any (cast(null as varchar(10)), 'bc')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// nullability depends on nullability of both sides
name|sql
argument_list|(
literal|"select empno< any (1, 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select nullif(empno,empno)> all (1, 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno in (1, nullif(empno,empno), 2) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1 = any (2, 'c')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"1 = any ^(2, 'c')^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1> all ^((2), (3,4))^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_VALUES_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"false and 1 = any ('b', 'c')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"false and ^1 = any (date '2012-01-02', date '2012-01-04')^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_OPERANDS_INCOMPATIBLE
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"1> 5 or ^(1, 2)< any (3, 4)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_IN_OPERANDS_INCOMPATIBLE
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDoubleNoAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp join dept on true"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp, dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp cross join dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDuplicateColumnAliasIsOK
parameter_list|()
block|{
comment|// duplicate column aliases are daft, but SQL:2003 allows them
name|sql
argument_list|(
literal|"select 1 as a, 2 as b, 3 as a from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDuplicateTableAliasFails
parameter_list|()
block|{
comment|// implicit alias clashes with implicit alias
name|sql
argument_list|(
literal|"select 1 from emp, ^emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// implicit alias clashes with implicit alias, using join syntax
name|sql
argument_list|(
literal|"select 1 from emp join ^emp^ on emp.empno = emp.mgrno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// explicit alias clashes with implicit alias
name|sql
argument_list|(
literal|"select 1 from emp join ^dept as emp^ on emp.empno = emp.deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// implicit alias does not clash with overridden alias
name|sql
argument_list|(
literal|"select 1 from emp as e join emp on emp.empno = e.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// explicit alias does not clash with overridden alias
name|sql
argument_list|(
literal|"select 1 from emp as e join dept as emp on e.empno = emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// more than 2 in from clause
name|sql
argument_list|(
literal|"select 1 from emp, dept, emp as e, ^dept as emp^, emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// alias applied to sub-query
name|sql
argument_list|(
literal|"select 1 from emp, (^select 1 as x from (values (true))) as emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from emp, (^values (true,false)) as emp (b, c)^, dept as emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// alias applied to table function. doesn't matter that table fn
comment|// doesn't exist - should find the alias problem first
name|sql
argument_list|(
literal|"select 1 from emp, ^table(foo()) as emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// explicit table
name|sql
argument_list|(
literal|"select 1 from emp, ^(table foo.bar.emp) as emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// alias does not clash with alias inherited from enclosing context
name|sql
argument_list|(
literal|"select 1 from emp, dept where exists (\n"
operator|+
literal|"  select 1 from emp where emp.empno = emp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSchemaTableStar
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^sales.e^.* from sales.emp as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'SALES\\.E'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.dept.* from sales.dept"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.* from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.* from emp as emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// MySQL gives: "Unknown table 'emp'"
comment|// (consistent with MySQL)
name|sql
argument_list|(
literal|"select ^sales.emp^.* from emp as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'SALES.EMP'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSchemaTableColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select emp.empno from sales.emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.empno from sales.emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.empno from sales.emp\n"
operator|+
literal|"where sales.emp.deptno> 0"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from sales.emp where sales.emp.^bad^< 0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'BAD' not found in table 'SALES.EMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^sales.bad^.empno from sales.emp\n"
operator|+
literal|"where sales.emp.deptno> 0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'SALES\\.BAD' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.deptno from sales.emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from sales.emp where sales.emp.deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from sales.emp order by sales.emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// alias does not hide the fully-qualified name if same
comment|// (consistent with MySQL)
name|sql
argument_list|(
literal|"select sales.emp.deptno from sales.emp as emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// alias hides the fully-qualified name
comment|// (consistent with MySQL)
name|sql
argument_list|(
literal|"select ^sales.emp^.deptno from sales.emp as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'SALES\\.EMP' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sales.emp.deptno from sales.emp, ^sales.emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'EMP' in FROM clause"
argument_list|)
expr_stmt|;
comment|// Table exists but not used in FROM clause
name|sql
argument_list|(
literal|"select ^sales.emp^.deptno from sales.dept as d1, sales.dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'SALES.EMP' not found"
argument_list|)
expr_stmt|;
comment|// Table does not exist
name|sql
argument_list|(
literal|"select ^sales.bad^.deptno from sales.dept as d1, sales.dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'SALES.BAD' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-881">[CALCITE-881]    * Allow schema.table.column references in GROUP BY</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testSchemaTableColumnInGroupBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1 from sales.emp group by sales.emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from sales.emp group by sales.emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno + 1 from sales.emp group by sales.emp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInvalidGroupBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^empno^, deptno from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInvalidGroupBy2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) from emp group by ^deptno + 'a'^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply '\\+' to arguments of type.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from emp group by deptno + 'a'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInvalidGroupBy3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno / 2 + 1, count(*) as c\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(deptno / 2, sal), rollup(empno, ^deptno + 'a'^)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply '\\+' to arguments of type.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno / 2 + 1, count(*) as c\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(deptno / 2, sal), rollup(empno, ^deptno + 'a'^)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for<a href="https://issues.apache.org/jira/browse/CALCITE-3003">[CALCITE-3003]    * AssertionError when GROUP BY nested field</a>.    *    *<p>Make sure table name of GROUP BY item with nested field could be    * properly validated.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testInvalidGroupByWithInvalidTableName
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|"  coord.x,\n"
operator|+
literal|"  avg(coord.y)\n"
operator|+
literal|"from\n"
operator|+
literal|"  customer.contact_peek\n"
operator|+
literal|"group by\n"
operator|+
literal|"  ^unknown_table_alias.coord^.x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'UNKNOWN_TABLE_ALIAS.COORD' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1781">[CALCITE-1781]    * Allow expression in CUBE and ROLLUP</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCubeExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select deptno + 1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by cube(deptno + 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select deptno + 2 - 2\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by cube(deptno + 2, empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select ^deptno^\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by cube(deptno + 1)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"select ^deptno^ + 10\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(empno, deptno + 10 - 10)"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql5
init|=
literal|"select deptno + 10\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(deptno + 10 - 10, deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Unit test for    * {@link org.apache.calcite.sql.validate.SqlValidatorUtil#rollup}. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testRollupBitSets
parameter_list|()
block|{
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3}, {1}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 3}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// non-disjoint bit sets
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// some bit sets are empty
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// one empty bit set
name|assertThat
argument_list|(
name|rollup
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// no bit sets
name|assertThat
argument_list|(
name|rollup
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|rollup
parameter_list|(
name|ImmutableBitSet
modifier|...
name|sets
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|rollup
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|sets
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/** Unit test for    * {@link org.apache.calcite.sql.validate.SqlValidatorUtil#cube}. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCubeBitSets
parameter_list|()
block|{
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3}, {1}, {3}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1}, {3, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 3}, {4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {3}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// non-disjoint bit sets
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {3, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// some bit sets are empty, and there are duplicates
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1, 3, 4}, {1, 4}, {3, 4}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{1}, {}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{}]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cube
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"[{}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|cube
parameter_list|(
name|ImmutableBitSet
modifier|...
name|sets
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|cube
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|sets
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGrouping
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, grouping(deptno) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(deptno, deptno) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno / 2, grouping(deptno / 2),\n"
operator|+
literal|" ^grouping(deptno / 2, empno)^\n"
operator|+
literal|"from emp group by deptno / 2, empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(^empno^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(deptno, ^empno^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(^empno^, deptno) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(^deptno + 1^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(emp.^xxx^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'XXX' not found in table 'EMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, ^grouping(deptno)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, sum(^grouping(deptno)^) over () from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno having grouping(deptno)< 5"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno order by grouping(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as xx from emp group by deptno order by grouping(xx)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as empno from emp\n"
operator|+
literal|"group by deptno order by grouping(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 as deptno from emp\n"
operator|+
literal|"group by deptno order by grouping(^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno order by grouping(emp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^deptno^ from emp group by empno order by grouping(deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp order by ^grouping(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp where ^grouping(deptno)^ = 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp where ^grouping(deptno)^ = 1 group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno, ^grouping(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets(deptno, ^grouping(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by cube(empno, ^grouping(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by rollup(empno, ^grouping(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupingId
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, grouping_id(deptno) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping_id(deptno, deptno) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno / 2, grouping_id(deptno / 2),\n"
operator|+
literal|" ^grouping_id(deptno / 2, empno)^\n"
operator|+
literal|"from emp group by deptno / 2, empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno / 2, ^grouping_id()^\n"
operator|+
literal|"from emp group by deptno / 2, empno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'GROUPING_ID'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping_id(^empno^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING_ID operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping_id(^deptno + 1^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING_ID operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping_id(emp.^xxx^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'XXX' not found in table 'EMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, ^grouping_id(deptno)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, sum(^grouping_id(deptno)^) over () from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno having grouping_id(deptno)< 5"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno order by grouping_id(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as xx from emp group by deptno order by grouping_id(xx)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as empno from emp\n"
operator|+
literal|"group by deptno order by grouping_id(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 as deptno from emp\n"
operator|+
literal|"group by deptno order by grouping_id(^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING_ID operator must be a grouped expression"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno\n"
operator|+
literal|"order by grouping_id(emp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^deptno^ from emp group by empno order by grouping_id(deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp order by ^grouping_id(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp where ^grouping_id(deptno)^ = 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp where ^grouping_id(deptno)^ = 1\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno, ^grouping_id(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets(deptno, ^grouping_id(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by cube(empno, ^grouping_id(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by rollup(empno, ^grouping_id(deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupId
parameter_list|()
block|{
specifier|final
name|String
name|groupIdOnlyInAggregate
init|=
literal|"GROUP_ID operator may only occur in an aggregate query"
decl_stmt|;
specifier|final
name|String
name|groupIdWrongClause
init|=
literal|"GROUP_ID operator may only occur in SELECT, HAVING or ORDER BY clause"
decl_stmt|;
name|sql
argument_list|(
literal|"select deptno, group_id() from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, ^group_id^ as x from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'GROUP_ID' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, ^group_id(deptno)^ from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'GROUP_ID'\\. "
operator|+
literal|"Was expecting 0 arguments"
argument_list|)
expr_stmt|;
comment|// Oracle throws "GROUPING function only supported with GROUP BY CUBE or
comment|// ROLLUP"
name|sql
argument_list|(
literal|"select ^group_id()^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdOnlyInAggregate
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp order by ^group_id(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdOnlyInAggregate
argument_list|)
expr_stmt|;
comment|// Oracle throws "GROUPING function only supported with GROUP BY CUBE or
comment|// ROLLUP"
name|sql
argument_list|(
literal|"select 1 from emp order by ^group_id()^"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdOnlyInAggregate
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from emp order by ^grouping(deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"GROUPING operator may only occur in an aggregate query"
argument_list|)
expr_stmt|;
comment|// Oracle throws "group function is not allowed here"
name|sql
argument_list|(
literal|"select deptno from emp where ^group_id()^ = 1"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdOnlyInAggregate
argument_list|)
expr_stmt|;
comment|// Oracle throws "group function is not allowed here"
name|sql
argument_list|(
literal|"select deptno from emp group by ^group_id()^"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp where ^group_id()^ = 1 group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno, ^group_id()^"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by grouping sets(deptno, ^group_id()^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by cube(empno, ^group_id()^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by rollup(empno, ^group_id()^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select grouping(^group_id()^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdOnlyInAggregate
argument_list|)
expr_stmt|;
comment|// Oracle throws "not a GROUP BY expression"
name|sql
argument_list|(
literal|"select grouping(^group_id()^) from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
name|groupIdWrongClause
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^grouping(sum(empno))^ from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Aggregate expressions cannot be nested"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCubeGrouping
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, grouping(deptno) from emp group by cube(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, grouping(^deptno + 1^) from emp\n"
operator|+
literal|"group by cube(deptno, empno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to GROUPING operator must be a grouped expression"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSumInvalidArgs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^sum(ename)^, deptno from emp group by deptno"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'SUM' to arguments of type 'SUM\\(<VARCHAR\\(20\\)>\\)'\\. .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(ename), deptno from emp group by deptno"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(DECIMAL(19, 9) NOT NULL EXPR$0, INTEGER NOT NULL DEPTNO) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSumTooManyArgs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^sum(empno, deptno)^, deptno from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'SUM'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSumTooFewArgs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^sum()^, deptno from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'SUM'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSingleNoAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testObscuredAliasFails
parameter_list|()
block|{
comment|// It is an error to refer to a table which has been given another
comment|// alias.
name|sql
argument_list|(
literal|"select * from emp as e where exists (\n"
operator|+
literal|"  select 1 from dept where dept.deptno = ^emp^.deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMP' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFromReferenceFails
parameter_list|()
block|{
comment|// You cannot refer to a table ('e2') in the parent scope of a query in
comment|// the from clause.
name|sql
argument_list|(
literal|"select * from emp as e1 where exists (\n"
operator|+
literal|"  select * from emp as e2,\n"
operator|+
literal|"    (select * from dept where dept.deptno = ^e2^.deptno))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E2' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWhereReference
parameter_list|()
block|{
comment|// You can refer to a table ('e1') in the parent scope of a query in
comment|// the from clause.
comment|//
comment|// Note: Oracle10g does not allow this query.
name|sql
argument_list|(
literal|"select * from emp as e1 where exists (\n"
operator|+
literal|"  select * from emp as e2,\n"
operator|+
literal|"    (select * from dept where dept.deptno = e1.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionNameResolution
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp as e1 where exists (\n"
operator|+
literal|"  select * from emp as e2,\n"
operator|+
literal|"  (select deptno from dept as d\n"
operator|+
literal|"   union\n"
operator|+
literal|"   select deptno from emp as e3 where deptno = ^e2^.deptno))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E2' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from dept where ^empno^< 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionCountMismatchFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1,2 from emp\n"
operator|+
literal|"union\n"
operator|+
literal|"select ^3^ from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column count mismatch in UNION"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionCountMismatcWithValuesFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from ( values (1))\n"
operator|+
literal|"union\n"
operator|+
literal|"select ^*^ from ( values (1,2))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column count mismatch in UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ( values (1))\n"
operator|+
literal|"union\n"
operator|+
literal|"select ^*^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column count mismatch in UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"union\n"
operator|+
literal|"select ^*^ from ( values (1))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column count mismatch in UNION"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionTypeMismatchFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select 1, ^2^ from emp union select deptno, name from dept"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 2 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1, 2 from emp union select deptno, ^name^ from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^slacker^ from emp union select name from dept"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 1 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^slacker^ from emp union select name from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionTypeMismatchWithStarFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^*^ from dept union select 1, 2 from emp"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 2 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept union select 1, 2 from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^dept.*^ from dept union select 1, 2 from emp"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 2 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select dept.* from dept union select 1, 2 from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnionTypeMismatchWithValuesFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values (1, ^2^, 3), (3, 4, 5), (6, 7, 8) union\n"
operator|+
literal|"select deptno, name, deptno from dept"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 2 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values (1, 2, 3), (3, 4, 5), (6, 7, 8) union\n"
operator|+
literal|"select deptno, ^name^, deptno from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from (values (^'x'^)) union\n"
operator|+
literal|"select 'a' from (values ('y'))"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 1 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from (values ('x')) union\n"
operator|+
literal|"select 'a' from (values ('y'))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from (values (^'x'^)) union\n"
operator|+
literal|"(values ('a'))"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 1 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from (values ('x')) union\n"
operator|+
literal|"(values ('a'))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1, ^2^, 3 union\n "
operator|+
literal|"select deptno, name, deptno from dept"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Type mismatch in column 2 of UNION"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 1, 2, 3 union\n "
operator|+
literal|"select deptno, name, deptno from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testValuesTypeMismatchFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"^values (1), ('a')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Values passed to VALUES operator must have compatible types"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalCrossJoinFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp natural cross ^join^ dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify condition \\(NATURAL keyword, or ON or USING "
operator|+
literal|"clause\\) following CROSS JOIN"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCrossJoinUsingFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp cross join dept ^using^ (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify condition \\(NATURAL keyword, or ON or USING "
operator|+
literal|"clause\\) following CROSS JOIN"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinUsing
parameter_list|()
block|{
specifier|final
name|String
name|empDeptType
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from emp join dept using (deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
name|empDeptType
argument_list|)
expr_stmt|;
comment|// NATURAL has same effect as USING
name|sql
argument_list|(
literal|"select * from emp natural join dept"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
name|empDeptType
argument_list|)
expr_stmt|;
comment|// fail: comm exists on one side not the other
comment|// todo: The error message could be improved.
name|sql
argument_list|(
literal|"select * from emp join dept using (deptno, ^comm^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'COMM' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp join dept using (^empno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept join emp using (^empno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in any table"
argument_list|)
expr_stmt|;
comment|// not on either side
name|sql
argument_list|(
literal|"select * from dept join emp using (^abc^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ABC' not found in any table"
argument_list|)
expr_stmt|;
comment|// column exists, but wrong case
name|sql
argument_list|(
literal|"select * from dept join emp using (^\"deptno\"^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'deptno' not found in any table"
argument_list|)
expr_stmt|;
comment|// ok to repeat (ok in Oracle10g too)
specifier|final
name|String
name|sql
init|=
literal|"select * from emp join dept using (deptno, deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
name|empDeptType
argument_list|)
expr_stmt|;
comment|// inherited column, not found in either side of the join, in the
comment|// USING clause
name|sql
argument_list|(
literal|"select * from dept where exists (\n"
operator|+
literal|"select 1 from emp join bonus using (^dname^))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DNAME' not found in any table"
argument_list|)
expr_stmt|;
comment|// inherited column, found in only one side of the join, in the
comment|// USING clause
name|sql
argument_list|(
literal|"select * from dept where exists (\n"
operator|+
literal|"select 1 from emp join bonus using (^deptno^))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' not found in any table"
argument_list|)
expr_stmt|;
comment|// cannot qualify common column in Oracle.
name|sql
argument_list|(
literal|"select ^emp.deptno^ from emp join dept using (deptno)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^emp.deptno^ from emp natural join dept"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(^emp.deptno^) from emp join dept using (deptno)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot qualify common column 'EMP.DEPTNO'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.deptno from emp join dept using (deptno)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|MYSQL_5
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno from emp natural join dept"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno from emp join dept using (deptno)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_12
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCrossJoinOnFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp cross join dept\n"
operator|+
literal|" ^on^ emp.deptno = dept.deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify condition \\(NATURAL keyword, or ON or "
operator|+
literal|"USING clause\\) following CROSS JOIN"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInnerJoinWithoutUsingOrOnFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp inner ^join^ dept\n"
operator|+
literal|"where emp.deptno = dept.deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"INNER, LEFT, RIGHT or FULL join requires a condition "
operator|+
literal|"\\(NATURAL keyword or ON or USING clause\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalJoinWithOnFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp natural join dept on ^emp.deptno = dept.deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify NATURAL keyword with ON or USING clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalJoinWithUsing
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp natural join dept ^using (deptno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot specify NATURAL keyword with ON or USING clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalJoinCaseSensitive
parameter_list|()
block|{
comment|// With case-insensitive match, more columns are recognized as join columns
comment|// and therefore "*" expands to fewer columns.
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select empno, deptno from emp)\n"
operator|+
literal|"natural join (select deptno as \"deptno\", name from dept)"
decl_stmt|;
specifier|final
name|String
name|type0
init|=
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL deptno,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
decl_stmt|;
specifier|final
name|String
name|type1
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
name|type0
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|type
argument_list|(
name|type1
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalJoinIncompatibleDatatype
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from (select ename as name, hiredate as deptno from emp)\n"
operator|+
literal|"^natural^ join\n"
operator|+
literal|"(select deptno, name as sal from dept)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' matched using NATURAL keyword or USING clause "
operator|+
literal|"has incompatible types: "
operator|+
literal|"cannot compare 'TIMESTAMP\\(0\\)' to 'INTEGER'"
argument_list|)
expr_stmt|;
comment|// INTEGER and VARCHAR are comparable: VARCHAR implicit converts to INTEGER
name|sql
argument_list|(
literal|"select * from emp natural ^join^\n"
operator|+
literal|"(select deptno, name as sal from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// make sal occur more than once on rhs, it is ignored and therefore
comment|// there is no error about incompatible types
name|sql
argument_list|(
literal|"select * from emp natural join\n"
operator|+
literal|" (select deptno, name as sal, 'foo' as sal2 from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinUsingIncompatibleDatatype
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from (select ename as name, hiredate as deptno from emp)\n"
operator|+
literal|"join (select deptno, name as sal from dept) using (^deptno^, sal)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' matched using NATURAL keyword or USING clause "
operator|+
literal|"has incompatible types: "
operator|+
literal|"cannot compare 'TIMESTAMP\\(0\\)' to 'INTEGER'"
argument_list|)
expr_stmt|;
comment|// INTEGER and VARCHAR are comparable: VARCHAR implicit converts to INTEGER
specifier|final
name|String
name|sql
init|=
literal|"select * from emp\n"
operator|+
literal|"join (select deptno, name as sal from dept) using (deptno, sal)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinUsingInvalidColsFails
parameter_list|()
block|{
comment|// todo: Improve error msg
name|sql
argument_list|(
literal|"select * from emp left join dept using (^gender^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'GENDER' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5171">[CALCITE-5171]    * NATURAL join and USING should fail if join columns are not unique</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testJoinDuplicateColumns
parameter_list|()
block|{
comment|// NATURAL join and USING should fail if join columns are not unique
specifier|final
name|String
name|message
init|=
literal|"Column name 'DEPTNO' in NATURAL join or "
operator|+
literal|"USING clause is not unique on one side of join"
decl_stmt|;
name|sql
argument_list|(
literal|"select e.ename, d.name\n"
operator|+
literal|"from dept as d\n"
operator|+
literal|"^natural^ join (select ename, sal as deptno, deptno from emp) as e"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// A similar query with USING fails with the same error
name|sql
argument_list|(
literal|"select e.ename, d.name\n"
operator|+
literal|"from dept as d\n"
operator|+
literal|"join (select ename, sal as deptno, deptno from emp) as e\n"
operator|+
literal|"  using (^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Reversed query gives reversed error message
name|sql
argument_list|(
literal|"select e.ename, d.name\n"
operator|+
literal|"from (select ename, sal as deptno, deptno from emp) as e\n"
operator|+
literal|"join dept as d\n"
operator|+
literal|"  using (^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// Also with "*". (Proves that FROM is validated before SELECT.)
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from emp\n"
operator|+
literal|"left join (select deptno, name as deptno from dept)\n"
operator|+
literal|"  using (^deptno^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
annotation|@
name|DisplayName
argument_list|(
literal|"Natural join require input column uniqueness"
argument_list|)
name|void
name|testNaturalJoinRequireInputColumnUniqueness
parameter_list|()
block|{
specifier|final
name|String
name|message
init|=
literal|"Column name 'DEPTNO' in NATURAL join or "
operator|+
literal|"USING clause is not unique on one side of join"
decl_stmt|;
comment|// Invalid. NATURAL JOIN eliminates duplicate columns from its output but
comment|// requires input columns to be unique.
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from (emp as e cross join dept as d)\n"
operator|+
literal|"^natural^ join\n"
operator|+
literal|"(emp as e2 cross join dept as d2)"
argument_list|)
operator|.
name|fails
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
annotation|@
name|DisplayName
argument_list|(
literal|"Should produce two DEPTNO columns"
argument_list|)
name|void
name|testReturnsCorrectRowTypeOnCombinedJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"natural join dept as d\n"
operator|+
literal|"join (select deptno as x, deptno from dept) as d2"
operator|+
literal|"  on d2.deptno = e.deptno"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType("
operator|+
literal|"INTEGER NOT NULL DEPTNO, "
operator|+
literal|"INTEGER NOT NULL EMPNO, "
operator|+
literal|"VARCHAR(20) NOT NULL ENAME, "
operator|+
literal|"VARCHAR(10) NOT NULL JOB, "
operator|+
literal|"INTEGER MGR, "
operator|+
literal|"TIMESTAMP(0) NOT NULL HIREDATE, "
operator|+
literal|"INTEGER NOT NULL SAL, "
operator|+
literal|"INTEGER NOT NULL COMM, "
operator|+
literal|"BOOLEAN NOT NULL SLACKER, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME, "
operator|+
literal|"INTEGER NOT NULL X, "
operator|+
literal|"INTEGER NOT NULL DEPTNO1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5171">[CALCITE-5171]    * NATURAL join and USING should fail if join columns are not unique</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCorrectJoinDuplicateColumns
parameter_list|()
block|{
comment|// The error only occurs if the duplicate column is referenced. The
comment|// following query has a duplicate hiredate column.
name|sql
argument_list|(
literal|"select e.ename, d.name\n"
operator|+
literal|"from dept as d\n"
operator|+
literal|"join (select ename, sal as hiredate, deptno from emp) as e\n"
operator|+
literal|"  using (deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Previous join chain does not affect validation.
name|sql
argument_list|(
literal|"select * from EMP natural join EMPNULLABLES natural join DEPT"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNaturalEmptyKey
parameter_list|()
block|{
comment|// If there are no columns in common, natural join is empty, and that's OK.
name|sql
argument_list|(
literal|"select *\n"
operator|+
literal|"from (select ename from emp) as e\n"
operator|+
literal|"natural join dept as d"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType("
operator|+
literal|"VARCHAR(20) NOT NULL ENAME, "
operator|+
literal|"INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME) NOT NULL"
argument_list|)
expr_stmt|;
comment|// If there are duplicates on one side, that's OK, because the empty natural
comment|// join prevents us from checking.
name|sql
argument_list|(
literal|"select d.*\n"
operator|+
literal|"from (select ename, sal as ename from emp) as e\n"
operator|+
literal|"natural join dept as d"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType("
operator|+
literal|"INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Cannot expand star if it contains duplicate columns.
comment|// (Postgres thinks this query is OK.)
name|sql
argument_list|(
literal|"select ^e.*^\n"
operator|+
literal|"from (select ename, sal as ename from emp) as e\n"
operator|+
literal|"natural join dept as d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' is ambiguous"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinRowType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp left join dept on emp.deptno = dept.deptno"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER,"
operator|+
literal|" INTEGER DEPTNO0,"
operator|+
literal|" VARCHAR(10) NAME) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp right join dept on emp.deptno = dept.deptno"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER EMPNO,"
operator|+
literal|" VARCHAR(20) ENAME,"
operator|+
literal|" VARCHAR(10) JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) HIREDATE,"
operator|+
literal|" INTEGER SAL,"
operator|+
literal|" INTEGER COMM,"
operator|+
literal|" INTEGER DEPTNO,"
operator|+
literal|" BOOLEAN SLACKER,"
operator|+
literal|" INTEGER NOT NULL DEPTNO0,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp full join dept on emp.deptno = dept.deptno"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER EMPNO,"
operator|+
literal|" VARCHAR(20) ENAME,"
operator|+
literal|" VARCHAR(10) JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) HIREDATE,"
operator|+
literal|" INTEGER SAL,"
operator|+
literal|" INTEGER COMM,"
operator|+
literal|" INTEGER DEPTNO,"
operator|+
literal|" BOOLEAN SLACKER,"
operator|+
literal|" INTEGER DEPTNO0,"
operator|+
literal|" VARCHAR(10) NAME) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinUsingWithParentheses
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (emp join bonus using (job))\n"
operator|+
literal|"join dept using (deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Cannot alias a JOIN (until
comment|// [CALCITE-5168] Allow AS after parenthesized JOIN
comment|// is fixed).
name|sql
argument_list|(
literal|"select * from (emp ^join^ bonus using (job)) as x\n"
operator|+
literal|"join dept using (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Join expression encountered in illegal context"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (emp join bonus using (job))\n"
operator|+
literal|"join dept using (^dname^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DNAME' not found in any table"
argument_list|)
expr_stmt|;
comment|// Needs real Error Message and error marks in query
name|sql
argument_list|(
literal|"select * from (emp join bonus using (job))\n"
operator|+
literal|"join (select 1 as job from ^(^true)) using (job)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\( true\" at .*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"bug: should fail if sub-query does not have alias"
argument_list|)
annotation|@
name|Test
name|void
name|testJoinSubQuery
parameter_list|()
block|{
comment|// Sub-queries require alias
name|sql
argument_list|(
literal|"select * from (select 1 as uno from emp)\n"
operator|+
literal|"join (values (1), (2)) on true"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"require alias"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnIn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp join dept\n"
operator|+
literal|"on dept.deptno in (select deptno from emp)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnInCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e join dept\n"
operator|+
literal|"on dept.deptno in (select deptno from emp where deptno< e.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnInCorrelatedFails
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e join dept as d\n"
operator|+
literal|"on d.deptno in (select deptno from emp where deptno< d.^empno^)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in table 'D'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnExistsCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e join dept\n"
operator|+
literal|"on exists (select 1, 2 from emp where deptno< e.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnScalarCorrelated
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e join dept d\n"
operator|+
literal|"on d.deptno = (select 1 from emp where deptno< e.deptno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinOnScalarFails
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from emp as e join dept d\n"
operator|+
literal|"on d.deptno = (^select 1, 2 from emp where deptno< e.deptno^)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"(?s)Cannot apply '\\$SCALAR_QUERY' to arguments "
operator|+
literal|"of type '\\$SCALAR_QUERY\\(<RECORDTYPE\\(INTEGER EXPR\\$0, INTEGER "
operator|+
literal|"EXPR\\$1\\)>\\)'\\. Supported form\\(s\\).*"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJoinUsingThreeWay
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"join emp as e2 using (empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"join dept as d2 using (^deptno^)"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)"
decl_stmt|;
specifier|final
name|String
name|type2
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|type
argument_list|(
name|type2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"select *\n"
operator|+
literal|"from emp as e\n"
operator|+
literal|"join dept as d using (deptno)\n"
operator|+
literal|"join (values (10, 1000)) as b (empno, bonus) using (empno)"
decl_stmt|;
specifier|final
name|String
name|type3
init|=
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER,"
operator|+
literal|" VARCHAR(10) NOT NULL NAME,"
operator|+
literal|" INTEGER NOT NULL BONUS) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|type
argument_list|(
name|type3
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where ^sal^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"WHERE clause must be a condition"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp e1 left outer join emp e2 on ^e1.sal^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"ON clause must be a condition"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testHaving
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp having ^sum(sal)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"HAVING clause must be a condition"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^*^ from emp having sum(sal)> 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// agg in select and having, no group by
name|sql
argument_list|(
literal|"select sum(sal + sal) from emp having sum(sal)> 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT deptno FROM emp GROUP BY deptno HAVING ^sal^> 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testHavingBetween
parameter_list|()
block|{
comment|// FRG-115: having clause with between not working
name|sql
argument_list|(
literal|"select deptno from emp group by deptno\n"
operator|+
literal|"having deptno between 10 and 12"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// this worked even before FRG-115 was fixed
name|sql
argument_list|(
literal|"select deptno from emp group by deptno having deptno + 5> 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests the {@code QUALIFY} clause. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testQualifyPositive
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|qualifyWithoutAlias
init|=
literal|"SELECT\n"
operator|+
literal|"empno, ename\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY ROW_NUMBER() over (partition by ename order by deptno) = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithoutAlias
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|qualifyWithAlias
init|=
literal|"SELECT empno, ename, deptno,\n"
operator|+
literal|"   ROW_NUMBER() over (partition by ename order by deptno) as row_num\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY row_num = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithAlias
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|qualifyWithWindowClause
init|=
literal|"SELECT empno, ename,\n"
operator|+
literal|" SUM(deptno) OVER myWindow as sumDeptNo\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"WINDOW myWindow AS (PARTITION BY ename ORDER BY empno)\n"
operator|+
literal|"QUALIFY sumDeptNo = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithWindowClause
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|qualifyWithEverything
init|=
literal|"SELECT DISTINCT empno, ename, deptno,\n"
operator|+
literal|"    RANK() OVER (PARTITION BY ename ORDER BY deptno DESC) as rank_val\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"WHERE sal> 1000\n"
operator|+
literal|"QUALIFY rank_val = (SELECT COUNT(*) FROM emp)\n"
operator|+
literal|"ORDER BY deptno\n"
operator|+
literal|"LIMIT 5"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithEverything
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|qualifyReferencingCommonColumnInJoin
init|=
literal|"SELECT * \n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"NATURAL JOIN dept\n"
operator|+
literal|"QUALIFY ROW_NUMBER() over (partition by ename order by emp.deptno) = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyReferencingCommonColumnInJoin
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|qualifyOnMultipleWindowFunctions
init|=
literal|"SELECT"
operator|+
literal|"   AVG(deptno) OVER (PARTITION BY ename) avgDeptNo,"
operator|+
literal|"   RANK() OVER (PARTITION BY deptno ORDER BY ename) as myRank\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY avgDeptNo = 1 AND myRank = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyOnMultipleWindowFunctions
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Negative tests for the {@code QUALIFY} clause. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testQualifyNegative
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|f
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
comment|// The predicate in the QUALIFY clause expression must be a boolean, since
comment|// we use it as a filter.
specifier|final
name|String
name|qualifyWithNonBooleanExpression
init|=
literal|"SELECT\n"
operator|+
literal|"empno, ename\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY ^1 + 1^"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithNonBooleanExpression
argument_list|)
operator|.
name|fails
argument_list|(
literal|"QUALIFY clause must be a condition"
argument_list|)
expr_stmt|;
comment|// We don't allow for using ordinal column references in QUALIFY.
specifier|final
name|String
name|qualifyWithOrdinal
init|=
literal|"SELECT\n"
operator|+
literal|"empno, ename, ROW_NUMBER() over (partition by ename order by deptno) = 1\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY ^3^"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyWithOrdinal
argument_list|)
operator|.
name|fails
argument_list|(
literal|"QUALIFY clause must be a condition"
argument_list|)
expr_stmt|;
comment|// 'deptno' is a common column in both the emp and dept table.
comment|// We need to use emp.deptno or dept.deptno to make it unambiguous
specifier|final
name|String
name|qualifyReferencingAmbiguousCommonColumnInJoin
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"NATURAL JOIN dept\n"
operator|+
literal|"QUALIFY ROW_NUMBER() over (partition by ename order by ^deptno^) = 1"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyReferencingAmbiguousCommonColumnInJoin
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
comment|// Qualify must contain a window function. This matches the behavior where
comment|// HAVING needs to have an aggregate or reference a group column.
specifier|final
name|String
name|qualifyOnNonWindowFunction
init|=
literal|"SELECT * \n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY ^SUM(deptno) = 1^"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyOnNonWindowFunction
argument_list|)
operator|.
name|fails
argument_list|(
literal|"QUALIFY expression 'SUM\\(`EMP`\\.`DEPTNO`\\) = 1' "
operator|+
literal|"must contain a window function"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|qualifyOnAliasedNonWindowFunction
init|=
literal|""
operator|+
literal|"SELECT ^SUM(deptno) as sumDeptNo\n"
operator|+
literal|"FROM emp\n"
operator|+
literal|"QUALIFY sumDeptNo = 1^"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|qualifyOnAliasedNonWindowFunction
argument_list|)
operator|.
name|fails
argument_list|(
literal|"QUALIFY expression 'SUM\\(`EMP`\\.`DEPTNO`\\) = 1' "
operator|+
literal|"must contain a window function"
argument_list|)
expr_stmt|;
comment|// This query fails, since it's a mix of regular aggregates and window
comment|// functions. This query needs to fail, since we assume that qualify filters
comment|// on the result of a window function in SqlToRelConverter and that the
comment|// input Rel is a LogicalProject and not a LogicalAggregate.
specifier|final
name|String
name|mixedNonAggregateAndWindowAggregate
init|=
literal|"SELECT\n"
operator|+
literal|"   SUM(deptno) as sumDeptNo, "
operator|+
literal|"   RANK() OVER (PARTITION BY ^ename^ ORDER BY deptno) myRank\n"
operator|+
literal|"FROM emp\n"
decl_stmt|;
name|f
operator|.
name|withSql
argument_list|(
name|mixedNonAggregateAndWindowAggregate
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'ENAME' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests the {@code WITH} clause, also called common table expressions. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWith
parameter_list|()
block|{
comment|// simplest possible
name|sql
argument_list|(
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|type
argument_list|(
name|EMP_RECORD_TYPE
argument_list|)
expr_stmt|;
comment|// degree of emp2 column list does not match its query
name|sql
argument_list|(
literal|"with emp2 ^(x, y)^ as (select * from emp)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of columns must match number of query columns"
argument_list|)
expr_stmt|;
comment|// duplicate names in column list
name|sql
argument_list|(
literal|"with emp2 (x, y, ^y^, x) as (select sal, deptno, ename, empno from emp)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'Y' in column list"
argument_list|)
expr_stmt|;
comment|// column list required if aliases are not unique
name|sql
argument_list|(
literal|"with emp2 as (^select empno as e, sal, deptno as e from emp^)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column has duplicate column name 'E' and no column list specified"
argument_list|)
expr_stmt|;
comment|// forward reference
name|sql
argument_list|(
literal|"with emp3 as (select * from ^emp2^),\n"
operator|+
literal|" emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp3"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'EMP2' not found"
argument_list|)
expr_stmt|;
comment|// forward reference in with-item not used; should still fail
name|sql
argument_list|(
literal|"with emp3 as (select * from ^emp2^),\n"
operator|+
literal|" emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'EMP2' not found"
argument_list|)
expr_stmt|;
comment|// table not used is ok
name|sql
argument_list|(
literal|"with emp2 as (select * from emp),\n"
operator|+
literal|" emp3 as (select * from emp2)\n"
operator|+
literal|"select * from emp2"
argument_list|)
operator|.
name|type
argument_list|(
name|EMP_RECORD_TYPE
argument_list|)
expr_stmt|;
comment|// self-reference is not ok, even in table not used
name|sql
argument_list|(
literal|"with emp2 as (select * from emp),\n"
operator|+
literal|" emp3 as (select * from ^emp3^)\n"
operator|+
literal|"values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'EMP3' not found"
argument_list|)
expr_stmt|;
comment|// self-reference not ok
name|sql
argument_list|(
literal|"with emp2 as (select * from ^emp2^)\n"
operator|+
literal|"select * from emp2 where false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'EMP2' not found"
argument_list|)
expr_stmt|;
comment|// refer to 2 previous tables, not just immediately preceding
name|sql
argument_list|(
literal|"with emp2 as (select * from emp),\n"
operator|+
literal|" dept2 as (select * from dept),\n"
operator|+
literal|" empDept as (select emp2.empno, dept2.deptno from dept2 join emp2 using (deptno))\n"
operator|+
literal|"select 1 as uno from empDept"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL UNO) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests the {@code WITH} clause with UNION. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithUnion
parameter_list|()
block|{
comment|// nested WITH (parentheses required - and even with parentheses SQL
comment|// standard doesn't allow sub-query to have WITH)
name|sql
argument_list|(
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"select * from emp2 union all select * from emp"
argument_list|)
operator|.
name|type
argument_list|(
name|EMP_RECORD_TYPE
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests the {@code WITH} clause and column aliases. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithColumnAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"with w(x, y) as (select * from dept)\n"
operator|+
literal|"select * from w"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, VARCHAR(10) NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"with w(x, y) as (select * from dept)\n"
operator|+
literal|"select * from w, w as w2"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, VARCHAR(10) NOT NULL Y, "
operator|+
literal|"INTEGER NOT NULL X0, VARCHAR(10) NOT NULL Y0) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"with w(x, y) as (select * from dept)\n"
operator|+
literal|"select ^deptno^ from w"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"with w(x, ^x^) as (select * from dept)\n"
operator|+
literal|"select * from w"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'X' in column list"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests the {@code WITH} clause in sub-queries. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithSubQuery
parameter_list|()
block|{
comment|// nested WITH (parentheses required - and even with parentheses SQL
comment|// standard doesn't allow sub-query to have WITH)
name|sql
argument_list|(
literal|"with emp2 as (select * from emp)\n"
operator|+
literal|"(\n"
operator|+
literal|"  with dept2 as (select * from dept)\n"
operator|+
literal|"  (\n"
operator|+
literal|"    with empDept as (select emp2.empno, dept2.deptno from dept2 join emp2 using (deptno))\n"
operator|+
literal|"    select 1 as uno from empDept))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL UNO) NOT NULL"
argument_list|)
expr_stmt|;
comment|// WITH inside WHERE can see enclosing tables
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"where exists (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= emp.deptno)\n"
operator|+
literal|"  select 1 from dept2 where deptno<= emp.deptno)"
argument_list|)
operator|.
name|type
argument_list|(
name|EMP_RECORD_TYPE
argument_list|)
expr_stmt|;
comment|// WITH inside FROM cannot see enclosing tables
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"join (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= ^emp^.deptno)\n"
operator|+
literal|"  select * from dept2) as d on true"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMP' not found"
argument_list|)
expr_stmt|;
comment|// as above, using USING
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"join (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno>= ^emp^.deptno)\n"
operator|+
literal|"  select * from dept2) as d using (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMP' not found"
argument_list|)
expr_stmt|;
comment|// WITH inside FROM
name|sql
argument_list|(
literal|"select e.empno, d.* from emp as e\n"
operator|+
literal|"join (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno> 10)\n"
operator|+
literal|"  select deptno, 1 as uno from dept2) as d using (deptno)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL UNO) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^e^.empno, d.* from emp\n"
operator|+
literal|"join (\n"
operator|+
literal|"  with dept2 as (select * from dept where dept.deptno> 10)\n"
operator|+
literal|"  select deptno, 1 as uno from dept2) as d using (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithOrderAgg
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) from emp order by count(*)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with q as (select * from emp)\n"
operator|+
literal|"select count(*) from q group by deptno order by count(*)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with q as (select * from emp)\n"
operator|+
literal|"select count(*) from q order by count(*)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ORDER BY on UNION would produce a similar parse tree,
comment|// SqlOrderBy(SqlUnion(SqlSelect ...)), but is not valid SQL.
name|sql
argument_list|(
literal|"select count(*) from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"select count(*) from emp\n"
operator|+
literal|"order by ^count(*)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Aggregate expression is illegal in ORDER BY clause of non-aggregating SELECT"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4092">[CALCITE-4092]    * NPE using WITH clause without a corresponding SELECT FROM</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithNotSelected
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"with emp2 as (select max(empno) as empno from emp)\n"
operator|+
literal|"select * from emp where empno< ^emp2^.empno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMP2' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests a large scalar expression, which will expose any O(n^2) algorithms    * lurking in the validation process.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLarge
parameter_list|()
block|{
name|checkLarge
argument_list|(
literal|700
argument_list|,
name|sql
lambda|->
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|static
name|void
name|checkLarge
parameter_list|(
name|int
name|x
parameter_list|,
name|Consumer
argument_list|<
name|String
argument_list|>
name|f
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|)
block|{
comment|// NOTE jvs 1-Nov-2006:  Default thread stack size
comment|// on Windows is too small, so avoid stack overflow
name|x
operator|/=
literal|3
expr_stmt|;
block|}
comment|// E.g. large = "deptno * 1 + deptno * 2 + deptno * 3".
name|String
name|large
init|=
name|list
argument_list|(
literal|" + "
argument_list|,
literal|"deptno * "
argument_list|,
name|x
argument_list|)
decl_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select "
operator|+
name|large
operator|+
literal|"from emp"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select distinct "
operator|+
name|large
operator|+
literal|"from emp"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select "
operator|+
name|large
operator|+
literal|" from emp "
operator|+
literal|"group by deptno"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select * from emp where "
operator|+
name|large
operator|+
literal|"> 5"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select * from emp order by "
operator|+
name|large
operator|+
literal|" desc"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select "
operator|+
name|large
operator|+
literal|" from emp order by 1"
argument_list|)
expr_stmt|;
name|f
operator|.
name|accept
argument_list|(
literal|"select distinct "
operator|+
name|large
operator|+
literal|" from emp order by "
operator|+
name|large
argument_list|)
expr_stmt|;
comment|// E.g. "in (0, 1, 2, ...)"
name|f
operator|.
name|accept
argument_list|(
literal|"select * from emp where deptno in ("
operator|+
name|list
argument_list|(
literal|", "
argument_list|,
literal|""
argument_list|,
name|x
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
comment|// E.g. "where x = 1 or x = 2 or x = 3 ..."
name|f
operator|.
name|accept
argument_list|(
literal|"select * from emp where "
operator|+
name|list
argument_list|(
literal|" or "
argument_list|,
literal|"deptno = "
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
comment|// E.g. "select x1, x2 ... from (
comment|// select 'a' as x1, 'a' as x2, ... from emp union
comment|// select 'bb' as x1, 'bb' as x2, ... from dept)"
name|f
operator|.
name|accept
argument_list|(
literal|"select "
operator|+
name|list
argument_list|(
literal|", "
argument_list|,
literal|"x"
argument_list|,
name|x
argument_list|)
operator|+
literal|" from (select "
operator|+
name|list
argument_list|(
literal|", "
argument_list|,
literal|"'a' as x"
argument_list|,
name|x
argument_list|)
operator|+
literal|" from emp "
operator|+
literal|"union all select "
operator|+
name|list
argument_list|(
literal|", "
argument_list|,
literal|"'bb' as x"
argument_list|,
name|x
argument_list|)
operator|+
literal|" from dept)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
specifier|static
name|String
name|list
parameter_list|(
name|String
name|sep
parameter_list|,
name|String
name|before
parameter_list|,
name|int
name|count
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|before
argument_list|)
operator|.
name|append
argument_list|(
name|i
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
end_function

begin_function
annotation|@
name|Test
name|void
name|testAbstractConformance
parameter_list|()
throws|throws
name|InvocationTargetException
throws|,
name|IllegalAccessException
block|{
specifier|final
name|SqlAbstractConformance
name|c0
init|=
operator|new
name|SqlAbstractConformance
argument_list|()
block|{     }
decl_stmt|;
specifier|final
name|SqlConformance
name|c1
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|SqlConformance
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
specifier|final
name|Object
name|o0
init|=
name|method
operator|.
name|invoke
argument_list|(
name|c0
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|o1
init|=
name|method
operator|.
name|invoke
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|method
operator|.
name|toString
argument_list|()
argument_list|,
name|Objects
operator|.
name|equals
argument_list|(
name|o0
argument_list|,
name|o1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUserDefinedConformance
parameter_list|()
block|{
specifier|final
name|SqlAbstractConformance
name|custom
init|=
operator|new
name|SqlDelegatingConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
block|{
specifier|public
name|boolean
name|isBangEqualAllowed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|// Our conformance behaves differently from ORACLE_10 for FROM-less query.
name|sql
argument_list|(
literal|"^select 2+2^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|custom
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|fails
argument_list|(
literal|"SELECT must have a FROM clause"
argument_list|)
expr_stmt|;
comment|// Our conformance behaves like ORACLE_10 for "!=" operator.
name|sql
argument_list|(
literal|"select * from (values 1) where 1 ^!=^ 2"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|custom
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Bang equal '!=' is not allowed under the current SQL conformance level"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values 1) where 1 ^!=^ any (2, 3)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|custom
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Bang equal '!=' is not allowed under the current SQL conformance level"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrder
parameter_list|()
block|{
specifier|final
name|SqlConformance
name|conformance
init|=
name|fixture
argument_list|()
operator|.
name|conformance
argument_list|()
decl_stmt|;
name|sql
argument_list|(
literal|"select empno as x from emp order by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// invalid use of 'asc'
name|sql
argument_list|(
literal|"select empno, sal from emp order by ^asc^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ASC' not found in any table"
argument_list|)
expr_stmt|;
comment|// In sql92, empno is obscured by the alias.
comment|// Otherwise valid.
comment|// Checked Oracle10G -- is it valid.
name|sql
argument_list|(
literal|"select empno as x from emp order by empno"
argument_list|)
operator|.
name|failsIf
argument_list|(
name|conformance
operator|.
name|isSortByAliasObscures
argument_list|()
argument_list|,
literal|"unknown column empno"
argument_list|)
expr_stmt|;
comment|// valid in oracle and pre-99 sql,
comment|// invalid in sql:2003
name|sql
argument_list|(
literal|"select empno as x from emp order by ^x^"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"Column 'X' not found in any table"
argument_list|)
expr_stmt|;
comment|// invalid in oracle and pre-99;
comment|// valid from sql:99 onwards (but sorting by constant achieves
comment|// nothing!)
name|sql
argument_list|(
literal|"select empno as x from emp order by ^10^"
argument_list|)
operator|.
name|failsIf
argument_list|(
name|conformance
operator|.
name|isSortByOrdinal
argument_list|()
argument_list|,
literal|"Ordinal out of range"
argument_list|)
expr_stmt|;
comment|// Has different meanings in different dialects (which makes it very
comment|// confusing!) but is always valid.
name|sql
argument_list|(
literal|"select empno + 1 as empno from emp order by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Always fails
name|sql
argument_list|(
literal|"select empno as x from emp, dept order by ^deptno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DEPTNO' is ambiguous"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno + 1 from emp order by deptno asc, empno + 1 desc"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Alias 'deptno' is closer in scope than 'emp.deptno'
comment|// and 'dept.deptno', and is therefore not ambiguous.
comment|// Checked Oracle10G -- it is valid.
comment|// Ambiguous in SQL:2003
name|sql
argument_list|(
literal|"select empno as deptno from emp, dept order by deptno"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"col ambig"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from dept\n"
operator|+
literal|"union\n"
operator|+
literal|"select empno from emp\n"
operator|+
literal|"order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from dept\n"
operator|+
literal|"union\n"
operator|+
literal|"select empno from emp\n"
operator|+
literal|"order by ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in any table"
argument_list|)
expr_stmt|;
comment|// invalid in oracle and pre-99
name|sql
argument_list|(
literal|"select deptno from dept\n"
operator|+
literal|"union\n"
operator|+
literal|"select empno from emp\n"
operator|+
literal|"order by ^10^"
argument_list|)
operator|.
name|failsIf
argument_list|(
name|conformance
operator|.
name|isSortByOrdinal
argument_list|()
argument_list|,
literal|"Ordinal out of range"
argument_list|)
expr_stmt|;
comment|// Sort by scalar sub-query
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"order by (select name from dept where deptno = emp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp\n"
operator|+
literal|"order by (select name from dept where deptno = emp.^foo^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'FOO' not found in table 'EMP'"
argument_list|)
expr_stmt|;
comment|// REVIEW jvs 10-Apr-2008:  I disabled this because I don't
comment|// understand what it means; see
comment|// testAggregateInOrderByFails for the discrimination I added
comment|// (SELECT should be aggregating for this to make sense).
comment|/*         // Sort by aggregate. Oracle allows this.         check("select 1 from emp order by sum(sal)"); */
comment|// ORDER BY and SELECT *
name|sql
argument_list|(
literal|"select * from emp order by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp order by ^nonExistent^, deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'NONEXISTENT' not found in any table"
argument_list|)
expr_stmt|;
comment|// Overriding expression has different type.
name|sql
argument_list|(
literal|"select 'foo' as empno from emp order by ^empno + 5^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply '\\+' to arguments of type '<CHAR\\(3\\)> \\+<INTEGER>'\\..*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 'foo' as empno from emp order by empno + 5"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that you can reference a column alias in the ORDER BY clause if    * {@link SqlConformance#isSortByAlias()}. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrderByAlias
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) as total from emp order by ^total^"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|BIG_QUERY
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|STRICT_2003
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'TOTAL' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrderJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp as e, dept as d order by e.empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-633">[CALCITE-633]    * WITH ... ORDER BY cannot find table</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithOrder
parameter_list|()
block|{
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"select * from e as e1 order by e1.empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"select * from e as e1, e as e2 order by e1.empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-662">[CALCITE-662]    * Query validation fails when an ORDER BY clause is used with WITH    * CLAUSE</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testWithOrderInParentheses
parameter_list|()
block|{
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"(select e.empno from e order by e.empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"(select e.empno from e order by 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"(select ee.empno from e as ee order by ee.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// worked even before CALCITE-662 fixed
name|sql
argument_list|(
literal|"with e as (select * from emp)\n"
operator|+
literal|"(select e.empno from e)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOrderUnion
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by ^asc^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ASC' not found in any table"
argument_list|)
expr_stmt|;
comment|// name belongs to emp but is not projected so cannot sort on it
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by ^ename^ desc"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' not found in any table"
argument_list|)
expr_stmt|;
comment|// empno is not an alias in the first select in the union
name|sql
argument_list|(
literal|"select deptno, deptno as no2 from dept "
operator|+
literal|"union all "
operator|+
literal|"select empno, sal from emp "
operator|+
literal|"order by deptno asc, ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in any table"
argument_list|)
expr_stmt|;
comment|// ordinals ok
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// ordinal out of range -- if 'order by<ordinal>' means something in
comment|// this dialect
specifier|final
name|SqlConformance
name|conformance
init|=
name|fixture
argument_list|()
operator|.
name|conformance
argument_list|()
decl_stmt|;
if|if
condition|(
name|conformance
operator|.
name|isSortByOrdinal
argument_list|()
condition|)
block|{
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by ^3^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Ordinal out of range"
argument_list|)
expr_stmt|;
block|}
comment|// Expressions made up of aliases are OK.
comment|// (This is illegal in Oracle 10G.)
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by empno * sal + 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sal from emp "
operator|+
literal|"union all "
operator|+
literal|"select deptno, deptno from dept "
operator|+
literal|"order by 'foobar'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of the ORDER BY clause when GROUP BY is present.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrderGroup
parameter_list|()
block|{
comment|// Group by
name|sql
argument_list|(
literal|"select 1 from emp group by deptno order by ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// order by can contain aggregate expressions
name|sql
argument_list|(
literal|"select empno from emp "
operator|+
literal|"group by empno, deptno "
operator|+
literal|"order by deptno * sum(sal + 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Having
name|sql
argument_list|(
literal|"select sum(sal) from emp having count(*)> 3 order by ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(sal) from emp having count(*)> 3 order by sum(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Select distinct
name|sql
argument_list|(
literal|"select distinct deptno from emp group by deptno order by ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct deptno from emp group by deptno order by deptno, ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct deptno from emp group by deptno order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// UNION of SELECT DISTINCT and GROUP BY behaves just like a UNION.
name|sql
argument_list|(
literal|"select distinct deptno from dept "
operator|+
literal|"union all "
operator|+
literal|"select empno from emp group by deptno, empno "
operator|+
literal|"order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// order by can contain a mixture of aliases and aggregate expressions
name|sql
argument_list|(
literal|"select empno as x "
operator|+
literal|"from emp "
operator|+
literal|"group by empno, deptno "
operator|+
literal|"order by x * sum(sal + 2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
name|fixture
argument_list|()
operator|.
name|conformance
argument_list|()
decl_stmt|;
name|sql
argument_list|(
literal|"select empno as x "
operator|+
literal|"from emp "
operator|+
literal|"group by empno, deptno "
operator|+
literal|"order by empno * sum(sal + 2)"
argument_list|)
operator|.
name|failsIf
argument_list|(
name|conformance
operator|.
name|isSortByAliasObscures
argument_list|()
argument_list|,
literal|"xxxx"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of the aliases in GROUP BY.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1306">[CALCITE-1306]    * Allow GROUP BY and HAVING to reference SELECT expressions by ordinal and    * alias</a>.    *    * @see SqlConformance#isGroupByAlias()    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAliasInGroupBy
parameter_list|()
block|{
specifier|final
name|SqlConformanceEnum
name|lenient
init|=
name|SqlConformanceEnum
operator|.
name|LENIENT
decl_stmt|;
specifier|final
name|SqlConformanceEnum
name|strict
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
comment|// Group by
name|sql
argument_list|(
literal|"select empno as e from emp group by ^e^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno as e from emp group by ^e^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as e from emp group by ^e^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select e.empno from emp as e group by e.empno"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select e.empno as eno from emp as e group by ^eno^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENO' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as dno from emp group by cube(^dno^)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DNO' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as dno, ename name, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ((^dno^), (name, deptno))"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'DNO' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ename as deptno from emp as e join dept as d on "
operator|+
literal|"e.deptno = d.deptno group by ^deptno^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select t.e, count(*) from (select empno as e from emp) t group by e"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// The following 2 tests have the same SQL but fail for different reasons.
name|sql
argument_list|(
literal|"select t.e, count(*) as c from "
operator|+
literal|" (select empno as e from emp) t group by e,^c^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'C' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.e, ^count(*)^ as c from "
operator|+
literal|" (select empno as e from emp) t group by e,c"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_GROUP_BY
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.e, e + ^count(*)^ as c from "
operator|+
literal|" (select empno as e from emp) t group by e,c"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_GROUP_BY
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select t.e, e + ^count(*)^ as c from "
operator|+
literal|" (select empno as e from emp) t group by e,2"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_GROUP_BY
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno,(select empno + 1 from emp) eno\n"
operator|+
literal|"from dept group by deptno,^eno^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENO' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno as e, deptno as e\n"
operator|+
literal|"from emp group by ^e^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' is ambiguous"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, ^count(*)^ c from emp group by empno, c"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_GROUP_BY
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno + empno as d, deptno + empno + mgr from emp"
operator|+
literal|" group by d,mgr"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// When alias is equal to one or more columns in the query then giving
comment|// priority to alias. But Postgres may throw ambiguous column error or give
comment|// priority to column name.
name|sql
argument_list|(
literal|"select count(*) from (\n"
operator|+
literal|"  select ename AS deptno FROM emp GROUP BY deptno) t"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from "
operator|+
literal|"(select ename AS deptno FROM emp, dept GROUP BY deptno) t"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno + deptno AS \"z\" FROM emp GROUP BY \"Z\""
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno + deptno as c, ^c^ + mgr as d from emp group by c, d"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'C' not found in any table"
argument_list|)
expr_stmt|;
comment|// Group by alias with strict conformance should fail.
name|sql
argument_list|(
literal|"select empno as e from emp group by ^e^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of ordinals in GROUP BY.    *    * @see SqlConformance#isGroupByOrdinal()    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrdinalInGroupBy
parameter_list|()
block|{
specifier|final
name|SqlConformanceEnum
name|lenient
init|=
name|SqlConformanceEnum
operator|.
name|LENIENT
decl_stmt|;
specifier|final
name|SqlConformanceEnum
name|strict
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
name|sql
argument_list|(
literal|"select ^empno^,deptno from emp group by 1, deptno"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^emp.empno^ as e from emp group by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP.EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 2 + ^emp.empno^ + 3 as e from emp group by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP.EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^e.empno^ from emp as e group by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'E.EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select e.empno from emp as e group by 1, empno"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^e.empno^ as eno from emp as e group by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'E.EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^deptno^ as dno from emp group by cube(1)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 as dno from emp group by cube(1)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno as dno, ename name, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ((1), (^name^, deptno))"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'NAME' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^e.deptno^ from emp as e\n"
operator|+
literal|"join dept as d on e.deptno = d.deptno group by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'E.DEPTNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^deptno^,(select empno from emp) eno from dept"
operator|+
literal|" group by 1,2"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^empno^, count(*) from emp group by 1 order by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^empno^ eno, count(*) from emp group by 1 order by 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from (select 1 from emp"
operator|+
literal|" group by substring(ename from 2 for 3))"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp group by deptno, ^100^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Ordinal out of range"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Calcite considers integers in GROUP BY to be constants, so test passes.
comment|// Postgres considers them ordinals and throws out of range position error.
name|sql
argument_list|(
literal|"select deptno from emp group by ^100^, deptno"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Ordinal out of range"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-5507">[CALCITE-5507]    * HAVING alias failed when aggregate function in condition</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAggregateFunAndAliasInHaving
parameter_list|()
block|{
specifier|final
name|SqlConformanceEnum
name|lenient
init|=
name|SqlConformanceEnum
operator|.
name|LENIENT
decl_stmt|;
specifier|final
name|SqlConformanceEnum
name|strict
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
name|sql
argument_list|(
literal|"select count(empno) as e from emp having ^e^> 10 and count(empno)> 10 "
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(empno) as e from emp having count(empno)> 10 and count(^e^)> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of the aliases in HAVING.    *    * @see SqlConformance#isHavingAlias()    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAliasInHaving
parameter_list|()
block|{
specifier|final
name|SqlConformanceEnum
name|lenient
init|=
name|SqlConformanceEnum
operator|.
name|LENIENT
decl_stmt|;
specifier|final
name|SqlConformanceEnum
name|strict
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
name|sql
argument_list|(
literal|"select count(empno) as e from emp having ^e^> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as e from emp group by ^e^ having e> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select emp.empno as e from emp group by empno having ^e^> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select e.empno from emp as e group by 1 having ^e.empno^> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'E.EMPNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// When alias is equal to one or more columns in the query then giving
comment|// priority to alias, but PostgreSQL throws ambiguous column error or gives
comment|// priority to column name.
name|sql
argument_list|(
literal|"select count(empno) as deptno from emp having ^deptno^> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Alias in aggregate is not allowed.
name|sql
argument_list|(
literal|"select empno as e from emp having max(^e^)> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(empno) as e from emp having ^e^> 10"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|strict
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'E' not found in any table"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|lenient
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of the ORDER BY clause when DISTINCT is present.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrderDistinct
parameter_list|()
block|{
comment|// Distinct on expressions with attempts to order on a column in
comment|// the underlying table
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) "
operator|+
literal|"from emp order by ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) "
operator|+
literal|"from emp order by ^emp.empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp order by ^emp.empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp as e order by ^e.empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'E\\.EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
comment|// These tests are primarily intended to test cases where sorting by
comment|// an alias is allowed.  But for instances that don't support sorting
comment|// by alias, the tests also verify that a proper exception is thrown.
specifier|final
name|SqlConformance
name|conformance
init|=
name|fixture
argument_list|()
operator|.
name|conformance
argument_list|()
decl_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp order by ^empno^"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as eno "
operator|+
literal|"from emp order by ^eno^"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"Column 'ENO' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp e order by ^empno^"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
comment|// Distinct on expressions, sorting using ordinals.
if|if
condition|(
name|conformance
operator|.
name|isSortByOrdinal
argument_list|()
condition|)
block|{
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) from emp order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as bigint) as empno "
operator|+
literal|"from emp as e order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
comment|// Distinct on expressions with ordering on expressions as well
name|sql
argument_list|(
literal|"select distinct cast(empno as varchar(10)) from emp "
operator|+
literal|"order by cast(empno as varchar(10))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct cast(empno as varchar(10)) as eno from emp "
operator|+
literal|" order by upper(^eno^)"
argument_list|)
operator|.
name|failsIf
argument_list|(
operator|!
name|conformance
operator|.
name|isSortByAlias
argument_list|()
argument_list|,
literal|"Column 'ENO' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Tests validation of the ORDER BY clause when DISTINCT and GROUP BY are    * present.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-634">[CALCITE-634]    * Allow ORDER BY aggregate function in SELECT DISTINCT, provided that it    * occurs in SELECT clause</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOrderGroupDistinct
parameter_list|()
block|{
comment|// Order by an aggregate function,
comment|// which exists in select-clause with distinct being present
name|sql
argument_list|(
literal|"select distinct count(empno) AS countEMPNO from emp\n"
operator|+
literal|"group by empno\n"
operator|+
literal|"order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct count(empno) from emp\n"
operator|+
literal|"group by empno\n"
operator|+
literal|"order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct count(empno) AS countEMPNO from emp\n"
operator|+
literal|"group by empno\n"
operator|+
literal|"order by count(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct count(empno) from emp\n"
operator|+
literal|"group by empno\n"
operator|+
literal|"order by count(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct count(empno) from emp\n"
operator|+
literal|"group by empno\n"
operator|+
literal|"order by count(empno) desc"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno from emp\n"
operator|+
literal|"ORDER BY deptno, ^sum(empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Aggregate expression is illegal in ORDER BY clause of non-aggregating SELECT"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno from emp\n"
operator|+
literal|"GROUP BY deptno ORDER BY deptno, ^sum(empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SUM\\(`EMPNO`\\)' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, min(empno) from emp\n"
operator|+
literal|"GROUP BY deptno ORDER BY deptno, ^sum(empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SUM\\(`EMPNO`\\)' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, sum(empno) from emp\n"
operator|+
literal|"GROUP BY deptno ORDER BY deptno, sum(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno from emp where ^sum(sal)^> 50"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Aggregate expression is illegal in WHERE clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^empno^ from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^*^ from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// If we're grouping on ALL columns, 'select *' is ok.
comment|// Checked on Oracle10G.
name|sql
argument_list|(
literal|"select * from (select empno,deptno from emp) group by deptno,empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// This query tries to reference an agg expression from within a
comment|// sub-query as a correlating expression, but the SQL syntax rules say
comment|// that the agg function SUM always applies to the current scope.
comment|// As it happens, the query is valid.
name|sql
argument_list|(
literal|"select deptno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"having exists (select sum(emp.sal)> 10 from (values(true)))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// if you reference a column from a sub-query, it must be a group col
name|sql
argument_list|(
literal|"select deptno "
operator|+
literal|"from emp "
operator|+
literal|"group by deptno "
operator|+
literal|"having exists (select 1 from (values(true)) where emp.deptno = 10)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Needs proper error message text and error markers in query
if|if
condition|(
name|TODO
condition|)
block|{
name|sql
argument_list|(
literal|"select deptno "
operator|+
literal|"from emp "
operator|+
literal|"group by deptno "
operator|+
literal|"having exists (select 1 from (values(true)) where emp.empno = 10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"xx"
argument_list|)
expr_stmt|;
block|}
comment|// constant expressions
name|sql
argument_list|(
literal|"select cast(1 as integer) + 2 from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select localtime, deptno + 3 from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-886">[CALCITE-886]    * System functions in GROUP BY clause</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testGroupBySystemFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select CURRENT_USER from emp group by CURRENT_USER"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select CURRENT_USER from emp group by rollup(CURRENT_USER)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select CURRENT_USER from emp group by rollup(CURRENT_USER, ^x^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'X' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select CURRENT_USER from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupingSets
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(1), ^empno^ from emp group by grouping sets (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, ename, sum(sal) from emp\n"
operator|+
literal|"group by grouping sets ((deptno), (ename, deptno))\n"
operator|+
literal|"order by 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// duplicate (and heavily nested) GROUPING SETS
name|sql
argument_list|(
literal|"select sum(sal) from emp\n"
operator|+
literal|"group by deptno,\n"
operator|+
literal|"  grouping sets (deptno,\n"
operator|+
literal|"    grouping sets (deptno, ename),\n"
operator|+
literal|"      (ename)),\n"
operator|+
literal|"  ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRollup
parameter_list|()
block|{
comment|// DEPTNO is not null in database, but rollup introduces nulls
name|sql
argument_list|(
literal|"select deptno, count(*) as c, sum(sal) as s\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER DEPTNO, BIGINT NOT NULL C, INTEGER NOT NULL S) NOT NULL"
argument_list|)
expr_stmt|;
comment|// EMPNO stays NOT NULL because it is not rolled up
name|sql
argument_list|(
literal|"select deptno, empno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by empno, rollup(deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER DEPTNO, INTEGER NOT NULL EMPNO) NOT NULL"
argument_list|)
expr_stmt|;
comment|// DEPTNO stays NOT NULL because it is not rolled up
name|sql
argument_list|(
literal|"select deptno, empno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(empno), deptno"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO, INTEGER EMPNO) NOT NULL"
argument_list|)
expr_stmt|;
comment|// empno becomes NULL because it is rolled up, and so does empno + 1.
name|sql
argument_list|(
literal|"select empno, empno + 1 as e1\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by rollup(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER EMPNO, INTEGER E1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupByCorrelatedColumn
parameter_list|()
block|{
comment|// This is not sql 2003 standard; see sql2003 part2,  7.9
comment|// But the extension seems harmless.
specifier|final
name|String
name|sql
init|=
literal|"select count(*)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"where exists (select count(*) from dept group by emp.empno)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupExpressionEquivalence
parameter_list|()
block|{
comment|// operator equivalence
name|sql
argument_list|(
literal|"select empno + 1 from emp group by empno + 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 + ^empno^ from emp group by empno + 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// datatype equivalence
name|sql
argument_list|(
literal|"select cast(empno as VARCHAR(10)) from emp\n"
operator|+
literal|"group by cast(empno as VARCHAR(10))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(^empno^ as VARCHAR(11)) from emp group by cast(empno as VARCHAR(10))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupExpressionEquivalenceId
parameter_list|()
block|{
comment|// identifier equivalence
name|sql
argument_list|(
literal|"select case empno when 10 then deptno else null end from emp "
operator|+
literal|"group by case empno when 10 then deptno else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// matches even when one column is qualified (checked on Oracle10.1)
name|sql
argument_list|(
literal|"select case empno when 10 then deptno else null end from emp "
operator|+
literal|"group by case empno when 10 then emp.deptno else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select case empno when 10 then deptno else null end from emp "
operator|+
literal|"group by case emp.empno when 10 then emp.deptno else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select case emp.empno when 10 then deptno else null end from emp "
operator|+
literal|"group by case empno when 10 then emp.deptno else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// emp.deptno is different to dept.deptno (even though there is an '='
comment|// between them)
name|sql
argument_list|(
literal|"select case ^emp.empno^ when 10 then emp.deptno else null end "
operator|+
literal|"from emp join dept on emp.deptno = dept.deptno "
operator|+
literal|"group by case emp.empno when 10 then dept.deptno else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"todo: enable when correlating variables work"
argument_list|)
name|void
name|testGroupExpressionEquivalenceCorrelated
parameter_list|()
block|{
comment|// dname comes from dept, so it is constant within the sub-query, and
comment|// is so is a valid expr in a group-by query
name|sql
argument_list|(
literal|"select * from dept where exists ("
operator|+
literal|"select dname from emp group by empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept where exists ("
operator|+
literal|"select dname + empno + 1 from emp group by empno, dept.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"todo: enable when params are implemented"
argument_list|)
name|void
name|testGroupExpressionEquivalenceParams
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(? as integer) from emp group by cast(? as integer)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupExpressionEquivalenceLiteral
parameter_list|()
block|{
comment|// The purpose of this test is to see whether the validator
comment|// regards a pair of constants as equivalent. If we just used the raw
comment|// constants the validator wouldn't care ('SELECT 1 FROM emp GROUP BY
comment|// 2' is legal), so we combine a column and a constant into the same
comment|// CASE expression.
comment|// literal equivalence
name|sql
argument_list|(
literal|"select case empno when 10 then date '1969-04-29' else null end\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by case empno when 10 then date '1969-04-29' else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// this query succeeds in oracle 10.1 because 1 and 1.0 have the same
comment|// type
name|sql
argument_list|(
literal|"select case ^empno^ when 10 then 1 else null end from emp "
operator|+
literal|"group by case empno when 10 then 1.0 else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// 3.1415 and 3.14150 are different literals (I don't care either way)
name|sql
argument_list|(
literal|"select case ^empno^ when 10 then 3.1415 else null end from emp "
operator|+
literal|"group by case empno when 10 then 3.14150 else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// 3 and 03 are the same literal (I don't care either way)
name|sql
argument_list|(
literal|"select case empno when 10 then 03 else null end from emp "
operator|+
literal|"group by case empno when 10 then 3 else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select case ^empno^ when 10 then 1 else null end from emp "
operator|+
literal|"group by case empno when 10 then 2 else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select case empno when 10 then timestamp '1969-04-29 12:34:56.0'\n"
operator|+
literal|"       else null end from emp\n"
operator|+
literal|"group by case empno when 10 then timestamp '1969-04-29 12:34:56'\n"
operator|+
literal|"              else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupExpressionEquivalenceStringLiteral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select case empno when 10 then 'foo bar' else null end from emp "
operator|+
literal|"group by case empno when 10 then 'foo bar' else null end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG78_FIXED
condition|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select case empno when 10\n"
operator|+
literal|"      then _iso-8859-1'foo bar' collate latin1$en$1\n"
operator|+
literal|"      else null end\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by case empno when 10\n"
operator|+
literal|"      then _iso-8859-1'foo bar' collate latin1$en$1\n"
operator|+
literal|"      else null end"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
name|sql
argument_list|(
literal|"select case ^empno^ when 10 then _iso-8859-1'foo bar' else null end from emp "
operator|+
literal|"group by case empno when 10 then _UTF16'foo bar' else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Bug
operator|.
name|FRG78_FIXED
condition|)
block|{
name|sql
argument_list|(
literal|"select case ^empno^ when 10 then 'foo bar' collate latin1$en$1 else null end from emp "
operator|+
literal|"group by case empno when 10 then 'foo bar' collate latin1$fr$1 else null end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testGroupByAliasedColumn
parameter_list|()
block|{
comment|// alias in GROUP BY query has been known to cause problems
name|sql
argument_list|(
literal|"select deptno as d, count(*) as c from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedAggFails
parameter_list|()
block|{
comment|// simple case
name|sql
argument_list|(
literal|"select ^sum(max(empno))^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
comment|// should still fail with intermediate expression
name|sql
argument_list|(
literal|"select ^sum(2*max(empno))^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
comment|// make sure it fails with GROUP BY too
name|sql
argument_list|(
literal|"select ^sum(max(empno))^ from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
comment|// make sure it fails in HAVING too
name|sql
argument_list|(
literal|"select count(*) from emp group by deptno "
operator|+
literal|"having ^sum(max(empno))^=3"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
comment|// double-nesting should fail too; bottom-up validation currently
comment|// causes us to flag the intermediate level
name|sql
argument_list|(
literal|"select sum(^max(min(empno))^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testNestedAggOver
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(max(empno))\n"
operator|+
literal|" OVER (order by ^deptno^ ROWS 2 PRECEDING)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(max(empno)) OVER w\n"
operator|+
literal|" from emp\n"
operator|+
literal|" window w as (order by ^deptno^ ROWS 2 PRECEDING)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(max(empno)) OVER w2, sum(deptno) OVER w1\n"
operator|+
literal|" from emp group by deptno\n"
operator|+
literal|" window w1 as (partition by ^empno^ ROWS 2 PRECEDING),\n"
operator|+
literal|" w2 as (order by deptno ROWS 2 PRECEDING)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(count(empno)) over w\n"
operator|+
literal|"from emp group by deptno\n"
operator|+
literal|"window w as (partition by deptno order by ^empno^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// in OVER clause with more than one level of nesting
name|sql
argument_list|(
literal|"select ^avg(sum(min(sal)))^ OVER (partition by deptno)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
comment|// OVER clause columns not appearing in GROUP BY clause NOT OK
name|sql
argument_list|(
literal|"select avg(^sal^) OVER (), avg(count(empno)) OVER (partition by 1)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(^sal^) OVER (), avg(count(empno)) OVER (partition by deptno)\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(deptno) OVER (partition by ^empno^),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by deptno)\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(deptno) OVER (order by ^empno^),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by deptno)\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^empno^)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^empno^)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^empno^)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^empno^)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by deptno order by ^empno^)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(^sal^) OVER (),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by min(deptno))\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(deptno) OVER (),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by deptno)"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// COUNT(*), PARTITION BY(CONST) with GROUP BY is OK
name|sql
argument_list|(
literal|"select avg(count(*)) OVER ()\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) OVER ()\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno) OVER ()\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno, deptno + 1) OVER ()\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno, deptno + 1, ^empno^) OVER ()\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(emp.^*^)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown field '\\*'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(emp.^*^) OVER ()\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown field '\\*'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(count(*)) OVER (partition by 1)\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by 1)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sal), avg(count(empno)) OVER (partition by 1)\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// expression is OK
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by 10 - deptno\n"
operator|+
literal|"   order by deptno / 2 desc)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sal),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by min(deptno))\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(min(sal)) OVER (),\n"
operator|+
literal|" avg(count(empno)) OVER (partition by min(deptno))\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// expression involving non-GROUP column is not OK
name|sql
argument_list|(
literal|"select avg(2+^sal^) OVER (partition by deptno)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by deptno + ^empno^)\n"
operator|+
literal|"from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by empno + deptno)\n"
operator|+
literal|"from emp group by empno + deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by empno + deptno + 1)\n"
operator|+
literal|"from emp group by empno + deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^deptno^ + 1)\n"
operator|+
literal|"from emp group by empno + deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(empno + deptno) OVER (partition by empno + deptno + 1),\n"
operator|+
literal|" count(empno + deptno) OVER (partition by empno + deptno + 1)\n"
operator|+
literal|" from emp group by empno + deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// expression is NOT OK (AS clause)
name|sql
argument_list|(
literal|"select sum(max(empno))\n"
operator|+
literal|" OVER (order by ^deptno^ ROWS 2 PRECEDING) AS sumEmpNo\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(max(empno)) OVER w AS sumEmpNo\n"
operator|+
literal|" from emp\n"
operator|+
literal|" window w AS (order by ^deptno^ ROWS 2 PRECEDING)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(^sal^) OVER () AS avgSal,"
operator|+
literal|" avg(count(empno)) OVER (partition by 1) AS avgEmpNo\n"
operator|+
literal|" from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'SAL' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno, deptno + 1, ^empno^) OVER () AS cntDeptEmp\n"
operator|+
literal|" from emp group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select avg(sum(sal)) OVER (partition by ^deptno^ + 1) AS avgSal\n"
operator|+
literal|"from emp group by empno + deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// OVER in clause
name|sql
argument_list|(
literal|"select ^sum(max(empno) OVER (order by deptno ROWS 2 PRECEDING))^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_NESTED_AGG
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateInGroupByFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) from emp group by ^sum(empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_GROUP_BY
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateInNonGroupBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(1), ^empno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
comment|// Even though deptno is not null, its sum may be, because emp may be empty.
name|sql
argument_list|(
literal|"select sum(deptno) from emp"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(deptno) from emp group by ()"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(deptno) from emp group by empno"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateInOrderByFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select empno from emp order by ^sum(empno)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|ERR_AGG_IN_ORDER_BY
argument_list|)
expr_stmt|;
comment|// but this should be OK
name|sql
argument_list|(
literal|"select sum(empno) from emp group by deptno order by sum(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// this should also be OK
name|sql
argument_list|(
literal|"select sum(empno) from emp order by sum(empno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFilter
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(empno) filter (where deptno< 10) as s from emp"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER S) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFilterNotBoolean
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(empno) filter (where ^deptno + 10^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"FILTER clause must be a condition"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFilterInHaving
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(empno) as s from emp\n"
operator|+
literal|"group by deptno\n"
operator|+
literal|"having sum(empno) filter (where deptno< 20)> 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAggregateFilterContainsAggregate
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select sum(empno) filter (where ^count(*)< 10^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"FILTER must not contain aggregate expression"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testWithinGroup
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group(order by 1)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select collect(empno) within group(order by 1)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by ()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group(order by deptno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group(order by deptno, hiredate desc)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno,\n"
operator|+
literal|" collect(empno) within group(\n"
operator|+
literal|"  order by cast(deptno as varchar), hiredate desc)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select collect(empno) within group(order by 1)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select collect(empno) within group(order by 1)\n"
operator|+
literal|"from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^power(deptno, 1) within group(order by 1)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*WITHIN GROUP not allowed with POWER function.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^collect(empno)^ within group(order by count(*))\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"WITHIN GROUP must not contain aggregate expression"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4644">[CALCITE-4644]    * Add PERCENTILE_CONT and PERCENTILE_DISC aggregate functions</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentile
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_cont(0.25) within group (order by sal) as c,\n"
operator|+
literal|" percentile_disc(0.5) within group (order by sal desc) as d\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C, INTEGER NOT NULL D) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that {@code PERCENTILE_CONT} only allows numeric fields. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentileContMustOrderByNumeric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_cont(0.25) within group (^order by ename^)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid type 'VARCHAR' in ORDER BY clause of "
operator|+
literal|"'PERCENTILE_CONT' function. Only NUMERIC types are supported"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that {@code PERCENTILE_CONT} only allows one sort key. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentileContMultipleOrderByFields
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_cont(0.25) within group (^order by deptno, empno^)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"'PERCENTILE_CONT' requires precisely one ORDER BY key"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileContFractionMustBeLiteral
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" ^percentile_cont(deptno)^ within group (order by empno)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'PERCENTILE_CONT' must be a literal"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPercentileContFractionOutOfRange
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" ^percentile_cont(1.5)^ within group (order by deptno)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument to function 'PERCENTILE_CONT' must be a numeric "
operator|+
literal|"literal between 0 and 1"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that {@code PERCENTILE_DISC} only allows numeric fields. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentileDiscMustOrderByNumeric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_disc(0.25) within group (^order by ename^)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid type 'VARCHAR' in ORDER BY clause of "
operator|+
literal|"'PERCENTILE_DISC' function. Only NUMERIC types are supported"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that {@code PERCENTILE_DISC} only allows one sort key. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testPercentileDiscMultipleOrderByFields
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select\n"
operator|+
literal|" percentile_disc(0.25) within group (^order by deptno, empno^)\n"
operator|+
literal|"from emp"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"'PERCENTILE_DISC' requires precisely one ORDER BY key"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelatingVariables
parameter_list|()
block|{
comment|// reference to unqualified correlating column
name|sql
argument_list|(
literal|"select * from emp where exists (\n"
operator|+
literal|"select * from dept where deptno = sal)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// reference to qualified correlating column
name|sql
argument_list|(
literal|"select * from emp where exists (\n"
operator|+
literal|"select * from dept where deptno = emp.sal)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testIntervalCompare
parameter_list|()
block|{
name|expr
argument_list|(
literal|"interval '1' hour = interval '1' day"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour<> interval '1' hour"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour< interval '1' second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' hour<= interval '1' minute"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' minute> interval '1' second"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' second>= interval '1' day"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' year>= interval '1' year"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' month = interval '1' year"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' month<> interval '1' month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '1' year>= interval '1' month"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' second>= interval '1' year"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '>=' to arguments of type "
operator|+
literal|"'<INTERVAL SECOND>>=<INTERVAL YEAR>'.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"interval '1' month = interval '1' day"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply '=' to arguments of type "
operator|+
literal|"'<INTERVAL MONTH> =<INTERVAL DAY>'.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-613">[CALCITE-613]    * Implicitly convert strings in comparisons</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testDateCompare
parameter_list|()
block|{
comment|// can convert character value to date, time, timestamp, interval
comment|// provided it is on one side of a comparison operator (=,<,>, BETWEEN)
name|expr
argument_list|(
literal|"date '2015-03-17'< '2015-03-18'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17'> '2015-03-18'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17' = '2015-03-18'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'2015-03-17'< date '2015-03-18'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17' between '2015-03-16' and '2015-03-19'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17' between '2015-03-16' and '2015-03'||'-19'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'2015-03-17' between date '2015-03-16' and date '2015-03-19'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17' between date '2015-03-16' and '2015-03-19'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"date '2015-03-17' between '2015-03-16' and date '2015-03-19'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"time '12:34:56'< '12:34:57'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"timestamp '2015-03-17 12:34:56'< '2015-03-17 12:34:57'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"interval '2' hour< '2:30'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// can convert to exact and approximate numeric
name|expr
argument_list|(
literal|"123> '72'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"12.3> '7.2'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// can convert to boolean
name|expr
argument_list|(
literal|"true = 'true'"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^true and 'true'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'AND' to arguments of type '<BOOLEAN> AND<CHAR\\(4\\)>'\\..*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testOverlaps
parameter_list|()
block|{
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3')\n"
operator|+
literal|" overlaps (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|" overlaps period (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3') overlaps (date '1-2-3', interval '1' "
operator|+
literal|"year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"(time '1:2:3', interval '1' second) overlaps (time '23:59:59', time"
operator|+
literal|" '1:2:3')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' ) overlaps "
operator|+
literal|"(timestamp '1-2-3 4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' )\n"
operator|+
literal|" overlaps (time '4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(time '4:5:6', timestamp '1-2-3 4:5:6' )\n"
operator|+
literal|" overlaps (time '4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(time '4:5:6', time '4:5:6' )\n"
operator|+
literal|" overlaps (time '4:5:6', date '1-2-3')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"1 overlaps 2"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type "
operator|+
literal|"'<INTEGER> OVERLAPS<INTEGER>'\\. Supported form.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"   overlaps (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// row with 3 arguments as left argument to overlaps
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(date '1-2-3', date '1-2-3', date '1-2-3')\n"
operator|+
literal|"   overlaps (date '1-2-3', date '1-2-3')^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
comment|// row with 3 arguments as right argument to overlaps
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  overlaps (date '1-2-3', date '1-2-3', date '1-2-3')^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"   overlaps (date '1-2-3', date '1-2-3', date '1-2-3')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(1, 2) overlaps (2, 3)^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
comment|// Other operators with similar syntax
name|String
index|[]
name|ops
init|=
block|{
literal|"overlaps"
block|,
literal|"contains"
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
for|for
control|(
name|String
name|op
range|:
name|ops
control|)
block|{
name|expr
argument_list|(
literal|"period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|" "
operator|+
name|op
operator|+
literal|" period (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3')\n"
operator|+
literal|" "
operator|+
name|op
operator|+
literal|" (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testContains
parameter_list|()
block|{
specifier|final
name|String
name|cannotApply
init|=
literal|"(?s).*Cannot apply 'CONTAINS' to arguments of type .*"
decl_stmt|;
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3')\n"
operator|+
literal|" contains (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|" contains period (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains (date '1-2-3', interval '1' year)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"(time '1:2:3', interval '1' second)\n"
operator|+
literal|" contains (time '23:59:59', time '1:2:3')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6')\n"
operator|+
literal|" contains (timestamp '1-2-3 4:5:6', interval '1 2:3:4.5' day to "
operator|+
literal|"second)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// period contains point
name|expr
argument_list|(
literal|"(date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains date '1-2-3'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// same, with "period" keyword
name|expr
argument_list|(
literal|"period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains date '1-2-3'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// point contains period
name|wholeExpr
argument_list|(
literal|"date '1-2-3'\n"
operator|+
literal|"  contains (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
comment|// same, with "period" keyword
name|wholeExpr
argument_list|(
literal|"date '1-2-3'\n"
operator|+
literal|"  contains period (date '1-2-3', date '1-2-3')"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
comment|// point contains point
name|wholeExpr
argument_list|(
literal|"date '1-2-3' contains date '1-2-3'"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(timestamp '1-2-3 4:5:6', timestamp '1-2-3 4:5:6' )\n"
operator|+
literal|" contains (time '4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(time '4:5:6', timestamp '1-2-3 4:5:6' )\n"
operator|+
literal|" contains (time '4:5:6', interval '1 2:3:4.5' day to second)"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(time '4:5:6', time '4:5:6' )\n"
operator|+
literal|"  contains (time '4:5:6', date '1-2-3')"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"1 contains 2"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
comment|// row with 3 arguments
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(date '1-2-3', date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains (date '1-2-3', date '1-2-3')^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// second argument is a point
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains date '1-2-3'\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
comment|// first argument may be null, so result may be null
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or (date '1-2-3',\n"
operator|+
literal|"     case 1 when 2 then date '1-2-3' else null end)\n"
operator|+
literal|"  contains date '1-2-3'\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// second argument may be null, so result may be null
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains case 1 when 1 then date '1-2-3' else null end\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^period (date '1-2-3', date '1-2-3')\n"
operator|+
literal|"  contains period (date '1-2-3', time '4:5:6')^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(1, 2) contains (2, 3)^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotApply
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testExtract
parameter_list|()
block|{
comment|// TODO: Need to have extract return decimal type for seconds
comment|// so we can have seconds fractions
name|expr
argument_list|(
literal|"extract(year from interval '1-2' year to month)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"extract(minute from interval '1.1' second)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"extract(year from DATE '2008-2-2')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"extract(minute from interval '11' month)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"extract(year from interval '11' second)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCastToInterval
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(interval '1' hour as varchar(20))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '1' hour as bigint)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BIGINT NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(1000 as interval hour)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '1' month as interval year)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '1-1' year to month as interval month)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '1:1' hour to minute as interval day)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL DAY NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(interval '1:1' hour to minute as interval minute to second)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL MINUTE TO SECOND NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(interval '1:1' hour to minute as interval month)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cast function cannot convert value of type "
operator|+
literal|"INTERVAL HOUR TO MINUTE to type INTERVAL MONTH"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"cast(interval '1-1' year to month as interval second)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cast function cannot convert value of type "
operator|+
literal|"INTERVAL YEAR TO MONTH to type INTERVAL SECOND"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMinusDateOperator
parameter_list|()
block|{
name|expr
argument_list|(
literal|"(CURRENT_DATE - CURRENT_DATE) HOUR"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL HOUR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"(CURRENT_DATE - CURRENT_DATE) YEAR TO MONTH"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTERVAL YEAR TO MONTH NOT NULL"
argument_list|)
expr_stmt|;
name|wholeExpr
argument_list|(
literal|"(CURRENT_DATE - LOCALTIME) YEAR TO MONTH"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Parameters must be of the same type.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBind
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where deptno = ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where deptno = ? and sal< 100000"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select case when deptno = ? then 1 else 2 end from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// It is not possible to infer type of ?, because SUBSTRING is overloaded
name|sql
argument_list|(
literal|"select deptno from emp group by substring(ename from ^?^ for ?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal use of dynamic parameter"
argument_list|)
expr_stmt|;
comment|// In principle we could infer that ? should be a VARCHAR
name|sql
argument_list|(
literal|"select count(*) from emp group by position(^?^ in ename)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal use of dynamic parameter"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^deptno^ from emp\n"
operator|+
literal|"group by case when deptno = ? then 1 else 2 end"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno from emp\n"
operator|+
literal|"group by deptno, case when deptno = ? then 1 else 2 end"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select 1 from emp having sum(sal)< ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1310">[CALCITE-1310]    * Infer type of arguments to BETWEEN operator</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testBindBetween
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp where ename between ? and ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where deptno between ? and ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where ? between deptno and ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where ? between ? and deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp where ^?^ between ? and ?"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal use of dynamic parameter"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnnest
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select*from unnest(multiset[1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset[1, 2])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset[321.3, 2.33])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset[321.3, 4.23e0])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset[43.2e1, cast(null as decimal(4,2))])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset[1, 2.3, 1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset['1','22','333'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset['1','22','333','22'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from ^unnest(1)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'UNNEST' to arguments of type 'UNNEST.<INTEGER>.'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(multiset(select*from dept))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(multiset(select deptno from dept)) as t(c)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(multiset(select * from dept)) as t(^c^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; "
operator|+
literal|"table has 2 columns \\('DEPTNO', 'NAME'\\), "
operator|+
literal|"whereas alias list has 1 columns"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^c1^ from unnest(multiset(select name from dept)) as t(c)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'C1' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3789">[CALCITE-3789]    * Support validation of UNNEST multiple array columns like Presto</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testAliasUnnestMultipleArrays
parameter_list|()
block|{
comment|// for accessing a field in STRUCT type unnested from array
name|sql
argument_list|(
literal|"select e.ENAME\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.employees) as t(e)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(10) NOT NULL"
argument_list|)
expr_stmt|;
comment|// for unnesting multiple arrays at the same time
name|sql
argument_list|(
literal|"select d.deptno, e, k.empno, l.\"unit\", l.\"X\" * l.\"Y\"\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.admins, d.employees, d.offices) as t(e, k, l)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Make sure validation fails properly given illegal select items
name|sql
argument_list|(
literal|"select d.deptno, ^e^.some_column, k.empno\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.admins, d.employees) as t(e, k)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select d.deptno, e.detail, ^unknown^.detail\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.employees) as t(e)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRESTO
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Incompatible types"
argument_list|)
expr_stmt|;
comment|// Make sure validation fails without PRESTO conformance
name|sql
argument_list|(
literal|"select e.ENAME\n"
operator|+
literal|"from dept_nested_expanded as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.employees) as t(^e^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; table has 3 columns "
operator|+
literal|"\\('EMPNO', 'ENAME', 'DETAIL'\\), whereas alias list has 1 columns"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnnestArray
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select*from unnest(array[1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[1, 2])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[321.3, 2.33])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(5, 2) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[321.3, 4.23e0])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[43.2e1, cast(null as decimal(4,2))])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DOUBLE"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[1, 2.3, 1])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"DECIMAL(11, 1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array['1','22','333'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array['1','22','333','22'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array['1','22',null,'22'])"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(2)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from ^unnest(1)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'UNNEST' to arguments of type 'UNNEST.<INTEGER>.'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array(select*from dept))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[1,null])"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(array(select deptno from dept)) as t(c)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(array(select * from dept)) as t(^c^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; "
operator|+
literal|"table has 2 columns \\('DEPTNO', 'NAME'\\), "
operator|+
literal|"whereas alias list has 1 columns"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^c1^ from unnest(array(select name from dept)) as t(c)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'C1' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayConstructor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select array[1,2] as a from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select array[1,cast(null as integer), 2] as a\n"
operator|+
literal|"from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select array[1,null,2] as a from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select array['1',null,'234',''] as a from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"CHAR(3) ARRAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4999">[CALCITE-4999]    * ARRAY, MULTISET functions should return a collection of scalars    * if a sub-query returns 1 column</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testArrayQueryConstructor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select array(select 1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select array(select ROW(1,2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER NOT NULL EXPR$1) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCastAsCollectionType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(array[1,null,2] as int array) from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(array['1',null,'2'] as varchar(5) array) from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
comment|// test array type.
name|sql
argument_list|(
literal|"select cast(\"intArrayType\" as int array) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"varchar5ArrayType\" as varchar(5) array) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"intArrayArrayType\" as int array array) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL ARRAY NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"varchar5ArrayArrayType\" as varchar(5) array array) "
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL ARRAY NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
comment|// test multiset type.
name|sql
argument_list|(
literal|"select cast(\"intMultisetType\" as int multiset) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"varchar5MultisetType\" as varchar(5) multiset) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"intMultisetArrayType\" as int multiset array) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL MULTISET NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(\"varchar5MultisetArrayType\" as varchar(5) multiset array) "
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(5) NOT NULL MULTISET NOT NULL ARRAY NOT NULL"
argument_list|)
expr_stmt|;
comment|// test row type nested in collection type.
name|sql
argument_list|(
literal|"select cast(\"rowArrayMultisetType\" as row(f0 int array multiset, "
operator|+
literal|"f1 varchar(5) array) array multiset) "
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL ARRAY NOT NULL MULTISET NOT NULL F0, "
operator|+
literal|"VARCHAR(5) NOT NULL ARRAY NOT NULL F1) NOT NULL "
operator|+
literal|"ARRAY NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
comment|// test UDT collection type.
name|sql
argument_list|(
literal|"select cast(a as ^MyUDT^ array multiset) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'MYUDT'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCastAsRowType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select cast(a as row(f0 int, f1 varchar)) from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL F0, VARCHAR NOT NULL F1) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(b as row(f0 int not null, f1 varchar null))\n"
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL F0, VARCHAR F1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// test nested row type.
name|sql
argument_list|(
literal|"select "
operator|+
literal|"cast(c as row("
operator|+
literal|"f0 row(ff0 int not null, ff1 varchar null) null, "
operator|+
literal|"f1 timestamp not null))"
operator|+
literal|" from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType("
operator|+
literal|"RecordType(INTEGER FF0, VARCHAR FF1) F0, "
operator|+
literal|"TIMESTAMP(0) NOT NULL F1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// test row type in collection data types.
name|sql
argument_list|(
literal|"select cast(d as row(f0 bigint not null, f1 decimal null) array)\n"
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType(BIGINT NOT NULL F0, DECIMAL(19, 0) F1) NOT NULL "
operator|+
literal|"ARRAY NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select cast(e as row(f0 varchar not null, f1 timestamp null) multiset)\n"
operator|+
literal|"from COMPLEXTYPES.CTC_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|columnType
argument_list|(
literal|"RecordType(VARCHAR NOT NULL F0, TIMESTAMP(0) F1) NOT NULL "
operator|+
literal|"MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMultisetConstructor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select multiset[1,null,2] as a from (values (1))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4999">[CALCITE-4999]    * ARRAY, MULTISET functions should return an collection of scalars    * if a sub-query returns 1 column</a>.    */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testMultisetQueryConstructor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select multiset(select 1)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select multiset(select ROW(1,2))"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER NOT NULL EXPR$1) NOT NULL MULTISET NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnnestArrayColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql1
init|=
literal|"select d.deptno, e.*\n"
operator|+
literal|"from dept_nested as d,\n"
operator|+
literal|" UNNEST(d.employees) as e"
decl_stmt|;
specifier|final
name|String
name|type
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO,"
operator|+
literal|" INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(10) NOT NULL ENAME,"
operator|+
literal|" RecordType(RecordType(VARCHAR(10) NOT NULL TYPE, VARCHAR(20) NOT NULL DESC,"
operator|+
literal|" RecordType(VARCHAR(10) NOT NULL A, VARCHAR(10) NOT NULL B) NOT NULL OTHERS)"
operator|+
literal|" NOT NULL ARRAY NOT NULL SKILLS) NOT NULL DETAIL) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// equivalent query without table alias
specifier|final
name|String
name|sql1b
init|=
literal|"select d.deptno, e.*\n"
operator|+
literal|"from dept_nested as d,\n"
operator|+
literal|" UNNEST(employees) as e"
decl_stmt|;
name|sql
argument_list|(
name|sql1b
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// equivalent query using CROSS JOIN
specifier|final
name|String
name|sql2
init|=
literal|"select d.deptno, e.*\n"
operator|+
literal|"from dept_nested as d CROSS JOIN\n"
operator|+
literal|" UNNEST(d.employees) as e"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// equivalent query using CROSS JOIN, without table alias
specifier|final
name|String
name|sql2b
init|=
literal|"select d.deptno, e.*\n"
operator|+
literal|"from dept_nested as d CROSS JOIN\n"
operator|+
literal|" UNNEST(employees) as e"
decl_stmt|;
name|sql
argument_list|(
name|sql2b
argument_list|)
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|// LATERAL works left-to-right
specifier|final
name|String
name|sql3
init|=
literal|"select d.deptno, e.*\n"
operator|+
literal|"from UNNEST(^d^.employees) as e, dept_nested as d"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'D' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUnnestWithOrdinality
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select*from unnest(array[1, 2]) with ordinality"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EXPR$0, INTEGER NOT NULL ORDINALITY) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(array[43.2e1, cast(null as decimal(4,2))]) with ordinality"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(DOUBLE EXPR$0, INTEGER NOT NULL ORDINALITY) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(array(select deptno from dept)) with ordinality as t"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL T, INTEGER NOT NULL ORDINALITY) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select*from ^unnest(1) with ordinality^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'UNNEST' to arguments of type 'UNNEST.<INTEGER>.'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno\n"
operator|+
literal|"from unnest(array(select*from dept)) with ordinality\n"
operator|+
literal|"where ordinality< 5"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(\n"
operator|+
literal|"  array(select deptno from dept)) with ordinality as t(^c^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; table has 2 "
operator|+
literal|"columns \\('EXPR\\$0', 'ORDINALITY'\\), "
operator|+
literal|"whereas alias list has 1 columns"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(\n"
operator|+
literal|"  array(select deptno from dept)) with ordinality as t(c, d)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select c from unnest(\n"
operator|+
literal|"  array(select deptno from dept)) with ordinality as t(^c, d, e^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; table has 2 "
operator|+
literal|"columns \\('EXPR\\$0', 'ORDINALITY'\\), "
operator|+
literal|"whereas alias list has 3 columns"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select c\n"
operator|+
literal|"from unnest(array(select * from dept)) with ordinality as t(^c, d, e, f^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"List of column aliases must have same degree as table; table has 3 "
operator|+
literal|"columns \\('DEPTNO', 'NAME', 'ORDINALITY'\\), "
operator|+
literal|"whereas alias list has 4 columns"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^name^ from unnest(array(select name from dept)) with ordinality as t(c, o)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'NAME' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^ordinality^ from unnest(array(select name from dept)) with ordinality as t(c, o)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ORDINALITY' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|unnestMapMustNameColumnsKeyAndValueWhenNotAliased
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from unnest(map[1, 12, 2, 22])"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL KEY, INTEGER NOT NULL VALUE) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-4305">[CALCITE-4305]    * Implicit column alias for single-column UNNEST</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUnnestAlias
parameter_list|()
block|{
specifier|final
name|String
name|expectedType
init|=
literal|"RecordType(CHAR(6) NOT NULL FRUIT) NOT NULL"
decl_stmt|;
comment|// When UNNEST produces a single column, and you use an alias for the
comment|// relation, that alias becomes the name of the column.
name|sql
argument_list|(
literal|"select fruit.* from UNNEST(array ['apple', 'banana']) as fruit"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select fruit.* from UNNEST(array(select 'banana')) as fruit"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT array(SELECT y + 1 FROM UNNEST(s.x) y) FROM (SELECT ARRAY[1,2,3] as x) s"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// The magic doesn't happen if the query is not an UNNEST.
comment|// In this case, the query is a SELECT.
name|sql
argument_list|(
literal|"SELECT fruit.*\n"
operator|+
literal|"FROM (\n"
operator|+
literal|"  SELECT * FROM UNNEST(array ['apple', 'banana']) as x) as fruit"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(CHAR(6) NOT NULL X) NOT NULL"
argument_list|)
expr_stmt|;
comment|// The magic doesn't happen if the UNNEST yields more than one column.
name|sql
argument_list|(
literal|"select * from UNNEST(array [('apple', 1), ('banana', 2)]) as fruit"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(CHAR(6) NOT NULL EXPR$0, INTEGER NOT NULL EXPR$1) "
operator|+
literal|"NOT NULL"
argument_list|)
expr_stmt|;
comment|// VALUES gets the same treatment as ARRAY. (Unlike PostgreSQL.)
name|sql
argument_list|(
literal|"select * from (values ('apple'), ('banana')) as fruit"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(CHAR(6) NOT NULL FRUIT) NOT NULL"
argument_list|)
expr_stmt|;
comment|// UNNEST MULTISET gets the same treatment as UNNEST ARRAY.
name|sql
argument_list|(
literal|"select * from unnest(multiset [1, 2, 1]) as f"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL F) NOT NULL"
argument_list|)
expr_stmt|;
comment|// The magic doesn't happen if the UNNEST is used without AS operator.
name|sql
argument_list|(
literal|"select * from (SELECT ARRAY['banana'] as fruits) as t, UNNEST(t.fruits)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(CHAR(6) NOT NULL ARRAY NOT NULL FRUITS, "
operator|+
literal|"CHAR(6) NOT NULL EXPR$0) NOT NULL"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCorrelationJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select *,"
operator|+
literal|"         multiset(select * from emp where deptno=dept.deptno) "
operator|+
literal|"               as empset"
operator|+
literal|"      from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(select multiset[8] from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select*from unnest(select multiset[deptno] from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStructuredTypes
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values new address()"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ObjectSqlType(ADDRESS) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select home_address from emp_address"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ObjectSqlType(ADDRESS) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ea.home_address.zip from emp_address ea"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ea.mailing_address.city from emp_address ea"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(20) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLateral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp, (select * from dept where ^emp^.deptno=dept.deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'EMP' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp,\n"
operator|+
literal|"  LATERAL (select * from dept where emp.deptno=dept.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp,\n"
operator|+
literal|"  LATERAL (select * from dept where emp.deptno=dept.deptno) as ldt"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp,\n"
operator|+
literal|"  LATERAL (select * from dept where emp.deptno=dept.deptno) ldt"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCollect
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select collect(deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select collect(multiset[3]) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select collect(multiset[3]), ^deptno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFusion
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^fusion(deptno)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'FUSION' to arguments of type 'FUSION.<INTEGER>.'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select fusion(multiset[3]) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// todo. FUSION is an aggregate function. test that validator can only
comment|// take set operators in its select list once aggregation support is
comment|// complete
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCountFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(*) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(sal) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(1) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^count()^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'COUNT'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCountCompositeFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(ename, deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(ename, deptno, ^gender^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'GENDER' not found in any table"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(ename, 1, deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(distinct ename, 1, deptno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(deptno, ^*^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\*\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*^,^ deptno) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \",\" at .*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLastFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select LAST_VALUE(sal) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select LAST_VALUE(ename) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select FIRST_VALUE(sal) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select FIRST_VALUE(ename) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select NTH_VALUE(sal, 2) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select NTH_VALUE(ename, 2) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testMinMaxFunctions
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT MIN(true) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT MAX(false) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT MIN(sal+deptno) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT MAX(ename) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT MIN(5.5) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT MAX(5) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testArgMinMaxFunctions
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT ARG_MIN(1, true) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ARG_MAX(2, false) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ARG_MIN(sal, deptno) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ARG_MAX(deptno, sal) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ARG_MIN('a', 5.5) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ARG_MAX('b', 5) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testModeFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select MODE(sal) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select MODE(sal) over (order by empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select MODE(ename) from emp where sal=3000"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select MODE(sal) from emp group by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select MODE(sal) from emp group by deptno order by deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno,\n"
operator|+
literal|"^mode(empno)^ within group(order by 1)\n"
operator|+
literal|"from emp\n"
operator|+
literal|"group by deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Aggregate expression 'MODE' must not contain a WITHIN GROUP clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSomeEveryAndIntersectionFunctions
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select some(sal = 100), every(sal> 0), intersection(multiset[1,2]) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select some(sal = 100), ^empno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select every(sal> 0), ^empno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select intersection(multiset[1]), ^empno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testAnyValueFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT any_value(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBoolAndBoolOrFunction
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withOperatorTable
argument_list|(
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|POSTGRESQL
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"SELECT bool_and(true) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"SELECT bool_or(true) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select bool_and(col)\n"
operator|+
literal|"from (values(true), (false), (true)) as tbl(col)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select bool_or(col)\n"
operator|+
literal|"from (values(true), (false), (true)) as tbl(col)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select bool_and(col)\n"
operator|+
literal|"from (values(true), (false), (null)) as tbl(col)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select bool_or(col)\n"
operator|+
literal|"from (values(true), (false), (null)) as tbl(col)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"SELECT ^bool_and(ename)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'BOOL_AND' to arguments of type "
operator|+
literal|"'BOOL_AND\\(<VARCHAR\\(20\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"SELECT ^bool_or(ename)^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'BOOL_OR' to arguments of type "
operator|+
literal|"'BOOL_OR\\(<VARCHAR\\(20\\)>\\)'.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFunctionalDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select count(distinct sal) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select COALESCE(^distinct^ sal) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"DISTINCT/ALL not allowed with COALESCE function"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testColumnNotFound
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^b0^ from sales.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'B0' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testColumnNotFound2
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^b0^ from sales.emp, sales.dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'B0' not found in any table"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testColumnNotFound3
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select e.^b0^ from sales.emp as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'B0' not found in table 'E'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectDistinct
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, sal FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno FROM emp GROUP BY deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT ^deptno^ FROM emp GROUP BY sal"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT avg(sal) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT ^deptno^, avg(sal) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, sal from emp GROUP BY sal, deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT deptno FROM emp GROUP BY deptno HAVING deptno> 55"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, 33 FROM emp\n"
operator|+
literal|"GROUP BY deptno HAVING deptno> 55"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno, 33 FROM emp HAVING ^deptno^> 55"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// same query under a different conformance finds a different error first
name|sql
argument_list|(
literal|"SELECT DISTINCT ^deptno^, 33 FROM emp HAVING deptno> 55"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT 33 FROM emp HAVING ^deptno^> 55"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'DEPTNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT * from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT ^*^ from emp GROUP BY deptno"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMP\\.EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
comment|// similar validation for SELECT DISTINCT and GROUP BY
name|sql
argument_list|(
literal|"SELECT deptno FROM emp GROUP BY deptno ORDER BY deptno, ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno from emp ORDER BY deptno, ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno from emp ORDER BY deptno + 2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// The ORDER BY clause works on what is projected by DISTINCT - even if
comment|// GROUP BY is present.
name|sql
argument_list|(
literal|"SELECT DISTINCT deptno FROM emp GROUP BY deptno, empno ORDER BY deptno, ^empno^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not in the select clause"
argument_list|)
expr_stmt|;
comment|// redundant distinct; same query is in unitsql/optimizer/distinct.sql
name|sql
argument_list|(
literal|"select distinct * from (\n"
operator|+
literal|"  select distinct deptno from emp) order by 1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT DISTINCT 5, 10+5, 'string' from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectWithoutFrom
parameter_list|()
block|{
name|sql
argument_list|(
literal|"^select 2+2^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"^select 2+2^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|ORACLE_10
argument_list|)
operator|.
name|fails
argument_list|(
literal|"SELECT must have a FROM clause"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"^select 2+2^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|STRICT_2003
argument_list|)
operator|.
name|fails
argument_list|(
literal|"SELECT must have a FROM clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectAmbiguousField
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^t0^ from (select 1 as t0, 2 as T0 from dept)"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 't0' is ambiguous"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^t0^ from (select 1 as t0, 2 as t0,3 as t1,4 as t1, 5 as t2 from dept)"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 't0' is ambiguous"
argument_list|)
expr_stmt|;
comment|// t0 is not referenced,so this case is allowed
name|sql
argument_list|(
literal|"select 1 as t0, 2 as t0 from dept"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select t0 from (select 1 as t0, 2 as T0 from DEPT)"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTableExtend
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept extend (x int not null)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO, VARCHAR(10) NOT NULL NAME, "
operator|+
literal|"INTEGER NOT NULL X) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno + x as z\n"
operator|+
literal|"from dept extend (x int not null) as x\n"
operator|+
literal|"where x> 10"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL Z) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testExplicitTable
parameter_list|()
block|{
specifier|final
name|String
name|empRecordType
init|=
literal|"RecordType(INTEGER NOT NULL EMPNO,"
operator|+
literal|" VARCHAR(20) NOT NULL ENAME,"
operator|+
literal|" VARCHAR(10) NOT NULL JOB,"
operator|+
literal|" INTEGER MGR,"
operator|+
literal|" TIMESTAMP(0) NOT NULL HIREDATE,"
operator|+
literal|" INTEGER NOT NULL SAL,"
operator|+
literal|" INTEGER NOT NULL COMM,"
operator|+
literal|" INTEGER NOT NULL DEPTNO,"
operator|+
literal|" BOOLEAN NOT NULL SLACKER) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from (table emp)"
argument_list|)
operator|.
name|type
argument_list|(
name|empRecordType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"table emp"
argument_list|)
operator|.
name|type
argument_list|(
name|empRecordType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"table ^nonexistent^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'NONEXISTENT' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"table ^sales.nonexistent^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'NONEXISTENT' not found within 'SALES'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"table ^nonexistent.foo^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'NONEXISTENT' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCollectionTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(ramp(3))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL I) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^ramp('3')^)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'RAMP' to arguments of type "
operator|+
literal|"'RAMP\\(<CHAR\\(1\\)>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'RAMP\\(<NUMERIC>\\)'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(ramp('3'))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL I) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^abs^(-1))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"abs\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^1^ + 2)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"1\" at .*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^nonExistentRamp('3')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature NONEXISTENTRAMP\\(<CHARACTER>\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^bad_ramp(3)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Argument must be a table function: BAD_RAMP"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(^bad_table_function(3)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table function should have CURSOR type, not"
operator|+
literal|" RecordType\\(INTEGER I\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that Calcite gives an error if a table function is used anywhere    * that a scalar expression is expected. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testTableFunctionAsExpression
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^ramp(3)^ from (values (1))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot call table function here: 'RAMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values (1)) where ^ramp(3)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"WHERE clause must be a condition"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values (1)) where ^ramp(3) and 1 = 1^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'AND' to arguments of type '<CURSOR> AND "
operator|+
literal|"<BOOLEAN>'\\. Supported form\\(s\\): '<BOOLEAN> AND<BOOLEAN>'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values (1)) where ^ramp(3) is not null^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'IS NOT NULL' to arguments of type '<CURSOR> IS"
operator|+
literal|" NOT NULL'\\. Supported form\\(s\\): '<ANY> IS NOT NULL'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^sum(ramp(3))^ from (values (1))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'SUM' to arguments of type 'SUM\\(<CURSOR>\\)'\\. "
operator|+
literal|"Supported form\\(s\\): 'SUM\\(<NUMERIC>\\)'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select 0 from (values (1)) group by ^ramp(3)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot call table function here: 'RAMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from (values (1)) having ^ramp(3)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"HAVING clause must be a condition"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (values (1)) order by ^ramp(3)^ asc, 1 desc"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot call table function here: 'RAMP'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1309">[CALCITE-1309]    * Support LATERAL TABLE</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCollectionTableWithLateral
parameter_list|()
block|{
specifier|final
name|String
name|expectedType
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME, "
operator|+
literal|"INTEGER NOT NULL I) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from dept, lateral table(ramp(dept.deptno))"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept cross join lateral table(ramp(dept.deptno))"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept join lateral table(ramp(dept.deptno)) on true"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedType2
init|=
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME, "
operator|+
literal|"INTEGER I) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from dept left join lateral table(ramp(dept.deptno)) on true"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType2
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept, lateral table(^ramp(dept.name)^)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Cannot apply 'RAMP' to arguments of type 'RAMP\\(<VARCHAR\\(10\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept, lateral table(ramp(dept.name))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME, "
operator|+
literal|"INTEGER NOT NULL I) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from lateral table(ramp(^dept^.deptno)), dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'DEPT' not found"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expectedType3
init|=
literal|"RecordType(INTEGER NOT NULL I, "
operator|+
literal|"INTEGER NOT NULL DEPTNO, "
operator|+
literal|"VARCHAR(10) NOT NULL NAME) NOT NULL"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from lateral table(ramp(1234)), dept"
argument_list|)
operator|.
name|type
argument_list|(
name|expectedType3
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCollectionTableWithLateral2
parameter_list|()
block|{
comment|// The expression inside the LATERAL can only see tables before it in the
comment|// FROM clause. And it can't see itself.
name|sql
argument_list|(
literal|"select * from emp, lateral table(ramp(emp.deptno)), dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp, lateral table(ramp(^z^.i)) as z, dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'Z' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp, lateral table(ramp(^dept^.deptno)), dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'DEPT' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCollectionTableWithCursorParam
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(dedup(cursor(select * from emp),'ename'))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(1024) NOT NULL NAME) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(dedup(cursor(select * from ^bloop^),'ename'))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'BLOOP' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTemporalTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream * from orders, ^products^ for system_time as of"
operator|+
literal|" TIMESTAMP '2011-01-02 00:00:00'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'PRODUCTS' is not a temporal table, "
operator|+
literal|"can not be queried in system time period specification"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from orders, products_temporal "
operator|+
literal|"for system_time as of ^'2011-01-02 00:00:00'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"The system time period specification expects Timestamp type but is 'CHAR'"
argument_list|)
expr_stmt|;
comment|// verify inner join with a specific timestamp
name|sql
argument_list|(
literal|"select stream * from orders join products_temporal "
operator|+
literal|"for system_time as of timestamp '2011-01-02 00:00:00' "
operator|+
literal|"on orders.productid = products_temporal.productid"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// verify left join with a timestamp field
name|sql
argument_list|(
literal|"select stream * from orders left join products_temporal "
operator|+
literal|"for system_time as of orders.rowtime "
operator|+
literal|"on orders.productid = products_temporal.productid"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// verify left join with a timestamp expression
name|sql
argument_list|(
literal|"select stream * from orders left join products_temporal\n"
operator|+
literal|"for system_time as of orders.rowtime - INTERVAL '3' DAY\n"
operator|+
literal|"on orders.productid = products_temporal.productid"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// verify left join with a datetime value function
name|sql
argument_list|(
literal|"select stream * from orders left join products_temporal\n"
operator|+
literal|"for system_time as of CURRENT_TIMESTAMP\n"
operator|+
literal|"on orders.productid = products_temporal.productid"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testScalarSubQuery
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT  ename,(select name from dept where deptno=1) FROM emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT ename,(^select losal, hisal from salgrade where grade=1^) FROM emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply '\\$SCALAR_QUERY' to arguments of type "
operator|+
literal|"'\\$SCALAR_QUERY\\(<RECORDTYPE\\(INTEGER LOSAL, "
operator|+
literal|"INTEGER HISAL\\)>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"'\\$SCALAR_QUERY\\(<RECORDTYPE\\(SINGLE FIELD\\)>\\)'"
argument_list|)
expr_stmt|;
comment|// Note that X is a field (not a record) and is nullable even though
comment|// EMP.NAME is NOT NULL.
name|sql
argument_list|(
literal|"SELECT  ename,(select name from dept where deptno=1) FROM emp"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL ENAME, VARCHAR(10) EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// scalar subqery inside AS operator
name|sql
argument_list|(
literal|"SELECT  ename,(select name from dept where deptno=1) as X FROM emp"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL ENAME, VARCHAR(10) X) NOT NULL"
argument_list|)
expr_stmt|;
comment|// scalar subqery inside + operator
name|sql
argument_list|(
literal|"SELECT  ename, 1 + (select deptno from dept where deptno=1) as X FROM emp"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL ENAME, INTEGER X) NOT NULL"
argument_list|)
expr_stmt|;
comment|// scalar sub-query inside WHERE
name|sql
argument_list|(
literal|"select * from emp where (select true from dept)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"not supported"
argument_list|)
annotation|@
name|Test
name|void
name|testSubQueryInOnClause
parameter_list|()
block|{
comment|// Currently not supported. Should give validator error, but gives
comment|// internal error.
name|sql
argument_list|(
literal|"select * from emp as emps left outer join dept as depts\n"
operator|+
literal|"on emps.deptno = depts.deptno and emps.deptno = (\n"
operator|+
literal|"select min(deptno) from dept as depts2)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|dynamicParameterType
parameter_list|()
block|{
name|expr
argument_list|(
literal|"CAST(? AS INTEGER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRecordType
parameter_list|()
block|{
comment|// Have to qualify columns with table name.
name|sql
argument_list|(
literal|"SELECT ^coord^.x, coord.y FROM customer.contact"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'COORD' not found"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT contact.coord.x, contact.coord.y FROM customer.contact"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, INTEGER NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Qualifying with schema is OK.
name|sql
argument_list|(
literal|"SELECT customer.contact.coord.x, customer.contact.email,\n"
operator|+
literal|"  contact.coord.y\n"
operator|+
literal|"FROM customer.contact"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, VARCHAR(20) NOT NULL EMAIL, "
operator|+
literal|"INTEGER NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testArrayOfRecordType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT name, dept_nested.employees[1].^ne^ as ne from dept_nested"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown field 'NE'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT name, dept_nested.employees[1].ename as ename from dept_nested"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(10) NOT NULL NAME, VARCHAR(10) ENAME) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT dept_nested.employees[1].detail.skills[1].desc as DESCRIPTION\n"
operator|+
literal|"from dept_nested"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) DESCRIPTION) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT dept_nested.employees[1].detail.skills[1].others.a as oa\n"
operator|+
literal|"from dept_nested"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(10) OA) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testItemOperatorException
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^name[0]^ from dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'ITEM' to arguments of type 'ITEM\\(<VARCHAR\\(10\\)>, "
operator|+
literal|"<INTEGER>\\)'\\. Supported form\\(s\\):<ARRAY>\\[<INTEGER>\\]\n"
operator|+
literal|"<MAP>\\[<ANY>\\]\n"
operator|+
literal|"<ROW>\\[<CHARACTER>\\|<INTEGER>\\].*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-497">[CALCITE-497]    * Support optional qualifier for column name references</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testRecordTypeElided
parameter_list|()
block|{
name|sql
argument_list|(
literal|"SELECT contact.^x^, contact.coord.y FROM customer.contact"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'X' not found in table 'CONTACT'"
argument_list|)
expr_stmt|;
comment|// Fully qualified works.
name|sql
argument_list|(
literal|"SELECT contact.coord.x, contact.coord.y FROM customer.contact"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, INTEGER NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Because the types of CONTACT_PEEK.COORD and CONTACT_PEEK.COORD_NE are
comment|// marked "peek", the validator can see through them.
name|sql
argument_list|(
literal|"SELECT c.x, c.coord.y, c.m, c.b FROM customer.contact_peek as c"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, INTEGER NOT NULL Y, INTEGER NOT NULL M,"
operator|+
literal|" INTEGER NOT NULL B) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT c.coord.x, c.coord.y, c.coord_ne.m FROM customer.contact_peek as c"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, INTEGER NOT NULL Y, INTEGER NOT NULL M) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT x, a, c.coord.y FROM customer.contact_peek as c"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, INTEGER NOT NULL A, INTEGER NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Because CONTACT_PEEK.COORD_NE is marked "peek no expand",
comment|// "select *" does not flatten it.
name|sql
argument_list|(
literal|"SELECT coord_ne.* FROM customer.contact_peek as c"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL M, "
operator|+
literal|"RecordType:peek_no_expand(INTEGER NOT NULL A, INTEGER NOT NULL B) "
operator|+
literal|"NOT NULL SUB) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT * FROM customer.contact_peek as c"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL CONTACTNO, VARCHAR(10) NOT NULL FNAME, "
operator|+
literal|"VARCHAR(10) NOT NULL LNAME, VARCHAR(20) NOT NULL EMAIL, INTEGER NOT NULL X, "
operator|+
literal|"INTEGER NOT NULL Y, VARCHAR(20) NOT NULL unit, "
operator|+
literal|"RecordType:peek_no_expand(INTEGER NOT NULL M, "
operator|+
literal|"RecordType:peek_no_expand(INTEGER NOT NULL A, INTEGER NOT NULL B) "
operator|+
literal|"NOT NULL SUB) NOT NULL COORD_NE) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Qualifying with schema is OK.
specifier|final
name|String
name|sql
init|=
literal|"SELECT customer.contact_peek.x,\n"
operator|+
literal|" customer.contact_peek.email, contact_peek.coord.y\n"
operator|+
literal|"FROM customer.contact_peek"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL X, VARCHAR(20) NOT NULL EMAIL,"
operator|+
literal|" INTEGER NOT NULL Y) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSample
parameter_list|()
block|{
comment|// applied to table
name|sql
argument_list|(
literal|"SELECT * FROM emp TABLESAMPLE SUBSTITUTE('foo')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT * FROM emp TABLESAMPLE BERNOULLI(50)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT * FROM emp TABLESAMPLE SYSTEM(50)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// applied to query
name|sql
argument_list|(
literal|"SELECT * FROM ("
operator|+
literal|"SELECT deptno FROM emp "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE SUBSTITUTE('foo') "
operator|+
literal|"WHERE x.deptno< 100"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT x.^empno^ FROM ("
operator|+
literal|"SELECT deptno FROM emp TABLESAMPLE SUBSTITUTE('bar') "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE SUBSTITUTE('foo') "
operator|+
literal|"ORDER BY 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in table 'X'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (\n"
operator|+
literal|"    select * from emp\n"
operator|+
literal|"    join dept on emp.deptno = dept.deptno\n"
operator|+
literal|") tablesample substitute('SMALL')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT * FROM ("
operator|+
literal|"SELECT deptno FROM emp "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE BERNOULLI(50) "
operator|+
literal|"WHERE x.deptno< 100"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT x.^empno^ FROM ("
operator|+
literal|"SELECT deptno FROM emp TABLESAMPLE BERNOULLI(50) "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE BERNOULLI(10) "
operator|+
literal|"ORDER BY 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in table 'X'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (\n"
operator|+
literal|"    select * from emp\n"
operator|+
literal|"    join dept on emp.deptno = dept.deptno\n"
operator|+
literal|") tablesample bernoulli(10)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT * FROM ("
operator|+
literal|"SELECT deptno FROM emp "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE SYSTEM(50) "
operator|+
literal|"WHERE x.deptno< 100"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"SELECT x.^empno^ FROM ("
operator|+
literal|"SELECT deptno FROM emp TABLESAMPLE SYSTEM(50) "
operator|+
literal|"UNION ALL "
operator|+
literal|"SELECT deptno FROM dept) AS x TABLESAMPLE SYSTEM(10) "
operator|+
literal|"ORDER BY 1"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' not found in table 'X'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from (\n"
operator|+
literal|"    select * from emp\n"
operator|+
literal|"    join dept on emp.deptno = dept.deptno\n"
operator|+
literal|") tablesample system(10)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithoutIdentifierExpansion
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept"
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
literal|"SELECT *\n"
operator|+
literal|"FROM `DEPT`"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1238">[CALCITE-1238]    * Unparsing LIMIT without ORDER BY after validation</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithLimitWithoutOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select name from dept limit 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `NAME`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithLimitWithDynamicParameters
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select name from dept offset ? rows fetch next ? rows only"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `NAME`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"OFFSET ? ROWS\n"
operator|+
literal|"FETCH NEXT ? ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithOffsetWithoutOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select name from dept offset 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `NAME`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"OFFSET 2 ROWS"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithUnionFetchWithoutOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select name from dept union all select name from dept limit 2"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT *\n"
operator|+
literal|"FROM (SELECT `NAME`\n"
operator|+
literal|"FROM `DEPT`\n"
operator|+
literal|"UNION ALL\n"
operator|+
literal|"SELECT `NAME`\n"
operator|+
literal|"FROM `DEPT`)\n"
operator|+
literal|"FETCH NEXT 2 ROWS ONLY"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithIdentifierExpansion
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from dept"
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
literal|"SELECT `DEPT`.`DEPTNO`, `DEPT`.`NAME`\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`DEPT` AS `DEPT`"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithColumnReferenceExpansion
parameter_list|()
block|{
comment|// The names in the ORDER BY clause are not qualified.
comment|// This is because ORDER BY references columns in the SELECT clause
comment|// in preference to columns in tables in the FROM clause.
name|sql
argument_list|(
literal|"select name from dept where name = 'Moonracer' group by name"
operator|+
literal|" having sum(deptno)> 3 order by name"
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|withValidatorColumnReferenceExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
literal|"SELECT `DEPT`.`NAME`\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`DEPT` AS `DEPT`\n"
operator|+
literal|"WHERE `DEPT`.`NAME` = 'Moonracer'\n"
operator|+
literal|"GROUP BY `DEPT`.`NAME`\n"
operator|+
literal|"HAVING SUM(`DEPT`.`DEPTNO`)> 3\n"
operator|+
literal|"ORDER BY `NAME`"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteWithColumnReferenceExpansionAndFromAlias
parameter_list|()
block|{
comment|// In the ORDER BY clause, 'ename' is not qualified but 'deptno' and 'sal'
comment|// are. This is because 'ename' appears as an alias in the SELECT clause.
comment|// 'sal' is qualified in the ORDER BY clause, so remains qualified.
specifier|final
name|String
name|sql
init|=
literal|"select ename, sal from (select * from emp) as e"
operator|+
literal|" where ename = 'Moonracer' group by ename, deptno, sal"
operator|+
literal|" having sum(deptno)> 3 order by ename, deptno, e.sal"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `E`.`ENAME`, `E`.`SAL`\n"
operator|+
literal|"FROM (SELECT `EMP`.`EMPNO`, `EMP`.`ENAME`, `EMP`.`JOB`,"
operator|+
literal|" `EMP`.`MGR`, `EMP`.`HIREDATE`, `EMP`.`SAL`, `EMP`.`COMM`,"
operator|+
literal|" `EMP`.`DEPTNO`, `EMP`.`SLACKER`\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`EMP` AS `EMP`) AS `E`\n"
operator|+
literal|"WHERE `E`.`ENAME` = 'Moonracer'\n"
operator|+
literal|"GROUP BY `E`.`ENAME`, `E`.`DEPTNO`, `E`.`SAL`\n"
operator|+
literal|"HAVING SUM(`E`.`DEPTNO`)> 3\n"
operator|+
literal|"ORDER BY `ENAME`, `E`.`DEPTNO`, `E`.`SAL`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|withValidatorColumnReferenceExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRewriteExpansionOfColumnReferenceBeforeResolution
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select unexpanded.deptno from dept \n"
operator|+
literal|" where unexpanded.name = 'Moonracer' \n"
operator|+
literal|" group by unexpanded.deptno\n"
operator|+
literal|" having sum(unexpanded.deptno)> 0\n"
operator|+
literal|" order by unexpanded.deptno"
decl_stmt|;
specifier|final
name|String
name|expectedSql
init|=
literal|"SELECT `DEPT`.`DEPTNO`\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`DEPT` AS `DEPT`\n"
operator|+
literal|"WHERE `DEPT`.`NAME` = 'Moonracer'\n"
operator|+
literal|"GROUP BY `DEPT`.`DEPTNO`\n"
operator|+
literal|"HAVING SUM(`DEPT`.`DEPTNO`)> 0\n"
operator|+
literal|"ORDER BY `DEPT`.`DEPTNO`"
decl_stmt|;
name|SqlValidatorTestCase
operator|.
name|FIXTURE
operator|.
name|withFactory
argument_list|(
name|t
lambda|->
name|t
operator|.
name|withValidator
argument_list|(
name|UnexpandedToDeptValidator
operator|::
operator|new
argument_list|)
argument_list|)
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|withValidatorColumnReferenceExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expectedSql
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCoalesceWithoutRewrite
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select coalesce(deptno, empno) from emp"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT COALESCE(`EMP`.`DEPTNO`, `EMP`.`EMPNO`)\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`EMP` AS `EMP`"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT COALESCE(`DEPTNO`, `EMPNO`)\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|false
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected1
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCoalesceWithRewrite
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select coalesce(deptno, empno) from emp"
decl_stmt|;
specifier|final
name|String
name|expected1
init|=
literal|"SELECT CASE WHEN `EMP`.`DEPTNO` IS NOT NULL"
operator|+
literal|" THEN `EMP`.`DEPTNO` ELSE `EMP`.`EMPNO` END\n"
operator|+
literal|"FROM `CATALOG`.`SALES`.`EMP` AS `EMP`"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"SELECT CASE WHEN `DEPTNO` IS NOT NULL"
operator|+
literal|" THEN `DEPTNO` ELSE `EMPNO` END\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|true
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected1
argument_list|)
operator|.
name|withValidatorIdentifierExpansion
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected2
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDatePartWithRewrite
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select week(date '2022-04-27'), year(date '2022-04-27')"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT EXTRACT(WEEK FROM DATE '2022-04-27'),"
operator|+
literal|" EXTRACT(YEAR FROM DATE '2022-04-27')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|true
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|noParamSql
init|=
literal|"select ^week()^"
decl_stmt|;
name|sql
argument_list|(
name|noParamSql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|true
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'WEEK'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|multiParamsSql
init|=
literal|"select ^week(date '2022-04-27', 1)^"
decl_stmt|;
name|sql
argument_list|(
name|multiParamsSql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|true
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'WEEK'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDatePartWithoutRewrite
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select week(date '2022-04-27'), year(date '2022-04-27')"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT WEEK(DATE '2022-04-27'), YEAR(DATE '2022-04-27')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|false
argument_list|)
operator|.
name|rewritesTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
specifier|final
name|String
name|noParamSql
init|=
literal|"select ^week()^"
decl_stmt|;
name|sql
argument_list|(
name|noParamSql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'WEEK'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|multiParamsSql
init|=
literal|"select ^week(date '2022-04-27', 1)^"
decl_stmt|;
name|sql
argument_list|(
name|multiParamsSql
argument_list|)
operator|.
name|withValidatorCallRewrite
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'WEEK'. Was expecting 1 arguments"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
annotation|@
name|Test
name|void
name|testValuesWithAggFuncs
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values(^count(1)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Call to xxx is invalid\\. Direct calls to aggregate "
operator|+
literal|"functions not allowed in ROW definitions\\."
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testFieldOrigin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from emp join dept on true"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{CATALOG.SALES.EMP.EMPNO,"
operator|+
literal|" CATALOG.SALES.EMP.ENAME,"
operator|+
literal|" CATALOG.SALES.EMP.JOB,"
operator|+
literal|" CATALOG.SALES.EMP.MGR,"
operator|+
literal|" CATALOG.SALES.EMP.HIREDATE,"
operator|+
literal|" CATALOG.SALES.EMP.SAL,"
operator|+
literal|" CATALOG.SALES.EMP.COMM,"
operator|+
literal|" CATALOG.SALES.EMP.DEPTNO,"
operator|+
literal|" CATALOG.SALES.EMP.SLACKER,"
operator|+
literal|" CATALOG.SALES.DEPT.DEPTNO,"
operator|+
literal|" CATALOG.SALES.DEPT.NAME}"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select distinct emp.empno, hiredate, 1 as uno,\n"
operator|+
literal|" emp.empno * 2 as twiceEmpno\n"
operator|+
literal|"from emp join dept on true"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{CATALOG.SALES.EMP.EMPNO,"
operator|+
literal|" CATALOG.SALES.EMP.HIREDATE,"
operator|+
literal|" null,"
operator|+
literal|" null}"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select e.empno from dept_nested, unnest(employees) as e"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{CATALOG.SALES.DEPT_NESTED.EMPLOYEES.EMPNO}"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from UNNEST(ARRAY['a', 'b'])"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{null}"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from UNNEST(ARRAY['a', 'b'], ARRAY['d', 'e'])"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{null, null}"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select dpt.skill.desc from dept_nested as dpt"
argument_list|)
operator|.
name|assertFieldOrigin
argument_list|(
name|is
argument_list|(
literal|"{CATALOG.SALES.DEPT_NESTED.SKILL.DESC}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testBrackets
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select [e].EMPNO from [EMP] as [e]"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^e^.EMPNO from [EMP] as [e]"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found; did you mean 'e'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^x^ from (\n"
operator|+
literal|"  select [e].EMPNO as [x] from [EMP] as [e])"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'X' not found in any table; did you mean 'x'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^x^ from (\n"
operator|+
literal|"  select [e].EMPNO as [x ] from [EMP] as [e])"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'X' not found in any table"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select EMP^.^\"x\" from EMP"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\. \\\\\"\" at line .*"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select [x[y]] z ] from (\n"
operator|+
literal|"  select [e].EMPNO as [x[y]] z ] from [EMP] as [e])"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL x[y] z ) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLexJava
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select e.EMPNO from EMP as e"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL EMPNO) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^e^.EMPNO from EMP as E"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'e' not found; did you mean 'E'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^E^.EMPNO from EMP as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'E' not found; did you mean 'e'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^x^ from (\n"
operator|+
literal|"  select e.EMPNO as X from EMP as e)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'x' not found in any table; did you mean 'X'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^x^ from (\n"
operator|+
literal|"  select e.EMPNO as Xx from EMP as e)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'x' not found in any table"
argument_list|)
expr_stmt|;
comment|// double-quotes are not valid in this lexical convention
name|s
operator|.
name|withSql
argument_list|(
literal|"select EMP^.^\"x\" from EMP"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"\\. \\\\\"\" at line .*"
argument_list|)
expr_stmt|;
comment|// in Java mode, creating identifiers with spaces is not encouraged, but you
comment|// can use back-ticks if you really have to
name|s
operator|.
name|withSql
argument_list|(
literal|"select `x[y] z ` from (\n"
operator|+
literal|"  select e.EMPNO as `x[y] z ` from EMP as e)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL x[y] z ) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-145">[CALCITE-145]    * Unexpected upper-casing of keywords when using java lexer</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testLexJavaKeyword
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select path, x from (select 1 as path, 2 as x from (values (true)))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL path, INTEGER NOT NULL x) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select path, x from (select 1 as `path`, 2 as x from (values (true)))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL path, INTEGER NOT NULL x) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select `path`, x from (select 1 as path, 2 as x from (values (true)))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL path, INTEGER NOT NULL x) NOT NULL"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ^PATH^ from (select 1 as path from (values (true)))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'PATH' not found in any table; did you mean 'path'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select t.^PATH^ from (select 1 as path from (values (true))) as t"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'PATH' not found in table 't'; did you mean 'path'\\?"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select t.x, t.^PATH^ from (values (true, 1)) as t(path, x)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'PATH' not found in table 't'; did you mean 'path'\\?"
argument_list|)
expr_stmt|;
comment|// Built-in functions can be written in any case, even those with no args,
comment|// and regardless of spaces between function name and open parenthesis.
name|s
operator|.
name|withSql
argument_list|(
literal|"values (current_timestamp, floor(2.5), ceil (3.5))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"values (CURRENT_TIMESTAMP, FLOOR(2.5), CEIL (3.5))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"values (CURRENT_TIMESTAMP, CEIL (3.5))"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(TIMESTAMP(0) NOT NULL CURRENT_TIMESTAMP, "
operator|+
literal|"DECIMAL(2, 0) NOT NULL EXPR$1) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testLexAndQuoting
parameter_list|()
block|{
comment|// in Java mode, creating identifiers with spaces is not encouraged, but you
comment|// can use double-quote if you really have to
name|fixture
argument_list|()
operator|.
name|withLex
argument_list|(
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|)
operator|.
name|withSql
argument_list|(
literal|"select \"x[y] z \" from (\n"
operator|+
literal|"  select e.EMPNO as \"x[y] z \" from EMP as e)"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL x[y] z ) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests using case-insensitive matching of identifiers. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitive
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|sensitive
init|=
name|fixture
argument_list|()
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select EMPNO from EMP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select empno from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select [empno] from [emp]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select [E].[empno] from [emp] as e"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select t.[x] from (\n"
operator|+
literal|"  select [E].[empno] as x from [emp] as e) as [t]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// correlating variable
name|s
operator|.
name|withSql
argument_list|(
literal|"select * from emp as [e] where exists (\n"
operator|+
literal|"select 1 from dept where dept.deptno = [E].deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select * from emp as [e] where exists (\n"
operator|+
literal|"select 1 from dept where dept.deptno = ^[E]^.deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Table 'E' not found; did you mean 'e'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(1), ^empno^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Expression 'EMPNO' is not being grouped"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests using case-insensitive matching of user-defined functions. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveUdfs
parameter_list|()
block|{
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|MockSqlOperatorTable
operator|.
name|standard
argument_list|()
operator|.
name|extend
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|insensitive
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|sensitive
init|=
name|fixture
argument_list|()
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
decl_stmt|;
comment|// test table function lookup case-insensitively.
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select * from dept, lateral table(ramp(dept.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select * from dept, lateral table(RAMP(dept.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select * from dept, lateral table([RAMP](dept.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select * from dept, lateral table([Ramp](dept.deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test scalar function lookup case-insensitively.
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select myfun(EMPNO) from EMP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select MYFUN(empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select [MYFUN]([empno]) from [emp]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select [Myfun]([E].[empno]) from [emp] as e"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select t.[x] from (\n"
operator|+
literal|"  select [Myfun]([E].[empno]) as x from [emp] as e) as [t]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// correlating variable
name|insensitive
operator|.
name|withSql
argument_list|(
literal|"select * from emp as [e] where exists (\n"
operator|+
literal|"select 1 from dept where dept.deptno = myfun([E].deptno))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select * from emp as [e] where exists (\n"
operator|+
literal|"select 1 from dept where dept.deptno = ^[myfun]([e].deptno)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"No match found for function signature myfun\\(<NUMERIC>\\).*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests using case-sensitive matching of builtin functions. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseSensitiveBuiltinFunction
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|sensitive
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|)
decl_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select sum(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select [sum](EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select [SUM](EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select SUM(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select Sum(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select count(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select [count](EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select [COUNT](EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select COUNT(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select Count(EMPNO) from EMP group by ENAME, EMPNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-319">[CALCITE-319]    * Table aliases should follow case-sensitivity policy</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveTableAlias
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|sensitive
init|=
name|fixture
argument_list|()
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
comment|// Table aliases should follow case-sensitivity preference.
comment|//
comment|// In MySQL, table aliases are case-insensitive:
comment|// mysql> select `D`.day from DAYS as `d`, DAYS as `D`;
comment|// ERROR 1066 (42000): Not unique table/alias: 'D'
name|s
operator|.
name|withSql
argument_list|(
literal|"select count(*) from dept as [D], ^dept as [d]^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'd' in FROM clause"
argument_list|)
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select count(*) from dept as [D], dept as [d]"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
literal|"select count(*) from dept as [D], ^dept as [D]^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate relation name 'D' in FROM clause"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1305">[CALCITE-1305]    * Case-insensitive table aliases and GROUP BY</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveTableAliasInGroupBy
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select deptno, count(*) from EMP AS emp\n"
operator|+
literal|"group by eMp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select deptno, count(*) from EMP AS EMP\n"
operator|+
literal|"group by eMp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select deptno, count(*) from EMP\n"
operator|+
literal|"group by eMp.deptno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select * from EMP where exists (\n"
operator|+
literal|"  select 1 from dept\n"
operator|+
literal|"  group by eMp.deptno)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select deptno, count(*) from EMP group by DEPTNO"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1549">[CALCITE-1549]    * Improve error message when table or column not found</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testTableNotFoundDidYouMean
parameter_list|()
block|{
comment|// No table in default schema
name|sql
argument_list|(
literal|"select * from ^unknownTable^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'UNKNOWNTABLE' not found"
argument_list|)
expr_stmt|;
comment|// Similar table exists in default schema
name|sql
argument_list|(
literal|"select * from ^\"Emp\"^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'Emp' not found within 'SALES'; did you mean 'EMP'\\?"
argument_list|)
expr_stmt|;
comment|// Schema correct, but no table in specified schema
name|sql
argument_list|(
literal|"select * from ^sales.unknownTable^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'UNKNOWNTABLE' not found within 'SALES'"
argument_list|)
expr_stmt|;
comment|// Similar table exists in specified schema
name|sql
argument_list|(
literal|"select * from ^sales.\"Emp\"^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'Emp' not found within 'SALES'; did you mean 'EMP'\\?"
argument_list|)
expr_stmt|;
comment|// No schema found
name|sql
argument_list|(
literal|"select * from ^unknownSchema.unknownTable^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'UNKNOWNSCHEMA' not found"
argument_list|)
expr_stmt|;
comment|// Similar schema found
name|sql
argument_list|(
literal|"select * from ^\"sales\".emp^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'sales' not found; did you mean 'SALES'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ^\"saLes\".\"eMp\"^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'saLes' not found; did you mean 'SALES'\\?"
argument_list|)
expr_stmt|;
comment|// Spurious after table
name|sql
argument_list|(
literal|"select * from ^emp.foo^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'FOO' not found within 'SALES\\.EMP'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ^sales.emp.foo^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'FOO' not found within 'SALES\\.EMP'"
argument_list|)
expr_stmt|;
comment|// Alias not found
name|sql
argument_list|(
literal|"select ^aliAs^.\"name\"\n"
operator|+
literal|"from sales.emp as \"Alias\""
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'ALIAS' not found; did you mean 'Alias'\\?"
argument_list|)
expr_stmt|;
comment|// Alias not found, fully-qualified
name|sql
argument_list|(
literal|"select ^sales.\"emp\"^.\"name\" from sales.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'SALES\\.emp' not found; did you mean 'EMP'\\?"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testColumnNotFoundDidYouMean
parameter_list|()
block|{
comment|// Column not found
name|sql
argument_list|(
literal|"select ^\"unknownColumn\"^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'unknownColumn' not found in any table"
argument_list|)
expr_stmt|;
comment|// Similar column in table, unqualified table name
name|sql
argument_list|(
literal|"select ^\"empNo\"^ from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empNo' not found in any table; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table, table name qualified with schema
name|sql
argument_list|(
literal|"select ^\"empNo\"^ from sales.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empNo' not found in any table; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table, table name qualified with catalog and schema
name|sql
argument_list|(
literal|"select ^\"empNo\"^ from catalog.sales.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empNo' not found in any table; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
comment|// With table alias
name|sql
argument_list|(
literal|"select e.^\"empNo\"^ from catalog.sales.emp as e"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empNo' not found in table 'E'; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
comment|// With fully-qualified table alias
name|sql
argument_list|(
literal|"select catalog.sales.emp.^\"empNo\"^\n"
operator|+
literal|"from catalog.sales.emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empNo' not found in table 'CATALOG\\.SALES\\.EMP'; "
operator|+
literal|"did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table; multiple tables
name|sql
argument_list|(
literal|"select ^\"name\"^ from emp, dept"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'name' not found in any table; did you mean 'NAME'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table; table and a query
name|sql
argument_list|(
literal|"select ^\"name\"^ from emp,\n"
operator|+
literal|"  (select * from dept) as d"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'name' not found in any table; did you mean 'NAME'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table; table and an un-aliased query
name|sql
argument_list|(
literal|"select ^\"name\"^ from emp, (select * from dept)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'name' not found in any table; did you mean 'NAME'\\?"
argument_list|)
expr_stmt|;
comment|// Similar column in table, multiple tables
name|sql
argument_list|(
literal|"select ^\"deptno\"^ from emp,\n"
operator|+
literal|"  (select deptno as \"deptNo\" from dept)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'deptno' not found in any table; "
operator|+
literal|"did you mean 'DEPTNO', 'deptNo'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^\"deptno\"^ from emp,\n"
operator|+
literal|"  (select * from dept) as t(\"deptNo\", name)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'deptno' not found in any table; "
operator|+
literal|"did you mean 'DEPTNO', 'deptNo'\\?"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests matching of built-in operator names. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testUnquotedBuiltInFunctionNames
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|mysql
init|=
name|fixture
argument_list|()
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BACK_TICK
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|oracle
init|=
name|fixture
argument_list|()
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|TO_UPPER
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Built-in functions are always case-insensitive.
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select count(*), sum(deptno), floor(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select COUNT(*), FLOOR(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select cOuNt(*), FlOOr(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select cOuNt (*), FlOOr (2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select current_time from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select Current_Time from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select CURRENT_TIME from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select sum(deptno), floor(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select count(*), sum(deptno), floor(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select COUNT(*), FLOOR(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select cOuNt(*), FlOOr(2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select cOuNt (*), FlOOr (2.5) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select current_time from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select Current_Time from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select CURRENT_TIME from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// MySQL assumes that a quoted function name is not a built-in.
comment|//
comment|// mysql> select `sum`(`day`) from days;
comment|// ERROR 1630 (42000): FUNCTION foodmart.sum does not exist. Check the
comment|//   'Function Name Parsing and Resolution' section in the Reference Manual
comment|// mysql> select `SUM`(`day`) from days;
comment|// ERROR 1630 (42000): FUNCTION foodmart.SUM does not exist. Check the
comment|//   'Function Name Parsing and Resolution' section in the Reference Manual
comment|// mysql> select SUM(`day`) from days;
comment|// +------------+
comment|// | SUM(`day`) |
comment|// +------------+
comment|// |         28 |
comment|// +------------+
comment|// 1 row in set (0.00 sec)
comment|//
comment|// We do not follow MySQL in this regard. `count` is preserved in
comment|// lower-case, and is matched case-insensitively because it is a built-in.
comment|// So, the query succeeds.
name|oracle
operator|.
name|withSql
argument_list|(
literal|"select \"count\"(*) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mysql
operator|.
name|withSql
argument_list|(
literal|"select `count`(*) from dept"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Sanity check: All built-ins are upper-case. We rely on this. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testStandardOperatorNamesAreUpperCase
parameter_list|()
block|{
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
specifier|final
name|String
name|name
init|=
name|op
operator|.
name|getName
argument_list|()
decl_stmt|;
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
case|case
name|INTERNAL
case|:
break|break;
default|default:
name|assertThat
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
end_function

begin_function
specifier|private
specifier|static
name|int
name|prec
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|op
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|op
operator|.
name|getRightPrec
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/** Tests that operators, sorted by precedence, are in a sane order. Each    * operator has a {@link SqlOperator#getLeftPrec() left} and    * {@link SqlOperator#getRightPrec()} right} precedence, but we would like    * the order to remain the same even if we tweak particular operators'    * precedences. If you need to update the expected output, you might also    * need to change    *<a href="http://calcite.apache.org/docs/reference.html#operator-precedence">    * the documentation</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testOperatorsSortedByPrecedence
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Comparator
argument_list|<
name|SqlOperator
argument_list|>
name|comparator
init|=
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
name|int
name|c
init|=
name|Integer
operator|.
name|compare
argument_list|(
name|prec
argument_list|(
name|o1
argument_list|)
argument_list|,
name|prec
argument_list|(
name|o2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
operator|-
name|c
return|;
block|}
name|c
operator|=
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|o1
operator|.
name|getSyntax
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getSyntax
argument_list|()
argument_list|)
return|;
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operators
init|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
operator|.
name|getOperatorList
argument_list|()
decl_stmt|;
name|int
name|p
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|SqlOperator
name|op
range|:
name|Ordering
operator|.
name|from
argument_list|(
name|comparator
argument_list|)
operator|.
name|sortedCopy
argument_list|(
name|operators
argument_list|)
control|)
block|{
specifier|final
name|String
name|type
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|getSyntax
argument_list|()
condition|)
block|{
case|case
name|FUNCTION
case|:
case|case
name|FUNCTION_ID
case|:
case|case
name|FUNCTION_STAR
case|:
case|case
name|INTERNAL
case|:
continue|continue;
case|case
name|PREFIX
case|:
name|type
operator|=
literal|"pre"
expr_stmt|;
break|break;
case|case
name|POSTFIX
case|:
name|type
operator|=
literal|"post"
expr_stmt|;
break|break;
case|case
name|BINARY
case|:
if|if
condition|(
name|op
operator|.
name|getLeftPrec
argument_list|()
operator|<
name|op
operator|.
name|getRightPrec
argument_list|()
condition|)
block|{
name|type
operator|=
literal|"left"
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
literal|"right"
expr_stmt|;
block|}
break|break;
default|default:
if|if
condition|(
name|op
operator|instanceof
name|SqlSpecialOperator
condition|)
block|{
name|type
operator|=
literal|"-"
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
block|}
if|if
condition|(
name|prec
argument_list|(
name|op
argument_list|)
operator|!=
name|p
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|p
operator|=
name|prec
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|expected
init|=
literal|"\n"
operator|+
literal|"ARRAY -\n"
operator|+
literal|"ARRAY -\n"
operator|+
literal|"COLUMN_LIST -\n"
operator|+
literal|"CURSOR -\n"
operator|+
literal|"LATERAL -\n"
operator|+
literal|"MAP -\n"
operator|+
literal|"MAP -\n"
operator|+
literal|"MULTISET -\n"
operator|+
literal|"MULTISET -\n"
operator|+
literal|"ROW -\n"
operator|+
literal|"TABLE -\n"
operator|+
literal|"UNNEST -\n"
operator|+
literal|"\n"
operator|+
literal|"CURRENT_VALUE -\n"
operator|+
literal|"DEFAULT -\n"
operator|+
literal|"DOT -\n"
operator|+
literal|"ITEM -\n"
operator|+
literal|"NEXT_VALUE -\n"
operator|+
literal|"PATTERN_EXCLUDE -\n"
operator|+
literal|"PATTERN_PERMUTE -\n"
operator|+
literal|"WITHIN DISTINCT left\n"
operator|+
literal|"WITHIN GROUP left\n"
operator|+
literal|"\n"
operator|+
literal|"PATTERN_QUANTIFIER -\n"
operator|+
literal|"\n"
operator|+
literal|" left\n"
operator|+
literal|"$LiteralChain -\n"
operator|+
literal|"+ pre\n"
operator|+
literal|"- pre\n"
operator|+
literal|"FINAL pre\n"
operator|+
literal|"RUNNING pre\n"
operator|+
literal|"\n"
operator|+
literal|"| left\n"
operator|+
literal|"\n"
operator|+
literal|"% left\n"
operator|+
literal|"* left\n"
operator|+
literal|"/ left\n"
operator|+
literal|"/INT left\n"
operator|+
literal|"|| left\n"
operator|+
literal|"\n"
operator|+
literal|"+ left\n"
operator|+
literal|"+ -\n"
operator|+
literal|"- left\n"
operator|+
literal|"- -\n"
operator|+
literal|"EXISTS pre\n"
operator|+
literal|"UNIQUE pre\n"
operator|+
literal|"\n"
operator|+
literal|"< ALL left\n"
operator|+
literal|"< SOME left\n"
operator|+
literal|"<= ALL left\n"
operator|+
literal|"<= SOME left\n"
operator|+
literal|"<> ALL left\n"
operator|+
literal|"<> SOME left\n"
operator|+
literal|"= ALL left\n"
operator|+
literal|"= SOME left\n"
operator|+
literal|"> ALL left\n"
operator|+
literal|"> SOME left\n"
operator|+
literal|">= ALL left\n"
operator|+
literal|">= SOME left\n"
operator|+
literal|"BETWEEN ASYMMETRIC -\n"
operator|+
literal|"BETWEEN SYMMETRIC -\n"
operator|+
literal|"IN left\n"
operator|+
literal|"LIKE -\n"
operator|+
literal|"NEGATED POSIX REGEX CASE INSENSITIVE left\n"
operator|+
literal|"NEGATED POSIX REGEX CASE SENSITIVE left\n"
operator|+
literal|"NOT BETWEEN ASYMMETRIC -\n"
operator|+
literal|"NOT BETWEEN SYMMETRIC -\n"
operator|+
literal|"NOT IN left\n"
operator|+
literal|"NOT LIKE -\n"
operator|+
literal|"NOT SIMILAR TO -\n"
operator|+
literal|"POSIX REGEX CASE INSENSITIVE left\n"
operator|+
literal|"POSIX REGEX CASE SENSITIVE left\n"
operator|+
literal|"SIMILAR TO -\n"
operator|+
literal|"\n"
operator|+
literal|"$IS_DIFFERENT_FROM left\n"
operator|+
literal|"< left\n"
operator|+
literal|"<= left\n"
operator|+
literal|"<> left\n"
operator|+
literal|"= left\n"
operator|+
literal|"> left\n"
operator|+
literal|">= left\n"
operator|+
literal|"CONTAINS left\n"
operator|+
literal|"EQUALS left\n"
operator|+
literal|"IMMEDIATELY PRECEDES left\n"
operator|+
literal|"IMMEDIATELY SUCCEEDS left\n"
operator|+
literal|"IS DISTINCT FROM left\n"
operator|+
literal|"IS NOT DISTINCT FROM left\n"
operator|+
literal|"MEMBER OF left\n"
operator|+
literal|"NOT SUBMULTISET OF left\n"
operator|+
literal|"OVERLAPS left\n"
operator|+
literal|"PRECEDES left\n"
operator|+
literal|"SUBMULTISET OF left\n"
operator|+
literal|"SUCCEEDS left\n"
operator|+
literal|"\n"
operator|+
literal|"FORMAT JSON post\n"
operator|+
literal|"IS A SET post\n"
operator|+
literal|"IS EMPTY post\n"
operator|+
literal|"IS FALSE post\n"
operator|+
literal|"IS JSON ARRAY post\n"
operator|+
literal|"IS JSON OBJECT post\n"
operator|+
literal|"IS JSON SCALAR post\n"
operator|+
literal|"IS JSON VALUE post\n"
operator|+
literal|"IS NOT A SET post\n"
operator|+
literal|"IS NOT EMPTY post\n"
operator|+
literal|"IS NOT FALSE post\n"
operator|+
literal|"IS NOT JSON ARRAY post\n"
operator|+
literal|"IS NOT JSON OBJECT post\n"
operator|+
literal|"IS NOT JSON SCALAR post\n"
operator|+
literal|"IS NOT JSON VALUE post\n"
operator|+
literal|"IS NOT NULL post\n"
operator|+
literal|"IS NOT TRUE post\n"
operator|+
literal|"IS NOT UNKNOWN post\n"
operator|+
literal|"IS NULL post\n"
operator|+
literal|"IS TRUE post\n"
operator|+
literal|"IS UNKNOWN post\n"
operator|+
literal|"\n"
operator|+
literal|"NOT pre\n"
operator|+
literal|"\n"
operator|+
literal|"AND left\n"
operator|+
literal|"\n"
operator|+
literal|"OR left\n"
operator|+
literal|"\n"
operator|+
literal|"=> -\n"
operator|+
literal|"AS -\n"
operator|+
literal|"DESC post\n"
operator|+
literal|"FILTER left\n"
operator|+
literal|"IGNORE NULLS -\n"
operator|+
literal|"OVER left\n"
operator|+
literal|"RESPECT NULLS -\n"
operator|+
literal|"TABLESAMPLE -\n"
operator|+
literal|"\n"
operator|+
literal|"NULLS FIRST post\n"
operator|+
literal|"NULLS LAST post\n"
operator|+
literal|"\n"
operator|+
literal|"INTERSECT left\n"
operator|+
literal|"INTERSECT ALL left\n"
operator|+
literal|"MULTISET INTERSECT ALL left\n"
operator|+
literal|"MULTISET INTERSECT DISTINCT left\n"
operator|+
literal|"\n"
operator|+
literal|"EXCEPT left\n"
operator|+
literal|"EXCEPT ALL left\n"
operator|+
literal|"MULTISET EXCEPT ALL left\n"
operator|+
literal|"MULTISET EXCEPT DISTINCT left\n"
operator|+
literal|"MULTISET UNION ALL left\n"
operator|+
literal|"MULTISET UNION DISTINCT left\n"
operator|+
literal|"UNION left\n"
operator|+
literal|"UNION ALL left\n"
operator|+
literal|"\n"
operator|+
literal|"$throw -\n"
operator|+
literal|"Reinterpret -\n"
operator|+
literal|"TABLE pre\n"
operator|+
literal|"VALUES -\n"
operator|+
literal|"\n"
operator|+
literal|"CALL pre\n"
operator|+
literal|"ESCAPE -\n"
operator|+
literal|"NEW pre\n"
decl_stmt|;
name|assertThat
argument_list|(
name|b
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
block|}
end_function

begin_comment
comment|/** Tests that it is an error to insert into the same column twice, even using    * case-insensitive matching. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveInsert
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into EMP ([EMPNO], deptno, ^[empno]^)\n"
operator|+
literal|" values (1, 1, 1)"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Target column 'EMPNO' is assigned more than once"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests referencing columns from a sub-query that has duplicate column    * names. (The standard says it should be an error, but we don't right    * now.) */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveSubQuery
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|insensitive
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|false
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|sensitive
init|=
name|fixture
argument_list|()
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
literal|"select [e] from (\n"
operator|+
literal|"select EMPNO as [e], DEPTNO as d, 1 as [e2] from EMP)"
decl_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|String
name|sql1
init|=
literal|"select e2 from (\n"
operator|+
literal|"select EMPNO as [e2], DEPTNO as d, 1 as [E] from EMP)"
decl_stmt|;
name|insensitive
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sensitive
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests using case-insensitive matching of table names. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testCaseInsensitiveTables
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|mssql
init|=
name|fixture
argument_list|()
operator|.
name|withLex
argument_list|(
name|Lex
operator|.
name|SQL_SERVER
argument_list|)
decl_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select eMp.* from (select * from emp) as EmP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select ^eMp^.* from (select * from emp as EmP)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'eMp'"
argument_list|)
expr_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select eMp.* from (select * from emP) as EmP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select eMp.empNo from (select * from emP) as EmP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select empNo from (select Empno from emP) as EmP"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|mssql
operator|.
name|withSql
argument_list|(
literal|"select empNo from (select Empno from emP)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsert
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"values (1, 'Ambrosia')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"select 1, 'Ardy' from (values 'a')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"insert into emp\n"
operator|+
literal|"values (1, 'nom', 'job', 0, timestamp '1970-01-01 00:00:00', 1, 1,\n"
operator|+
literal|"  1, false)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into emp (empno, ename, job, mgr, hiredate,\n"
operator|+
literal|"  sal, comm, deptno, slacker)\n"
operator|+
literal|"select 1, 'nom', 'job', 0, timestamp '1970-01-01 00:00:00',\n"
operator|+
literal|"  1, 1, 1, false\n"
operator|+
literal|"from (values 'a')"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (ename, empno, deptno)\n"
operator|+
literal|"values ('Pat', 1, null)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"insert into empnullables (\n"
operator|+
literal|"  empno, ename, job, hiredate)\n"
operator|+
literal|"values (1, 'Jim', 'Baker', timestamp '1970-01-01 00:00:00')"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"select 1, 'b' from (values 'a')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (empno, ename)\n"
operator|+
literal|"values (1, 'Karl')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertWithNonEqualSourceSinkFieldsNum
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^dept^ select sid, ename, deptno "
operator|+
literal|"from "
operator|+
literal|"(select sum(empno) as sid, ename, deptno, sal "
operator|+
literal|"from emp group by ename, deptno, sal)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(2\\) "
operator|+
literal|"does not equal number of source items \\(3\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubset
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (1, 'nom', 'job', 0, timestamp '1970-01-01 00:00:00')"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (1, 'nom', null, 0, null)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1510">[CALCITE-1510]    * INSERT/UPSERT should allow fewer values than columns</a>,    * check for default value only when target field is null. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testInsertShouldNotCheckForDefaultValue
parameter_list|()
block|{
specifier|final
name|int
name|c
init|=
name|CountingFactory
operator|.
name|THREAD_CALL_COUNT
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into emp values(1, 'nom', 'job', 0, "
operator|+
literal|"timestamp '1970-01-01 00:00:00', 1, 1, 1, false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Should not check for default value if column is in INSERT"
argument_list|,
name|CountingFactory
operator|.
name|THREAD_CALL_COUNT
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now add a list of target columns, keeping the query otherwise the same.
specifier|final
name|String
name|sql2
init|=
literal|"insert into emp (empno, ename, job, mgr, hiredate,\n"
operator|+
literal|"  sal, comm, deptno, slacker)\n"
operator|+
literal|"values(1, 'nom', 'job', 0,\n"
operator|+
literal|"  timestamp '1970-01-01 00:00:00', 1, 1, 1, false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Should not check for default value if column is in INSERT"
argument_list|,
name|CountingFactory
operator|.
name|THREAD_CALL_COUNT
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now remove SLACKER, which is NOT NULL, from the target list.
specifier|final
name|String
name|sql3
init|=
literal|"insert into ^emp^ (empno, ename, job, mgr, hiredate,\n"
operator|+
literal|"  sal, comm, deptno)\n"
operator|+
literal|"values(1, 'nom', 'job', 0,\n"
operator|+
literal|"  timestamp '1970-01-01 00:00:00', 1, 1, 1)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'SLACKER' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Should not check for default value, even if if column is "
operator|+
literal|"missing from INSERT and nullable"
argument_list|,
name|CountingFactory
operator|.
name|THREAD_CALL_COUNT
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now remove DEPTNO, which has a default value, from the target list.
comment|// Will generate an extra call to newColumnDefaultValue at sql-to-rel time,
comment|// just not yet.
specifier|final
name|String
name|sql4
init|=
literal|"insert into ^emp^ (empno, ename, job, mgr, hiredate,\n"
operator|+
literal|"  sal, comm, slacker)\n"
operator|+
literal|"values(1, 'nom', 'job', 0,\n"
operator|+
literal|"  timestamp '1970-01-01 00:00:00', 1, 1, false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"Missing DEFAULT column generates a call to factory"
argument_list|,
name|CountingFactory
operator|.
name|THREAD_CALL_COUNT
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empnullables_20 (ename, empno, comm)\n"
operator|+
literal|"values ('Karl', 1, 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empnullables_20\n"
operator|+
literal|"values (1, 'Karl')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW (empno, ename, job)\n"
operator|+
literal|"values (1, 'Arthur', 'clown')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2 (empno, ename, job, extra)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2\n"
operator|+
literal|"values ('Arthur', 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2\n"
operator|+
literal|"values ('Arthur', 1, 'Knight', 20, false, 99999, true, timestamp '1370-01-01 00:00:00',"
operator|+
literal|" 1, 100)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBind
parameter_list|()
block|{
comment|// VALUES
specifier|final
name|String
name|sql0
init|=
literal|"insert into empnullables (empno, ename, deptno)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType0
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, INTEGER ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType0
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiple VALUES
specifier|final
name|String
name|sql1
init|=
literal|"insert into empnullables (empno, ename, deptno)\n"
operator|+
literal|"values (?, 'Pat', 1), (2, ?, ?), (3, 'Tod', ?), (4, 'Arthur', null)"
decl_stmt|;
specifier|final
name|String
name|expectedType1
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, INTEGER ?2, INTEGER ?3)"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType1
argument_list|)
argument_list|)
expr_stmt|;
comment|// VALUES with expression
name|sql
argument_list|(
literal|"insert into empnullables (ename, empno) values (?, ? + 1)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// SELECT
name|sql
argument_list|(
literal|"insert into empnullables (ename, empno) select ?, ? from (values (1))"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// WITH
specifier|final
name|String
name|sql3
init|=
literal|"insert into empnullables (ename, empno)\n"
operator|+
literal|"with v as (values ('a'))\n"
operator|+
literal|"select ?, ? from (values (1))"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// UNION
specifier|final
name|String
name|sql2
init|=
literal|"insert into empnullables (ename, empno)\n"
operator|+
literal|"select ?, ? from (values (1))\n"
operator|+
literal|"union all\n"
operator|+
literal|"select ?, ? from (values (time '1:2:3'))"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1,"
operator|+
literal|" VARCHAR(20) ?2, INTEGER ?3)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expected2
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertWithExtendedColumns
parameter_list|()
block|{
specifier|final
name|String
name|sql0
init|=
literal|"insert into empnullables\n"
operator|+
literal|" (empno, ename, \"f.dc\" ^varchar(10)^)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType0
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, VARCHAR(10) ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql0
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType0
argument_list|)
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Extended columns not allowed under "
operator|+
literal|"the current SQL conformance level"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into empnullables\n"
operator|+
literal|" (empno, ename, dynamic_column ^double^ not null)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType1
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, DOUBLE ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql1
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType1
argument_list|)
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Extended columns not allowed under "
operator|+
literal|"the current SQL conformance level"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into struct.t_extend\n"
operator|+
literal|" (f0.c0, f1.c1, \"F2\".\"C2\" ^varchar(20)^ not null)\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType2
init|=
literal|"RecordType(INTEGER ?0, INTEGER ?1, VARCHAR(20) ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType2
argument_list|)
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Extended columns not allowed under "
operator|+
literal|"the current SQL conformance level"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindSubset
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
comment|// VALUES
specifier|final
name|String
name|sql0
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType0
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, VARCHAR(10) ?2)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType0
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiple VALUES
specifier|final
name|String
name|sql1
init|=
literal|"insert into empnullables\n"
operator|+
literal|"values (?, 'Pat', 'Tailor'), (2, ?, ?),\n"
operator|+
literal|" (3, 'Tod', ?), (4, 'Arthur', null)"
decl_stmt|;
specifier|final
name|String
name|expectedType1
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1, VARCHAR(10) ?2, "
operator|+
literal|"VARCHAR(10) ?3)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType1
argument_list|)
argument_list|)
expr_stmt|;
comment|// VALUES with expression
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empnullables values (? + 1, ?)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// SELECT
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empnullables select ?, ? from (values (1))"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// WITH
specifier|final
name|String
name|sql3
init|=
literal|"insert into empnullables\n"
operator|+
literal|"with v as (values ('a'))\n"
operator|+
literal|"select ?, ? from (values (1))"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// UNION
specifier|final
name|String
name|sql2
init|=
literal|"insert into empnullables\n"
operator|+
literal|"select ?, ? from (values (1))\n"
operator|+
literal|"union all\n"
operator|+
literal|"select ?, ? from (values (time '1:2:3'))"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1,"
operator|+
literal|" INTEGER ?2, VARCHAR(20) ?3)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expected2
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW (mgr, empno, ename)"
operator|+
literal|" values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expectedType
init|=
literal|"RecordType(INTEGER ?0, INTEGER ?1, VARCHAR(20) ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expectedType
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertModifiableViewPassConstraint
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2 (deptno, empno, ename, extra)"
operator|+
literal|" values (20, 100, 'Lex', true)"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2 (empno, ename, extra)"
operator|+
literal|" values (100, 'Lex', true)"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2 values ('Edward', 20)"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertModifiableViewFailConstraint
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2 (deptno, empno, ename)"
operator|+
literal|" values (^21^, 100, 'Lex')"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2 (deptno, empno, ename)"
operator|+
literal|" values (^19+1^, 100, 'Lex')"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW2\n"
operator|+
literal|"values ('Arthur', 1, 'Knight', ^27^, false, 99999, true,"
operator|+
literal|"timestamp '1370-01-01 00:00:00', 1, 100)"
decl_stmt|;
specifier|final
name|String
name|error2
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|error2
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateModifiableViewPassConstraint
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2"
operator|+
literal|" set deptno = 20, empno = 99"
operator|+
literal|" where ename = 'Lex'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2"
operator|+
literal|" set empno = 99"
operator|+
literal|" where ename = 'Lex'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateModifiableViewFailConstraint
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"update EMP_MODIFIABLEVIEW2"
operator|+
literal|" set deptno = ^21^, empno = 99"
operator|+
literal|" where ename = 'Lex'"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"update EMP_MODIFIABLEVIEW2"
operator|+
literal|" set deptno = ^19 + 1^, empno = 99"
operator|+
literal|" where ename = 'Lex'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertTargetTableWithVirtualColumns
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into VIRTUALCOLUMNS.VC_T1\n"
operator|+
literal|"select a, b, c from VIRTUALCOLUMNS.VC_T2"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into ^VIRTUALCOLUMNS.VC_T1^\n"
operator|+
literal|"values(1, 2, 'abc', 3, 4)"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot INSERT into generated column 'D'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into ^VIRTUALCOLUMNS.VC_T1^\n"
operator|+
literal|"values(1, 2, 'abc', DEFAULT, DEFAULT)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into ^VIRTUALCOLUMNS.VC_T1^\n"
operator|+
literal|"values(1, 2, 'abc', DEFAULT)"
decl_stmt|;
specifier|final
name|String
name|error2
init|=
literal|"(?s).*Number of INSERT target columns \\(5\\) "
operator|+
literal|"does not equal number of source items \\(4\\).*"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|error2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"insert into ^VIRTUALCOLUMNS.VC_T1^\n"
operator|+
literal|"values(1, 2, 'abc', DEFAULT, DEFAULT, DEFAULT)"
decl_stmt|;
specifier|final
name|String
name|error3
init|=
literal|"(?s).*Number of INSERT target columns \\(5\\) "
operator|+
literal|"does not equal number of source items \\(6\\).*"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
name|error3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"insert into VIRTUALCOLUMNS.VC_T1\n"
operator|+
literal|"^values(1, '2', 'abc')^"
decl_stmt|;
specifier|final
name|String
name|error4
init|=
literal|"(?s).*Cannot assign to target field 'B' of type BIGINT "
operator|+
literal|"from source field 'EXPR\\$1' of type CHAR\\(1\\).*"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error4
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertFailNullability
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp^ (ename) values ('Kevin')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp^ (empno) values (10)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emp (empno, ename, deptno) ^values (5, null, 5)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetFailNullability
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into ^emp^ values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into emp ^values (null, 'Liam')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into emp ^values (45, null, 5)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertViewFailNullability
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp_20^ (ename) values ('Jake')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp_20^ (empno) values (9)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emp_20 (empno, ename, mgr) ^values (5, null, 5)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetViewFailNullability
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into ^EMP_20^ values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_20 ^values (null, 'Liam')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_20 ^values (45, null)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindFailNullability
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp^ (ename) values (?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp^ (empno) values (?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emp (empno, ename, deptno) ^values (?, null, 5)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindSubsetFailNullability
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into ^emp^ values (?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into emp ^values (null, ?)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into emp ^values (?, null)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetDisallowed
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp^ values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp^ values (null)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp^ values (1, 'Kevin')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(2\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetViewDisallowed
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp_20^ values (1)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(8\\) does not equal "
operator|+
literal|"number of source items \\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp_20^ values (null)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(8\\) does not equal "
operator|+
literal|"number of source items \\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp_20^ values (?, ?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(8\\) does not equal "
operator|+
literal|"number of source items \\(2\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindSubsetDisallowed
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into ^emp^ values (?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^emp^ values (?, ?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(2\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectExtendedColumnDuplicate
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select deptno, extra from emp (extra int, \"extra\" boolean)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, extra from emp (extra int, \"extra\" int)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, extra from emp (extra int, ^extra^ int)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select deptno, extra from emp (extra int, ^extra^ boolean)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select deptno, extra\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW (extra int, ^extra^ int)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select deptno, extra from EMP_MODIFIABLEVIEW"
operator|+
literal|" (extra int, ^extra^ boolean)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewFailExcludedColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^deptno^, empno from EMP_MODIFIABLEVIEW"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Column 'DEPTNO' not found in any table"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (SAL int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, \"Sal\", HIREDATE, MGR\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (\"Sal\" VARCHAR)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnExtendedCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, EXTRA\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW2 extend (EXTRA boolean)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, EXTRA,"
operator|+
literal|" \"EXtra\"\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW2 extend (\"EXtra\" VARCHAR)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnUnderlyingCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (COMM int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, \"comM\"\n"
operator|+
literal|" from EMP_MODIFIABLEVIEW3 extend (\"comM\" BOOLEAN)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectExtendedColumnCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMPDEFAULTS extend (COMM int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM, \"ComM\"\n"
operator|+
literal|" from EMPDEFAULTS extend (\"ComM\" int)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectExtendedColumnFailCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMPDEFAULTS extend (^COMM^ boolean)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'COMM' of type INTEGER "
operator|+
literal|"from source field 'COMM' of type BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMPDEFAULTS extend (^EMPNO^ integer)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EMPNO' of type INTEGER NOT NULL "
operator|+
literal|"from source field 'EMPNO' of type INTEGER"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE, MGR, COMM\n"
operator|+
literal|" from EMPDEFAULTS extend (^\"EMPNO\"^ integer)\n"
operator|+
literal|" where SAL = 20"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EMPNO' of type INTEGER NOT NULL "
operator|+
literal|"from source field 'EMPNO' of type INTEGER"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnFailCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, EXTRA\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW2 extend (^SLACKER^ integer)\n"
operator|+
literal|" where SAL = 20"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot assign to target field 'SLACKER' of type"
operator|+
literal|" BOOLEAN from source field 'SLACKER' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, COMM\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW2 extend (^EMPNO^ integer)\n"
operator|+
literal|" where SAL = 20"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Cannot assign to target field 'EMPNO' of type"
operator|+
literal|" INTEGER NOT NULL from source field 'EMPNO' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnFailExtendedCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, EXTRA\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW2 extend (^EXTRA^ integer)\n"
operator|+
literal|" where SAL = 20"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'EXTRA' of type"
operator|+
literal|" BOOLEAN from source field 'EXTRA' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, EXTRA\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW2 extend (^\"EXTRA\"^ integer)\n"
operator|+
literal|" where SAL = 20"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectViewExtendedColumnFailUnderlyingCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, COMM\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW3 extend (^COMM^ boolean)\n"
operator|+
literal|"where SAL = 20"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'COMM' of type INTEGER"
operator|+
literal|" from source field 'COMM' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"select ENAME, EMPNO, JOB, SLACKER, SAL, HIREDATE,"
operator|+
literal|" MGR, COMM\n"
operator|+
literal|"from EMP_MODIFIABLEVIEW3 extend (^\"COMM\"^ boolean)\n"
operator|+
literal|" where SAL = 20"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectFailCaseSensitivity
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ^\"empno\"^, ename, deptno from EMP"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empno' not found in any table; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^\"extra\"^, ename, deptno from EMP (extra boolean)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'extra' not found in any table; did you mean 'EXTRA'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^extra^, ename, deptno from EMP (\"extra\" boolean)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EXTRA' not found in any table; did you mean 'extra'\\?"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertFailCaseSensitivity
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW"
operator|+
literal|" (^\"empno\"^, ename, deptno)"
operator|+
literal|" values (45, 'Jake', 5)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'empno'"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW (\"extra\" int)"
operator|+
literal|" (^extra^, ename, deptno)"
operator|+
literal|" values (45, 'Jake', 5)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'EXTRA'"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW (extra int)"
operator|+
literal|" (^\"extra\"^, ename, deptno)"
operator|+
literal|" values (45, 'Jake', 5)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'extra'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertFailExcludedColumn
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|""
operator|+
literal|"insert into EMP_MODIFIABLEVIEW (empno, ename, ^deptno^)"
operator|+
literal|" values (45, 'Jake', 5)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'DEPTNO'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindViewFailExcludedColumn
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW (empno, ename, ^deptno^)"
operator|+
literal|" values (?, ?, ?)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'DEPTNO'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertWithCustomInitializerExpressionFactory
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empdefaults (deptno) values (1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empdefaults (ename, empno) values ('Quan', 50)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empdefaults (ename, deptno) ^values (null, 1)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^empdefaults^ values (null, 'Tod')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(2\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertSubsetWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults values (101)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults values (101, 'Coral')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults ^values (null, 'Tod')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults ^values (78, null)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindWithCustomInitializerExpressionFactory
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empdefaults (deptno) values (?)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(INTEGER ?0)"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empdefaults (ename, empno) values (?, ?)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1)"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empdefaults (ename, deptno) ^values (null, ?)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'ENAME' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into ^empdefaults^ values (null, ?)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Number of INSERT target columns \\(9\\) does not equal "
operator|+
literal|"number of source items \\(2\\)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindSubsetWithCustomInitializerExpressionFactory
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults values (101, ?)"
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0)"
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into empdefaults ^values (null, ?)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'EMPNO' has no default value and does not allow NULLs"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindWithCustomColumnResolving
parameter_list|()
block|{
specifier|final
name|SqlConformanceEnum
name|pragmatic
init|=
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"insert into struct.t\n"
operator|+
literal|"values (?, ?, ?, ?, ?, ?, ?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"RecordType(VARCHAR(20) ?0, VARCHAR(20) ?1,"
operator|+
literal|" INTEGER ?2, BOOLEAN ?3, INTEGER ?4, INTEGER ?5, INTEGER ?6,"
operator|+
literal|" INTEGER ?7, INTEGER ?8)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into struct.t_nullables (c0, c2, c1) values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expected2
init|=
literal|"RecordType(INTEGER ?0, INTEGER ?1, VARCHAR(20) ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expected2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"insert into struct.t_nullables (f1.c0, f1.c2, f0.c1) values (?, ?, ?)"
decl_stmt|;
specifier|final
name|String
name|expected3
init|=
literal|"RecordType(INTEGER ?0, INTEGER ?1, INTEGER ?2)"
decl_stmt|;
name|sql
argument_list|(
name|sql3
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
name|expected3
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into struct.t_nullables (c0, ^c4^, c1) values (?, ?, ?)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'C4'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into struct.t_nullables (^a0^, c2, c1) values (?, ?, ?)"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'A0'"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"insert into struct.t_nullables (\n"
operator|+
literal|"  f1.c0, ^f0.a0^, f0.c1) values (?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql4
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'F0.A0'"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql5
init|=
literal|"insert into struct.t_nullables (\n"
operator|+
literal|"  f1.c0, f1.c2, ^f1.c0^) values (?, ?, ?)"
decl_stmt|;
name|sql
argument_list|(
name|sql5
argument_list|)
operator|.
name|withConformance
argument_list|(
name|pragmatic
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Target column '\"F1\".\"C0\"' is assigned more than once"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateBind
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set ename = ?\n"
operator|+
literal|"where deptno = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(VARCHAR(20) ?0, INTEGER ?1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteBind
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emp\n"
operator|+
literal|"where deptno = ?\n"
operator|+
literal|"or ename = ?"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
operator|.
name|assertBindType
argument_list|(
name|is
argument_list|(
literal|"RecordType(INTEGER ?0, VARCHAR(20) ?1)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStream
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream * from orders"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from ^emp^"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToStream
argument_list|(
literal|"EMP"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ^orders^"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToRelation
argument_list|(
literal|"ORDERS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamWhere
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream * from orders where productId< 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from ^emp^ where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToStream
argument_list|(
literal|"EMP"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from ^emp^ as e where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToStream
argument_list|(
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from (^select * from emp as e1^) as e\n"
operator|+
literal|"where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot convert table 'E' to stream"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from ^orders^ where productId> 10"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToRelation
argument_list|(
literal|"ORDERS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamGroupBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream rowtime, productId, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by productId, rowtime"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream floor(rowtime to hour) as rowtime, productId,\n"
operator|+
literal|" count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by floor(rowtime to hour), productId"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream productId, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"^group by productId^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream ^count(*)^ as c\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream count(*) as c\n"
operator|+
literal|"from orders ^group by ()^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamHaving
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream rowtime, productId, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by productId, rowtime\n"
operator|+
literal|"having count(*)> 5"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream floor(rowtime to hour) as rowtime, productId,\n"
operator|+
literal|" count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by floor(rowtime to hour), productId\n"
operator|+
literal|"having false"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream productId, count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"^group by productId^\n"
operator|+
literal|"having count(*)> 5"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream 1\n"
operator|+
literal|"from orders\n"
operator|+
literal|"having ^count(*)> 3^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Tests that various expressions are monotonic. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testMonotonic
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream floor(rowtime to hour) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream ceil(rowtime to minute) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(minute from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream (rowtime - timestamp '1970-01-01 00:00:00') hour from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"cast((rowtime - timestamp '1970-01-01 00:00:00') hour as integer)\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"cast((rowtime - timestamp '1970-01-01 00:00:00') hour as integer) / 15\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"mod(cast((rowtime - timestamp '1970-01-01 00:00:00') hour as integer), 15)\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
argument_list|)
expr_stmt|;
comment|// constant
name|sql
argument_list|(
literal|"select stream 1 - 2 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream 1 + 2 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
comment|// extract(YEAR) is monotonic, extract(other time unit) is not
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(month from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
argument_list|)
expr_stmt|;
comment|//<monotonic> - constant
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) - 3 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) * 5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) * -5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|DECREASING
argument_list|)
argument_list|)
expr_stmt|;
comment|//<monotonic> / constant
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) / -5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|DECREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) / 5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) / 0 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
comment|// +inf is constant!
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) / null from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream null / extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) / cast(null as integer) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream cast(null as integer) / extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
comment|// constant /<monotonic> is not monotonic (we don't know whether sign of
comment|// expression ever changes)
name|sql
argument_list|(
literal|"select stream 5 / extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
argument_list|)
expr_stmt|;
comment|//<monotonic> * constant
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) * -5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|DECREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) * 5 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream extract(year from rowtime) * 0 from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
comment|// 0 is constant!
comment|// constant *<monotonic>
name|sql
argument_list|(
literal|"select stream -5 * extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|DECREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream 5 * extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream 0 * extract(year from rowtime) from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|CONSTANT
argument_list|)
argument_list|)
expr_stmt|;
comment|//<monotonic> -<monotonic>
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"extract(year from rowtime) - extract(year from rowtime)\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
argument_list|)
expr_stmt|;
comment|//<monotonic> +<monotonic>
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"extract(year from rowtime) + extract(year from rowtime)\n"
operator|+
literal|"from orders"
argument_list|)
operator|.
name|assertMonotonicity
argument_list|(
name|is
argument_list|(
name|SqlMonotonicity
operator|.
name|INCREASING
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamUnionAll
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select orderId\n"
operator|+
literal|"from ^orders^\n"
operator|+
literal|"union all\n"
operator|+
literal|"select orderId\n"
operator|+
literal|"from shipments"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToRelation
argument_list|(
literal|"ORDERS"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream orderId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"union all\n"
operator|+
literal|"^select orderId\n"
operator|+
literal|"from shipments^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_SET_OP_INCONSISTENT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno\n"
operator|+
literal|"from emp\n"
operator|+
literal|"union all\n"
operator|+
literal|"^select stream orderId\n"
operator|+
literal|"from orders^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_SET_OP_INCONSISTENT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream orderId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"union all\n"
operator|+
literal|"select stream orderId\n"
operator|+
literal|"from shipments"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream rowtime, orderId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"union all\n"
operator|+
literal|"select stream rowtime, orderId\n"
operator|+
literal|"from shipments\n"
operator|+
literal|"order by rowtime"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamValues
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream * from (^values 1) as e^"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToStream
argument_list|(
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream * from (^values 1) as e (c)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotConvertToStream
argument_list|(
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream orderId from orders\n"
operator|+
literal|"union all\n"
operator|+
literal|"^values 1^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_SET_OP_INCONSISTENT
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"values 1, 2\n"
operator|+
literal|"union all\n"
operator|+
literal|"^select stream orderId from orders^\n"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_SET_OP_INCONSISTENT
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamOrderBy
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by rowtime"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by floor(rowtime to hour)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream floor(rowtime to minute), productId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by floor(rowtime to hour)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream floor(rowtime to minute), productId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by floor(rowtime to minute), productId desc"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by ^productId^, rowtime"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_ORDER_REQUIRES_MONO
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by ^rowtime desc^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_ORDER_REQUIRES_MONO
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select stream *\n"
operator|+
literal|"from orders\n"
operator|+
literal|"order by floor(rowtime to hour), rowtime desc"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|" orders.rowtime as rowtime, orders.orderId as orderId,\n"
operator|+
literal|" products.supplierId as supplierId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"join products on orders.productId = products.productId"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"^select stream *\n"
operator|+
literal|"from products\n"
operator|+
literal|"join suppliers on products.supplierId = suppliers.supplierId^"
argument_list|)
operator|.
name|fails
argument_list|(
name|cannotStreamResultsForNonStreamingInputs
argument_list|(
literal|"PRODUCTS, SUPPLIERS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDummy
parameter_list|()
block|{
comment|// (To debug individual statements, paste them into this method.)
name|expr
argument_list|(
literal|"true\n"
operator|+
literal|"or ^(date '1-2-3', date '1-2-3', date '1-2-3')\n"
operator|+
literal|"   overlaps (date '1-2-3', date '1-2-3')^\n"
operator|+
literal|"or false"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'OVERLAPS' to arguments of type .*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCustomColumnResolving
parameter_list|()
block|{
name|checkCustomColumnResolving
argument_list|(
literal|"T"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testCustomColumnResolvingWithView
parameter_list|()
block|{
name|checkCustomColumnResolving
argument_list|(
literal|"T_10"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
specifier|private
name|void
name|checkCustomColumnResolving
parameter_list|(
name|String
name|table
parameter_list|)
block|{
comment|// Table STRUCT.T is defined as: (
comment|//   K0 VARCHAR(20) NOT NULL,
comment|//   C1 VARCHAR(20) NOT NULL,
comment|//   RecordType:PEEK_FIELDS_DEFAULT(
comment|//     C0 INTEGER NOT NULL,
comment|//     C1 INTEGER NOT NULL) F0,
comment|//   RecordType:PEEK_FIELDS(
comment|//      C0 INTEGER,
comment|//      C2 INTEGER NOT NULL,
comment|//      A0 INTEGER NOT NULL) F1,
comment|//   RecordType:PEEK_FIELDS(
comment|//      C3 INTEGER NOT NULL,
comment|//      A0 BOOLEAN NOT NULL) F2)
comment|//
comment|// The labels 'PEEK_FIELDS_DEFAULT' and 'PEEK_FIELDS' mean that F0, F1 and
comment|// F2 can all be transparent. F0 has default struct priority; F1 and F2 have
comment|// lower priority.
name|sql
argument_list|(
literal|"select * from struct."
operator|+
name|table
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// Resolve K0 as top-level column K0.
name|sql
argument_list|(
literal|"select k0 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL K0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve C2 as secondary-level column F1.C2.
name|sql
argument_list|(
literal|"select c2 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve F1.C2 as fully qualified column F1.C2.
name|sql
argument_list|(
literal|"select f1.c2 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve C1 as top-level column C1 as opposed to F0.C1.
name|sql
argument_list|(
literal|"select c1 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve C0 as secondary-level column F0.C0 as opposed to F1.C0, since F0
comment|// has the default priority.
name|sql
argument_list|(
literal|"select c0 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve F1.C0 as fully qualified column F1.C0 (as evidenced by "INTEGER"
comment|// rather than "INTEGER NOT NULL")
name|sql
argument_list|(
literal|"select f1.c0 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER C0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Fail ambiguous column reference A0, since F1.A0 and F2.A0 both exist with
comment|// the same resolving priority.
name|sql
argument_list|(
literal|"select ^a0^ from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'A0' is ambiguous"
argument_list|)
expr_stmt|;
comment|// Resolve F2.A0 as fully qualified column F2.A0.
name|sql
argument_list|(
literal|"select f2.a0 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(BOOLEAN NOT NULL A0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve T0.K0 as top-level column K0, since T0 is recognized as the table
comment|// alias.
name|sql
argument_list|(
literal|"select t0.k0 from struct."
operator|+
name|table
operator|+
literal|" t0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL K0) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve T0.C2 as secondary-level column F1.C2, since T0 is recognized as
comment|// the table alias here.
name|sql
argument_list|(
literal|"select t0.c2 from struct."
operator|+
name|table
operator|+
literal|" t0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve F0.C2 as secondary-level column F1.C2, since F0 is recognized as
comment|// the table alias here.
name|sql
argument_list|(
literal|"select f0.c2 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve F0.C1 as top-level column C1 as opposed to F0.C1, since F0 is
comment|// recognized as the table alias here.
name|sql
argument_list|(
literal|"select f0.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve C1 as inner INTEGER column not top-level VARCHAR column.
name|sql
argument_list|(
literal|"select f0.f0.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve<table>.C1 as top-level column C1 as opposed to F0.C1, since<table> is
comment|// recognized as the table name.
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".c1 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Alias "f0" obscures table name "<table>"
name|sql
argument_list|(
literal|"select ^"
operator|+
name|table
operator|+
literal|"^.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table '"
operator|+
name|table
operator|+
literal|"' not found"
argument_list|)
expr_stmt|;
comment|// Resolve STRUCT.<table>.C1 as top-level column C1 as opposed to F0.C1, since
comment|// STRUCT.<table> is recognized as the schema and table name.
name|sql
argument_list|(
literal|"select struct."
operator|+
name|table
operator|+
literal|".c1 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(VARCHAR(20) NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Table alias "f0" obscures table name "STRUCT.<table>"
name|sql
argument_list|(
literal|"select ^struct."
operator|+
name|table
operator|+
literal|"^.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'STRUCT."
operator|+
name|table
operator|+
literal|"' not found"
argument_list|)
expr_stmt|;
comment|// Resolve F0.F0.C1 as secondary-level column F0.C1, since the first F0 is
comment|// recognized as the table alias here.
name|sql
argument_list|(
literal|"select f0.f0.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve<table>.F0.C1 as secondary-level column F0.C1, since<table> is
comment|// recognized as the table name.
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".f0.c1 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Table alias obscures
name|sql
argument_list|(
literal|"select ^"
operator|+
name|table
operator|+
literal|".f0^.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table '"
operator|+
name|table
operator|+
literal|".F0' not found"
argument_list|)
expr_stmt|;
comment|// Resolve STRUCT.<table>.F0.C1 as secondary-level column F0.C1, since
comment|// STRUCT.<table> is recognized as the schema and table name.
name|sql
argument_list|(
literal|"select struct."
operator|+
name|table
operator|+
literal|".f0.c1 from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL C1) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Table alias "f0" obscures table name "STRUCT.<table>"
name|sql
argument_list|(
literal|"select ^struct."
operator|+
name|table
operator|+
literal|".f0^.c1 from struct."
operator|+
name|table
operator|+
literal|" f0"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Table 'STRUCT."
operator|+
name|table
operator|+
literal|".F0' not found"
argument_list|)
expr_stmt|;
comment|// Resolve struct type F1 with wildcard.
name|sql
argument_list|(
literal|"select f1.* from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL A0, INTEGER C0,"
operator|+
literal|" INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Resolve struct type F1 with wildcard.
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".f1.* from struct."
operator|+
name|table
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(INTEGER NOT NULL A0, INTEGER C0,"
operator|+
literal|" INTEGER NOT NULL C2) NOT NULL"
argument_list|)
expr_stmt|;
comment|// Fail non-existent column B0.
name|sql
argument_list|(
literal|"select ^b0^ from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'B0' not found in any table"
argument_list|)
expr_stmt|;
comment|// A column family can only be referenced with a star expansion.
name|sql
argument_list|(
literal|"select ^f1^ from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'F1' not found in any table"
argument_list|)
expr_stmt|;
comment|// If we fail to find a column, give an error based on the shortest prefix
comment|// that fails.
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".^f0.notFound^.a.b.c.d from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'F0\\.NOTFOUND' not found in table '"
operator|+
name|table
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".^f0.notFound^ from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'F0\\.NOTFOUND' not found in table '"
operator|+
name|table
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select "
operator|+
name|table
operator|+
literal|".^f0.c1.notFound^ from struct."
operator|+
name|table
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'F0\\.C1\\.NOTFOUND' not found in table '"
operator|+
name|table
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDescriptor
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(tumble(table orders, descriptor(rowtime), interval '2' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(tumble(table orders, descriptor(^column_not_exist^), "
operator|+
literal|"interval '2' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown identifier 'COLUMN_NOT_EXIST'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testTumbleTableFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end' from table(\n"
operator|+
literal|"tumble(table orders, descriptor(rowtime), interval '2' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end' from table(\n"
operator|+
literal|"tumble(table orders, descriptor(rowtime), interval '2' hour, interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test named params.
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"tumble(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"size => interval '2' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"tumble(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"size => interval '2' hour,\n"
operator|+
literal|"\"OFFSET\" => interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// negative tests.
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"^tumble(table orders, descriptor(productid), interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TUMBLE' to arguments of type 'TUMBLE\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>\\)'\\. Supported form\\(s\\): TUMBLE\\(TABLE table_name, "
operator|+
literal|"DESCRIPTOR\\(timecol\\), datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"^tumble(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(productid),\n"
operator|+
literal|"size => interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TUMBLE' to arguments of type 'TUMBLE\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>\\)'\\. Supported form\\(s\\): TUMBLE\\(TABLE table_name, "
operator|+
literal|"DESCRIPTOR\\(timecol\\), datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"tumble(\n"
operator|+
literal|"^\"data\"^ => table orders,\n"
operator|+
literal|"TIMECOL => descriptor(rowtime),\n"
operator|+
literal|"SIZE => interval '2' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Param 'data' not found in function 'TUMBLE'; did you mean 'DATA'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"^tumble(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"SIZE => interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TUMBLE'. Was expecting 3 arguments"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^tumble(table orders, descriptor(rowtime))^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'TUMBLE'. Was expecting 3 arguments"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^tumble(table orders, descriptor(rowtime), 'test')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TUMBLE' to arguments of type 'TUMBLE\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,"
operator|+
literal|"<CHAR\\(4\\)>\\)'\\. Supported form\\(s\\): TUMBLE\\(TABLE "
operator|+
literal|"table_name, DESCRIPTOR\\(timecol\\), datetime interval"
operator|+
literal|"\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^tumble(table orders, 'test', interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TUMBLE' to arguments of type 'TUMBLE\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<CHAR\\"
operator|+
literal|"(4\\)>,<INTERVAL HOUR>\\)'\\. Supported form\\(s\\): TUMBLE\\(TABLE "
operator|+
literal|"table_name, DESCRIPTOR\\(timecol\\), datetime interval"
operator|+
literal|"\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end' from table(\n"
operator|+
literal|"^tumble(table orders, descriptor(rowtime), interval '2' hour, 'test')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'TUMBLE' to arguments of type 'TUMBLE\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,"
operator|+
literal|"<INTERVAL HOUR>,<CHAR\\(4\\)>\\)'\\. Supported form\\(s\\): TUMBLE\\(TABLE "
operator|+
literal|"table_name, DESCRIPTOR\\(timecol\\), datetime interval"
operator|+
literal|"\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"tumble(TABLE ^tabler_not_exist^, descriptor(rowtime), interval '2' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'TABLER_NOT_EXIST' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testHopTableFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(table orders, descriptor(rowtime), interval '2' hour, interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(table orders, descriptor(rowtime), interval '2' hour, interval '1' hour, "
operator|+
literal|"interval '20' minute))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test named params.
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"slide => interval '2' hour,\n"
operator|+
literal|"size => interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"slide => interval '2' hour,\n"
operator|+
literal|"size => interval '1' hour,\n"
operator|+
literal|"\"OFFSET\" => interval '20' minute))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// negative tests.
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"^hop(table orders, descriptor(productid), interval '2' hour, interval '1' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>,<INTERVAL HOUR>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"HOP\\(TABLE table_name, DESCRIPTOR\\(timecol\\), "
operator|+
literal|"datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select rowtime, productid, orderid, 'window_start', 'window_end'\n"
operator|+
literal|"from table(\n"
operator|+
literal|"^hop(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(productid),\n"
operator|+
literal|"size => interval '2' hour,\n"
operator|+
literal|"slide => interval '1' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\"
operator|+
literal|"(TIMESTAMP\\(0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>,<INTERVAL HOUR>\\)'\\. Supported form\\(s\\): "
operator|+
literal|"HOP\\(TABLE table_name, DESCRIPTOR\\(timecol\\), "
operator|+
literal|"datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(\n"
operator|+
literal|"^\"data\"^ => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"slide => interval '2' hour,\n"
operator|+
literal|"size => interval '1' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Param 'data' not found in function 'HOP'; did you mean 'DATA'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"slide => interval '2' hour,\n"
operator|+
literal|"size => interval '1' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'HOP'. Was expecting 4 arguments"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(table orders, descriptor(rowtime), interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Invalid number of arguments to function 'HOP'. Was expecting 4 arguments"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(table orders, descriptor(rowtime), interval '2' hour, 'test')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\(TIMESTAMP\\(0\\) "
operator|+
literal|"ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,<INTERVAL HOUR>, "
operator|+
literal|"<CHAR\\(4\\)>\\)'. Supported form\\(s\\): HOP\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(table orders, descriptor(rowtime), 'test', interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\(TIMESTAMP\\(0\\) "
operator|+
literal|"ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,<CHAR\\(4\\)>, "
operator|+
literal|"<INTERVAL HOUR>\\)'. Supported form\\(s\\): HOP\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(table orders, 'test', interval '2' hour, interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\(TIMESTAMP\\(0\\) "
operator|+
literal|"ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<CHAR\\(4\\)>,<INTERVAL HOUR>, "
operator|+
literal|"<INTERVAL HOUR>\\)'. Supported form\\(s\\): HOP\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^hop(table orders, descriptor(rowtime), interval '2' hour, interval '1' hour, 'test')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'HOP' to arguments of type 'HOP\\(<RECORDTYPE\\(TIMESTAMP\\(0\\) "
operator|+
literal|"ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,<INTERVAL HOUR>, "
operator|+
literal|"<INTERVAL HOUR>,<CHAR\\(4\\)>\\)'. Supported form\\(s\\): HOP\\(TABLE table_name, "
operator|+
literal|"DESCRIPTOR\\(timecol\\), datetime interval, datetime interval\\[, datetime interval\\]\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"hop(TABLE ^tabler_not_exist^, descriptor(rowtime), interval '2' hour, interval '1' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'TABLER_NOT_EXIST' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSessionTableFunction
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(table orders, descriptor(rowtime), descriptor(productid), interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test without key descriptor
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(table orders, descriptor(rowtime), interval '2' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// test named params.
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"key => descriptor(productid),\n"
operator|+
literal|"size => interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"size => interval '1' hour))"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// negative tests.
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^session(\n"
operator|+
literal|"data => table orders,\n"
operator|+
literal|"key => descriptor(productid),\n"
operator|+
literal|"size => interval '1' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'SESSION' to arguments of type 'SESSION\\(<RECORDTYPE\\(TIMESTAMP\\("
operator|+
literal|"0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>\\)'. Supported form\\(s\\): SESSION\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), DESCRIPTOR\\(key\\) optional, datetime interval\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(\n"
operator|+
literal|"^\"data\"^ => table orders,\n"
operator|+
literal|"timecol => descriptor(rowtime),\n"
operator|+
literal|"key => descriptor(productid),\n"
operator|+
literal|"size => interval '1' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Param 'data' not found in function 'SESSION'; did you mean 'DATA'\\?"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^session(table orders, descriptor(rowtime), descriptor(productid), 'test')^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'SESSION' to arguments of type 'SESSION\\(<RECORDTYPE\\(TIMESTAMP\\("
operator|+
literal|"0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,<COLUMN_LIST>, "
operator|+
literal|"<CHAR\\(4\\)>\\)'. Supported form\\(s\\): SESSION\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), DESCRIPTOR\\(key\\) optional, datetime interval\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^session(table orders, descriptor(rowtime), 'test', interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'SESSION' to arguments of type 'SESSION\\(<RECORDTYPE\\(TIMESTAMP\\("
operator|+
literal|"0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<COLUMN_LIST>,<CHAR\\(4\\)>, "
operator|+
literal|"<INTERVAL HOUR>\\)'. Supported form\\(s\\): SESSION\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), DESCRIPTOR\\(key\\) optional, datetime interval\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"^session(table orders, 'test', descriptor(productid), interval '2' hour)^)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot apply 'SESSION' to arguments of type 'SESSION\\(<RECORDTYPE\\(TIMESTAMP\\("
operator|+
literal|"0\\) ROWTIME, INTEGER PRODUCTID, INTEGER ORDERID\\)>,<CHAR\\(4\\)>,<COLUMN_LIST>, "
operator|+
literal|"<INTERVAL HOUR>\\)'. Supported form\\(s\\): SESSION\\(TABLE table_name, DESCRIPTOR\\("
operator|+
literal|"timecol\\), DESCRIPTOR\\(key\\) optional, datetime interval\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from table(\n"
operator|+
literal|"session(TABLE ^tabler_not_exist^, descriptor(rowtime), descriptor(productid), interval '1' hour))"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Object 'TABLER_NOT_EXIST' not found"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamTumble
parameter_list|()
block|{
comment|// TUMBLE
name|sql
argument_list|(
literal|"select stream tumble_end(rowtime, interval '2' hour) as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour), productId"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream ^tumble(rowtime, interval '2' hour)^ as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour), productId"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Group function '\\$TUMBLE' can only appear in GROUP BY clause"
argument_list|)
expr_stmt|;
comment|// TUMBLE with align argument
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  tumble_end(rowtime, interval '2' hour, time '00:12:00') as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour, time '00:12:00')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
comment|// TUMBLE_END without corresponding TUMBLE
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  ^tumble_end(rowtime, interval '2' hour, time '00:13:00')^ as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by floor(rowtime to hour)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Call to auxiliary group function 'TUMBLE_END' must have "
operator|+
literal|"matching call to group function '\\$TUMBLE' in GROUP BY clause"
argument_list|)
expr_stmt|;
comment|// Arguments to TUMBLE_END are slightly different to arguments to TUMBLE
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  ^tumble_start(rowtime, interval '2' hour, time '00:13:00')^ as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour, time '00:12:00')"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Call to auxiliary group function 'TUMBLE_START' must have "
operator|+
literal|"matching call to group function '\\$TUMBLE' in GROUP BY clause"
argument_list|)
expr_stmt|;
comment|// Even though align defaults to TIME '00:00:00', we need structural
comment|// equivalence, not semantic equivalence.
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  ^tumble_end(rowtime, interval '2' hour, time '00:00:00')^ as rowtime\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Call to auxiliary group function 'TUMBLE_END' must have "
operator|+
literal|"matching call to group function '\\$TUMBLE' in GROUP BY clause"
argument_list|)
expr_stmt|;
comment|// TUMBLE query produces no monotonic column - OK
name|sql
argument_list|(
literal|"select stream productId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by tumble(rowtime, interval '2' hour), productId"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream productId\n"
operator|+
literal|"from orders\n"
operator|+
literal|"^group by productId,\n"
operator|+
literal|"  tumble(timestamp '1990-03-04 12:34:56', interval '2' hour)^"
argument_list|)
operator|.
name|fails
argument_list|(
name|STR_AGG_REQUIRES_MONO
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamHop
parameter_list|()
block|{
comment|// HOP
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  hop_start(rowtime, interval '1' hour, interval '3' hour) as rowtime,\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by hop(rowtime, interval '1' hour, interval '3' hour)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  ^hop_start(rowtime, interval '1' hour, interval '2' hour)^,\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by hop(rowtime, interval '1' hour, interval '3' hour)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Call to auxiliary group function 'HOP_START' must have "
operator|+
literal|"matching call to group function '\\$HOP' in GROUP BY clause"
argument_list|)
expr_stmt|;
comment|// HOP with align
name|sql
argument_list|(
literal|"select stream\n"
operator|+
literal|"  hop_start(rowtime, interval '1' hour, interval '3' hour,\n"
operator|+
literal|"    time '12:34:56') as rowtime,\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by hop(rowtime, interval '1' hour, interval '3' hour,\n"
operator|+
literal|"    time '12:34:56')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testStreamSession
parameter_list|()
block|{
comment|// SESSION
name|sql
argument_list|(
literal|"select stream session_start(rowtime, interval '1' hour) as rowtime,\n"
operator|+
literal|"  session_end(rowtime, interval '1' hour),\n"
operator|+
literal|"  count(*) as c\n"
operator|+
literal|"from orders\n"
operator|+
literal|"group by session(rowtime, interval '1' hour)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empdefaults(extra BOOLEAN, note VARCHAR)"
operator|+
literal|" (deptno, empno, ename, extra, note) values (1, 10, '2', true, 'ok')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emp(\"rank\" INT, extra BOOLEAN)"
operator|+
literal|" values (1, 'nom', 'job', 0, timestamp '1970-01-01 00:00:00', 1, 1,"
operator|+
literal|"  1, false, 100, false)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindExtendedColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empdefaults(extra BOOLEAN, note VARCHAR)"
operator|+
literal|" (deptno, empno, ename, extra, note) values (1, 10, '2', ?, 'ok')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into emp(\"rank\" INT, extra BOOLEAN)"
operator|+
literal|" values (1, 'nom', 'job', 0, timestamp '1970-01-01 00:00:00', 1, 1,"
operator|+
literal|"  1, false, ?, ?)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extra2 BOOLEAN,"
operator|+
literal|" note VARCHAR) (deptno, empno, ename, extra2, note)\n"
operator|+
literal|"values (20, 10, '2', true, 'ok')"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"rank\" INT,"
operator|+
literal|" extra2 BOOLEAN)\n"
operator|+
literal|"values ('nom', 1, 'job', 20, true, 0, false,"
operator|+
literal|" timestamp '1970-01-01 00:00:00', 1, 1,  1, false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2(extra2 BOOLEAN, note VARCHAR)"
operator|+
literal|" (deptno, empno, ename, extra2, note) values (20, 10, '2', true, ?)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"insert into EMP_MODIFIABLEVIEW2(\"rank\" INT, extra2 BOOLEAN)"
operator|+
literal|" values ('nom', 1, 'job', 20, true, 0, false, timestamp '1970-01-01 00:00:00', 1, 1,"
operator|+
literal|"  ?, false)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewFailConstraint
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extra2 BOOLEAN,"
operator|+
literal|" note VARCHAR) (deptno, empno, ename, extra2, note)\n"
operator|+
literal|"values (^1^, 10, '2', true, 'ok')"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extra2 BOOLEAN,"
operator|+
literal|" note VARCHAR) (deptno, empno, ename, extra2, note)\n"
operator|+
literal|"values (^?^, 10, '2', true, 'ok')"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"rank\" INT,"
operator|+
literal|" extra2 BOOLEAN)\n"
operator|+
literal|"values ('nom', 1, 'job', ^0^, true, 0, false,"
operator|+
literal|" timestamp '1970-01-01 00:00:00', 1, 1,  1, false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewFailColumnCount
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into ^EMP_MODIFIABLEVIEW2(\"rank\" INT, extra2 BOOLEAN)^"
operator|+
literal|" values ('nom', 1, 'job', 0, true, 0, false,"
operator|+
literal|" timestamp '1970-01-01 00:00:00', 1, 1,  1)"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Number of INSERT target columns \\(12\\) does not"
operator|+
literal|" equal number of source items \\(11\\)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into ^EMP_MODIFIABLEVIEW2(\"rank\" INT, extra2 BOOLEAN)^"
operator|+
literal|" (deptno, empno, ename, extra2, \"rank\") values (?, 10, '2', true)"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Number of INSERT target columns \\(5\\) does not"
operator|+
literal|" equal number of source items \\(4\\)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnFailDuplicate
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extcol INT,"
operator|+
literal|" ^extcol^ BOOLEAN)\n"
operator|+
literal|"values ('nom', 1, 'job', 0, true, 0, false,"
operator|+
literal|" timestamp '1970-01-01 00:00:00', 1, 1,  1)"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Duplicate name 'EXTCOL' in column list"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extcol INT,"
operator|+
literal|" ^extcol^ BOOLEAN) (extcol) values (1)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(extcol INT,"
operator|+
literal|" ^extcol^ BOOLEAN) (extcol) values (false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"insert into EMP(extcol INT, ^extcol^ BOOLEAN)"
operator|+
literal|" (extcol) values (1)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"insert into EMP(extcol INT, ^extcol^ BOOLEAN)"
operator|+
literal|" (extcol) values (false)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumn
parameter_list|()
block|{
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
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(extra BOOLEAN)"
operator|+
literal|" set extra = true, deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(\"empNo\" VARCHAR)"
operator|+
literal|" set \"empNo\" = '5', deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertFailDataType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into empnullables ^values ('5', 'bob')^"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EMPNO' of type INTEGER"
operator|+
literal|" from source field 'EXPR\\$0' of type CHAR\\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables values ('5', 'bob')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (^empno^, ename) values ('5', 'bob')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EMPNO' of type INTEGER"
operator|+
literal|" from source field 'EXPR\\$0' of type CHAR\\(1\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables (empno, ename) values ('5', 'bob')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables(extra BOOLEAN)"
operator|+
literal|" (empno, ename, ^extra^) values (5, 'bob', 'true')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EXTRA' of type BOOLEAN"
operator|+
literal|" from source field 'EXPR\\$2' of type CHAR\\(4\\)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into empnullables(extra BOOLEAN)"
operator|+
literal|" (empno, ename, ^extra^) values (5, 'bob', 'true')"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-1727"
argument_list|)
annotation|@
name|Test
name|void
name|testUpdateFailDataType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update emp"
operator|+
literal|" set ^empNo^ = '5', deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EMPNO' of type INTEGER"
operator|+
literal|" from source field 'EXPR$0' of type CHAR(1)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"update emp(extra boolean)"
operator|+
literal|" set ^extra^ = '5', deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'EXTRA' of type BOOLEAN"
operator|+
literal|" from source field 'EXPR$0' of type CHAR(1)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-1727"
argument_list|)
annotation|@
name|Test
name|void
name|testUpdateFailCaseSensitivity
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults"
operator|+
literal|" set empNo = '5', deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'empno' not found in any table; did you mean 'EMPNO'\\?"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnFailCaseSensitivity
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults(\"extra\" BOOLEAN)"
operator|+
literal|" set ^extra^ = true, deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'EXTRA'"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(extra BOOLEAN)"
operator|+
literal|" set ^\"extra\"^ = true, deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Unknown target column 'extra'"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateBindExtendedColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults(extra BOOLEAN, note VARCHAR)"
operator|+
literal|" set deptno = 1, extra = true, empno = 20, ename = 'Bob', note = ?"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"update empdefaults(extra BOOLEAN)"
operator|+
literal|" set extra = ?, deptno = 1, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN, note VARCHAR)"
operator|+
literal|" set deptno = 20, extra2 = true, empno = 20, ename = 'Bob', note = 'legion'"
operator|+
literal|" where ename = 'Jane'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN)"
operator|+
literal|" set extra2 = true, ename = 'Bob'"
operator|+
literal|" where ename = 'Jane'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateBindExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN, note VARCHAR)"
operator|+
literal|" set deptno = 20, extra2 = true, empno = 20, ename = 'Bob', note = ?"
operator|+
literal|" where ename = 'Jane'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN)"
operator|+
literal|" set extra2 = ?, ename = 'Bob'"
operator|+
literal|" where ename = 'Jane'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewFailConstraint
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN,"
operator|+
literal|" note VARCHAR)\n"
operator|+
literal|"set deptno = ^1^, extra2 = true, empno = 20, ename = 'Bob',"
operator|+
literal|" note = 'legion'\n"
operator|+
literal|"where ename = 'Jane'"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Modifiable view constraint is not satisfied"
operator|+
literal|" for column 'DEPTNO' of base table 'EMP_MODIFIABLEVIEW2'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"update EMP_MODIFIABLEVIEW2(extra2 BOOLEAN)"
operator|+
literal|" set extra2 = true, deptno = ^1^, ename = 'Bob'"
operator|+
literal|" where ename = 'Jane'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"update empdefaults(empno INTEGER NOT NULL, deptno INTEGER)"
operator|+
literal|" set deptno = 1, empno = 20, ename = 'Bob'"
operator|+
literal|" where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW3(empno INTEGER NOT NULL,"
operator|+
literal|" deptno INTEGER)\n"
operator|+
literal|"set deptno = 20, empno = 20, ename = 'Bob'\n"
operator|+
literal|"where empno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"update EMP_MODIFIABLEVIEW3(empno INTEGER NOT NULL,"
operator|+
literal|" \"deptno\" BOOLEAN)\n"
operator|+
literal|"set \"deptno\" = true, empno = 20, ename = 'Bob'\n"
operator|+
literal|"where empno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnFailCollision
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update empdefaults(^empno^ BOOLEAN, deptno INTEGER)\n"
operator|+
literal|"set deptno = 1, empno = false, ename = 'Bob'\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Cannot assign to target field 'EMPNO' of type "
operator|+
literal|"INTEGER NOT NULL from source field 'EMPNO' of type BOOLEAN"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Disabled
argument_list|(
literal|"CALCITE-1727"
argument_list|)
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnFailCollision2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update empdefaults(^\"deptno\"^ BOOLEAN)\n"
operator|+
literal|"set \"deptno\" = 1, empno = 1, ename = 'Bob'\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Cannot assign to target field 'deptno' of type "
operator|+
literal|"BOOLEAN NOT NULL from source field 'deptno' of type INTEGER"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewFailCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW3(^empno^ BOOLEAN,"
operator|+
literal|" deptno INTEGER)\n"
operator|+
literal|"set deptno = 1, empno = false, ename = 'Bob'\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'EMPNO' of type"
operator|+
literal|" INTEGER NOT NULL from source field 'EMPNO' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewFailExtendedCollision
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'EXTRA' of type"
operator|+
literal|" BOOLEAN from source field 'EXTRA' of type INTEGER"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW2(^extra^ INTEGER,"
operator|+
literal|" deptno INTEGER)\n"
operator|+
literal|"set deptno = 20, empno = 20, ename = 'Bob', extra = 5\n"
operator|+
literal|"where empno = 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnModifiableViewFailUnderlyingCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"update EMP_MODIFIABLEVIEW3(^comm^ BOOLEAN,"
operator|+
literal|" deptno INTEGER)\n"
operator|+
literal|"set deptno = 1, empno = 20, ename = 'Bob', comm = true\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'COMM' of type"
operator|+
literal|" INTEGER from source field 'COMM' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testUpdateExtendedColumnFailDuplicate
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"update emp(comm BOOLEAN, ^comm^ INTEGER)\n"
operator|+
literal|"set deptno = 1, empno = 20, ename = 'Bob', comm = 1\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Duplicate name 'COMM' in column list"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"update EMP_MODIFIABLEVIEW3(comm BOOLEAN,"
operator|+
literal|" ^comm^ INTEGER)\n"
operator|+
literal|"set deptno = 1, empno = 20, ename = 'Bob', comm = true\n"
operator|+
literal|"where deptno = 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(^comm^ INTEGER) (empno, ename, job, comm)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', 5)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewCollision
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW3(^sal^ INTEGER)\n"
operator|+
literal|" (empno, ename, job, sal)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', 5)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewExtendedCollision
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(^extra^ BOOLEAN)"
operator|+
literal|" (empno, ename, job, extra)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewUnderlyingCollision
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"insert into EMP_MODIFIABLEVIEW3(^comm^ INTEGER)\n"
operator|+
literal|" (empno, ename, job, comm)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', 5)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnFailCollision
parameter_list|()
block|{
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(^comm^ BOOLEAN)"
operator|+
literal|" (empno, ename, job, comm)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'COMM' of type INTEGER"
operator|+
literal|" from source field 'COMM' of type BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, ^comm^)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'COMM' of type INTEGER"
operator|+
literal|" from source field 'EXPR\\$3' of type BOOLEAN"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, comm)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, ^\"comm\"^)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', 1)"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Cannot assign to target field 'comm' of type BOOLEAN"
operator|+
literal|" from source field 'EXPR\\$3' of type INTEGER"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"insert into EMPDEFAULTS(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, \"comm\")\n"
operator|+
literal|"values (1, 'Arthur', 'clown', 1)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewFailCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(^slacker^ INTEGER)"
operator|+
literal|" (empno, ename, job, slacker) values (1, 'Arthur', 'clown', true)"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot assign to target field 'SLACKER' of type"
operator|+
literal|" BOOLEAN from source field 'SLACKER' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"slacker\" INTEGER)"
operator|+
literal|" (empno, ename, job, ^slacker^) values (1, 'Arthur', 'clown', 1)"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Cannot assign to target field 'SLACKER' of type"
operator|+
literal|" BOOLEAN from source field 'EXPR\\$3' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"slacker\" INTEGER)"
operator|+
literal|" (empno, ename, job, ^\"slacker\"^)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
decl_stmt|;
specifier|final
name|String
name|error2
init|=
literal|"Cannot assign to target field 'slacker' of type"
operator|+
literal|" INTEGER from source field 'EXPR\\$3' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error2
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewFailExtendedCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(^extra^ INTEGER)"
operator|+
literal|" (empno, ename, job, extra) values (1, 'Arthur', 'clown', true)"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot assign to target field 'EXTRA' of type"
operator|+
literal|" BOOLEAN from source field 'EXTRA' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"extra\" INTEGER)"
operator|+
literal|" (empno, ename, job, ^extra^) values (1, 'Arthur', 'clown', 1)"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Cannot assign to target field 'EXTRA' of type"
operator|+
literal|" BOOLEAN from source field 'EXPR\\$3' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"extra\" INTEGER)"
operator|+
literal|" (empno, ename, job, extra) values (1, 'Arthur', 'clown', 1)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"extra\" INTEGER)"
operator|+
literal|" (empno, ename, job, ^\"extra\"^)\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
decl_stmt|;
specifier|final
name|String
name|error3
init|=
literal|"Cannot assign to target field 'extra' of type"
operator|+
literal|" INTEGER from source field 'EXPR\\$3' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"insert into EMP_MODIFIABLEVIEW2(\"extra\" INTEGER)"
operator|+
literal|" (empno, ename, job, \"extra\")\n"
operator|+
literal|"values (1, 'Arthur', 'clown', true)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInsertExtendedColumnModifiableViewFailUnderlyingCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot assign to target field 'COMM' of type"
operator|+
literal|" INTEGER from source field 'COMM' of type BOOLEAN"
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"insert into EMP_MODIFIABLEVIEW3(^comm^ BOOLEAN)"
operator|+
literal|" (empno, ename, job, comm) values (1, 'Arthur', 'clown', true)"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"insert into EMP_MODIFIABLEVIEW3(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, ^comm^) values (1, 'Arthur', 'clown', 5)"
decl_stmt|;
specifier|final
name|String
name|error1
init|=
literal|"Unknown target column 'COMM'"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"insert into EMP_MODIFIABLEVIEW3(\"comm\" BOOLEAN)"
operator|+
literal|" (empno, ename, job, ^\"comm\"^) values (1, 'Arthur', 'clown', 1)"
decl_stmt|;
specifier|final
name|String
name|error2
init|=
literal|"Cannot assign to target field 'comm' of type"
operator|+
literal|" BOOLEAN from source field 'EXPR\\$3' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
name|error2
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDelete
parameter_list|()
block|{
name|sql
argument_list|(
literal|"delete from empdefaults where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"delete from empdefaults(extra BOOLEAN) where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"delete from empdefaults(extra BOOLEAN) where extra = false"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteBindExtendedColumn
parameter_list|()
block|{
name|sql
argument_list|(
literal|"delete from empdefaults(extra BOOLEAN) where deptno = ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"delete from empdefaults(extra BOOLEAN) where extra = ?"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW2 where deptno = 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW2 where deptno = 20"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW2 where empno = 30"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnModifiableView
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW2(extra BOOLEAN) where sal> 10"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW2(note BOOLEAN) where note = 'fired'"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnCollision
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"delete from emp(empno INTEGER NOT NULL) where sal> 10"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnModifiableViewCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"delete from EMP_MODIFIABLEVIEW2("
operator|+
literal|"empno INTEGER NOT NULL) where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(\"empno\" INTEGER)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(extra BOOLEAN)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql3
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(\"extra\" VARCHAR)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql3
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql4
init|=
literal|"delete from EMP_MODIFIABLEVIEW3(comm INTEGER)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql4
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
specifier|final
name|String
name|sql5
init|=
literal|"delete from EMP_MODIFIABLEVIEW3(\"comm\" BIGINT)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql5
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnFailCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(^empno^ BOOLEAN)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
specifier|final
name|String
name|error0
init|=
literal|"Cannot assign to target field 'EMPNO' of type"
operator|+
literal|" INTEGER NOT NULL from source field 'EMPNO' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(^empno^ INTEGER)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'EMPNO' of type"
operator|+
literal|" INTEGER NOT NULL from source field 'EMPNO' of type INTEGER"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"delete from EMP_MODIFIABLEVIEW2(^\"EMPNO\"^ INTEGER)"
operator|+
literal|" where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql2
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnModifiableViewFailCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"delete from EMP_MODIFIABLEVIEW(^deptno^ BOOLEAN)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'DEPTNO' of type"
operator|+
literal|" INTEGER from source field 'DEPTNO' of type BOOLEAN"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"delete from EMP_MODIFIABLEVIEW(^\"DEPTNO\"^ BOOLEAN)"
operator|+
literal|" where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnModifiableViewFailExtendedCollision
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
specifier|final
name|String
name|error
init|=
literal|"Cannot assign to target field 'SLACKER' of type"
operator|+
literal|" BOOLEAN from source field 'SLACKER' of type INTEGER"
decl_stmt|;
specifier|final
name|String
name|sql0
init|=
literal|"delete from EMP_MODIFIABLEVIEW(^slacker^ INTEGER)\n"
operator|+
literal|"where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql0
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql1
init|=
literal|"delete from EMP_MODIFIABLEVIEW(^\"SLACKER\"^ INTEGER)"
operator|+
literal|" where sal> 10"
decl_stmt|;
name|s
operator|.
name|withSql
argument_list|(
name|sql1
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testDeleteExtendedColumnFailDuplicate
parameter_list|()
block|{
specifier|final
name|SqlValidatorFixture
name|s
init|=
name|fixture
argument_list|()
operator|.
name|withExtendedCatalog
argument_list|()
decl_stmt|;
name|sql
argument_list|(
literal|"delete from emp (extra VARCHAR, ^extra^ VARCHAR)"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW (extra VARCHAR, ^extra^ VARCHAR)"
operator|+
literal|" where extra = 'test'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
name|s
operator|.
name|withSql
argument_list|(
literal|"delete from EMP_MODIFIABLEVIEW (extra VARCHAR, ^\"EXTRA\"^ VARCHAR)"
operator|+
literal|" where extra = 'test'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Duplicate name 'EXTRA' in column list"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1804">[CALCITE-1804]    * Cannot assign NOT NULL array to nullable array</a>. */
end_comment

begin_function
annotation|@
name|Test
name|void
name|testArrayAssignment
parameter_list|()
block|{
specifier|final
name|SqlTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigint
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigintNullable
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|bigint
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigintNotNull
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|bigint
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|date
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|dateNotNull
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|date
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|bigintNullable
argument_list|,
name|bigintNotNull
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
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|bigintNullable
argument_list|,
name|dateNotNull
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|bigintNullableArray
init|=
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|bigintNullable
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigintArrayNullable
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|bigintNullableArray
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigintNotNullArray
init|=
operator|new
name|ArraySqlType
argument_list|(
name|bigintNotNull
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|bigintArrayNullable
argument_list|,
name|bigintNotNullArray
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|dateNotNullArray
init|=
operator|new
name|ArraySqlType
argument_list|(
name|dateNotNull
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|bigintArrayNullable
argument_list|,
name|dateNotNullArray
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectRolledUpColumn
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in SELECT"
decl_stmt|;
name|sql
argument_list|(
literal|"select ^slackingmin^ from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select a from (select ^slackingmin^ from emp_r)"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^slackingmin^ as b from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, ^slackingmin^ from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select slackingmin from (select empno as slackingmin from emp_r)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^emp_r.slackingmin^ from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^sales.emp_r.slackingmin^ from sales.emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^sales.emp_r.slackingmin^ from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^catalog.sales.emp_r.slackingmin^ from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select (select ^slackingmin^ from emp_r), a from (select empno as a from emp_r)"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select (((^slackingmin^))) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^slackingmin^ from nest.emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"with emp_r as (select 1 as slackingmin) select slackingmin from emp_r"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with emp_r as (select ^slackingmin^ from emp_r) select slackingmin from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"with emp_r1 as (select 1 as slackingmin) select emp_r1.slackingmin from emp_r, emp_r1"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"with emp_r1 as (select 1 as slackingmin) select ^emp_r.slackingmin^ from emp_r, emp_r1"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testSelectAggregateOnRolledUpColumn
parameter_list|()
block|{
specifier|final
name|String
name|maxError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in MAX"
decl_stmt|;
specifier|final
name|String
name|plusError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in PLUS"
decl_stmt|;
name|sql
argument_list|(
literal|"select max(^slackingmin^) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|maxError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(slackingmin) from emp_r"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(empno, deptno, slackingmin) from emp_r"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(slackingmin) from emp_r"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, min(slackingmin) from emp_r group by empno"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select count(distinct slackingmin) from emp_r"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(empno + ^slackingmin^) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|plusError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select max(^slackingmin^) over t as a "
operator|+
literal|"from emp_r window t as (partition by empno order by empno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|maxError
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRolledUpColumnInWhere
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in GREATER_THAN"
decl_stmt|;
comment|// Fire these slackers!!
name|sql
argument_list|(
literal|"select empno from emp_r where slacker and ^slackingmin^> 60"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(slackingmin) filter (where slacker and ^slackingmin^> 60) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRolledUpColumnInHaving
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in SUM"
decl_stmt|;
name|sql
argument_list|(
literal|"select deptno, sum(slackingmin) from emp_r group "
operator|+
literal|"by deptno having sum(^slackingmin^)> 1000"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRollUpInWindow
parameter_list|()
block|{
specifier|final
name|String
name|partitionError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in PARTITION BY"
decl_stmt|;
specifier|final
name|String
name|orderByError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in ORDER BY"
decl_stmt|;
name|sql
argument_list|(
literal|"select empno, sum(slackingmin) over (partition by ^slackingmin^) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|partitionError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sum(slackingmin) over (partition by empno, ^slackingmin^) from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|partitionError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sum(slackingmin) over (partition by empno order by ^slackingmin^) "
operator|+
literal|"from emp_r"
argument_list|)
operator|.
name|fails
argument_list|(
name|orderByError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sum(slackingmin) over slackingmin "
operator|+
literal|"from emp_r window slackingmin as (partition by ^slackingmin^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|partitionError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(slackingmin) over t "
operator|+
literal|"from emp_r window t as (partition by empno order by ^slackingmin^, empno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|orderByError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select sum(slackingmin) over t as a "
operator|+
literal|"from emp_r window t as (partition by empno order by ^slackingmin^, empno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|orderByError
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRollUpInGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in GROUP BY"
decl_stmt|;
name|sql
argument_list|(
literal|"select empno, count(distinct empno) from emp_r group by empno, ^slackingmin^"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select empno from emp_r group by grouping sets (empno, ^slackingmin^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRollUpInOrderBy
parameter_list|()
block|{
specifier|final
name|String
name|error
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in ORDER BY"
decl_stmt|;
name|sql
argument_list|(
literal|"select empno from emp_r order by ^slackingmin^ asc"
argument_list|)
operator|.
name|fails
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select slackingmin from (select empno as slackingmin from emp_r)\n"
operator|+
literal|"order by slackingmin"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select empno, sum(slackingmin) from emp_r group by empno\n"
operator|+
literal|"order by sum(slackingmin)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRollUpInJoin
parameter_list|()
block|{
specifier|final
name|String
name|onError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in ON"
decl_stmt|;
specifier|final
name|String
name|usingError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in USING"
decl_stmt|;
specifier|final
name|String
name|selectError
init|=
literal|"Rolled up column 'SLACKINGMIN' is not allowed in SELECT"
decl_stmt|;
name|sql
argument_list|(
literal|"select * from (select deptno, ^slackingmin^ from emp_r)\n"
operator|+
literal|" join dept using (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|selectError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from dept as a\n"
operator|+
literal|"join (select deptno, ^slackingmin^ from emp_r) using (deptno)"
argument_list|)
operator|.
name|fails
argument_list|(
name|selectError
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from emp_r as a\n"
operator|+
literal|"join dept_r as b using (deptno, ^slackingmin^)"
argument_list|)
operator|.
name|fails
argument_list|(
name|usingError
argument_list|)
expr_stmt|;
comment|// Even though the emp_r.slackingmin column will be under the SqlNode for '=',
comment|// The error should say it happened in 'ON' instead
name|sql
argument_list|(
literal|"select * from emp_r\n"
operator|+
literal|"join dept_r on (^emp_r.slackingmin^ = dept_r.slackingmin)"
argument_list|)
operator|.
name|fails
argument_list|(
name|onError
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonValueExpressionOperator
parameter_list|()
block|{
name|expr
argument_list|(
literal|"'{}' format json"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' format json encoding utf8"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' format json encoding utf16"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' format json encoding utf32"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' format json"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'null' format json"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ANY NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"cast(null as varchar) format json"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ANY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"null format json"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"ANY"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^null^ format json"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal use of .NULL.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonExists
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_exists('{}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_exists('{}', 'lax $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonValue
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_value('{\"foo\":\"bar\"}', 'lax $.foo')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":\"bar\"}', 123)"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":\"bar\"}', 'lax $.foo')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo'"
operator|+
literal|"returning integer)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo'"
operator|+
literal|"returning integer default 0 on empty default 0 on error)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo'"
operator|+
literal|"returning integer default null on empty default null on error)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":true}', 'lax $.foo'"
operator|+
literal|"returning boolean default 100 on empty default 100 on error)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
comment|// test type inference of default value
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' default 'empty' on empty)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_value('{\"foo\":100}', 'lax $.foo' returning boolean"
operator|+
literal|" default 100 on empty)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonQuery
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'strict $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'strict $' WITH WRAPPER)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'strict $' EMPTY OBJECT ON EMPTY)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'strict $' EMPTY ARRAY ON ERROR)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_query('{\"foo\":\"bar\"}', 'strict $' EMPTY OBJECT ON EMPTY "
operator|+
literal|"EMPTY ARRAY ON ERROR EMPTY ARRAY ON EMPTY NULL ON ERROR)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArray
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_array()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_array('foo', 'bar')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_array('foo', 'bar')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonArrayAgg
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_arrayagg(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_arrayagg('foo')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonObject
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_object()"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': 'bar')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_object('foo': 'bar')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^json_object(100: 'bar')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Expected a character type*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonPretty
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_pretty(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_pretty('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_pretty('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select json_pretty(^NULL^) from emp"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Illegal use of .NULL.*"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select json_pretty(NULL) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_2869_FIXED
condition|)
block|{
comment|// the case should throw an error but currently validation
comment|// is done during sql-to-rel process.
comment|//
comment|// see StandardConvertletTable.JsonOperatorValueExprConvertlet
return|return;
block|}
name|sql
argument_list|(
literal|"select json_pretty(^1^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(.*)JSON_VALUE_EXPRESSION(.*)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonStorageSize
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_storage_size(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_storage_size('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_storage_size('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_type(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_type('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_type('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(20)"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_2869_FIXED
condition|)
block|{
return|return;
block|}
name|sql
argument_list|(
literal|"select json_type(^1^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(.*)JSON_VALUE_EXPRESSION(.*)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonDepth
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_depth(ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_depth('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_2869_FIXED
condition|)
block|{
return|return;
block|}
name|sql
argument_list|(
literal|"select json_depth(^1^) from emp"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(.*)JSON_VALUE_EXPRESSION(.*)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonLength
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_length('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\":\"bar\"}')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_length('{\"foo\":\"bar\"}', 'strict $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"INTEGER"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonKeys
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_keys('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_keys('{\"foo\":\"bar\"}', 'lax $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_keys('{\"foo\":\"bar\"}', 'strict $')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonRemove
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_remove('{\"foo\":\"bar\"}', '$')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_remove('{\"foo\":\"bar\"}', '$')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_remove('{\"foo\":\"bar\"}', 1, '2', 3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_remove('{\"foo\":\"bar\"}', 1, 2, 3)"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ^json_remove('{\"foo\":\"bar\"}')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Invalid number of arguments.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testJsonObjectAgg
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select json_objectagg(ename: empno) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select json_objectagg(empno: ename) from emp"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select ^json_objectagg(empno: ename)^ from emp"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"json_objectagg('foo': 'bar')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
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
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is json value"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is json object"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'[]' is json array"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'100' is json scalar"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json value"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'{}' is not json object"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'[]' is not json array"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"'100' is not json scalar"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"100 is json value"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"BOOLEAN NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^100 is json value^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testJsonInsert
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_insert('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_insert('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"select ^json_insert('{\"foo\":\"bar\"}')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Invalid number of arguments.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testJsonReplace
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_replace('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"select ^json_replace('{\"foo\":\"bar\"}')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Invalid number of arguments.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
specifier|public
name|void
name|testJsonSet
parameter_list|()
block|{
name|expr
argument_list|(
literal|"json_set('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
name|expr
argument_list|(
literal|"json_set('{ \"a\": 1, \"b\": [2]}', '$.a', 10, '$.c', '[true]')"
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR(2000)"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"select ^json_set('{\"foo\":\"bar\"}')^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Invalid number of arguments.*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testRegexpReplace
parameter_list|()
block|{
specifier|final
name|SqlOperatorTable
name|opTable
init|=
name|operatorTableFor
argument_list|(
name|SqlLibrary
operator|.
name|ORACLE
argument_list|)
decl_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('a b c', 'a', 'X')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def ghi', '[a-z]+', 'X', 2)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def ghi', '[a-z]+', 'X', 1, 3)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def GHI', '[a-z]+', 'X', 1, 3, 'c')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
comment|// Implicit type coercion.
name|expr
argument_list|(
literal|"REGEXP_REPLACE(null, '(-)', '###')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('100-200', null, '###')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('100-200', '(-)', null)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def ghi', '[a-z]+', 'X', '2')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def ghi', '[a-z]+', 'X', '1', '3')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
comment|// The last argument to REGEXP_REPLACE should be specific character, but with
comment|// implicit type coercion, the validation still passes.
name|expr
argument_list|(
literal|"REGEXP_REPLACE('abc def ghi', '[a-z]+', 'X', '1', '3', '1')"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|opTable
argument_list|)
operator|.
name|columnType
argument_list|(
literal|"VARCHAR NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testInvalidFunctionCall
parameter_list|()
block|{
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|MockSqlOperatorTable
operator|.
name|standard
argument_list|()
operator|.
name|extend
argument_list|()
decl_stmt|;
comment|// With implicit type coercion.
name|expr
argument_list|(
literal|"^unknown_udf(1, 2)^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature "
operator|+
literal|"UNKNOWN_UDF\\(<NUMERIC>,<NUMERIC>\\).*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^power(cast(1 as timestamp), cast(2 as timestamp))^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'POWER' to arguments of type "
operator|+
literal|"'POWER\\(<TIMESTAMP\\(0\\)>,<TIMESTAMP\\(0\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^myFUN(cast('124' as timestamp))^"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'MYFUN' to arguments of type "
operator|+
literal|"'MYFUN\\(<TIMESTAMP\\(0\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^myFUN(1, 2)^"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature "
operator|+
literal|"MYFUN\\(<NUMERIC>,<NUMERIC>\\).*"
argument_list|)
expr_stmt|;
comment|// Without implicit type coercion.
name|expr
argument_list|(
literal|"^unknown_udf(1, 2)^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature "
operator|+
literal|"UNKNOWN_UDF\\(<NUMERIC>,<NUMERIC>\\).*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^power(cast(1 as timestamp), cast(2 as timestamp))^"
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'POWER' to arguments of type "
operator|+
literal|"'POWER\\(<TIMESTAMP\\(0\\)>,<TIMESTAMP\\(0\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^myFUN(cast('124' as timestamp))^"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Cannot apply 'MYFUN' to arguments of type "
operator|+
literal|"'MYFUN\\(<TIMESTAMP\\(0\\)>\\)'.*"
argument_list|)
expr_stmt|;
name|expr
argument_list|(
literal|"^myFUN(1, 2)^"
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
literal|true
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
operator|.
name|withTypeCoercion
argument_list|(
literal|false
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*No match found for function signature "
operator|+
literal|"MYFUN\\(<NUMERIC>,<NUMERIC>\\).*"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testPositionalAggregateWithExpandedCurrentDateFunction
parameter_list|()
block|{
name|SqlConformance
name|defaultPlusOrdinalGroupBy
init|=
operator|new
name|SqlDelegatingConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isGroupByOrdinal
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|sql
argument_list|(
literal|"SELECT HIREDATE>= CURRENT_DATE, COUNT(*) "
operator|+
literal|"FROM EMP GROUP BY 1"
argument_list|)
operator|.
name|withConformance
argument_list|(
name|defaultPlusOrdinalGroupBy
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testValidatorReportsOriginalQueryUsingReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select a from b"
decl_stmt|;
specifier|final
name|SqlParser
operator|.
name|Config
name|config
init|=
name|SqlParser
operator|.
name|config
argument_list|()
decl_stmt|;
specifier|final
name|String
name|messagePassingSqlString
decl_stmt|;
try|try
block|{
specifier|final
name|SqlParser
name|sqlParserReader
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|,
name|config
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|sqlParserReader
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|fixture
argument_list|()
operator|.
name|factory
operator|.
name|createValidator
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|x
init|=
name|validator
operator|.
name|validate
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expecting an error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|CalciteContextException
name|error
parameter_list|)
block|{
comment|// we want the exception to report column and line
name|assertThat
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"At line 1, column 15: Object 'B' not found"
argument_list|)
argument_list|)
expr_stmt|;
name|messagePassingSqlString
operator|=
name|error
operator|.
name|getMessage
argument_list|()
expr_stmt|;
comment|// the error does not contain the original query (even using a String)
name|assertNull
argument_list|(
name|error
operator|.
name|getOriginalStatement
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// parse SQL text using a java.io.Reader and not a java.lang.String
try|try
block|{
specifier|final
name|SqlParser
name|sqlParserReader
init|=
name|SqlParser
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|sql
argument_list|)
argument_list|,
name|config
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|sqlParserReader
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|fixture
argument_list|()
operator|.
name|factory
operator|.
name|createValidator
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|x
init|=
name|validator
operator|.
name|validate
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expecting an error, got "
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteContextException
name|error
parameter_list|)
block|{
comment|// we want exactly the same error as with java.lang.String input
name|assertThat
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
name|messagePassingSqlString
argument_list|)
argument_list|)
expr_stmt|;
comment|// the error does not contain the original query (using a Reader)
name|assertThat
argument_list|(
name|error
operator|.
name|getOriginalStatement
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Test
name|void
name|testValidateParameterizedExpression
parameter_list|()
throws|throws
name|SqlParseException
block|{
specifier|final
name|SqlParser
operator|.
name|Config
name|config
init|=
name|SqlParser
operator|.
name|config
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|fixture
argument_list|()
operator|.
name|factory
operator|.
name|createValidator
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
name|RelDataType
name|intType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intTypeNull
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|intType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|nameToTypeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|nameToTypeMap
operator|.
name|put
argument_list|(
literal|"A"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|nameToTypeMap
operator|.
name|put
argument_list|(
literal|"B"
argument_list|,
name|intTypeNull
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expr
init|=
literal|"a + b"
decl_stmt|;
specifier|final
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|expr
argument_list|,
name|config
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
specifier|final
name|SqlNode
name|validated
init|=
name|validator
operator|.
name|validateParameterizedExpression
argument_list|(
name|sqlNode
argument_list|,
name|nameToTypeMap
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|resultType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|validated
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultType
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
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
name|testAccessingNestedFieldsOfNullableRecord
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select ROW_COLUMN_ARRAY[0].NOT_NULL_FIELD from NULLABLEROWS.NR_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(BIGINT EXPR$0) NOT NULL"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select ROW_COLUMN_ARRAY[0]['NOT_NULL_FIELD'] from NULLABLEROWS.NR_T1"
argument_list|)
operator|.
name|withExtendedCatalog
argument_list|()
operator|.
name|type
argument_list|(
literal|"RecordType(BIGINT EXPR$0) NOT NULL"
argument_list|)
expr_stmt|;
specifier|final
name|SqlOperatorTable
name|operatorTable
init|=
name|MockSqlOperatorTable
operator|.
name|standard
argument_list|()
operator|.
name|extend
argument_list|()
decl_stmt|;
name|sql
argument_list|(
literal|"select * FROM TABLE(ROW_FUNC()) AS T(a, b)"
argument_list|)
operator|.
name|withOperatorTable
argument_list|(
name|operatorTable
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(BIGINT NOT NULL A, BIGINT B) NOT NULL"
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/** Validator that rewrites columnar sql identifiers 'UNEXPANDED'.'Something'    * to 'DEPT'.'Something', where 'Something' is any string. */
end_comment

begin_class
specifier|private
specifier|static
class|class
name|UnexpandedToDeptValidator
extends|extends
name|SqlValidatorImpl
block|{
name|UnexpandedToDeptValidator
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|validate
parameter_list|(
name|SqlNode
name|topNode
parameter_list|)
block|{
name|SqlNode
name|rewrittenNode
init|=
name|rewriteNode
argument_list|(
name|topNode
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|validate
argument_list|(
name|rewrittenNode
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|SqlNode
name|rewriteNode
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
block|{
return|return
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
name|id
parameter_list|)
block|{
return|return
name|rewriteIdentifier
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|SqlIdentifier
name|rewriteIdentifier
parameter_list|(
name|SqlIdentifier
name|sqlIdentifier
parameter_list|)
block|{
if|if
condition|(
name|sqlIdentifier
operator|.
name|names
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
return|return
name|sqlIdentifier
return|;
block|}
if|if
condition|(
name|sqlIdentifier
operator|.
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"UNEXPANDED"
argument_list|)
condition|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|asList
argument_list|(
literal|"DEPT"
argument_list|,
name|sqlIdentifier
operator|.
name|names
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
name|sqlIdentifier
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|asList
argument_list|(
name|sqlIdentifier
operator|.
name|getComponentParserPosition
argument_list|(
literal|0
argument_list|)
argument_list|,
name|sqlIdentifier
operator|.
name|getComponentParserPosition
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|sqlIdentifier
operator|.
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"DEPT"
argument_list|)
condition|)
block|{
comment|//  Identifiers are expanded multiple times
return|return
name|sqlIdentifier
return|;
block|}
else|else
block|{
return|return
name|sqlIdentifier
return|;
block|}
block|}
block|}
end_class

unit|}
end_unit

