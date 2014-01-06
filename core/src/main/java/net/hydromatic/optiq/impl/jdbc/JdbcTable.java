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
name|*
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
name|*
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
name|function
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
name|AbstractTableQueryable
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
name|AbstractQueryableTable
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
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
name|runtime
operator|.
name|ResultSetEnumerable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptTable
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
name|reltype
operator|.
name|RelDataTypeField
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
name|RelProtoDataType
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|parser
operator|.
name|SqlParserPos
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|util
operator|.
name|SqlString
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
name|Pair
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
name|*
import|;
end_import

begin_comment
comment|/**  * Queryable that gets its data from a table within a JDBC connection.  *  *<p>The idea is not to read the whole table, however. The idea is to use  * this as a building block for a query, by applying Queryable operators  * such as {@link net.hydromatic.linq4j.Queryable#where(net.hydromatic.linq4j.function.Predicate2)}.  * The resulting queryable can then be converted to a SQL query, which can be  * executed efficiently on the JDBC server.</p>  */
end_comment

begin_class
class|class
name|JdbcTable
extends|extends
name|AbstractQueryableTable
implements|implements
name|TranslatableTable
block|{
specifier|private
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|private
specifier|final
name|JdbcSchema
name|jdbcSchema
decl_stmt|;
specifier|private
specifier|final
name|String
name|jdbcCatalogName
decl_stmt|;
specifier|private
specifier|final
name|String
name|jdbcSchemaName
decl_stmt|;
specifier|private
specifier|final
name|String
name|jdbcTableName
decl_stmt|;
specifier|private
specifier|final
name|Schema
operator|.
name|TableType
name|jdbcTableType
decl_stmt|;
specifier|public
name|JdbcTable
parameter_list|(
name|JdbcSchema
name|jdbcSchema
parameter_list|,
name|String
name|jdbcCatalogName
parameter_list|,
name|String
name|jdbcSchemaName
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Schema
operator|.
name|TableType
name|jdbcTableType
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
name|jdbcSchema
operator|=
name|jdbcSchema
expr_stmt|;
name|this
operator|.
name|jdbcCatalogName
operator|=
name|jdbcCatalogName
expr_stmt|;
name|this
operator|.
name|jdbcSchemaName
operator|=
name|jdbcSchemaName
expr_stmt|;
name|this
operator|.
name|jdbcTableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|jdbcTableType
operator|=
name|jdbcTableType
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JdbcTable {"
operator|+
name|jdbcTableName
operator|+
literal|"}"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|jdbcTableType
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
try|try
block|{
name|protoRowType
operator|=
name|jdbcSchema
operator|.
name|getRelDataType
argument_list|(
name|jdbcCatalogName
argument_list|,
name|jdbcSchemaName
argument_list|,
name|jdbcTableName
argument_list|)
expr_stmt|;
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
name|jdbcTableName
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
specifier|private
name|List
argument_list|<
name|Pair
argument_list|<
name|Primitive
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|fieldClasses
parameter_list|(
specifier|final
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
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
return|return
name|Functions
operator|.
name|adapt
argument_list|(
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|,
operator|new
name|Function1
argument_list|<
name|RelDataTypeField
argument_list|,
name|Pair
argument_list|<
name|Primitive
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Pair
argument_list|<
name|Primitive
argument_list|,
name|Integer
argument_list|>
name|apply
parameter_list|(
name|RelDataTypeField
name|field
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|Primitive
operator|.
name|of
argument_list|(
name|clazz
argument_list|)
argument_list|,
name|Primitive
operator|.
name|OTHER
argument_list|)
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getJdbcOrdinal
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
name|SqlString
name|generateSql
parameter_list|()
block|{
name|SqlSelect
name|node
init|=
name|SqlStdOperatorTable
operator|.
name|selectOperator
operator|.
name|createCall
argument_list|(
name|SqlNodeList
operator|.
name|Empty
argument_list|,
operator|new
name|SqlNodeList
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
literal|"*"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|tableName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|jdbcSchema
operator|.
name|dialect
argument_list|)
decl_stmt|;
name|node
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
return|return
name|writer
operator|.
name|toSqlString
argument_list|()
return|;
block|}
name|SqlIdentifier
name|tableName
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|jdbcSchema
operator|.
name|catalog
operator|!=
literal|null
condition|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|jdbcSchema
operator|.
name|catalog
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jdbcSchema
operator|.
name|schema
operator|!=
literal|null
condition|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|jdbcSchema
operator|.
name|schema
argument_list|)
expr_stmt|;
block|}
name|strings
operator|.
name|add
argument_list|(
name|jdbcTableName
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|strings
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|strings
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
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
return|return
operator|new
name|JdbcTableScan
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
name|jdbcSchema
operator|.
name|convention
argument_list|)
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
name|AbstractTableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
operator|(
name|OptiqConnection
operator|)
name|queryProvider
operator|)
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|SqlString
name|sql
init|=
name|generateSql
argument_list|()
decl_stmt|;
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
name|ResultSetEnumerable
operator|.
name|of
argument_list|(
name|jdbcSchema
operator|.
name|getDataSource
argument_list|()
argument_list|,
name|sql
operator|.
name|getSql
argument_list|()
argument_list|,
name|JdbcUtils
operator|.
name|ObjectArrayRowBuilder
operator|.
name|factory
argument_list|(
name|fieldClasses
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcTable.java
end_comment

end_unit

