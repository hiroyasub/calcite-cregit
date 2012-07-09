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
name|Queryable
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
name|linq4j
operator|.
name|expressions
operator|.
name|Expressions
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
name|DataContext
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
name|Member
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
name|Parameter
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
name|Schema
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
specifier|private
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
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
comment|/**      * Creates a JDBC schema.      *      * @param dataSource Data source      * @param catalog Catalog name, or null      * @param schema Schema name pattern      * @param typeFactory Type factory      */
specifier|public
name|JdbcSchema
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|dataSource
operator|=
name|dataSource
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
block|}
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|DataContext
operator|.
name|class
return|;
block|}
specifier|public
name|List
argument_list|<
name|Member
argument_list|>
name|getMembers
parameter_list|(
specifier|final
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
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|createMultisetType
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
expr|<
name|Member
operator|>
name|singletonList
argument_list|(
operator|new
name|Member
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JdbcTable {"
operator|+
name|tableName
operator|+
literal|"}"
return|;
block|}
specifier|public
name|List
argument_list|<
name|Parameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|Queryable
name|evaluate
parameter_list|(
name|Object
name|target
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
assert|assert
name|arguments
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|getTableQueryable
argument_list|(
name|tableName
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|tableName
return|;
block|}
block|}
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
specifier|private
name|Queryable
name|getTableQueryable
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO:
block|}
specifier|public
name|Expression
name|getSubSchemaExpression
parameter_list|(
name|Expression
name|schemaExpression
parameter_list|,
name|Schema
name|schema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// JDBC has no sub-schemas.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Expression
name|getMemberExpression
parameter_list|(
name|Expression
name|schemaExpression
parameter_list|,
name|Member
name|member
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
block|{
assert|assert
name|arguments
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|schemaExpression
argument_list|,
literal|"getTable"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|member
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
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
name|Object
name|getSubSchemaInstance
parameter_list|(
name|Object
name|schemaInstance
parameter_list|,
name|String
name|subSchemaName
parameter_list|,
name|Schema
name|subSchema
parameter_list|)
block|{
comment|// JDBC does not support sub-schemas.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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

