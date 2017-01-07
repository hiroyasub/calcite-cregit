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
name|avatica
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Test class for AvaticaStatement  */
end_comment

begin_class
specifier|public
class|class
name|AvaticaStatementTest
block|{
specifier|private
name|AvaticaStatement
name|statement
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// Disabled on JDK9 due to Mockito bug; see [CALCITE-1567].
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|compareTo
argument_list|(
literal|"9"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|statement
operator|=
name|mock
argument_list|(
name|AvaticaStatement
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateCounts
parameter_list|()
throws|throws
name|SQLException
block|{
name|long
index|[]
name|longValues
init|=
operator|new
name|long
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|3
block|,
literal|1
block|,
literal|5
block|,
operator|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|+
literal|1
block|}
decl_stmt|;
name|int
index|[]
name|intValues
init|=
operator|new
name|int
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|3
block|,
literal|1
block|,
literal|5
block|,
name|Integer
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|when
argument_list|(
name|statement
operator|.
name|executeBatch
argument_list|()
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|statement
operator|.
name|executeLargeBatch
argument_list|()
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|statement
operator|.
name|executeBatchInternal
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|longValues
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|intValues
argument_list|,
name|statement
operator|.
name|executeBatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|longValues
argument_list|,
name|statement
operator|.
name|executeLargeBatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaStatementTest.java
end_comment

end_unit

