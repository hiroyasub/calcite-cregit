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
name|geode
operator|.
name|rel
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
name|geode
operator|.
name|util
operator|.
name|JavaTypeFactoryExtImpl
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
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|query
operator|.
name|QueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|query
operator|.
name|SelectResults
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
name|ImmutableList
operator|.
name|Builder
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
comment|/**  * Table based on a Geode Region  */
end_comment

begin_class
specifier|public
class|class
name|GeodeTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GeodeTable
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|GeodeSchema
name|schema
decl_stmt|;
specifier|private
name|String
name|regionName
decl_stmt|;
specifier|private
name|RelDataType
name|relDataType
decl_stmt|;
specifier|private
name|ClientCache
name|clientCache
decl_stmt|;
specifier|public
name|GeodeTable
parameter_list|(
name|GeodeSchema
name|schema
parameter_list|,
name|String
name|regionName
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|,
name|ClientCache
name|clientCache
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
name|regionName
operator|=
name|regionName
expr_stmt|;
name|this
operator|.
name|relDataType
operator|=
name|relDataType
expr_stmt|;
name|this
operator|.
name|clientCache
operator|=
name|clientCache
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"GeodeTable {"
operator|+
name|regionName
operator|+
literal|"}"
return|;
block|}
comment|/**    * Executes an OQL query on the underlying table.    *    *<p>Called by the {@link GeodeQueryable} which in turn is    * called via the generated code.    *    * @param clientCache Geode client cache    * @param fields      List of fields to project    * @param predicates  A list of predicates which should be used in the query    * @return Enumerator of results    */
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|query
parameter_list|(
specifier|final
name|ClientCache
name|clientCache
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
name|aggregateFunctions
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupByFields
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
name|orderByFields
parameter_list|,
name|String
name|limit
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryExtImpl
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|fieldInfo
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|SqlTypeName
name|typeName
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
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
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aggFuncMap
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|aggregateFunctions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aggFuncMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|e
range|:
name|aggregateFunctions
control|)
block|{
name|aggFuncMapBuilder
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aggFuncMap
operator|=
name|aggFuncMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|// Construct the list of fields to project
name|Builder
argument_list|<
name|String
argument_list|>
name|selectBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|groupByFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|groupByField
range|:
name|groupByFields
control|)
block|{
name|selectBuilder
operator|.
name|add
argument_list|(
name|groupByField
operator|+
literal|" AS "
operator|+
name|groupByField
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|aggFuncMap
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
name|String
argument_list|>
name|e
range|:
name|aggFuncMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|selectBuilder
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|" AS "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|selectFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|aggFuncMap
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
name|String
argument_list|>
name|e
range|:
name|aggFuncMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|selectBuilder
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|" AS "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|selectBuilder
operator|.
name|add
argument_list|(
literal|"*"
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
name|selectBuilder
operator|.
name|add
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
operator|+
literal|" AS "
operator|+
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|String
name|oqlSelectStatement
init|=
name|Util
operator|.
name|toString
argument_list|(
name|selectBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|" "
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
decl_stmt|;
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
name|oqlSelectStatement
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" FROM /"
operator|+
name|regionName
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
name|groupByFields
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
name|groupByFields
argument_list|,
literal|" GROUP BY "
argument_list|,
literal|", "
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|orderByFields
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
name|orderByFields
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
if|if
condition|(
name|limit
operator|!=
literal|null
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
specifier|final
name|String
name|oqlQuery
init|=
name|queryBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"OQL: "
operator|+
name|oqlQuery
argument_list|)
expr_stmt|;
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
name|SelectResults
name|results
init|=
literal|null
decl_stmt|;
name|QueryService
name|queryService
init|=
name|clientCache
operator|.
name|getQueryService
argument_list|()
decl_stmt|;
try|try
block|{
name|results
operator|=
operator|(
name|SelectResults
operator|)
name|queryService
operator|.
name|newQuery
argument_list|(
name|oqlQuery
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|GeodeEnumerator
argument_list|(
name|results
argument_list|,
name|resultRowType
argument_list|)
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
name|GeodeQueryable
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
name|GeodeTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|GeodeRel
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
return|return
name|relDataType
return|;
block|}
comment|/**    * Implementation of {@link Queryable} based on a {@link GeodeTable}.    *    * @param<T> type    */
specifier|public
specifier|static
class|class
name|GeodeQueryable
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
name|GeodeQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|GeodeTable
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
comment|// tzolov: this should never be called for queryable tables???
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Enumerator on Queryable should never be called"
argument_list|)
throw|;
block|}
specifier|private
name|GeodeTable
name|getTable
parameter_list|()
block|{
return|return
operator|(
name|GeodeTable
operator|)
name|table
return|;
block|}
specifier|private
name|ClientCache
name|getClientCache
parameter_list|()
block|{
return|return
name|schema
operator|.
name|unwrap
argument_list|(
name|GeodeSchema
operator|.
name|class
argument_list|)
operator|.
name|clientCache
return|;
block|}
comment|/**      * Called via code-generation.      */
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|aggregateFunctions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|groupByFields
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
name|String
name|limit
parameter_list|)
block|{
return|return
name|getTable
argument_list|()
operator|.
name|query
argument_list|(
name|getClientCache
argument_list|()
argument_list|,
name|fields
argument_list|,
name|selectFields
argument_list|,
name|aggregateFunctions
argument_list|,
name|groupByFields
argument_list|,
name|predicates
argument_list|,
name|order
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End GeodeTable.java
end_comment

end_unit
