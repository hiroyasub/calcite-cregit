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
name|Before
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
name|Before
specifier|public
name|void
name|testSetup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|FrameworkConfig
name|config
init|=
name|PigRelBuilderTest
operator|.
name|config
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|converter
operator|=
name|PigConverter
operator|.
name|create
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PigRelTestBase.java
end_comment

end_unit

