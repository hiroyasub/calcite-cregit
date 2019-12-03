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
name|piglet
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|pig
operator|.
name|data
operator|.
name|DataBag
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|data
operator|.
name|DataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|newplan
operator|.
name|logical
operator|.
name|relational
operator|.
name|LogicalSchema
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

begin_comment
comment|/**  * Utility methods for converting Pig data types to SQL types.  */
end_comment

begin_class
class|class
name|PigTypes
block|{
specifier|private
name|PigTypes
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|String
name|PIG_TUPLE_WRAPPER
init|=
literal|"PIG_WRAPPER"
decl_stmt|;
comment|// Specialized type factory to handle type conversion
specifier|static
specifier|final
name|PigRelDataTypeFactory
name|TYPE_FACTORY
init|=
operator|new
name|PigRelDataTypeFactory
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
comment|/**    * Type factory that produces types with the nullability when converting    * from Pig types. It also translates a Pig DataBag type into a multiset of    * objects type.    */
specifier|static
class|class
name|PigRelDataTypeFactory
extends|extends
name|JavaTypeFactoryImpl
block|{
specifier|private
name|PigRelDataTypeFactory
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|)
block|{
name|super
argument_list|(
name|typeSystem
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|super
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|super
operator|.
name|createStructType
argument_list|(
name|typeList
argument_list|,
name|fieldNameList
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createMultisetType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|long
name|maxCardinality
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|super
operator|.
name|createMultisetType
argument_list|(
name|type
argument_list|,
name|maxCardinality
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createMapType
parameter_list|(
name|RelDataType
name|keyType
parameter_list|,
name|RelDataType
name|valueType
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|super
operator|.
name|createMapType
argument_list|(
name|keyType
argument_list|,
name|valueType
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|toSql
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|JavaType
operator|&&
operator|(
operator|(
name|JavaType
operator|)
name|type
operator|)
operator|.
name|getJavaClass
argument_list|()
operator|==
name|DataBag
operator|.
name|class
condition|)
block|{
comment|// We don't know the structure of each tuple inside the bag until the runtime.
comment|// Thus just consider a bag as a multiset of unknown objects.
return|return
name|createMultisetType
argument_list|(
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|toSql
argument_list|(
name|type
argument_list|)
return|;
block|}
block|}
comment|/**    * Converts a Pig schema field to relational type.    *    * @param pigField Pig schema field    * @return Relational type    */
specifier|static
name|RelDataType
name|convertSchemaField
parameter_list|(
name|LogicalSchema
operator|.
name|LogicalFieldSchema
name|pigField
parameter_list|)
block|{
return|return
name|convertSchemaField
argument_list|(
name|pigField
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Converts a Pig schema field to relational type.    *    * @param pigField Pig schema field    * @param nullable true if the type is nullable    * @return Relational type    */
specifier|static
name|RelDataType
name|convertSchemaField
parameter_list|(
name|LogicalSchema
operator|.
name|LogicalFieldSchema
name|pigField
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
switch|switch
condition|(
name|pigField
operator|.
name|type
condition|)
block|{
case|case
name|DataType
operator|.
name|UNKNOWN
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|NULL
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|NULL
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|BOOLEAN
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|BYTE
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|INTEGER
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|LONG
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|FLOAT
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|DOUBLE
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|DATETIME
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|BYTEARRAY
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|CHARARRAY
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|BIGINTEGER
case|:
case|case
name|DataType
operator|.
name|BIGDECIMAL
case|:
return|return
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|nullable
argument_list|)
return|;
case|case
name|DataType
operator|.
name|TUPLE
case|:
block|{
if|if
condition|(
name|pigField
operator|.
name|alias
operator|!=
literal|null
operator|&&
name|pigField
operator|.
name|alias
operator|.
name|equals
argument_list|(
name|PIG_TUPLE_WRAPPER
argument_list|)
condition|)
block|{
if|if
condition|(
name|pigField
operator|.
name|schema
operator|==
literal|null
operator|||
name|pigField
operator|.
name|schema
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Expect one subfield from "
operator|+
name|pigField
operator|.
name|schema
argument_list|)
throw|;
block|}
return|return
name|convertSchemaField
argument_list|(
name|pigField
operator|.
name|schema
operator|.
name|getField
argument_list|(
literal|0
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|return
name|convertSchema
argument_list|(
name|pigField
operator|.
name|schema
argument_list|,
name|nullable
argument_list|)
return|;
block|}
case|case
name|DataType
operator|.
name|MAP
case|:
block|{
specifier|final
name|RelDataType
name|relKey
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|pigField
operator|.
name|schema
operator|==
literal|null
condition|)
block|{
comment|// The default type of Pig Map value is bytearray
return|return
name|TYPE_FACTORY
operator|.
name|createMapType
argument_list|(
name|relKey
argument_list|,
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BINARY
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|pigField
operator|.
name|schema
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|TYPE_FACTORY
operator|.
name|createMapType
argument_list|(
name|relKey
argument_list|,
name|convertSchemaField
argument_list|(
name|pigField
operator|.
name|schema
operator|.
name|getField
argument_list|(
literal|0
argument_list|)
argument_list|,
name|nullable
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
block|}
case|case
name|DataType
operator|.
name|BAG
case|:
block|{
if|if
condition|(
name|pigField
operator|.
name|schema
operator|==
literal|null
condition|)
block|{
return|return
name|TYPE_FACTORY
operator|.
name|createMultisetType
argument_list|(
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
return|;
block|}
assert|assert
name|pigField
operator|.
name|schema
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|TYPE_FACTORY
operator|.
name|createMultisetType
argument_list|(
name|convertSchemaField
argument_list|(
name|pigField
operator|.
name|schema
operator|.
name|getField
argument_list|(
literal|0
argument_list|)
argument_list|,
name|nullable
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|nullable
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported conversion for Pig Data type: "
operator|+
name|DataType
operator|.
name|findTypeName
argument_list|(
name|pigField
operator|.
name|type
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Converts a Pig tuple schema to a SQL row type.    *    * @param pigSchema Pig tuple schema    * @return a SQL row type    */
specifier|static
name|RelDataType
name|convertSchema
parameter_list|(
name|LogicalSchema
name|pigSchema
parameter_list|)
block|{
return|return
name|convertSchema
argument_list|(
name|pigSchema
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Converts a Pig tuple schema to a SQL row type.    *    * @param pigSchema Pig tuple schema    * @param nullable true if the type is nullable    * @return a SQL row type    */
specifier|static
name|RelDataType
name|convertSchema
parameter_list|(
name|LogicalSchema
name|pigSchema
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
if|if
condition|(
name|pigSchema
operator|!=
literal|null
operator|&&
name|pigSchema
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|pigSchema
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|LogicalSchema
operator|.
name|LogicalFieldSchema
name|subPigField
init|=
name|pigSchema
operator|.
name|getField
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|fieldNameList
operator|.
name|add
argument_list|(
name|subPigField
operator|.
name|alias
operator|!=
literal|null
condition|?
name|subPigField
operator|.
name|alias
else|:
literal|"$"
operator|+
name|i
argument_list|)
expr_stmt|;
name|typeList
operator|.
name|add
argument_list|(
name|convertSchemaField
argument_list|(
name|subPigField
argument_list|,
name|nullable
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|typeList
argument_list|,
name|fieldNameList
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|return
operator|new
name|DynamicTupleRecordType
argument_list|(
name|TYPE_FACTORY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

