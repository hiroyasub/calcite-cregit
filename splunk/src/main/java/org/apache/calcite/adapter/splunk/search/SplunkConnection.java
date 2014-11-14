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
name|adapter
operator|.
name|splunk
operator|.
name|search
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
name|Enumerator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Connection to Splunk.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SplunkConnection
block|{
name|void
name|getSearchResults
parameter_list|(
name|String
name|search
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherArgs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldList
parameter_list|,
name|SearchResultListener
name|srl
parameter_list|)
function_decl|;
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|getSearchResultEnumerator
parameter_list|(
name|String
name|search
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherArgs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldList
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SplunkConnection.java
end_comment

end_unit

