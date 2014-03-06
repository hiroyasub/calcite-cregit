begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|eigenbase
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
name|eigenbase
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|BasicDataSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
comment|/**  * Implementation of {@link Schema} that is backed by a JDBC data source.  *  *<p>The tables in the JDBC data source appear to be tables in this schema;  * queries against this schema are executed against those tables, pushing down  * as much as possible of the query logic to SQL.</p>  */
end_comment

begin_class
specifier|public
class|class
name|JdbcSchema
implements|implements
name|Schema
block|{
specifier|private
specifier|final
name|SchemaPlus
name|parentSchema
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
specifier|final
name|String
name|catalog
decl_stmt|;
specifier|final
name|String
name|schema
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
specifier|private
name|Supplier
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|JdbcTable
argument_list|>
argument_list|>
name|tableMapSupplier
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|JdbcTable
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|JdbcTable
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|computeTables
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**    * Creates a JDBC schema.    *    * @param name Schema name    * @param dataSource Data source    * @param dialect SQL dialect    * @param catalog Catalog name, or null    * @param schema Schema name pattern    */
specifier|public
name|JdbcSchema
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
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|catalog
operator|=
name|catalog
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|convention
operator|=
name|JdbcConvention
operator|.
name|of
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
assert|assert
name|dialect
operator|!=
literal|null
assert|;
assert|assert
name|dataSource
operator|!=
literal|null
assert|;
block|}
comment|/**    * Creates a JdbcSchema, taking credentials from a map.    *    * @param parentSchema Parent schema    * @param name Name    * @param operand Map of property/value pairs    * @return A JdbcSchema    */
specifier|public
specifier|static
name|JdbcSchema
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
name|DataSource
name|dataSource
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|dataSourceName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"dataSource"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataSourceName
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
operator|(
name|String
operator|)
name|dataSourceName
argument_list|)
decl_stmt|;
name|dataSource
operator|=
operator|(
name|DataSource
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|jdbcUrl
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcUrl"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jdbcDriver
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcDriver"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jdbcUser
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcUser"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jdbcPassword
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcPassword"
argument_list|)
decl_stmt|;
name|dataSource
operator|=
name|dataSource
argument_list|(
name|jdbcUrl
argument_list|,
name|jdbcDriver
argument_list|,
name|jdbcUser
argument_list|,
name|jdbcPassword
argument_list|)
expr_stmt|;
block|}
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
literal|"Error while reading dataSource"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|jdbcCatalog
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcCatalog"
argument_list|)
decl_stmt|;
name|String
name|jdbcSchema
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"jdbcSchema"
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
name|dataSource
argument_list|)
decl_stmt|;
return|return
operator|new
name|JdbcSchema
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|dataSource
argument_list|,
name|dialect
argument_list|,
name|jdbcCatalog
argument_list|,
name|jdbcSchema
argument_list|)
return|;
block|}
comment|/** Returns a suitable SQL dialect for the given data source. */
specifier|public
specifier|static
name|SqlDialect
name|createDialect
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
return|return
name|JdbcUtils
operator|.
name|DialectPool
operator|.
name|INSTANCE
operator|.
name|get
argument_list|(
name|dataSource
argument_list|)
return|;
block|}
comment|/** Creates a JDBC data source with the given specification. */
specifier|public
specifier|static
name|DataSource
name|dataSource
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|driverClassName
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:hsqldb:"
argument_list|)
condition|)
block|{
comment|// Prevent hsqldb from screwing up java.util.logging.
name|System
operator|.
name|setProperty
argument_list|(
literal|"hsqldb.reconfig_logging"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
name|BasicDataSource
name|dataSource
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setDriverClassName
argument_list|(
name|driverClassName
argument_list|)
expr_stmt|;
return|return
name|dataSource
return|;
block|}
specifier|public
name|SchemaPlus
name|getParentSchema
parameter_list|()
block|{
return|return
name|parentSchema
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|// Used by generated code.
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
block|{
return|return
name|dataSource
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|Schemas
operator|.
name|subSchemaExpression
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|JdbcSchema
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
name|Multimap
argument_list|<
name|String
argument_list|,
name|TableFunction
argument_list|>
name|getTableFunctions
parameter_list|()
block|{
comment|// TODO: populate map from JDBC metadata
return|return
name|ImmutableMultimap
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Collection
argument_list|<
name|TableFunction
argument_list|>
name|getTableFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getTableFunctions
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getTableFunctionNames
parameter_list|()
block|{
return|return
name|getTableFunctions
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|JdbcTable
argument_list|>
name|computeTables
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|ResultSet
name|resultSet
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|resultSet
operator|=
name|metaData
operator|.
name|getTables
argument_list|(
name|catalog
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|JdbcTable
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
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
name|tableName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|String
name|catalogName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|schemaName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tableTypeName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|// Clean up table type. In particular, this ensures that 'SYSTEM TABLE',
comment|// returned by Phoenix among others, maps to TableType.SYSTEM_TABLE.
comment|// We know enum constants are upper-case without spaces, so we can't
comment|// make things worse.
specifier|final
name|String
name|tableTypeName2
init|=
name|tableTypeName
operator|.
name|toUpperCase
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
decl_stmt|;
specifier|final
name|TableType
name|tableType
init|=
name|Util
operator|.
name|enumVal
argument_list|(
name|TableType
operator|.
name|class
argument_list|,
name|tableTypeName2
argument_list|)
decl_stmt|;
specifier|final
name|JdbcTable
name|table
init|=
operator|new
name|JdbcTable
argument_list|(
name|this
argument_list|,
name|catalogName
argument_list|,
name|schemaName
argument_list|,
name|tableName
argument_list|,
name|tableType
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|tableName
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
literal|"Exception while reading tables"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|connection
argument_list|,
literal|null
argument_list|,
name|resultSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tableMapSupplier
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
name|RelProtoDataType
name|getRelDataType
parameter_list|(
name|String
name|catalogName
parameter_list|,
name|String
name|schemaName
parameter_list|,
name|String
name|tableName
parameter_list|)
throws|throws
name|SQLException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|DatabaseMetaData
name|metaData
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
return|return
name|getRelDataType
argument_list|(
name|metaData
argument_list|,
name|catalogName
argument_list|,
name|schemaName
argument_list|,
name|tableName
argument_list|)
return|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|connection
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|RelProtoDataType
name|getRelDataType
parameter_list|(
name|DatabaseMetaData
name|metaData
parameter_list|,
name|String
name|catalogName
parameter_list|,
name|String
name|schemaName
parameter_list|,
name|String
name|tableName
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ResultSet
name|resultSet
init|=
name|metaData
operator|.
name|getColumns
argument_list|(
name|catalogName
argument_list|,
name|schemaName
argument_list|,
name|tableName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Temporary type factory, just for the duration of this method. Allowable
comment|// because we're creating a proto-type, not a type; before being used, the
comment|// proto-type will be copied into a real type factory.
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|()
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
name|columnName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
decl_stmt|;
specifier|final
name|int
name|dataType
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
specifier|final
name|int
name|scale
init|=
name|resultSet
operator|.
name|getInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|RelDataType
name|sqlType
init|=
name|sqlType
argument_list|(
name|typeFactory
argument_list|,
name|dataType
argument_list|,
name|size
argument_list|,
name|scale
argument_list|)
decl_stmt|;
name|boolean
name|nullable
init|=
name|resultSet
operator|.
name|getBoolean
argument_list|(
literal|11
argument_list|)
decl_stmt|;
name|fieldInfo
operator|.
name|add
argument_list|(
name|columnName
argument_list|,
name|sqlType
argument_list|)
operator|.
name|nullable
argument_list|(
name|nullable
argument_list|)
expr_stmt|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
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
specifier|private
name|RelDataType
name|sqlType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|int
name|dataType
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|)
block|{
name|SqlTypeName
name|sqlTypeName
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|dataType
argument_list|)
decl_stmt|;
if|if
condition|(
name|precision
operator|>=
literal|0
operator|&&
name|scale
operator|>=
literal|0
operator|&&
name|sqlTypeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
return|;
block|}
if|else if
condition|(
name|precision
operator|>=
literal|0
operator|&&
name|sqlTypeName
operator|.
name|allowsPrecNoScale
argument_list|()
condition|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
name|precision
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|sqlTypeName
operator|.
name|allowsNoPrecNoScale
argument_list|()
assert|;
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|)
return|;
block|}
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
return|return
name|tableMapSupplier
operator|.
name|get
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// JDBC does not support sub-schemas.
return|return
literal|null
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|close
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Statement
name|statement
parameter_list|,
name|ResultSet
name|resultSet
parameter_list|)
block|{
if|if
condition|(
name|resultSet
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|/** Schema factory that creates a    * {@link net.hydromatic.optiq.impl.jdbc.JdbcSchema}.    * This allows you to create a jdbc schema inside a model.json file.    *    *<pre>{@code    * {    *   version: '1.0',    *   defaultSchema: 'FOODMART_CLONE',    *   schemas: [    *     {    *       name: 'FOODMART_CLONE',    *       type: 'custom',    *       factory: 'net.hydromatic.optiq.impl.clone.JdbcSchema$Factory',    *       operand: {    *         jdbcDriver: 'com.mysql.jdbc.Driver',    *         jdbcUrl: 'jdbc:mysql://localhost/foodmart',    *         jdbcUser: 'foodmart',    *         jdbcPassword: 'foodmart'    *       }    *     }    *   ]    * }    * }</pre>    */
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
return|return
name|JdbcSchema
operator|.
name|create
argument_list|(
name|parentSchema
argument_list|,
name|name
argument_list|,
name|operand
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JdbcSchema.java
end_comment

end_unit

