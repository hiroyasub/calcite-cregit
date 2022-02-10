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
name|rel
operator|.
name|metadata
package|;
end_package

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
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertDoesNotThrow
import|;
end_import

begin_comment
comment|/**  * Test cases for {@link MetadataDef}.  */
end_comment

begin_class
class|class
name|MetadataDefTest
block|{
annotation|@
name|Test
name|void
name|staticMethodInHandlerIsIgnored
parameter_list|()
block|{
name|assertDoesNotThrow
argument_list|(
parameter_list|()
lambda|->
name|MetadataDef
operator|.
name|of
argument_list|(
name|TestMetadata
operator|.
name|class
argument_list|,
name|MetadataHandlerWithStaticMethod
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|synthenticMethodInHandlerIsIgnored
parameter_list|()
block|{
name|assertDoesNotThrow
argument_list|(
parameter_list|()
lambda|->
name|MetadataDef
operator|.
name|of
argument_list|(
name|TestMetadata
operator|.
name|class
argument_list|,
name|TestMetadataHandlers
operator|.
name|handlerClassWithSyntheticMethod
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

