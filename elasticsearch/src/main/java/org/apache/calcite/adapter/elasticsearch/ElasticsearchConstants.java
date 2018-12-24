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
name|elasticsearch
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Internal constants referenced in this package.  */
end_comment

begin_interface
interface|interface
name|ElasticsearchConstants
block|{
name|String
name|INDEX
init|=
literal|"_index"
decl_stmt|;
name|String
name|TYPE
init|=
literal|"_type"
decl_stmt|;
name|String
name|FIELDS
init|=
literal|"fields"
decl_stmt|;
name|String
name|SOURCE_PAINLESS
init|=
literal|"params._source"
decl_stmt|;
name|String
name|SOURCE_GROOVY
init|=
literal|"_source"
decl_stmt|;
comment|/**    * Attribute which uniquely identifies a document (ID)    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-id-field.html">ID Field</a>    */
name|String
name|ID
init|=
literal|"_id"
decl_stmt|;
name|String
name|UID
init|=
literal|"_uid"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|META_COLUMNS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|UID
argument_list|,
name|ID
argument_list|,
name|TYPE
argument_list|,
name|INDEX
argument_list|)
decl_stmt|;
block|}
end_interface

begin_comment
comment|// End ElasticsearchConstants.java
end_comment

end_unit

