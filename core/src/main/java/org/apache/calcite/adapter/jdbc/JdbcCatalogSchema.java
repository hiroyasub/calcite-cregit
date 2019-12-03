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
name|jdbc
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
name|DataContext
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
name|tree
operator|.
name|Expression
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
name|tree
operator|.
name|Expressions
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
name|SqlDialectFactory
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
name|SqlDialectFactoryImpl
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
name|BuiltInMethod
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_comment
comment|/**  * Schema based upon a JDBC catalog (database).  *  *<p>This schema does not directly contain tables, but contains a sub-schema  * for each schema in the catalog in the back-end. Each of those sub-schemas is  * an instance of {@link JdbcSchema}.  *  *<p>This schema is lazy: it does not compute the list of schema names until  * the first call to {@link #getSubSchemaMap()}. Then it creates a  * {@link JdbcSchema} for each schema name. Each JdbcSchema will populate its  * tables on demand.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcCatalogSchema
extends|extends
name|AbstractSchema
block|{
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
specifier|public
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
specifier|final
name|JdbcConvention
name|convention
decl_stmt|;
specifier|final
name|String
name|catalog
decl_stmt|;
comment|/** Sub-schemas by name, lazily initialized. */
specifier|final
name|Supplier
argument_list|<
name|SubSchemaMap
argument_list|>
name|subSchemaMapSupplier
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
parameter_list|()
lambda|->
name|computeSubSchemaMap
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Creates a JdbcCatalogSchema. */
specifier|public
name|JdbcCatalogSchema
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|SqlDialect
name|dialect
parameter_list|,
name|JdbcConvention
name|convention
parameter_list|,
name|String
name|catalog
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|dialect
argument_list|)
expr_stmt|;
name|this
operator|.
name|convention
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|convention
argument_list|)
expr_stmt|;
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
block|}
specifier|public
specifier|static
name|JdbcCatalogSchema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|DataSource
name|dataSource
parameter_list|,
name|String
name|catalog
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|dataSource
argument_list|,
name|SqlDialectFactoryImpl
operator|.
name|INSTANCE
argument_list|,
name|catalog
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|JdbcCatalogSchema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|DataSource
name|dataSource
parameter_list|,
name|SqlDialectFactory
name|dialectFactory
parameter_list|,
name|String
name|catalog
parameter_list|)
block|{
specifier|final
name|Expression
name|expression
init|=
name|parentSchema
operator|!=
literal|null
condition|?
name|Schemas
operator|.
name|subSchemaExpression
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|JdbcCatalogSchema
operator|.
name|class
argument_list|)
else|:
name|Expressions
operator|.
name|call
argument_list|(
name|DataContext
operator|.
name|ROOT
argument_list|,
name|BuiltInMethod
operator|.
name|DATA_CONTEXT_GET_ROOT_SCHEMA
operator|.
name|method
argument_list|)
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
init|=
name|JdbcSchema
operator|.
name|createDialect
argument_list|(
name|dialectFactory
argument_list|,
name|dataSource
argument_list|)
decl_stmt|;
specifier|final
name|JdbcConvention
name|convention
init|=
name|JdbcConvention
operator|.
name|of
argument_list|(
name|dialect
argument_list|,
name|expression
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
operator|new
name|JdbcCatalogSchema
argument_list|(
name|dataSource
argument_list|,
name|dialect
argument_list|,
name|convention
argument_list|,
name|catalog
argument_list|)
return|;
block|}
specifier|private
name|SubSchemaMap
name|computeSubSchemaMap
parameter_list|()
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|String
name|defaultSchemaName
decl_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
init|;
name|ResultSet
name|resultSet
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getSchemas
argument_list|(
name|catalog
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|defaultSchemaName
operator|=
name|connection
operator|.
name|getSchema
argument_list|()
expr_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
specifier|final
name|String
name|schemaName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|schemaName
argument_list|,
operator|new
name|JdbcSchema
argument_list|(
name|dataSource
argument_list|,
name|dialect
argument_list|,
name|convention
argument_list|,
name|catalog
argument_list|,
name|schemaName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
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
return|return
operator|new
name|SubSchemaMap
argument_list|(
name|defaultSchemaName
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|getSubSchemaMap
parameter_list|()
block|{
return|return
name|subSchemaMapSupplier
operator|.
name|get
argument_list|()
operator|.
name|map
return|;
block|}
comment|/** Returns the name of the default sub-schema. */
specifier|public
name|String
name|getDefaultSubSchemaName
parameter_list|()
block|{
return|return
name|subSchemaMapSupplier
operator|.
name|get
argument_list|()
operator|.
name|defaultSchemaName
return|;
block|}
comment|/** Returns the data source. */
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
return|return
name|dataSource
return|;
block|}
comment|/** Contains sub-schemas by name, and the name of the default schema. */
specifier|private
specifier|static
class|class
name|SubSchemaMap
block|{
specifier|final
name|String
name|defaultSchemaName
decl_stmt|;
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|map
decl_stmt|;
specifier|private
name|SubSchemaMap
parameter_list|(
name|String
name|defaultSchemaName
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|defaultSchemaName
operator|=
name|defaultSchemaName
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

