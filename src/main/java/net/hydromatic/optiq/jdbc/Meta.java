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
name|Enumerable
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
name|Linq4j
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
name|Function1
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
name|Predicate1
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
name|runtime
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
name|util
operator|.
name|Util
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
name|Field
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

begin_comment
comment|/**  * Helper for implementing the {@code getXxx} methods such as  * {@link OptiqDatabaseMetaData#getTables}.  */
end_comment

begin_class
class|class
name|Meta
block|{
specifier|final
name|OptiqConnectionImpl
name|connection
decl_stmt|;
specifier|public
name|Meta
parameter_list|(
name|OptiqConnectionImpl
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
block|}
specifier|static
parameter_list|<
name|T
extends|extends
name|Named
parameter_list|>
name|Predicate1
argument_list|<
name|T
argument_list|>
name|matcher
parameter_list|(
specifier|final
name|String
name|pattern
parameter_list|)
block|{
return|return
operator|new
name|Predicate1
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|T
name|v1
parameter_list|)
block|{
return|return
name|matches
argument_list|(
name|v1
argument_list|,
name|pattern
argument_list|)
return|;
block|}
block|}
return|;
block|}
name|ResultSet
name|getTables
parameter_list|(
name|String
name|catalog
parameter_list|,
specifier|final
name|String
name|schemaPattern
parameter_list|,
name|String
name|tableNamePattern
parameter_list|,
name|String
index|[]
name|types
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|IteratorResultSet
operator|.
name|create
argument_list|(
name|schemas
argument_list|(
name|catalog
argument_list|)
operator|.
name|where
argument_list|(
name|Meta
operator|.
expr|<
name|MetaSchema
operator|>
name|matcher
argument_list|(
name|schemaPattern
argument_list|)
argument_list|)
operator|.
name|selectMany
argument_list|(
operator|new
name|Function1
argument_list|<
name|MetaSchema
argument_list|,
name|Enumerable
argument_list|<
name|MetaTable
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|MetaTable
argument_list|>
name|apply
parameter_list|(
name|MetaSchema
name|a0
parameter_list|)
block|{
return|return
name|tables
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|where
argument_list|(
name|Meta
operator|.
expr|<
name|MetaTable
operator|>
name|matcher
argument_list|(
name|tableNamePattern
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|NamedFieldGetter
argument_list|(
name|MetaTable
operator|.
name|class
argument_list|,
literal|"TABLE_CAT"
argument_list|,
literal|"TABLE_SCHEM"
argument_list|,
literal|"TABLE_NAME"
argument_list|,
literal|"TABLE_TYPE"
argument_list|,
literal|"REMARKS"
argument_list|,
literal|"TYPE_CAT"
argument_list|,
literal|"TYPE_SCHEM"
argument_list|,
literal|"TYPE_NAME"
argument_list|,
literal|"SELF_REFERENCING_COL_NAME"
argument_list|,
literal|"REF_GENERATION"
argument_list|)
argument_list|)
return|;
block|}
name|ResultSet
name|getColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schemaPattern
parameter_list|,
name|String
name|tableNamePattern
parameter_list|,
name|String
name|columnNamePattern
parameter_list|)
block|{
return|return
name|IteratorResultSet
operator|.
name|create
argument_list|(
name|schemas
argument_list|(
name|catalog
argument_list|)
operator|.
name|where
argument_list|(
name|Meta
operator|.
expr|<
name|MetaSchema
operator|>
name|matcher
argument_list|(
name|schemaPattern
argument_list|)
argument_list|)
operator|.
name|selectMany
argument_list|(
operator|new
name|Function1
argument_list|<
name|MetaSchema
argument_list|,
name|Enumerable
argument_list|<
name|MetaTable
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|MetaTable
argument_list|>
name|apply
parameter_list|(
name|MetaSchema
name|a0
parameter_list|)
block|{
return|return
name|tables
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|where
argument_list|(
name|Meta
operator|.
expr|<
name|MetaTable
operator|>
name|matcher
argument_list|(
name|tableNamePattern
argument_list|)
argument_list|)
operator|.
name|selectMany
argument_list|(
operator|new
name|Function1
argument_list|<
name|MetaTable
argument_list|,
name|Enumerable
argument_list|<
name|MetaColumn
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|MetaColumn
argument_list|>
name|apply
parameter_list|(
name|MetaTable
name|a0
parameter_list|)
block|{
return|return
name|columns
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|where
argument_list|(
name|Meta
operator|.
expr|<
name|MetaColumn
operator|>
name|matcher
argument_list|(
name|columnNamePattern
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|NamedFieldGetter
argument_list|(
name|MetaColumn
operator|.
name|class
argument_list|,
literal|"TABLE_CAT"
argument_list|,
literal|"TABLE_SCHEM"
argument_list|,
literal|"TABLE_NAME"
argument_list|,
literal|"COLUMN_NAME"
argument_list|,
literal|"DATA_TYPE"
argument_list|,
literal|"TYPE_NAME"
argument_list|,
literal|"COLUMN_SIZE"
argument_list|,
literal|"BUFFER_LENGTH"
argument_list|,
literal|"DECIMAL_DIGITS"
argument_list|,
literal|"NUM_PREC_RADIX"
argument_list|,
literal|"NULLABLE"
argument_list|,
literal|"REMARKS"
argument_list|,
literal|"COLUMN_DEF"
argument_list|,
literal|"SQL_DATA_TYPE"
argument_list|,
literal|"SQL_DATETIME_SUB"
argument_list|,
literal|"CHAR_OCTET_LENGTH"
argument_list|,
literal|"ORDINAL_POSITION"
argument_list|,
literal|"IS_NULLABLE"
argument_list|,
literal|"SCOPE_CATALOG"
argument_list|,
literal|"SCOPE_TABLE"
argument_list|,
literal|"SOURCE_DATA_TYPE"
argument_list|,
literal|"IS_AUTOINCREMENT"
argument_list|,
literal|"IS_GENERATEDCOLUMN"
argument_list|)
argument_list|)
return|;
block|}
name|Enumerable
argument_list|<
name|MetaSchema
argument_list|>
name|schemas
parameter_list|(
name|String
name|catalog
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|schemaNames
init|=
name|connection
operator|.
name|rootSchema
operator|.
name|getSubSchemaNames
argument_list|()
decl_stmt|;
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|schemaNames
argument_list|)
operator|.
name|select
argument_list|(
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|MetaSchema
argument_list|>
argument_list|()
block|{
specifier|public
name|MetaSchema
name|apply
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MetaSchema
argument_list|(
name|connection
operator|.
name|rootSchema
operator|.
name|getSubSchema
argument_list|(
name|name
argument_list|)
argument_list|,
name|connection
operator|.
name|getCatalog
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
name|Enumerable
argument_list|<
name|MetaTable
argument_list|>
name|tables
parameter_list|(
specifier|final
name|MetaSchema
name|schema
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|tableNames
init|=
name|schema
operator|.
name|optiqSchema
operator|.
name|getTableNames
argument_list|()
decl_stmt|;
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|tableNames
argument_list|)
operator|.
name|select
argument_list|(
operator|new
name|Function1
argument_list|<
name|String
argument_list|,
name|MetaTable
argument_list|>
argument_list|()
block|{
specifier|public
name|MetaTable
name|apply
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MetaTable
argument_list|(
name|schema
operator|.
name|optiqSchema
operator|.
name|getTable
argument_list|(
name|name
argument_list|)
argument_list|,
name|schema
operator|.
name|catalogName
argument_list|,
name|schema
operator|.
name|schemaName
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|matches
parameter_list|(
name|Named
name|element
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
return|return
name|pattern
operator|==
literal|null
operator|||
name|pattern
operator|.
name|equals
argument_list|(
literal|"%"
argument_list|)
operator|||
name|element
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|pattern
argument_list|)
return|;
comment|// TODO: better wildcard
block|}
specifier|public
name|Enumerable
argument_list|<
name|MetaColumn
argument_list|>
name|columns
parameter_list|(
specifier|final
name|MetaTable
name|table
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|table
operator|.
name|optiqTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|select
argument_list|(
operator|new
name|Function1
argument_list|<
name|RelDataTypeField
argument_list|,
name|MetaColumn
argument_list|>
argument_list|()
block|{
specifier|public
name|MetaColumn
name|apply
parameter_list|(
name|RelDataTypeField
name|a0
parameter_list|)
block|{
specifier|final
name|int
name|precision
init|=
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|allowsPrec
argument_list|()
operator|&&
operator|!
operator|(
name|a0
operator|.
name|getType
argument_list|()
operator|instanceof
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
condition|?
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getPrecision
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
return|return
operator|new
name|MetaColumn
argument_list|(
name|table
operator|.
name|tableCat
argument_list|,
name|table
operator|.
name|tableSchem
argument_list|,
name|table
operator|.
name|tableName
argument_list|,
name|a0
operator|.
name|getName
argument_list|()
argument_list|,
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getJdbcOrdinal
argument_list|()
argument_list|,
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|precision
argument_list|,
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|allowsScale
argument_list|()
condition|?
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|getScale
argument_list|()
else|:
literal|null
argument_list|,
literal|10
argument_list|,
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|?
name|DatabaseMetaData
operator|.
name|columnNullable
else|:
name|DatabaseMetaData
operator|.
name|columnNoNulls
argument_list|,
name|precision
argument_list|,
name|a0
operator|.
name|getIndex
argument_list|()
operator|+
literal|1
argument_list|,
name|a0
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|?
literal|"YES"
else|:
literal|"NO"
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
interface|interface
name|Named
block|{
name|String
name|getName
parameter_list|()
function_decl|;
block|}
specifier|static
class|class
name|MetaColumn
implements|implements
name|Named
block|{
specifier|public
specifier|final
name|String
name|tableCat
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableSchem
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableName
decl_stmt|;
specifier|public
specifier|final
name|String
name|columnName
decl_stmt|;
specifier|public
specifier|final
name|int
name|dataType
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|public
specifier|final
name|int
name|columnSize
decl_stmt|;
specifier|public
specifier|final
name|String
name|bufferLength
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|Integer
name|decimalDigits
decl_stmt|;
specifier|public
specifier|final
name|int
name|numPrecRadix
decl_stmt|;
specifier|public
specifier|final
name|int
name|nullable
decl_stmt|;
specifier|public
specifier|final
name|String
name|remarks
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|columnDef
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|sqlDataType
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|sqlDatetimeSub
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|int
name|charOctetLength
decl_stmt|;
specifier|public
specifier|final
name|int
name|ordinalPosition
decl_stmt|;
specifier|public
specifier|final
name|String
name|isNullable
decl_stmt|;
specifier|public
specifier|final
name|String
name|scopeCatalog
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|scopeTable
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|sourceDataType
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|isAutoincrement
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|isGeneratedcolumn
init|=
literal|null
decl_stmt|;
name|MetaColumn
parameter_list|(
name|String
name|tableCat
parameter_list|,
name|String
name|tableSchem
parameter_list|,
name|String
name|tableName
parameter_list|,
name|String
name|columnName
parameter_list|,
name|int
name|dataType
parameter_list|,
name|String
name|typeName
parameter_list|,
name|int
name|columnSize
parameter_list|,
name|Integer
name|decimalDigits
parameter_list|,
name|int
name|numPrecRadix
parameter_list|,
name|int
name|nullable
parameter_list|,
name|int
name|charOctetLength
parameter_list|,
name|int
name|ordinalPosition
parameter_list|,
name|String
name|isNullable
parameter_list|)
block|{
name|this
operator|.
name|tableCat
operator|=
name|tableCat
expr_stmt|;
name|this
operator|.
name|tableSchem
operator|=
name|tableSchem
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|columnName
operator|=
name|columnName
expr_stmt|;
name|this
operator|.
name|dataType
operator|=
name|dataType
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|columnSize
operator|=
name|columnSize
expr_stmt|;
name|this
operator|.
name|decimalDigits
operator|=
name|decimalDigits
expr_stmt|;
name|this
operator|.
name|numPrecRadix
operator|=
name|numPrecRadix
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
name|this
operator|.
name|charOctetLength
operator|=
name|charOctetLength
expr_stmt|;
name|this
operator|.
name|ordinalPosition
operator|=
name|ordinalPosition
expr_stmt|;
name|this
operator|.
name|isNullable
operator|=
name|isNullable
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|columnName
return|;
block|}
block|}
specifier|static
class|class
name|MetaTable
implements|implements
name|Named
block|{
specifier|private
specifier|final
name|Table
name|optiqTable
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableCat
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableSchem
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableName
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableType
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|remarks
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeCat
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeSchem
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeName
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|selfReferencingColName
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|String
name|refGeneration
init|=
literal|null
decl_stmt|;
specifier|public
name|MetaTable
parameter_list|(
name|Table
name|optiqTable
parameter_list|,
name|String
name|tableCat
parameter_list|,
name|String
name|tableSchem
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|this
operator|.
name|optiqTable
operator|=
name|optiqTable
expr_stmt|;
name|this
operator|.
name|tableCat
operator|=
name|tableCat
expr_stmt|;
name|this
operator|.
name|tableSchem
operator|=
name|tableSchem
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
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
specifier|static
class|class
name|MetaSchema
implements|implements
name|Named
block|{
specifier|private
specifier|final
name|Schema
name|optiqSchema
decl_stmt|;
specifier|public
specifier|final
name|String
name|catalogName
decl_stmt|;
specifier|public
specifier|final
name|String
name|schemaName
decl_stmt|;
specifier|public
name|MetaSchema
parameter_list|(
name|Schema
name|optiqSchema
parameter_list|,
name|String
name|catalogName
parameter_list|,
name|String
name|schemaName
parameter_list|)
block|{
name|this
operator|.
name|optiqSchema
operator|=
name|optiqSchema
expr_stmt|;
name|this
operator|.
name|catalogName
operator|=
name|catalogName
expr_stmt|;
name|this
operator|.
name|schemaName
operator|=
name|schemaName
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|schemaName
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|NamedFieldGetter
implements|implements
name|AbstractIterResultSet
operator|.
name|ColumnGetter
block|{
specifier|private
specifier|final
name|Field
index|[]
name|fields
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|columnNames
decl_stmt|;
specifier|public
name|NamedFieldGetter
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|columnNameList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Field
argument_list|>
name|fieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|columnNameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|nextUpper
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|nextUpper
operator|=
name|i
operator|+
literal|1
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|nextUpper
operator|==
name|i
condition|)
block|{
name|c
operator|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|String
name|fieldName
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|clazz
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
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
block|}
name|this
operator|.
name|fields
operator|=
name|fieldList
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[
name|fieldList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|columnNames
operator|=
name|columnNameList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|columnNameList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getColumnNames
parameter_list|()
block|{
return|return
name|columnNames
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|o
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
try|try
block|{
return|return
name|fields
index|[
name|columnIndex
operator|-
literal|1
index|]
operator|.
name|get
argument_list|(
name|o
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while retrieving field "
operator|+
name|fields
index|[
name|columnIndex
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while retrieving field "
operator|+
name|fields
index|[
name|columnIndex
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End Meta.java
end_comment

end_unit

