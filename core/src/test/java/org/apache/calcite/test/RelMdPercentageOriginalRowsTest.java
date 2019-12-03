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
name|java
operator|.
name|ReflectiveSchema
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
comment|/** Test case for CALCITE-2894 */
end_comment

begin_class
specifier|public
class|class
name|RelMdPercentageOriginalRowsTest
block|{
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2894">[CALCITE-2894]    * NullPointerException thrown by RelMdPercentageOriginalRows when explaining    * plan with all attributes</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testExplainAllAttributesSemiJoinUnionCorrelate
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|LEX
argument_list|,
name|Lex
operator|.
name|JAVA
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FORCE_DECORRELATE
argument_list|,
literal|false
argument_list|)
operator|.
name|withSchema
argument_list|(
literal|"s"
argument_list|,
operator|new
name|ReflectiveSchema
argument_list|(
operator|new
name|JdbcTest
operator|.
name|HrSchema
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
literal|"select deptno, name from depts where deptno in (\n"
operator|+
literal|" select e.deptno from emps e where exists (select 1 from depts d where d.deptno=e.deptno)\n"
operator|+
literal|" union select e.deptno from emps e where e.salary> 10000) "
argument_list|)
operator|.
name|explainMatches
argument_list|(
literal|"including all attributes "
argument_list|,
name|CalciteAssert
operator|.
name|checkResultContains
argument_list|(
literal|"EnumerableCorrelate"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

