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
name|avatica
operator|.
name|util
operator|.
name|Casing
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
name|jdbc
operator|.
name|CalciteSchema
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
name|runtime
operator|.
name|Hook
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
name|Table
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
name|AbstractSchema
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
name|MaterializedViewTable
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
name|SqlDialect
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
name|SqlSelect
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
name|SqlWriter
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
name|parser
operator|.
name|SqlParseException
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
name|parser
operator|.
name|SqlParser
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
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
name|AbstractTableMetadata
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
name|Cluster
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
name|ClusteringOrder
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
name|ColumnMetadata
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
name|DataType
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
name|KeyspaceMetadata
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
name|MaterializedViewMetadata
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
name|com
operator|.
name|datastax
operator|.
name|driver
operator|.
name|core
operator|.
name|TableMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

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
name|ImmutableList
import|;
end_import

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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
comment|/**  * Schema mapped onto a Cassandra column family  */
end_comment

begin_class
specifier|public
class|class
name|CassandraSchema
extends|extends
name|AbstractSchema
block|{
specifier|final
name|Session
name|session
decl_stmt|;
specifier|final
name|String
name|keyspace
decl_stmt|;
specifier|private
specifier|final
name|SchemaPlus
name|parentSchema
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|/**    * Creates a Cassandra schema.    *    * @param host Cassandra host, e.g. "localhost"    * @param keyspace Cassandra keyspace name, e.g. "twissandra"    */
specifier|public
name|CassandraSchema
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|keyspace
parameter_list|,
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyspace
operator|=
name|keyspace
expr_stmt|;
try|try
block|{
name|Cluster
name|cluster
init|=
name|Cluster
operator|.
name|builder
argument_list|()
operator|.
name|addContactPoint
argument_list|(
name|host
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|session
operator|=
name|cluster
operator|.
name|connect
argument_list|(
name|keyspace
argument_list|)
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
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|parentSchema
operator|=
name|parentSchema
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|Hook
operator|.
name|TRIMMED
operator|.
name|add
argument_list|(
operator|new
name|Function
argument_list|<
name|RelNode
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
name|CassandraSchema
operator|.
name|this
operator|.
name|addMaterializedViews
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|RelProtoDataType
name|getRelDataType
parameter_list|(
name|String
name|columnFamily
parameter_list|,
name|boolean
name|view
parameter_list|)
block|{
name|List
argument_list|<
name|ColumnMetadata
argument_list|>
name|columns
decl_stmt|;
if|if
condition|(
name|view
condition|)
block|{
name|columns
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getMaterializedView
argument_list|(
name|columnFamily
argument_list|)
operator|.
name|getColumns
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|columns
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getTable
argument_list|(
name|columnFamily
argument_list|)
operator|.
name|getColumns
argument_list|()
expr_stmt|;
block|}
comment|// Temporary type factory, just for the duration of this method. Allowable
comment|// because we're creating a proto-type, not a type; before being used, the
comment|// proto-type will be copied into a real type factory.
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
for|for
control|(
name|ColumnMetadata
name|column
range|:
name|columns
control|)
block|{
specifier|final
name|String
name|columnName
init|=
name|column
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|DataType
name|type
init|=
name|column
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// TODO: This mapping of types can be done much better
name|SqlTypeName
name|typeName
init|=
name|SqlTypeName
operator|.
name|ANY
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DataType
operator|.
name|uuid
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|timeuuid
argument_list|()
condition|)
block|{
comment|// We currently rely on this in CassandraFilter to detect UUID columns.
comment|// That is, these fixed length literals should be unquoted in CQL.
name|typeName
operator|=
name|SqlTypeName
operator|.
name|CHAR
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|DataType
operator|.
name|ascii
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|text
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|varchar
argument_list|()
condition|)
block|{
name|typeName
operator|=
name|SqlTypeName
operator|.
name|VARCHAR
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|DataType
operator|.
name|cint
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|varint
argument_list|()
condition|)
block|{
name|typeName
operator|=
name|SqlTypeName
operator|.
name|INTEGER
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|DataType
operator|.
name|bigint
argument_list|()
condition|)
block|{
name|typeName
operator|=
name|SqlTypeName
operator|.
name|BIGINT
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|DataType
operator|.
name|cdouble
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|cfloat
argument_list|()
operator|||
name|type
operator|==
name|DataType
operator|.
name|decimal
argument_list|()
condition|)
block|{
name|typeName
operator|=
name|SqlTypeName
operator|.
name|DOUBLE
expr_stmt|;
block|}
name|fieldInfo
operator|.
name|add
argument_list|(
name|columnName
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
block|}
return|return
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|fieldInfo
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get all primary key columns from the underlying CQL table    *    * @return A list of field names that are part of the partition and clustering keys    */
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
parameter_list|(
name|String
name|columnFamily
parameter_list|,
name|boolean
name|view
parameter_list|)
block|{
name|AbstractTableMetadata
name|table
decl_stmt|;
if|if
condition|(
name|view
condition|)
block|{
name|table
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getMaterializedView
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getTable
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ColumnMetadata
argument_list|>
name|partitionKey
init|=
name|table
operator|.
name|getPartitionKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pKeyFields
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnMetadata
name|column
range|:
name|partitionKey
control|)
block|{
name|pKeyFields
operator|.
name|add
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ColumnMetadata
argument_list|>
name|clusteringKey
init|=
name|table
operator|.
name|getClusteringColumns
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cKeyFields
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnMetadata
name|column
range|:
name|clusteringKey
control|)
block|{
name|cKeyFields
operator|.
name|add
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|pKeyFields
argument_list|)
argument_list|,
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|cKeyFields
argument_list|)
argument_list|)
return|;
block|}
comment|/** Get the collation of all clustering key columns.    *    * @return A RelCollations representing the collation of all clustering keys    */
specifier|public
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|getClusteringOrder
parameter_list|(
name|String
name|columnFamily
parameter_list|,
name|boolean
name|view
parameter_list|)
block|{
name|AbstractTableMetadata
name|table
decl_stmt|;
if|if
condition|(
name|view
condition|)
block|{
name|table
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getMaterializedView
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|=
name|getKeyspace
argument_list|()
operator|.
name|getTable
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ClusteringOrder
argument_list|>
name|clusteringOrder
init|=
name|table
operator|.
name|getClusteringOrder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|keyCollations
init|=
operator|new
name|ArrayList
argument_list|<
name|RelFieldCollation
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ClusteringOrder
name|order
range|:
name|clusteringOrder
control|)
block|{
name|RelFieldCollation
operator|.
name|Direction
name|direction
decl_stmt|;
switch|switch
condition|(
name|order
condition|)
block|{
case|case
name|DESC
case|:
name|direction
operator|=
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
expr_stmt|;
break|break;
case|case
name|ASC
case|:
default|default:
name|direction
operator|=
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
expr_stmt|;
break|break;
block|}
name|keyCollations
operator|.
name|add
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|i
argument_list|,
name|direction
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|keyCollations
return|;
block|}
comment|/** Add all materialized views defined in the schema to this column family    */
specifier|private
name|void
name|addMaterializedViews
parameter_list|()
block|{
for|for
control|(
name|MaterializedViewMetadata
name|view
range|:
name|getKeyspace
argument_list|()
operator|.
name|getMaterializedViews
argument_list|()
control|)
block|{
name|String
name|tableName
init|=
name|view
operator|.
name|getBaseTable
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|StringBuilder
name|queryBuilder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SELECT "
argument_list|)
decl_stmt|;
comment|// Add all the selected columns to the query
name|List
argument_list|<
name|String
argument_list|>
name|columnNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ColumnMetadata
name|column
range|:
name|view
operator|.
name|getColumns
argument_list|()
control|)
block|{
name|columnNames
operator|.
name|add
argument_list|(
literal|"\""
operator|+
name|column
operator|.
name|getName
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|queryBuilder
operator|.
name|append
argument_list|(
name|Util
operator|.
name|toString
argument_list|(
name|columnNames
argument_list|,
literal|""
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" FROM \""
operator|+
name|tableName
operator|+
literal|"\""
argument_list|)
expr_stmt|;
comment|// Get the where clause from the system schema
name|String
name|whereQuery
init|=
literal|"SELECT where_clause from system_schema.views "
operator|+
literal|"WHERE keyspace_name='"
operator|+
name|keyspace
operator|+
literal|"' AND view_name='"
operator|+
name|view
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
decl_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" WHERE "
operator|+
name|session
operator|.
name|execute
argument_list|(
name|whereQuery
argument_list|)
operator|.
name|one
argument_list|()
operator|.
name|getString
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Parse and unparse the view query to get properly quoted field names
name|String
name|query
init|=
name|queryBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SqlParser
operator|.
name|ConfigBuilder
name|configBuilder
init|=
name|SqlParser
operator|.
name|configBuilder
argument_list|()
decl_stmt|;
name|configBuilder
operator|.
name|setUnquotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
expr_stmt|;
name|SqlSelect
name|parsedQuery
decl_stmt|;
try|try
block|{
name|parsedQuery
operator|=
operator|(
name|SqlSelect
operator|)
name|SqlParser
operator|.
name|create
argument_list|(
name|query
argument_list|,
name|configBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|parseQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Could not parse query {} for CQL view {}.{}"
argument_list|,
name|query
argument_list|,
name|keyspace
argument_list|,
name|view
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|(
name|query
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|SqlWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|CALCITE
argument_list|,
literal|true
argument_list|,
name|printWriter
argument_list|)
decl_stmt|;
name|parsedQuery
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|query
operator|=
name|stringWriter
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// Add the view for this query
name|String
name|viewName
init|=
literal|"$"
operator|+
name|getTableNames
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|SchemaPlus
name|schema
init|=
name|parentSchema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|CalciteSchema
name|calciteSchema
init|=
name|CalciteSchema
operator|.
name|from
argument_list|(
name|schema
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
init|=
name|calciteSchema
operator|.
name|path
argument_list|(
name|viewName
argument_list|)
decl_stmt|;
name|schema
operator|.
name|add
argument_list|(
name|viewName
argument_list|,
name|MaterializedViewTable
operator|.
name|create
argument_list|(
name|calciteSchema
argument_list|,
name|query
argument_list|,
literal|null
argument_list|,
name|viewPath
argument_list|,
name|view
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|TableMetadata
name|table
range|:
name|getKeyspace
argument_list|()
operator|.
name|getTables
argument_list|()
control|)
block|{
name|String
name|tableName
init|=
name|table
operator|.
name|getName
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|tableName
argument_list|,
operator|new
name|CassandraTable
argument_list|(
name|this
argument_list|,
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|MaterializedViewMetadata
name|view
range|:
name|table
operator|.
name|getViews
argument_list|()
control|)
block|{
name|String
name|viewName
init|=
name|view
operator|.
name|getName
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|viewName
argument_list|,
operator|new
name|CassandraTable
argument_list|(
name|this
argument_list|,
name|viewName
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|KeyspaceMetadata
name|getKeyspace
parameter_list|()
block|{
return|return
name|session
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|getKeyspace
argument_list|(
name|keyspace
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End CassandraSchema.java
end_comment

end_unit

