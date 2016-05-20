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
name|cassandra
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
name|RelFieldCollation
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
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
name|RelDataTypeSystem
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
name|RelProtoDataType
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
name|SqlTypeFactoryImpl
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
name|Pair
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
name|datastax
operator|.
name|driver
operator|.
name|core
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|datastax
operator|.
name|driver
operator|.
name|core
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Table based on a Cassandra column family  */
end_comment

begin_class
specifier|public
class|class
name|CassandraTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
name|RelProtoDataType
name|protoRowType
decl_stmt|;
name|Pair
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|keyFields
decl_stmt|;
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|clusteringOrder
decl_stmt|;
specifier|private
specifier|final
name|CassandraSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|String
name|columnFamily
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|view
decl_stmt|;
specifier|public
name|CassandraTable
parameter_list|(
name|CassandraSchema
name|schema
parameter_list|,
name|String
name|columnFamily
parameter_list|,
name|boolean
name|view
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
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|columnFamily
operator|=
name|columnFamily
expr_stmt|;
name|this
operator|.
name|view
operator|=
name|view
expr_stmt|;
block|}
specifier|public
name|CassandraTable
parameter_list|(
name|CassandraSchema
name|schema
parameter_list|,
name|String
name|columnFamily
parameter_list|)
block|{
name|this
argument_list|(
name|schema
argument_list|,
name|columnFamily
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CassandraTable {"
operator|+
name|columnFamily
operator|+
literal|"}"
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
if|if
condition|(
name|protoRowType
operator|==
literal|null
condition|)
block|{
name|protoRowType
operator|=
name|schema
operator|.
name|getRelDataType
argument_list|(
name|columnFamily
argument_list|,
name|view
argument_list|)
expr_stmt|;
block|}
return|return
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|Pair
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getKeyFields
parameter_list|()
block|{
if|if
condition|(
name|keyFields
operator|==
literal|null
condition|)
block|{
name|keyFields
operator|=
name|schema
operator|.
name|getKeyFields
argument_list|(
name|columnFamily
argument_list|,
name|view
argument_list|)
expr_stmt|;
block|}
return|return
name|keyFields
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|getClusteringOrder
parameter_list|()
block|{
if|if
condition|(
name|clusteringOrder
operator|==
literal|null
condition|)
block|{
name|clusteringOrder
operator|=
name|schema
operator|.
name|getClusteringOrder
argument_list|(
name|columnFamily
argument_list|,
name|view
argument_list|)
expr_stmt|;
block|}
return|return
name|clusteringOrder
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|query
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|session
argument_list|,
name|Collections
operator|.
expr|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** Executes a CQL query on the underlying table.    *    * @param session Cassandra session    * @param fields List of fields to project    * @param predicates A list of predicates which should be used in the query    * @return Enumerator of results    */
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|query
parameter_list|(
specifier|final
name|Session
name|session
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
name|String
argument_list|>
argument_list|>
name|selectFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|predicates
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|order
parameter_list|,
specifier|final
name|Integer
name|offset
parameter_list|,
specifier|final
name|Integer
name|fetch
parameter_list|)
block|{
comment|// Build the type of the resulting row based on the provided fields
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
name|fieldInfo
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
name|addField
init|=
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|SqlTypeName
name|typeName
init|=
name|rowType
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|selectFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|field
range|:
name|fields
control|)
block|{
name|addField
operator|.
name|apply
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|field
range|:
name|selectFields
control|)
block|{
name|addField
operator|.
name|apply
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RelProtoDataType
name|resultRowType
init|=
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|fieldInfo
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// Construct the list of fields to project
specifier|final
name|String
name|selectString
decl_stmt|;
if|if
condition|(
name|selectFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|selectString
operator|=
literal|"*"
expr_stmt|;
block|}
else|else
block|{
name|selectString
operator|=
name|Util
operator|.
name|toString
argument_list|(
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|selectIterator
init|=
name|selectFields
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|selectIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|selectIterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" AS "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
literal|""
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// Combine all predicates conjunctively
name|String
name|whereClause
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
name|predicates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|whereClause
operator|=
literal|" WHERE "
expr_stmt|;
name|whereClause
operator|+=
name|Util
operator|.
name|toString
argument_list|(
name|predicates
argument_list|,
literal|""
argument_list|,
literal|" AND "
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// Build and issue the query and return an Enumerator over the results
name|StringBuilder
name|queryBuilder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SELECT "
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|selectString
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" FROM \""
operator|+
name|columnFamily
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|whereClause
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|order
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|Util
operator|.
name|toString
argument_list|(
name|order
argument_list|,
literal|" ORDER BY "
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|limit
init|=
name|offset
decl_stmt|;
if|if
condition|(
name|fetch
operator|>=
literal|0
condition|)
block|{
name|limit
operator|+=
name|fetch
expr_stmt|;
block|}
if|if
condition|(
name|limit
operator|>
literal|0
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" LIMIT "
operator|+
name|limit
argument_list|)
expr_stmt|;
block|}
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" ALLOW FILTERING"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query
init|=
name|queryBuilder
operator|.
name|toString
argument_list|()
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
name|ResultSet
name|results
init|=
name|session
operator|.
name|execute
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|// Skip results until we get to the right offset
name|int
name|skip
init|=
literal|0
decl_stmt|;
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
init|=
operator|new
name|CassandraEnumerator
argument_list|(
name|results
argument_list|,
name|resultRowType
argument_list|)
decl_stmt|;
while|while
condition|(
name|skip
operator|<
name|offset
operator|&&
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|skip
operator|++
expr_stmt|;
block|}
return|return
name|enumerator
return|;
block|}
block|}
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
name|CassandraQueryable
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
name|CassandraTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CassandraRel
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
comment|/** Implementation of {@link org.apache.calcite.linq4j.Queryable} based on    * a {@link org.apache.calcite.adapter.cassandra.CassandraTable}. */
specifier|public
specifier|static
class|class
name|CassandraQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
block|{
specifier|public
name|CassandraQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|CassandraTable
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
name|query
argument_list|(
name|getSession
argument_list|()
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
name|CassandraTable
name|getTable
parameter_list|()
block|{
return|return
operator|(
name|CassandraTable
operator|)
name|table
return|;
block|}
specifier|private
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|schema
operator|.
name|unwrap
argument_list|(
name|CassandraSchema
operator|.
name|class
argument_list|)
operator|.
name|session
return|;
block|}
comment|/** Called via code-generation.      *      * @see org.apache.calcite.adapter.cassandra.CassandraMethod#CASSANDRA_QUERYABLE_QUERY      */
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
name|query
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|selectFields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|predicates
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|order
parameter_list|,
name|Integer
name|offset
parameter_list|,
name|Integer
name|fetch
parameter_list|)
block|{
return|return
name|getTable
argument_list|()
operator|.
name|query
argument_list|(
name|getSession
argument_list|()
argument_list|,
name|fields
argument_list|,
name|selectFields
argument_list|,
name|predicates
argument_list|,
name|order
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CassandraTable.java
end_comment

end_unit

