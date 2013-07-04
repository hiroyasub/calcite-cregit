begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|reltype
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
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
name|*
import|;
end_import

begin_comment
comment|/**  * Abstract base for implementations of {@link RelDataTypeFactory}.  *  * @author jhyde  * @version $Id$  * @since May 31, 2003  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelDataTypeFactoryImpl
implements|implements
name|RelDataTypeFactory
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|RelDataTypeFamily
argument_list|>
name|CLASS_FAMILIES
init|=
name|Util
operator|.
expr|<
name|Class
decl_stmt|,
name|RelDataTypeFamily
decl|>
name|mapOf
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|SqlTypeFamily
operator|.
name|CHARACTER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelDataTypeFactoryImpl
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|createJavaType
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
specifier|final
name|JavaType
name|javaType
init|=
name|clazz
operator|==
name|String
operator|.
name|class
condition|?
operator|new
name|JavaType
argument_list|(
name|clazz
argument_list|,
literal|true
argument_list|,
name|getDefaultCharset
argument_list|()
argument_list|,
name|SqlCollation
operator|.
name|IMPLICIT
argument_list|)
else|:
operator|new
name|JavaType
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|javaType
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|createJoinType
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|)
block|{
specifier|final
name|RelDataType
index|[]
name|flattenedTypes
init|=
name|getTypeArray
argument_list|(
name|types
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
operator|new
name|RelCrossType
argument_list|(
name|flattenedTypes
argument_list|,
name|getFieldArray
argument_list|(
name|flattenedTypes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|,
name|String
index|[]
name|fieldNames
parameter_list|)
block|{
specifier|final
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|fieldNames
index|[
name|i
index|]
argument_list|,
name|i
argument_list|,
name|types
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|canonize
argument_list|(
operator|new
name|RelRecordType
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
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
parameter_list|)
block|{
specifier|final
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
name|Pair
argument_list|<
name|RelDataType
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|typeList
argument_list|,
name|fieldNameList
argument_list|)
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|pair
operator|.
name|left
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|canonize
argument_list|(
operator|new
name|RelRecordType
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
name|RelDataTypeFactory
operator|.
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
name|canonize
argument_list|(
operator|new
name|RelRecordType
argument_list|(
name|iterable
argument_list|(
name|fieldInfo
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|iterable
parameter_list|(
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataTypeField
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
name|fieldInfo
operator|.
name|getFieldType
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fieldInfo
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
specifier|final
name|RelDataType
name|createStructType
parameter_list|(
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|fieldList
parameter_list|)
block|{
return|return
name|createStructType
argument_list|(
operator|new
name|FieldInfo
argument_list|()
block|{
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|fieldList
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFieldName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fieldList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getKey
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getFieldType
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fieldList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|leastRestrictive
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
block|{
assert|assert
operator|(
name|types
operator|!=
literal|null
operator|)
assert|;
assert|assert
operator|(
name|types
operator|.
name|size
argument_list|()
operator|>=
literal|1
operator|)
assert|;
name|RelDataType
name|type0
init|=
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|type0
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
name|leastRestrictiveStructuredType
argument_list|(
name|types
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|RelDataType
name|leastRestrictiveStructuredType
parameter_list|(
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
block|{
name|RelDataType
name|type0
init|=
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|nFields
init|=
name|type0
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// precheck that all types are structs with same number of fields
for|for
control|(
name|RelDataType
name|type
range|:
name|types
control|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|nFields
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// recursively compute column-wise least restrictive
name|RelDataType
index|[]
name|outputTypes
init|=
operator|new
name|RelDataType
index|[
name|nFields
index|]
decl_stmt|;
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[
name|nFields
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nFields
condition|;
operator|++
name|j
control|)
block|{
comment|// REVIEW jvs 22-Jan-2004:  Always use the field name from the
comment|// first type?
name|fieldNames
index|[
name|j
index|]
operator|=
name|type0
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getName
argument_list|()
expr_stmt|;
specifier|final
name|int
name|k
init|=
name|j
decl_stmt|;
name|outputTypes
index|[
name|j
index|]
operator|=
name|leastRestrictive
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataType
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|types
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|types
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|createStructType
argument_list|(
name|outputTypes
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
comment|// copy a non-record type, setting nullability
specifier|private
name|RelDataType
name|copySimpleType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
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
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|javaType
argument_list|)
condition|)
block|{
return|return
operator|new
name|JavaType
argument_list|(
name|javaType
operator|.
name|clazz
argument_list|,
name|nullable
argument_list|,
name|javaType
operator|.
name|charset
argument_list|,
name|javaType
operator|.
name|collation
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|JavaType
argument_list|(
name|javaType
operator|.
name|clazz
argument_list|,
name|nullable
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// REVIEW: RelCrossType if it stays around; otherwise get rid of
comment|// this comment
return|return
name|type
return|;
block|}
block|}
comment|// recursively copy a record type
specifier|private
name|RelDataType
name|copyRecordType
parameter_list|(
specifier|final
name|RelRecordType
name|type
parameter_list|,
specifier|final
name|boolean
name|ignoreNullable
parameter_list|,
specifier|final
name|boolean
name|nullable
parameter_list|)
block|{
comment|// REVIEW: angel 18-Aug-2005 dtbug336
comment|// Shouldn't null refer to the nullability of the record type
comment|// not the individual field types?
comment|// For flattening and outer joins, it is desirable to change
comment|// the nullability of the individual fields.
return|return
name|createStructType
argument_list|(
operator|new
name|FieldInfo
argument_list|()
block|{
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFieldName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getFieldType
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|RelDataType
name|fieldType
init|=
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ignoreNullable
condition|)
block|{
return|return
name|copyType
argument_list|(
name|fieldType
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|fieldType
argument_list|,
name|nullable
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|copyType
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
name|copyRecordType
argument_list|(
operator|(
name|RelRecordType
operator|)
name|type
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|type
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|RelDataType
name|createTypeWithNullability
parameter_list|(
specifier|final
name|RelDataType
name|type
parameter_list|,
specifier|final
name|boolean
name|nullable
parameter_list|)
block|{
name|RelDataType
name|newType
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|RelRecordType
condition|)
block|{
comment|// REVIEW: angel 18-Aug-2005 dtbug 336 workaround
comment|// Changed to ignore nullable parameter if nullable is false since
comment|// copyRecordType implementation is doubtful
if|if
condition|(
name|nullable
condition|)
block|{
comment|// Do a deep copy, setting all fields of the record type
comment|// to be nullable regardless of initial nullability
name|newType
operator|=
name|copyRecordType
argument_list|(
operator|(
name|RelRecordType
operator|)
name|type
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Keep same type as before, ignore nullable parameter
comment|// RelRecordType currently always returns a nullability of false
name|newType
operator|=
name|copyRecordType
argument_list|(
operator|(
name|RelRecordType
operator|)
name|type
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|newType
operator|=
name|copySimpleType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
comment|/**      * Registers a type, or returns the existing type if it is already      * registered.      */
specifier|protected
name|RelDataType
name|canonize
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|RelDataType
name|type2
init|=
name|map
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|type2
operator|!=
literal|null
condition|)
block|{
return|return
name|type2
return|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
block|}
comment|/**      * Returns an array of the fields in an array of types.      */
specifier|private
specifier|static
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldArray
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
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
name|RelDataType
name|type
range|:
name|types
control|)
block|{
name|addFields
argument_list|(
name|type
argument_list|,
name|fieldList
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldList
return|;
block|}
comment|/**      * Returns an array of all atomic types in an array.      */
specifier|private
specifier|static
name|RelDataType
index|[]
name|getTypeArray
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
name|typeList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
name|getTypeArray
argument_list|(
name|types
argument_list|,
name|typeList
argument_list|)
expr_stmt|;
return|return
name|typeList
operator|.
name|toArray
argument_list|(
operator|new
name|RelDataType
index|[
name|typeList
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|getTypeArray
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|,
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
name|typeList
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RelDataType
name|type
init|=
name|types
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|RelCrossType
condition|)
block|{
name|getTypeArray
argument_list|(
operator|(
operator|(
name|RelCrossType
operator|)
name|type
operator|)
operator|.
name|types
argument_list|,
name|typeList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typeList
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Adds all fields in<code>type</code> to<code>fieldList</code>.      */
specifier|private
specifier|static
name|void
name|addFields
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
parameter_list|)
block|{
if|if
condition|(
name|type
operator|instanceof
name|RelCrossType
condition|)
block|{
specifier|final
name|RelCrossType
name|crossType
init|=
operator|(
name|RelCrossType
operator|)
name|type
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
name|crossType
operator|.
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addFields
argument_list|(
name|crossType
operator|.
name|types
index|[
name|i
index|]
argument_list|,
name|fieldList
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|type
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|fieldList
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isJavaType
parameter_list|(
name|RelDataType
name|t
parameter_list|)
block|{
return|return
name|t
operator|instanceof
name|JavaType
return|;
block|}
specifier|private
name|List
argument_list|<
name|RelDataTypeFieldImpl
argument_list|>
name|fieldsOf
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeFieldImpl
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeFieldImpl
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|clazz
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|field
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
name|createJavaType
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
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|list
return|;
block|}
comment|/**      * implement RelDataTypeFactory with SQL 2003 compliant behavior. Let p1, s1      * be the precision and scale of the first operand Let p2, s2 be the      * precision and scale of the second operand Let p, s be the precision and      * scale of the result, Then the result type is a decimal with:      *      *<ul>      *<li>p = p1 + p2</li>      *<li>s = s1 + s2</li>      *</ul>      *      * p and s are capped at their maximum values      *      * @sql.2003 Part 2 Section 6.26      */
specifier|public
name|RelDataType
name|createDecimalProduct
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|scale
init|=
name|s1
operator|+
name|s2
decl_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|SqlTypeName
operator|.
name|MAX_NUMERIC_SCALE
argument_list|)
expr_stmt|;
name|int
name|precision
init|=
name|p1
operator|+
name|p2
decl_stmt|;
name|precision
operator|=
name|Math
operator|.
name|min
argument_list|(
name|precision
argument_list|,
name|SqlTypeName
operator|.
name|MAX_NUMERIC_PRECISION
argument_list|)
expr_stmt|;
name|RelDataType
name|ret
decl_stmt|;
name|ret
operator|=
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|// implement RelDataTypeFactory
specifier|public
name|boolean
name|useDoubleMultiplication
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
assert|assert
operator|(
name|createDecimalProduct
argument_list|(
name|type1
argument_list|,
name|type2
argument_list|)
operator|!=
literal|null
operator|)
assert|;
return|return
literal|false
return|;
block|}
comment|/**      * implement RelDataTypeFactory Let p1, s1 be the precision and scale of the      * first operand Let p2, s2 be the precision and scale of the second operand      * Let p, s be the precision and scale of the result, Let d be the number of      * whole digits in the result Then the result type is a decimal with:      *      *<ul>      *<li>d = p1 - s1 + s2</li>      *<li>s<= max(6, s1 + p2 + 1)</li>      *<li>p = d + s</li>      *</ul>      *      * p and s are capped at their maximum values      *      * @sql.2003 Part 2 Section 6.26      */
specifier|public
name|RelDataType
name|createDecimalQuotient
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
name|int
name|p1
init|=
name|type1
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type2
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|type1
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type2
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|dout
init|=
name|Math
operator|.
name|min
argument_list|(
name|p1
operator|-
name|s1
operator|+
name|s2
argument_list|,
name|SqlTypeName
operator|.
name|MAX_NUMERIC_PRECISION
argument_list|)
decl_stmt|;
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
literal|6
argument_list|,
name|s1
operator|+
name|p2
operator|+
literal|1
argument_list|)
decl_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|SqlTypeName
operator|.
name|MAX_NUMERIC_PRECISION
operator|-
name|dout
argument_list|)
expr_stmt|;
name|scale
operator|=
name|Math
operator|.
name|min
argument_list|(
name|scale
argument_list|,
name|SqlTypeName
operator|.
name|MAX_NUMERIC_SCALE
argument_list|)
expr_stmt|;
name|int
name|precision
init|=
name|dout
operator|+
name|scale
decl_stmt|;
assert|assert
operator|(
name|precision
operator|<=
name|SqlTypeName
operator|.
name|MAX_NUMERIC_PRECISION
operator|)
assert|;
assert|assert
operator|(
name|precision
operator|>
literal|0
operator|)
assert|;
name|RelDataType
name|ret
decl_stmt|;
name|ret
operator|=
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Charset
name|getDefaultCharset
parameter_list|()
block|{
return|return
name|Util
operator|.
name|getDefaultCharset
argument_list|()
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|// TODO jvs 13-Dec-2004:  move to OJTypeFactoryImpl?
comment|/**      * Type which is based upon a Java class.      */
specifier|public
class|class
name|JavaType
extends|extends
name|RelDataTypeImpl
block|{
specifier|private
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|nullable
decl_stmt|;
specifier|private
name|SqlCollation
name|collation
decl_stmt|;
specifier|private
name|Charset
name|charset
decl_stmt|;
specifier|public
name|JavaType
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
operator|!
name|clazz
operator|.
name|isPrimitive
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JavaType
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|nullable
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JavaType
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|boolean
name|nullable
parameter_list|,
name|Charset
name|charset
parameter_list|,
name|SqlCollation
name|collation
parameter_list|)
block|{
name|super
argument_list|(
name|fieldsOf
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
assert|assert
operator|(
name|charset
operator|!=
literal|null
operator|)
operator|==
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|this
argument_list|)
operator|:
literal|"Need to be a chartype"
assert|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|this
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Class
name|getJavaClass
parameter_list|()
block|{
return|return
name|clazz
return|;
block|}
specifier|public
name|boolean
name|isNullable
parameter_list|()
block|{
return|return
name|nullable
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
name|RelDataTypeFamily
name|family
init|=
name|CLASS_FAMILIES
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
return|return
name|family
operator|!=
literal|null
condition|?
name|family
else|:
name|this
return|;
block|}
specifier|protected
name|void
name|generateTypeString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|withDetail
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"JavaType("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|getComponentType
parameter_list|()
block|{
specifier|final
name|Class
name|componentType
init|=
name|clazz
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|componentType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|createJavaType
argument_list|(
name|componentType
argument_list|)
return|;
block|}
block|}
specifier|public
name|Charset
name|getCharset
parameter_list|()
throws|throws
name|RuntimeException
block|{
return|return
name|this
operator|.
name|charset
return|;
block|}
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
throws|throws
name|RuntimeException
block|{
return|return
name|this
operator|.
name|collation
return|;
block|}
specifier|public
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
block|{
specifier|final
name|SqlTypeName
name|typeName
init|=
name|JavaToSqlTypeConversionRules
operator|.
name|instance
argument_list|()
operator|.
name|lookup
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
return|return
name|SqlTypeName
operator|.
name|OTHER
return|;
block|}
return|return
name|typeName
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelDataTypeFactoryImpl.java
end_comment

end_unit

