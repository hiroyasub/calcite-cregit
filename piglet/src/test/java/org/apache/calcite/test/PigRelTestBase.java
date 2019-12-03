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
name|piglet
operator|.
name|PigConverter
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
name|RelNode
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
name|FrameworkConfig
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|piglet
operator|.
name|PigConverter
operator|.
name|create
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
name|PigRelBuilderTest
operator|.
name|config
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
name|assumeFalse
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|getProperty
import|;
end_import

begin_comment
comment|/**  * Abstract class for Pig to {@link RelNode} tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PigRelTestBase
block|{
name|PigConverter
name|converter
decl_stmt|;
annotation|@
name|BeforeEach
specifier|public
name|void
name|testSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
argument_list|,
literal|"Skip: Pig/Hadoop tests do not work on Windows"
argument_list|)
expr_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|config
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|converter
operator|=
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

