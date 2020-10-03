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
name|innodb
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
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|TableReaderFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|column
operator|.
name|ColumnType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|Column
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|TableDef
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|provider
operator|.
name|TableDefProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|schema
operator|.
name|provider
operator|.
name|impl
operator|.
name|SqlFileTableDefProvider
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_comment
comment|/**  * Schema for an InnoDB data source.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbSchema
extends|extends
name|AbstractSchema
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|sqlFilePathList
decl_stmt|;
specifier|final
name|String
name|ibdDataFileBasePath
decl_stmt|;
specifier|final
name|TableReaderFactory
name|tableReaderFactory
decl_stmt|;
specifier|static
specifier|final
name|ColumnTypeToSqlTypeConversionRules
name|COLUMN_TYPE_TO_SQL_TYPE
init|=
name|ColumnTypeToSqlTypeConversionRules
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|public
name|InnodbSchema
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|sqlFilePathList
parameter_list|,
name|String
name|ibdDataFileBasePath
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|CollectionUtils
operator|.
name|isNotEmpty
argument_list|(
name|sqlFilePathList
argument_list|)
argument_list|,
literal|"SQL file path list cannot be empty"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|ibdDataFileBasePath
argument_list|)
argument_list|,
literal|"InnoDB data file with ibd suffix cannot be empty"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sqlFilePathList
operator|=
name|sqlFilePathList
expr_stmt|;
name|this
operator|.
name|ibdDataFileBasePath
operator|=
name|ibdDataFileBasePath
expr_stmt|;
name|List
argument_list|<
name|TableDefProvider
argument_list|>
name|tableDefProviderList
init|=
name|sqlFilePathList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|SqlFileTableDefProvider
operator|::
operator|new
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|tableReaderFactory
operator|=
name|TableReaderFactory
operator|.
name|builder
argument_list|()
operator|.
name|withProviders
argument_list|(
name|tableDefProviderList
argument_list|)
operator|.
name|withDataFileBasePath
argument_list|(
name|ibdDataFileBasePath
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|RelProtoDataType
name|getRelDataType
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
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
name|Builder
name|fieldInfo
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|tableReaderFactory
operator|.
name|existTableDef
argument_list|(
name|tableName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Table definition "
operator|+
name|tableName
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|TableDef
name|tableDef
init|=
name|tableReaderFactory
operator|.
name|getTableDef
argument_list|(
name|tableName
argument_list|)
decl_stmt|;
for|for
control|(
name|Column
name|column
range|:
name|tableDef
operator|.
name|getColumnList
argument_list|()
control|)
block|{
specifier|final
name|SqlTypeName
name|sqlTypeName
init|=
name|COLUMN_TYPE_TO_SQL_TYPE
operator|.
name|lookup
argument_list|(
name|column
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|precision
decl_stmt|;
specifier|final
name|int
name|scale
decl_stmt|;
switch|switch
condition|(
name|column
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|ColumnType
operator|.
name|TIMESTAMP
case|:
case|case
name|ColumnType
operator|.
name|TIME
case|:
case|case
name|ColumnType
operator|.
name|DATETIME
case|:
name|precision
operator|=
name|column
operator|.
name|getPrecision
argument_list|()
expr_stmt|;
name|scale
operator|=
literal|0
expr_stmt|;
break|break;
default|default:
name|precision
operator|=
name|column
operator|.
name|getPrecision
argument_list|()
expr_stmt|;
name|scale
operator|=
name|column
operator|.
name|getScale
argument_list|()
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|sqlTypeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
operator|&&
name|column
operator|.
name|getPrecision
argument_list|()
operator|>=
literal|0
operator|&&
name|column
operator|.
name|getScale
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|fieldInfo
operator|.
name|add
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|,
name|sqlTypeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|sqlTypeName
operator|.
name|allowsPrecNoScale
argument_list|()
operator|&&
name|precision
operator|>=
literal|0
condition|)
block|{
name|fieldInfo
operator|.
name|add
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|,
name|sqlTypeName
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|sqlTypeName
operator|.
name|allowsNoPrecNoScale
argument_list|()
assert|;
name|fieldInfo
operator|.
name|add
argument_list|(
name|column
operator|.
name|getName
argument_list|()
argument_list|,
name|sqlTypeName
argument_list|)
expr_stmt|;
block|}
name|fieldInfo
operator|.
name|nullable
argument_list|(
name|column
operator|.
name|isNullable
argument_list|()
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
comment|/**    * Return table definition.    */
specifier|public
name|TableDef
name|getTableDef
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|tableReaderFactory
operator|.
name|existTableDef
argument_list|(
name|tableName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot find table definition for "
operator|+
name|tableName
argument_list|)
throw|;
block|}
return|return
name|tableReaderFactory
operator|.
name|getTableDef
argument_list|(
name|tableName
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
name|Map
argument_list|<
name|String
argument_list|,
name|TableDef
argument_list|>
name|map
init|=
name|tableReaderFactory
operator|.
name|getTableNameToDefMap
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
name|TableDef
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|tableName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|tableName
argument_list|,
operator|new
name|InnodbTable
argument_list|(
name|this
argument_list|,
name|tableName
argument_list|)
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
block|}
end_class

end_unit
