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
name|ConnectionConfig
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

begin_comment
comment|/**  * A factory for constructing {@link AvaticaHttpClient}'s.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AvaticaHttpClientFactory
block|{
comment|/**    * Construct the appropriate implementation of {@link AvaticaHttpClient}.    *    * @param url URL that the client is for.    * @param config Configuration to use when constructing the implementation.    * @return An instance of {@link AvaticaHttpClient}.    */
name|AvaticaHttpClient
name|getClient
parameter_list|(
name|URL
name|url
parameter_list|,
name|ConnectionConfig
name|config
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End AvaticaHttpClientFactory.java
end_comment

end_unit

