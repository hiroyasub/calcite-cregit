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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|QueryProvider
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
name|Queryable
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelOptTable
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|schema
operator|.
name|SchemaPlus
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
name|schema
operator|.
name|TranslatableTable
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
name|schema
operator|.
name|impl
operator|.
name|AbstractTableQueryable
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Table based on an Elasticsearch type.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractElasticsearchTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
comment|/**    * Creates an ElasticsearchTable.    * @param indexName Elastic Search index    * @param typeName Elastic Search index type    * @param mapper Jackson API to parse (and created) JSON documents    */
name|AbstractElasticsearchTable
parameter_list|(
name|String
name|indexName
parameter_list|,
name|String
name|typeName
parameter_list|,
name|ObjectMapper
name|mapper
parameter_list|)
block|{
name|super
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indexName
argument_list|,
literal|"indexName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeName
argument_list|,
literal|"typeName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mapper
argument_list|,
literal|"mapper"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ElasticsearchTable{"
operator|+
name|indexName
operator|+
literal|"/"
operator|+
name|typeName
operator|+
literal|"}"
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|relDataTypeFactory
parameter_list|)
block|{
specifier|final
name|RelDataType
name|mapType
init|=
name|relDataTypeFactory
operator|.
name|createMapType
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|relDataTypeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|relDataTypeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|relDataTypeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"_MAP"
argument_list|,
name|mapType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
return|return
operator|new
name|ElasticsearchQueryable
argument_list|<>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|context
operator|.
name|getCluster
argument_list|()
decl_stmt|;
return|return
operator|new
name|ElasticsearchTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * In ES 5.x scripted fields start with {@code params._source.foo} while in ES2.x    * {@code _source.foo}. Helper method to build correct query based on runtime version of elastic.    * Used to keep backwards compatibility with ES2.    *    * @see<a href="https://github.com/elastic/elasticsearch/issues/20068">_source variable</a>    * @see<a href="https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting-fields.html">Scripted Fields</a>    * @return string to be used for scripted fields    */
specifier|protected
specifier|abstract
name|String
name|scriptedFieldPrefix
parameter_list|()
function_decl|;
comment|/** Executes a "find" operation on the underlying type.    *    *<p>For example,    *<code>client.prepareSearch(index).setTypes(type)    * .setSource("{\"fields\" : [\"state\"]}")</code></p>    *    * @param index Elasticsearch index    * @param ops List of operations represented as Json strings.    * @param fields List of fields to project; or null to return map    * @return Enumerator of results    */
specifier|protected
specifier|abstract
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
function_decl|;
comment|/**    * Implementation of {@link Queryable} based on    * a {@link AbstractElasticsearchTable}.    *    * @param<T> element type    */
specifier|public
specifier|static
class|class
name|ElasticsearchQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
block|{
name|ElasticsearchQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|AbstractElasticsearchTable
name|table
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|super
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|table
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|private
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|schema
operator|.
name|unwrap
argument_list|(
name|ElasticsearchSchema
operator|.
name|class
argument_list|)
operator|.
name|getIndex
argument_list|()
return|;
block|}
specifier|private
name|AbstractElasticsearchTable
name|getTable
parameter_list|()
block|{
return|return
operator|(
name|AbstractElasticsearchTable
operator|)
name|table
return|;
block|}
comment|/** Called via code-generation.      * @param ops list of queries (as strings)      * @param fields projection      * @see ElasticsearchMethod#ELASTICSEARCH_QUERYABLE_FIND      * @return result as enumerable      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|find
parameter_list|(
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
return|return
name|getTable
argument_list|()
operator|.
name|find
argument_list|(
name|getIndex
argument_list|()
argument_list|,
name|ops
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AbstractElasticsearchTable.java
end_comment

end_unit
