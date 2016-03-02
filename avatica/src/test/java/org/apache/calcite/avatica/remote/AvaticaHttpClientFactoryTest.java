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
name|remote
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
name|avatica
operator|.
name|BuiltInConnectionProperty
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
name|avatica
operator|.
name|ConnectionConfig
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
name|avatica
operator|.
name|ConnectionConfigImpl
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for the factory that creates http clients.  */
end_comment

begin_class
specifier|public
class|class
name|AvaticaHttpClientFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testDefaultHttpClient
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8765"
argument_list|)
decl_stmt|;
name|ConnectionConfig
name|config
init|=
operator|new
name|ConnectionConfigImpl
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|AvaticaHttpClientFactory
name|httpClientFactory
init|=
operator|new
name|AvaticaHttpClientFactoryImpl
argument_list|()
decl_stmt|;
name|AvaticaHttpClient
name|client
init|=
name|httpClientFactory
operator|.
name|getClient
argument_list|(
name|url
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Client was an instance of "
operator|+
name|client
operator|.
name|getClass
argument_list|()
argument_list|,
name|client
operator|instanceof
name|AvaticaCommonsHttpClientImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverridenHttpClient
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|BuiltInConnectionProperty
operator|.
name|HTTP_CLIENT_IMPL
operator|.
name|name
argument_list|()
argument_list|,
name|AvaticaHttpClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8765"
argument_list|)
decl_stmt|;
name|ConnectionConfig
name|config
init|=
operator|new
name|ConnectionConfigImpl
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|AvaticaHttpClientFactory
name|httpClientFactory
init|=
operator|new
name|AvaticaHttpClientFactoryImpl
argument_list|()
decl_stmt|;
name|AvaticaHttpClient
name|client
init|=
name|httpClientFactory
operator|.
name|getClient
argument_list|(
name|url
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Client was an instance of "
operator|+
name|client
operator|.
name|getClass
argument_list|()
argument_list|,
name|client
operator|instanceof
name|AvaticaHttpClientImpl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaHttpClientFactoryTest.java
end_comment

end_unit

