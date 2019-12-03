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
name|validate
operator|.
name|implicit
operator|.
name|TypeCoercion
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

begin_comment
comment|/**  * Test cases for implicit type coercion converter. see {@link TypeCoercion} doc  * or<a href="https://docs.google.com/spreadsheets/d/1GhleX5h5W8-kJKh7NMJ4vtoE78pwfaZRJl88ULX_MgU/edit?usp=sharing">CalciteImplicitCasts</a>  * for conversion details.  */
end_comment

begin_class
specifier|public
class|class
name|TypeCoercionConverterTest
extends|extends
name|SqlToRelTestBase
block|{
annotation|@
name|Override
specifier|protected
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|TypeCoercionConverterTest
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Tester
name|createTester
parameter_list|()
block|{
return|return
name|super
operator|.
name|createTester
argument_list|()
operator|.
name|withCatalogReaderFactory
argument_list|(
operator|new
name|TypeCoercionTest
argument_list|()
operator|.
name|getCatalogReaderFactory
argument_list|()
argument_list|)
return|;
block|}
comment|/** Test case for {@link TypeCoercion#commonTypeForBinaryComparison}. */
annotation|@
name|Test
specifier|public
name|void
name|testBinaryComparison
parameter_list|()
block|{
comment|// for constant cast, there is reduce rule
name|checkPlanEquals
argument_list|(
literal|"select\n"
operator|+
literal|"1<'1' as f0,\n"
operator|+
literal|"1<='1' as f1,\n"
operator|+
literal|"1>'1' as f2,\n"
operator|+
literal|"1>='1' as f3,\n"
operator|+
literal|"1='1' as f4,\n"
operator|+
literal|"t1_date> t1_timestamp as f5,\n"
operator|+
literal|"'2' is not distinct from 2 as f6,\n"
operator|+
literal|"'2019-09-23' between t1_date and t1_timestamp as f7,\n"
operator|+
literal|"cast('2019-09-23' as date) between t1_date and t1_timestamp as f8\n"
operator|+
literal|"from t1"
argument_list|)
expr_stmt|;
block|}
comment|/** Test cases for {@link TypeCoercion#inOperationCoercion}. */
annotation|@
name|Test
specifier|public
name|void
name|testInOperation
parameter_list|()
block|{
name|checkPlanEquals
argument_list|(
literal|"select\n"
operator|+
literal|"1 in ('1', '2', '3') as f0,\n"
operator|+
literal|"(1, 2) in (('1', '2')) as f1,\n"
operator|+
literal|"(1, 2) in (('1', '2'), ('3', '4')) as f2\n"
operator|+
literal|"from (values (true, true, true))"
argument_list|)
expr_stmt|;
block|}
comment|/** Test cases for    * {@link org.apache.calcite.sql.validate.implicit.TypeCoercionImpl#booleanEquality}. */
annotation|@
name|Test
specifier|public
name|void
name|testBooleanEquality
parameter_list|()
block|{
comment|// REVIEW Danny 2018-05-16: Now we do not support cast between numeric<-> boolean for
comment|// Calcite execution runtime, but we still add cast in the plan so other systems
comment|// using Calcite can rewrite Cast operator implementation.
comment|// for this case, we replace the boolean literal with numeric 1.
name|checkPlanEquals
argument_list|(
literal|"select\n"
operator|+
literal|"1=true as f0,\n"
operator|+
literal|"1.0=true as f1,\n"
operator|+
literal|"0.0=true=true as f2,\n"
operator|+
literal|"1.23=t1_boolean as f3,\n"
operator|+
literal|"t1_smallint=t1_boolean as f4,\n"
operator|+
literal|"10000000000=true as f5\n"
operator|+
literal|"from t1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCaseWhen
parameter_list|()
block|{
name|checkPlanEquals
argument_list|(
literal|"select case when 1> 0 then t2_bigint else t2_decimal end from t2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBuiltinFunctionCoercion
parameter_list|()
block|{
name|checkPlanEquals
argument_list|(
literal|"select 1||'a' from (values true)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStarImplicitTypeCoercion
parameter_list|()
block|{
name|checkPlanEquals
argument_list|(
literal|"select * from (values(1, '3')) union select * from (values('2', 4))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetOperation
parameter_list|()
block|{
comment|// int decimal smallint double
comment|// char decimal float bigint
comment|// char decimal float double
comment|// char decimal smallint double
specifier|final
name|String
name|sql
init|=
literal|"select t1_int, t1_decimal, t1_smallint, t1_double from t1 "
operator|+
literal|"union select t2_varchar20, t2_decimal, t2_float, t2_bigint from t2 "
operator|+
literal|"union select t1_varchar20, t1_decimal, t1_float, t1_double from t1 "
operator|+
literal|"union select t2_varchar20, t2_decimal, t2_smallint, t2_double from t2"
decl_stmt|;
name|checkPlanEquals
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkPlanEquals
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|tester
operator|.
name|assertConvertsTo
argument_list|(
name|sql
argument_list|,
literal|"${plan}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

