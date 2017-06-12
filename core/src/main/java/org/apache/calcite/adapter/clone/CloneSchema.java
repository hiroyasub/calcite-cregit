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
name|clone
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
name|JavaTypeFactory
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
name|jdbc
operator|.
name|JdbcSchema
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
name|ColumnMetaData
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
name|CalciteConnection
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
name|rel
operator|.
name|RelCollation
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
name|RelCollations
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
name|QueryableTable
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
name|Schema
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
name|SchemaFactory
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
name|Schemas
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|Suppliers
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
import|import static
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
operator|.
name|MATERIALIZATION_CONNECTION
import|;
end_import

begin_comment
comment|/**  * Schema that contains in-memory copies of tables from a JDBC schema.  */
end_comment

begin_class
specifier|public
class|class
name|CloneSchema
extends|extends
name|AbstractSchema
block|{
comment|// TODO: implement 'driver' property
comment|// TODO: implement 'source' property
comment|// TODO: test Factory
specifier|private
specifier|final
name|SchemaPlus
name|sourceSchema
decl_stmt|;
comment|/**    * Creates a CloneSchema.    *    * @param sourceSchema JDBC data source    */
specifier|public
name|CloneSchema
parameter_list|(
name|SchemaPlus
name|sourceSchema
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceSchema
operator|=
name|sourceSchema
expr_stmt|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|sourceSchema
operator|.
name|getTableNames
argument_list|()
control|)
block|{
specifier|final
name|Table
name|table
init|=
name|sourceSchema
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|QueryableTable
condition|)
block|{
specifier|final
name|QueryableTable
name|sourceTable
init|=
operator|(
name|QueryableTable
operator|)
name|table
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|createCloneTable
argument_list|(
name|MATERIALIZATION_CONNECTION
argument_list|,
name|sourceTable
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|private
name|Table
name|createCloneTable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|QueryableTable
name|sourceTable
parameter_list|,
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Queryable
argument_list|<
name|Object
argument_list|>
name|queryable
init|=
name|sourceTable
operator|.
name|asQueryable
argument_list|(
name|queryProvider
argument_list|,
name|sourceSchema
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
operator|(
name|CalciteConnection
operator|)
name|queryProvider
operator|)
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|createCloneTable
argument_list|(
name|typeFactory
argument_list|,
name|Schemas
operator|.
name|proto
argument_list|(
name|sourceTable
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
argument_list|,
literal|null
argument_list|,
name|queryable
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Table
name|createCloneTable
parameter_list|(
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|RelProtoDataType
name|protoRowType
parameter_list|,
specifier|final
name|List
argument_list|<
name|ColumnMetaData
operator|.
name|Rep
argument_list|>
name|repList
parameter_list|,
specifier|final
name|Enumerable
argument_list|<
name|T
argument_list|>
name|source
parameter_list|)
block|{
return|return
name|createCloneTable
argument_list|(
name|typeFactory
argument_list|,
name|protoRowType
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
argument_list|,
name|repList
argument_list|,
name|source
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Table
name|createCloneTable
parameter_list|(
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|,
specifier|final
name|RelProtoDataType
name|protoRowType
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|,
specifier|final
name|List
argument_list|<
name|ColumnMetaData
operator|.
name|Rep
argument_list|>
name|repList
parameter_list|,
specifier|final
name|Enumerable
argument_list|<
name|T
argument_list|>
name|source
parameter_list|)
block|{
specifier|final
name|Type
name|elementType
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|QueryableTable
condition|)
block|{
name|elementType
operator|=
operator|(
operator|(
name|QueryableTable
operator|)
name|source
operator|)
operator|.
name|getElementType
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|repList
operator|!=
literal|null
condition|)
block|{
name|elementType
operator|=
name|repList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|clazz
expr_stmt|;
block|}
else|else
block|{
name|elementType
operator|=
name|Object
operator|.
name|class
expr_stmt|;
block|}
block|}
else|else
block|{
name|elementType
operator|=
name|Object
index|[]
operator|.
name|class
expr_stmt|;
block|}
return|return
operator|new
name|ArrayTable
argument_list|(
name|elementType
argument_list|,
name|protoRowType
argument_list|,
name|Suppliers
operator|.
name|memoize
argument_list|(
operator|new
name|Supplier
argument_list|<
name|ArrayTable
operator|.
name|Content
argument_list|>
argument_list|()
block|{
specifier|public
name|ArrayTable
operator|.
name|Content
name|get
parameter_list|()
block|{
specifier|final
name|ColumnLoader
name|loader
init|=
operator|new
name|ColumnLoader
argument_list|<>
argument_list|(
name|typeFactory
argument_list|,
name|source
argument_list|,
name|protoRowType
argument_list|,
name|repList
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collation2
init|=
name|collations
operator|.
name|isEmpty
argument_list|()
operator|&&
name|loader
operator|.
name|sortField
operator|>=
literal|0
condition|?
name|RelCollations
operator|.
name|createSingleton
argument_list|(
name|loader
operator|.
name|sortField
argument_list|)
else|:
name|collations
decl_stmt|;
return|return
operator|new
name|ArrayTable
operator|.
name|Content
argument_list|(
name|loader
operator|.
name|representationValues
argument_list|,
name|loader
operator|.
name|size
argument_list|()
argument_list|,
name|collation2
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
comment|/** Schema factory that creates a    * {@link org.apache.calcite.adapter.clone.CloneSchema}.    * This allows you to create a clone schema inside a model.json file.    *    *<blockquote><pre>    * {    *   version: '1.0',    *   defaultSchema: 'FOODMART_CLONE',    *   schemas: [    *     {    *       name: 'FOODMART_CLONE',    *       type: 'custom',    *       factory: 'org.apache.calcite.adapter.clone.CloneSchema$Factory',    *       operand: {    *         jdbcDriver: 'com.mysql.jdbc.Driver',    *         jdbcUrl: 'jdbc:mysql://localhost/foodmart',    *         jdbcUser: 'foodmart',    *         jdbcPassword: 'foodmart'    *       }    *     }    *   ]    * }</pre></blockquote>    */
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|SchemaFactory
block|{
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
name|SchemaPlus
name|schema
init|=
name|parentSchema
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|JdbcSchema
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|name
operator|+
literal|"$source"
argument_list|,
name|operand
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|CloneSchema
argument_list|(
name|schema
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CloneSchema.java
end_comment

end_unit

