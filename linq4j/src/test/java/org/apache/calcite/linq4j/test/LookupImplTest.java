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
name|linq4j
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
name|linq4j
operator|.
name|Lookup
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
name|Test
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
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Unit tests for LookupImpl.java  *  */
end_comment

begin_class
specifier|public
class|class
name|LookupImplTest
extends|extends
name|TestCase
block|{
specifier|private
name|Lookup
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|impl
decl_stmt|;
annotation|@
name|BeforeEach
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|impl
operator|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Linq4jTest
operator|.
name|emps
argument_list|)
operator|.
name|toLookup
argument_list|(
name|Linq4jTest
operator|.
name|EMP_DEPTNO_SELECTOR
argument_list|,
name|Linq4jTest
operator|.
name|EMP_NAME_SELECTOR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPut
parameter_list|()
block|{
name|int
name|initSize
init|=
name|impl
operator|.
name|size
argument_list|()
decl_stmt|;
name|impl
operator|.
name|put
argument_list|(
literal|99
argument_list|,
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impl
operator|.
name|containsKey
argument_list|(
literal|99
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impl
operator|.
name|size
argument_list|()
operator|==
name|initSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContainsValue
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"D"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|impl
operator|.
name|put
argument_list|(
literal|100
argument_list|,
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impl
operator|.
name|containsValue
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impl
operator|.
name|containsValue
argument_list|(
name|list2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End LookupImplTest.java
end_comment

end_unit

