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
name|hamcrest
operator|.
name|CoreMatchers
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
name|util
operator|.
name|Map
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
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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

begin_comment
comment|/**  * Tests the generated implementation of  * {@link org.apache.calcite.runtime.CalciteResource} (mostly a sanity check for  * the resource-generation infrastructure).  */
end_comment

begin_class
specifier|public
class|class
name|CalciteResourceTest
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|CalciteResourceTest
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Verifies that resource properties such as SQLSTATE are available at    * runtime.    */
annotation|@
name|Test
specifier|public
name|void
name|testSqlstateProperty
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|RESOURCE
operator|.
name|illegalIntervalLiteral
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"SQLSTATE"
argument_list|)
argument_list|,
name|CoreMatchers
operator|.
name|equalTo
argument_list|(
literal|"42000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteResourceTest.java
end_comment

end_unit

