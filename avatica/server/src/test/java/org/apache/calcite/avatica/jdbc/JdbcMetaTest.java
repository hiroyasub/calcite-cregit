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
operator|.
name|jdbc
package|;
end_package

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
name|junit
operator|.
name|Assert
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
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link JdbcMeta}.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcMetaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testExceptionPropagation
parameter_list|()
throws|throws
name|SQLException
block|{
name|JdbcMeta
name|meta
init|=
operator|new
name|JdbcMeta
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
specifier|final
name|Throwable
name|e
init|=
operator|new
name|Exception
argument_list|()
decl_stmt|;
specifier|final
name|RuntimeException
name|rte
decl_stmt|;
try|try
block|{
name|meta
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|caughtException
parameter_list|)
block|{
name|rte
operator|=
name|caughtException
expr_stmt|;
name|assertThat
argument_list|(
name|rte
operator|.
name|getCause
argument_list|()
argument_list|,
name|is
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JdbcMetaTest.java
end_comment

end_unit

