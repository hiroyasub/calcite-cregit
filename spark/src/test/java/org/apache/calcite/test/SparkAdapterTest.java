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
name|adapter
operator|.
name|spark
operator|.
name|SparkRel
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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Tests for using Calcite with Spark as an internal engine, as implemented by  * the {@link org.apache.calcite.adapter.spark} package.  */
end_comment

begin_class
specifier|public
class|class
name|SparkAdapterTest
block|{
comment|/**    * Tests a VALUES query evaluated using Spark.    * There are no data sources.    */
annotation|@
name|Test
specifier|public
name|void
name|testValues
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// Insert a spurious reference to a class in Calcite's Spark adapter.
comment|// Otherwise this test doesn't depend on the Spark module at all, and
comment|// Javadoc gets confused.
name|Util
operator|.
name|discard
argument_list|(
name|SparkRel
operator|.
name|class
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SPARK
argument_list|)
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b'))"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1; EXPR$1=a\n"
operator|+
literal|"EXPR$0=2; EXPR$1=b\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"SparkToEnumerableConverter\n"
operator|+
literal|"  SparkValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests values followed by filter, evaluated by Spark. */
annotation|@
name|Test
specifier|public
name|void
name|testValuesFilter
parameter_list|()
throws|throws
name|SQLException
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SPARK
argument_list|)
operator|.
name|query
argument_list|(
literal|"select *\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b')) as t(x, y)\n"
operator|+
literal|"where x< 2"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"X=1; Y=a\n"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=SparkToEnumerableConverter\n"
operator|+
literal|"  SparkCalc(expr#0..1=[{inputs}], expr#2=[2], expr#3=[<($t0, $t2)], proj#0..1=[{exprs}], $condition=[$t3])\n"
operator|+
literal|"    SparkValues(tuples=[[{ 1, 'a' }, { 2, 'b' }]])\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SparkAdapterTest.java
end_comment

end_unit

