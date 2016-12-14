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
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|remote
operator|.
name|AvaticaHttpClientFactory
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
name|remote
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Connection configuration.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConnectionConfig
block|{
comment|/** @see BuiltInConnectionProperty#SCHEMA */
name|String
name|schema
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#TIME_ZONE */
name|String
name|timeZone
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#FACTORY */
name|Service
operator|.
name|Factory
name|factory
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#URL */
name|String
name|url
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#SERIALIZATION */
name|String
name|serialization
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#AUTHENTICATION */
name|String
name|authentication
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#AVATICA_USER */
name|String
name|avaticaUser
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#AVATICA_PASSWORD */
name|String
name|avaticaPassword
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#HTTP_CLIENT_FACTORY */
name|AvaticaHttpClientFactory
name|httpClientFactory
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#HTTP_CLIENT_IMPL */
name|String
name|httpClientClass
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#PRINCIPAL */
name|String
name|kerberosPrincipal
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#KEYTAB */
name|File
name|kerberosKeytab
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#TRUSTSTORE */
name|File
name|truststore
parameter_list|()
function_decl|;
comment|/** @see BuiltInConnectionProperty#TRUSTSTORE_PASSWORD */
name|String
name|truststorePassword
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End ConnectionConfig.java
end_comment

end_unit

