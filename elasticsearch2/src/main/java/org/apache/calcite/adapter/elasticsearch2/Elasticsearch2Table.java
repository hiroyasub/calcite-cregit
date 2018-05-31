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
name|elasticsearch2
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
name|adapter
operator|.
name|elasticsearch
operator|.
name|AbstractElasticsearchTable
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchHit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * Table based on an Elasticsearch2 type.  */
end_comment

begin_class
specifier|public
class|class
name|Elasticsearch2Table
extends|extends
name|AbstractElasticsearchTable
block|{
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
comment|/**    * Creates an Elasticsearch2Table.    */
specifier|public
name|Elasticsearch2Table
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
name|super
argument_list|(
name|indexName
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * ES version 2.x. To access document attributes ES2 uses {@code _source.foo} syntax.    */
annotation|@
name|Override
specifier|protected
name|String
name|scriptedFieldPrefix
parameter_list|()
block|{
return|return
literal|"_source"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|find
parameter_list|(
name|String
name|index
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ops
parameter_list|,
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
specifier|final
name|String
name|dbName
init|=
name|index
decl_stmt|;
specifier|final
name|String
name|queryString
init|=
literal|"{"
operator|+
name|Util
operator|.
name|toString
argument_list|(
name|ops
argument_list|,
literal|""
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
operator|+
literal|"}"
decl_stmt|;
specifier|final
name|Function1
argument_list|<
name|SearchHit
argument_list|,
name|Object
argument_list|>
name|getter
init|=
name|Elasticsearch2Enumerator
operator|.
name|getter
argument_list|(
name|fields
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|SearchHit
argument_list|>
name|cursor
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|dbName
argument_list|)
operator|.
name|setTypes
argument_list|(
name|typeName
argument_list|)
operator|.
name|setSource
argument_list|(
name|queryString
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getHits
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Elasticsearch2Enumerator
argument_list|(
name|cursor
argument_list|,
name|getter
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Elasticsearch2Table.java
end_comment

end_unit

