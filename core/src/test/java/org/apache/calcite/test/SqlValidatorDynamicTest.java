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
name|test
operator|.
name|catalog
operator|.
name|MockCatalogReaderDynamic
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
name|BeforeAll
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
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Concrete child class of {@link SqlValidatorTestCase}, containing lots of unit  * tests.  *  *<p>If you want to run these same tests in a different environment, create a  * derived class whose {@link #getTester} returns a different implementation of  * {@link SqlTester}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorDynamicTest
extends|extends
name|SqlValidatorTestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
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
name|SqlValidatorDynamicTest
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
literal|"Streaming aggregation requires at least one monotonic expression in GROUP BY clause"
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
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlValidatorDynamicTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlTester
name|getTester
parameter_list|()
block|{
comment|// Dynamic schema should not be reused since it is mutable, so
comment|// we create new SqlTestFactory for each test
return|return
operator|new
name|SqlValidatorTester
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
operator|.
name|withCatalogReader
argument_list|(
name|MockCatalogReaderDynamic
operator|::
operator|new
argument_list|)
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|BeforeAll
specifier|public
specifier|static
name|void
name|setUSLocale
parameter_list|()
block|{
comment|// This ensures numbers in exceptions are printed as in asserts.
comment|// For example, 1,000 vs 1 000
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1150">[CALCITE-1150]    * Dynamic Table / Dynamic Star support</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testAmbiguousDynamicStar
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^n_nation^\n"
operator|+
literal|"from (select * from \"SALES\".NATION),\n"
operator|+
literal|" (select * from \"SALES\".CUSTOMER)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'N_NATION' is ambiguous"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAmbiguousDynamicStar2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^n_nation^\n"
operator|+
literal|"from (select * from \"SALES\".NATION, \"SALES\".CUSTOMER)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'N_NATION' is ambiguous"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAmbiguousDynamicStar3
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select ^nc.n_nation^\n"
operator|+
literal|"from (select * from \"SALES\".NATION, \"SALES\".CUSTOMER) as nc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Column 'N_NATION' is ambiguous"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAmbiguousDynamicStar4
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select n.n_nation\n"
operator|+
literal|"from (select * from \"SALES\".NATION) as n,\n"
operator|+
literal|" (select * from \"SALES\".CUSTOMER)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(ANY N_NATION) NOT NULL"
argument_list|)
expr_stmt|;
block|}
comment|/** When resolve column reference, regular field has higher priority than    * dynamic star columns. */
annotation|@
name|Test
specifier|public
name|void
name|testDynamicStar2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|sql
init|=
literal|"select newid from (\n"
operator|+
literal|"  select *, NATION.N_NATION + 100 as newid\n"
operator|+
literal|"  from \"SALES\".NATION, \"SALES\".CUSTOMER)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|type
argument_list|(
literal|"RecordType(ANY NEWID) NOT NULL"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

