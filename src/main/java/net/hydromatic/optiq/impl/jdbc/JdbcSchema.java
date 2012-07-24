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
name|QueryProvider
import|;
end_import

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
name|impl
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
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
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
name|RelDataTypeFactory
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
name|SqlTypeName
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
name|Collections
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Schema} that is backed by a JDBC data source.  *  *<p>The tables in the JDBC data source appear to be tables in this schema;  * queries against this schema are executed against those tables, pushing down  * as much as possible of the query logic to SQL.</p>  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JdbcSchema
implements|implements
name|Schema
block|{
specifier|final
name|QueryProvider
name|queryProvider
decl_stmt|;
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
specifier|final
name|String
name|catalog
decl_stmt|;
specifier|private
specifier|final
name|String
name|schema
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
comment|/**      * Creates a JDBC schema.      *      * @param queryProvider Query provider      * @param dataSource Data source      * @param dialect SQL dialect      * @param catalog Catalog name, or null      * @param schema Schema name pattern      * @param typeFactory Type factory      */
specifier|public
name|JdbcSchema
parameter_list|(
name|QueryProvider
name|queryProvider
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
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
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
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
assert|assert
name|expression
operator|!=
literal|null
assert|;
assert|assert
name|typeFactory
operator|!=
literal|null
assert|;
assert|assert
name|dialect
operator|!=
literal|null
assert|;
assert|assert
name|queryProvider
operator|!=
literal|null
assert|;
assert|assert
name|dataSource
operator|!=
literal|null
assert|;
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
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
name|queryProvider
return|;
block|}
specifier|public
name|List
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
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Table
argument_list|<
name|T
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|elementType
parameter_list|)
block|{
assert|assert
name|elementType
operator|!=
literal|null
assert|;
comment|//noinspection unchecked
return|return
name|getTable
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
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
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
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
name|tableName
init|=
name|resultSet
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|resultSet
operator|=
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
expr_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
name|fieldInfo
init|=
operator|new
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
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
name|zzz
argument_list|(
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
if|if
condition|(
name|nullable
condition|)
block|{
name|sqlType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|sqlType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|fieldInfo
operator|.
name|add
argument_list|(
name|columnName
argument_list|,
name|sqlType
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
name|Type
name|javaType
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
operator|new
name|JdbcTable
argument_list|<
name|Object
argument_list|>
argument_list|(
name|javaType
argument_list|,
name|this
argument_list|,
name|name
argument_list|)
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
literal|"Exception while reading definition of table '"
operator|+
name|name
operator|+
literal|"'"
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
specifier|private
name|RelDataType
name|zzz
parameter_list|(
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
block|}
end_class

begin_comment
comment|// End JdbcSchema.java
end_comment

end_unit

