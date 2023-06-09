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
name|mongodb
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
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|FindIterable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|BsonDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bson
operator|.
name|conversions
operator|.
name|Bson
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Table based on a MongoDB collection.  */
end_comment

begin_class
specifier|public
class|class
name|MongoTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|private
specifier|final
name|String
name|collectionName
decl_stmt|;
comment|/** Creates a MongoTable. */
name|MongoTable
parameter_list|(
name|String
name|collectionName
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
name|collectionName
operator|=
name|collectionName
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
literal|"MongoTable {"
operator|+
name|collectionName
operator|+
literal|"}"
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|RelDataType
name|mapType
init|=
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
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
name|typeFactory
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
annotation|@
name|Override
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
name|MongoQueryable
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
annotation|@
name|Override
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
name|MongoTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|MongoRel
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
comment|/** Executes a "find" operation on the underlying collection.    *    *<p>For example,    *<code>zipsTable.find("{state: 'OR'}", "{city: 1, zipcode: 1}")</code></p>    *    * @param mongoDb MongoDB connection    * @param filterJson Filter JSON string, or null    * @param projectJson Project JSON string, or null    * @param fields List of fields to project; or null to return map    * @return Enumerator of results    */
specifier|private
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|find
parameter_list|(
name|MongoDatabase
name|mongoDb
parameter_list|,
name|String
name|filterJson
parameter_list|,
name|String
name|projectJson
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
name|MongoCollection
name|collection
init|=
name|mongoDb
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
specifier|final
name|Bson
name|filter
init|=
name|filterJson
operator|==
literal|null
condition|?
literal|null
else|:
name|BsonDocument
operator|.
name|parse
argument_list|(
name|filterJson
argument_list|)
decl_stmt|;
specifier|final
name|Bson
name|project
init|=
name|projectJson
operator|==
literal|null
condition|?
literal|null
else|:
name|BsonDocument
operator|.
name|parse
argument_list|(
name|projectJson
argument_list|)
decl_stmt|;
specifier|final
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|getter
init|=
name|MongoEnumerator
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
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|FindIterable
argument_list|<
name|Document
argument_list|>
name|cursor
init|=
name|collection
operator|.
name|find
argument_list|(
name|filter
argument_list|)
operator|.
name|projection
argument_list|(
name|project
argument_list|)
decl_stmt|;
return|return
operator|new
name|MongoEnumerator
argument_list|(
name|cursor
operator|.
name|iterator
argument_list|()
argument_list|,
name|getter
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Executes an "aggregate" operation on the underlying collection.    *    *<p>For example:    *<code>zipsTable.aggregate(    * "{$filter: {state: 'OR'}",    * "{$group: {_id: '$city', c: {$sum: 1}, p: {$sum: '$pop'}}}")    *</code></p>    *    * @param mongoDb MongoDB connection    * @param fields List of fields to project; or null to return map    * @param operations One or more JSON strings    * @return Enumerator of results    */
specifier|private
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|aggregate
parameter_list|(
specifier|final
name|MongoDatabase
name|mongoDb
parameter_list|,
specifier|final
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
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|operations
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Bson
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|operation
range|:
name|operations
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|BsonDocument
operator|.
name|parse
argument_list|(
name|operation
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Function1
argument_list|<
name|Document
argument_list|,
name|Object
argument_list|>
name|getter
init|=
name|MongoEnumerator
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
annotation|@
name|Override
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
name|Document
argument_list|>
name|resultIterator
decl_stmt|;
try|try
block|{
name|resultIterator
operator|=
name|mongoDb
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
operator|.
name|aggregate
argument_list|(
name|list
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"While running MongoDB query "
operator|+
name|Util
operator|.
name|toString
argument_list|(
name|operations
argument_list|,
literal|"["
argument_list|,
literal|",\n"
argument_list|,
literal|"]"
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|MongoEnumerator
argument_list|(
name|resultIterator
argument_list|,
name|getter
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Implementation of {@link org.apache.calcite.linq4j.Queryable} based on    * a {@link org.apache.calcite.adapter.mongodb.MongoTable}.    *    * @param<T> element type */
specifier|public
specifier|static
class|class
name|MongoQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
block|{
name|MongoQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|MongoTable
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
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
comment|//noinspection unchecked
specifier|final
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
init|=
operator|(
name|Enumerable
argument_list|<
name|T
argument_list|>
operator|)
name|getTable
argument_list|()
operator|.
name|find
argument_list|(
name|getMongoDb
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|private
name|MongoDatabase
name|getMongoDb
parameter_list|()
block|{
return|return
name|schema
operator|.
name|unwrap
argument_list|(
name|MongoSchema
operator|.
name|class
argument_list|)
operator|.
name|mongoDb
return|;
block|}
specifier|private
name|MongoTable
name|getTable
parameter_list|()
block|{
return|return
operator|(
name|MongoTable
operator|)
name|table
return|;
block|}
comment|/** Called via code-generation.      *      * @see org.apache.calcite.adapter.mongodb.MongoMethod#MONGO_QUERYABLE_AGGREGATE      */
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
name|aggregate
parameter_list|(
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
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|operations
parameter_list|)
block|{
return|return
name|getTable
argument_list|()
operator|.
name|aggregate
argument_list|(
name|getMongoDb
argument_list|()
argument_list|,
name|fields
argument_list|,
name|operations
argument_list|)
return|;
block|}
comment|/** Called via code-generation.      *      * @param filterJson Filter document      * @param projectJson Projection document      * @param fields List of expected fields (and their types)      * @return result of mongo query      *      * @see org.apache.calcite.adapter.mongodb.MongoMethod#MONGO_QUERYABLE_FIND      */
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
name|String
name|filterJson
parameter_list|,
name|String
name|projectJson
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
name|getMongoDb
argument_list|()
argument_list|,
name|filterJson
argument_list|,
name|projectJson
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

