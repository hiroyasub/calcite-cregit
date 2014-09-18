begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|avatica
operator|.
name|ByteString
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
name|Ord
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
name|Primitive
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
name|Types
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
name|runtime
operator|.
name|Unit
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
name|type
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Lists
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
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
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
comment|/**  * Implementation of {@link JavaTypeFactory}.  *  *<p><strong>NOTE: This class is experimental and subject to  * change/removal without notice</strong>.</p>  */
end_comment

begin_class
specifier|public
class|class
name|JavaTypeFactoryImpl
extends|extends
name|SqlTypeFactoryImpl
implements|implements
name|JavaTypeFactory
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|Pair
argument_list|<
name|Type
argument_list|,
name|Boolean
argument_list|>
argument_list|>
argument_list|,
name|SyntheticRecordType
argument_list|>
name|syntheticTypes
init|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|<
name|Pair
argument_list|<
name|Type
argument_list|,
name|Boolean
argument_list|>
argument_list|>
argument_list|,
name|SyntheticRecordType
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|JavaTypeFactoryImpl
parameter_list|()
block|{
name|this
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JavaTypeFactoryImpl
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
name|createStructType
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|type
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|field
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|==
literal|0
condition|)
block|{
comment|// FIXME: watch out for recursion
name|list
operator|.
name|add
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|createType
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|canonize
argument_list|(
operator|new
name|JavaRecordType
argument_list|(
name|list
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|RelDataType
condition|)
block|{
return|return
operator|(
name|RelDataType
operator|)
name|type
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|SyntheticRecordType
condition|)
block|{
name|SyntheticRecordType
name|syntheticRecordType
init|=
operator|(
name|SyntheticRecordType
operator|)
name|type
decl_stmt|;
return|return
name|syntheticRecordType
operator|.
name|relType
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|Class
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO: implement "
operator|+
name|type
argument_list|)
throw|;
block|}
specifier|final
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|type
decl_stmt|;
switch|switch
condition|(
name|Primitive
operator|.
name|flavor
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
case|case
name|PRIMITIVE
case|:
return|return
name|createJavaType
argument_list|(
name|clazz
argument_list|)
return|;
case|case
name|BOX
case|:
return|return
name|createJavaType
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|clazz
argument_list|)
operator|.
name|boxClass
argument_list|)
return|;
block|}
if|if
condition|(
name|JavaToSqlTypeConversionRules
operator|.
name|instance
argument_list|()
operator|.
name|lookup
argument_list|(
name|clazz
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|createJavaType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
if|else if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|createMultisetType
argument_list|(
name|createType
argument_list|(
name|clazz
operator|.
name|getComponentType
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
if|else if
condition|(
name|List
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|createArrayType
argument_list|(
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
if|else if
condition|(
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|createMapType
argument_list|(
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createStructType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
block|}
specifier|public
name|Type
name|getJavaClass
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
condition|)
block|{
name|JavaType
name|javaType
init|=
operator|(
name|JavaType
operator|)
name|type
decl_stmt|;
return|return
name|javaType
operator|.
name|getJavaClass
argument_list|()
return|;
block|}
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
operator|&&
name|type
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|getJavaClass
argument_list|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
operator|||
name|type
operator|instanceof
name|IntervalSqlType
condition|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|VARCHAR
case|:
case|case
name|CHAR
case|:
return|return
name|String
operator|.
name|class
return|;
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|INTEGER
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Integer
operator|.
name|class
else|:
name|int
operator|.
name|class
return|;
case|case
name|TIMESTAMP
case|:
case|case
name|BIGINT
case|:
case|case
name|INTERVAL_DAY_TIME
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Long
operator|.
name|class
else|:
name|long
operator|.
name|class
return|;
case|case
name|SMALLINT
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Short
operator|.
name|class
else|:
name|short
operator|.
name|class
return|;
case|case
name|TINYINT
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Byte
operator|.
name|class
else|:
name|byte
operator|.
name|class
return|;
case|case
name|DECIMAL
case|:
return|return
name|BigDecimal
operator|.
name|class
return|;
case|case
name|BOOLEAN
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Boolean
operator|.
name|class
else|:
name|boolean
operator|.
name|class
return|;
case|case
name|DOUBLE
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Double
operator|.
name|class
else|:
name|double
operator|.
name|class
return|;
case|case
name|REAL
case|:
case|case
name|FLOAT
case|:
return|return
name|type
operator|.
name|isNullable
argument_list|()
condition|?
name|Float
operator|.
name|class
else|:
name|float
operator|.
name|class
return|;
case|case
name|BINARY
case|:
case|case
name|VARBINARY
case|:
return|return
name|ByteString
operator|.
name|class
return|;
case|case
name|ARRAY
case|:
return|return
name|Array
operator|.
name|class
return|;
case|case
name|ANY
case|:
return|return
name|Object
operator|.
name|class
return|;
block|}
block|}
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ROW
case|:
assert|assert
name|type
operator|instanceof
name|RelRecordType
assert|;
if|if
condition|(
name|type
operator|instanceof
name|JavaRecordType
condition|)
block|{
return|return
operator|(
operator|(
name|JavaRecordType
operator|)
name|type
operator|)
operator|.
name|clazz
return|;
block|}
else|else
block|{
return|return
name|createSyntheticType
argument_list|(
operator|(
name|RelRecordType
operator|)
name|type
argument_list|)
return|;
block|}
case|case
name|MAP
case|:
return|return
name|Map
operator|.
name|class
return|;
case|case
name|ARRAY
case|:
case|case
name|MULTISET
case|:
return|return
name|List
operator|.
name|class
return|;
block|}
return|return
literal|null
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
name|RelRecordType
condition|)
block|{
return|return
name|createStructType
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|type
operator|.
name|getFieldList
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|RelDataTypeField
argument_list|,
name|RelDataType
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataTypeField
name|a0
parameter_list|)
block|{
return|return
name|toSql
argument_list|(
name|a0
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|type
operator|.
name|getFieldNames
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|type
operator|instanceof
name|JavaType
condition|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|createSqlType
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
argument_list|,
name|type
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
return|return
name|type
return|;
block|}
specifier|public
name|Type
name|createSyntheticType
parameter_list|(
name|List
argument_list|<
name|Type
argument_list|>
name|types
parameter_list|)
block|{
if|if
condition|(
name|types
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Unit is a pre-defined synthetic type to be used when there are 0
comment|// fields. Because all instances are the same, we use a singleton.
return|return
name|Unit
operator|.
name|class
return|;
block|}
specifier|final
name|String
name|name
init|=
literal|"Record"
operator|+
name|types
operator|.
name|size
argument_list|()
operator|+
literal|"_"
operator|+
name|syntheticTypes
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SyntheticRecordType
name|syntheticType
init|=
operator|new
name|SyntheticRecordType
argument_list|(
literal|null
argument_list|,
name|name
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Ord
argument_list|<
name|Type
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|types
argument_list|)
control|)
block|{
name|syntheticType
operator|.
name|fields
operator|.
name|add
argument_list|(
operator|new
name|RecordFieldImpl
argument_list|(
name|syntheticType
argument_list|,
literal|"f"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
argument_list|,
operator|!
name|Primitive
operator|.
name|is
argument_list|(
name|ord
operator|.
name|e
argument_list|)
argument_list|,
name|Modifier
operator|.
name|PUBLIC
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|register
argument_list|(
name|syntheticType
argument_list|)
return|;
block|}
specifier|private
name|SyntheticRecordType
name|register
parameter_list|(
specifier|final
name|SyntheticRecordType
name|syntheticType
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|Type
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|key
init|=
operator|new
name|AbstractList
argument_list|<
name|Pair
argument_list|<
name|Type
argument_list|,
name|Boolean
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Pair
argument_list|<
name|Type
argument_list|,
name|Boolean
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|Types
operator|.
name|RecordField
name|field
init|=
name|syntheticType
operator|.
name|getRecordFields
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|field
operator|.
name|nullable
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|syntheticType
operator|.
name|getRecordFields
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|SyntheticRecordType
name|syntheticType2
init|=
name|syntheticTypes
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|syntheticType2
operator|==
literal|null
condition|)
block|{
name|syntheticTypes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|syntheticType
argument_list|)
expr_stmt|;
return|return
name|syntheticType
return|;
block|}
else|else
block|{
return|return
name|syntheticType2
return|;
block|}
block|}
comment|/** Creates a synthetic Java class whose fields have the same names and    * relational types. */
specifier|private
name|Type
name|createSyntheticType
parameter_list|(
name|RelRecordType
name|type
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
literal|"Record"
operator|+
name|type
operator|.
name|getFieldCount
argument_list|()
operator|+
literal|"_"
operator|+
name|syntheticTypes
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SyntheticRecordType
name|syntheticType
init|=
operator|new
name|SyntheticRecordType
argument_list|(
name|type
argument_list|,
name|name
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|RelDataTypeField
name|recordField
range|:
name|type
operator|.
name|getFieldList
argument_list|()
control|)
block|{
specifier|final
name|Type
name|javaClass
init|=
name|getJavaClass
argument_list|(
name|recordField
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|syntheticType
operator|.
name|fields
operator|.
name|add
argument_list|(
operator|new
name|RecordFieldImpl
argument_list|(
name|syntheticType
argument_list|,
name|recordField
operator|.
name|getName
argument_list|()
argument_list|,
name|javaClass
argument_list|,
name|recordField
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
operator|&&
operator|!
name|Primitive
operator|.
name|is
argument_list|(
name|javaClass
argument_list|)
argument_list|,
name|Modifier
operator|.
name|PUBLIC
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|register
argument_list|(
name|syntheticType
argument_list|)
return|;
block|}
comment|/** Synthetic record type. */
specifier|public
specifier|static
class|class
name|SyntheticRecordType
implements|implements
name|Types
operator|.
name|RecordType
block|{
specifier|final
name|List
argument_list|<
name|Types
operator|.
name|RecordField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Types
operator|.
name|RecordField
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|relType
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|SyntheticRecordType
parameter_list|(
name|RelDataType
name|relType
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|relType
operator|=
name|relType
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
assert|assert
name|relType
operator|==
literal|null
operator|||
name|Util
operator|.
name|isDistinct
argument_list|(
name|relType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
operator|:
literal|"field names not distinct: "
operator|+
name|relType
assert|;
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
name|List
argument_list|<
name|Types
operator|.
name|RecordField
argument_list|>
name|getRecordFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/** Implementation of a field. */
specifier|private
specifier|static
class|class
name|RecordFieldImpl
implements|implements
name|Types
operator|.
name|RecordField
block|{
specifier|private
specifier|final
name|SyntheticRecordType
name|syntheticType
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|nullable
decl_stmt|;
specifier|private
specifier|final
name|int
name|modifiers
decl_stmt|;
specifier|public
name|RecordFieldImpl
parameter_list|(
name|SyntheticRecordType
name|syntheticType
parameter_list|,
name|String
name|name
parameter_list|,
name|Type
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|,
name|int
name|modifiers
parameter_list|)
block|{
name|this
operator|.
name|syntheticType
operator|=
name|syntheticType
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
name|this
operator|.
name|modifiers
operator|=
name|modifiers
expr_stmt|;
assert|assert
name|syntheticType
operator|!=
literal|null
assert|;
assert|assert
name|name
operator|!=
literal|null
assert|;
assert|assert
name|type
operator|!=
literal|null
assert|;
assert|assert
operator|!
operator|(
name|nullable
operator|&&
name|Primitive
operator|.
name|is
argument_list|(
name|type
argument_list|)
operator|)
operator|:
literal|"type ["
operator|+
name|type
operator|+
literal|"] can never be null"
assert|;
block|}
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
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
name|int
name|getModifiers
parameter_list|()
block|{
return|return
name|modifiers
return|;
block|}
specifier|public
name|boolean
name|nullable
parameter_list|()
block|{
return|return
name|nullable
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Type
name|getDeclaringClass
parameter_list|()
block|{
return|return
name|syntheticType
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JavaTypeFactoryImpl.java
end_comment

end_unit

