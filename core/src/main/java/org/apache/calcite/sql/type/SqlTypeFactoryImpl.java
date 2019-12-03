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
name|sql
operator|.
name|type
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
name|RelDataTypeFactoryImpl
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
name|RelDataTypeFamily
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
name|SqlCollation
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
name|SqlIntervalQualifier
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
name|Util
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
name|Charset
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
comment|/**  * SqlTypeFactoryImpl provides a default implementation of  * {@link RelDataTypeFactory} which supports SQL types.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTypeFactoryImpl
extends|extends
name|RelDataTypeFactoryImpl
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlTypeFactoryImpl
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
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
if|if
condition|(
name|typeName
operator|.
name|allowsPrec
argument_list|()
condition|)
block|{
return|return
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|typeSystem
operator|.
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
argument_list|)
return|;
block|}
name|assertBasic
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
name|RelDataType
name|newType
init|=
operator|new
name|BasicSqlType
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
specifier|final
name|int
name|maxPrecision
init|=
name|typeSystem
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxPrecision
operator|>=
literal|0
operator|&&
name|precision
operator|>
name|maxPrecision
condition|)
block|{
name|precision
operator|=
name|maxPrecision
expr_stmt|;
block|}
if|if
condition|(
name|typeName
operator|.
name|allowsScale
argument_list|()
condition|)
block|{
return|return
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|,
name|typeName
operator|.
name|getDefaultScale
argument_list|()
argument_list|)
return|;
block|}
name|assertBasic
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|precision
operator|>=
literal|0
operator|)
operator|||
operator|(
name|precision
operator|==
name|RelDataType
operator|.
name|PRECISION_NOT_SPECIFIED
operator|)
assert|;
comment|// Does not check precision when typeName is SqlTypeName#NULL.
name|RelDataType
name|newType
init|=
name|precision
operator|==
name|RelDataType
operator|.
name|PRECISION_NOT_SPECIFIED
condition|?
operator|new
name|BasicSqlType
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|)
else|:
operator|new
name|BasicSqlType
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|)
decl_stmt|;
name|newType
operator|=
name|SqlTypeUtil
operator|.
name|addCharsetAndCollation
argument_list|(
name|newType
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|)
block|{
name|assertBasic
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|precision
operator|>=
literal|0
operator|)
operator|||
operator|(
name|precision
operator|==
name|RelDataType
operator|.
name|PRECISION_NOT_SPECIFIED
operator|)
assert|;
specifier|final
name|int
name|maxPrecision
init|=
name|typeSystem
operator|.
name|getMaxPrecision
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxPrecision
operator|>=
literal|0
operator|&&
name|precision
operator|>
name|maxPrecision
condition|)
block|{
name|precision
operator|=
name|maxPrecision
expr_stmt|;
block|}
name|RelDataType
name|newType
init|=
operator|new
name|BasicSqlType
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
decl_stmt|;
name|newType
operator|=
name|SqlTypeUtil
operator|.
name|addCharsetAndCollation
argument_list|(
name|newType
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createUnknownType
parameter_list|()
block|{
return|return
name|canonize
argument_list|(
operator|new
name|UnknownSqlType
argument_list|(
name|this
argument_list|)
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
parameter_list|)
block|{
assert|assert
name|maxCardinality
operator|==
operator|-
literal|1
assert|;
name|RelDataType
name|newType
init|=
operator|new
name|MultisetSqlType
argument_list|(
name|type
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createArrayType
parameter_list|(
name|RelDataType
name|elementType
parameter_list|,
name|long
name|maxCardinality
parameter_list|)
block|{
assert|assert
name|maxCardinality
operator|==
operator|-
literal|1
assert|;
name|ArraySqlType
name|newType
init|=
operator|new
name|ArraySqlType
argument_list|(
name|elementType
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|newType
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
parameter_list|)
block|{
name|MapSqlType
name|newType
init|=
operator|new
name|MapSqlType
argument_list|(
name|keyType
argument_list|,
name|valueType
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createSqlIntervalType
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
block|{
name|RelDataType
name|newType
init|=
operator|new
name|IntervalSqlType
argument_list|(
name|typeSystem
argument_list|,
name|intervalQualifier
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createTypeWithCharsetAndCollation
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|Charset
name|charset
parameter_list|,
name|SqlCollation
name|collation
parameter_list|)
block|{
assert|assert
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|type
argument_list|)
operator|:
name|type
assert|;
assert|assert
name|charset
operator|!=
literal|null
assert|;
assert|assert
name|collation
operator|!=
literal|null
assert|;
name|RelDataType
name|newType
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
condition|)
block|{
name|BasicSqlType
name|sqlType
init|=
operator|(
name|BasicSqlType
operator|)
name|type
decl_stmt|;
name|newType
operator|=
name|sqlType
operator|.
name|createWithCharsetAndCollation
argument_list|(
name|charset
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
if|else if
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
name|newType
operator|=
operator|new
name|JavaType
argument_list|(
name|javaType
operator|.
name|getJavaClass
argument_list|()
argument_list|,
name|javaType
operator|.
name|isNullable
argument_list|()
argument_list|,
name|charset
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
literal|"need to implement "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|types
operator|!=
literal|null
assert|;
assert|assert
name|types
operator|.
name|size
argument_list|()
operator|>=
literal|1
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
name|getSqlTypeName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|RelDataType
name|resultType
init|=
name|leastRestrictiveSqlType
argument_list|(
name|types
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultType
operator|!=
literal|null
condition|)
block|{
return|return
name|resultType
return|;
block|}
return|return
name|leastRestrictiveByCast
argument_list|(
name|types
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|leastRestrictive
argument_list|(
name|types
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|leastRestrictiveByCast
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
block|{
name|RelDataType
name|resultType
init|=
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|anyNullable
init|=
name|resultType
operator|.
name|isNullable
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|types
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataType
name|type
init|=
name|types
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
name|anyNullable
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|anyNullable
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|type
argument_list|,
name|resultType
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|type
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|resultType
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
if|if
condition|(
name|anyNullable
condition|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|resultType
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|resultType
return|;
block|}
block|}
annotation|@
name|Override
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
specifier|final
name|RelDataType
name|newType
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|BasicSqlType
condition|)
block|{
name|newType
operator|=
operator|(
operator|(
name|BasicSqlType
operator|)
name|type
operator|)
operator|.
name|createWithNullability
argument_list|(
name|nullable
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|instanceof
name|MapSqlType
condition|)
block|{
name|newType
operator|=
name|copyMapType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|instanceof
name|ArraySqlType
condition|)
block|{
name|newType
operator|=
name|copyArrayType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|instanceof
name|MultisetSqlType
condition|)
block|{
name|newType
operator|=
name|copyMultisetType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|instanceof
name|IntervalSqlType
condition|)
block|{
name|newType
operator|=
name|copyIntervalType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|instanceof
name|ObjectSqlType
condition|)
block|{
name|newType
operator|=
name|copyObjectType
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|return
name|canonize
argument_list|(
name|newType
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertBasic
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
assert|assert
name|typeName
operator|!=
literal|null
assert|;
assert|assert
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|MULTISET
operator|:
literal|"use createMultisetType() instead"
assert|;
assert|assert
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|ARRAY
operator|:
literal|"use createArrayType() instead"
assert|;
assert|assert
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|MAP
operator|:
literal|"use createMapType() instead"
assert|;
assert|assert
name|typeName
operator|!=
name|SqlTypeName
operator|.
name|ROW
operator|:
literal|"use createStructType() instead"
assert|;
assert|assert
operator|!
name|SqlTypeName
operator|.
name|INTERVAL_TYPES
operator|.
name|contains
argument_list|(
name|typeName
argument_list|)
operator|:
literal|"use createSqlIntervalType() instead"
assert|;
block|}
specifier|private
name|RelDataType
name|leastRestrictiveSqlType
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
block|{
name|RelDataType
name|resultType
init|=
literal|null
decl_stmt|;
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
name|int
name|nullableCount
init|=
literal|0
decl_stmt|;
name|int
name|javaCount
init|=
literal|0
decl_stmt|;
name|int
name|anyCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataType
name|type
range|:
name|types
control|)
block|{
specifier|final
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|typeName
operator|==
name|SqlTypeName
operator|.
name|ANY
condition|)
block|{
name|anyCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
operator|++
name|nullableCount
expr_stmt|;
block|}
if|if
condition|(
name|typeName
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
operator|++
name|nullCount
expr_stmt|;
block|}
if|if
condition|(
name|isJavaType
argument_list|(
name|type
argument_list|)
condition|)
block|{
operator|++
name|javaCount
expr_stmt|;
block|}
block|}
comment|//  if any of the inputs are ANY, the output is ANY
if|if
condition|(
name|anyCount
operator|>
literal|0
condition|)
block|{
return|return
name|createTypeWithNullability
argument_list|(
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
name|nullCount
operator|>
literal|0
operator|||
name|nullableCount
operator|>
literal|0
argument_list|)
return|;
block|}
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
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|RelDataType
name|type
init|=
name|types
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelDataTypeFamily
name|family
init|=
name|type
operator|.
name|getFamily
argument_list|()
decl_stmt|;
specifier|final
name|SqlTypeName
name|typeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeName
operator|==
name|SqlTypeName
operator|.
name|NULL
condition|)
block|{
continue|continue;
block|}
comment|// Convert Java types; for instance, JavaType(int) becomes INTEGER.
comment|// Except if all types are either NULL or Java types.
if|if
condition|(
name|isJavaType
argument_list|(
name|type
argument_list|)
operator|&&
name|javaCount
operator|+
name|nullCount
operator|<
name|types
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|RelDataType
name|originalType
init|=
name|type
decl_stmt|;
name|type
operator|=
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
condition|?
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|type
operator|.
name|getScale
argument_list|()
argument_list|)
else|:
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|?
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|)
else|:
name|createSqlType
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
name|type
operator|=
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|originalType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resultType
operator|==
literal|null
condition|)
block|{
name|resultType
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|resultType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|ROW
condition|)
block|{
return|return
name|leastRestrictiveStructuredType
argument_list|(
name|types
argument_list|)
return|;
block|}
block|}
name|RelDataTypeFamily
name|resultFamily
init|=
name|resultType
operator|.
name|getFamily
argument_list|()
decl_stmt|;
name|SqlTypeName
name|resultTypeName
init|=
name|resultType
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultFamily
operator|!=
name|family
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharOrBinaryFamilies
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|Charset
name|charset1
init|=
name|type
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|Charset
name|charset2
init|=
name|resultType
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|SqlCollation
name|collation1
init|=
name|type
operator|.
name|getCollation
argument_list|()
decl_stmt|;
name|SqlCollation
name|collation2
init|=
name|resultType
operator|.
name|getCollation
argument_list|()
decl_stmt|;
comment|// TODO:  refine collation combination rules
specifier|final
name|int
name|precision
init|=
name|SqlTypeUtil
operator|.
name|maxPrecision
argument_list|(
name|resultType
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|)
decl_stmt|;
comment|// If either type is LOB, then result is LOB with no precision.
comment|// Otherwise, if either is variable width, result is variable
comment|// width.  Otherwise, result is fixed width.
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isLob
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|createSqlType
argument_list|(
name|resultType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isLob
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|createSqlType
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isBoundedVariableWidth
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|createSqlType
argument_list|(
name|resultType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// this catch-all case covers type variable, and both fixed
name|SqlTypeName
name|newTypeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeSystem
operator|.
name|shouldConvertRaggedUnionTypesToVarying
argument_list|()
condition|)
block|{
if|if
condition|(
name|resultType
operator|.
name|getPrecision
argument_list|()
operator|!=
name|type
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
if|if
condition|(
name|newTypeName
operator|==
name|SqlTypeName
operator|.
name|CHAR
condition|)
block|{
name|newTypeName
operator|=
name|SqlTypeName
operator|.
name|VARCHAR
expr_stmt|;
block|}
if|else if
condition|(
name|newTypeName
operator|==
name|SqlTypeName
operator|.
name|BINARY
condition|)
block|{
name|newTypeName
operator|=
name|SqlTypeName
operator|.
name|VARBINARY
expr_stmt|;
block|}
block|}
block|}
name|resultType
operator|=
name|createSqlType
argument_list|(
name|newTypeName
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
name|Charset
name|charset
init|=
literal|null
decl_stmt|;
name|SqlCollation
name|collation
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|charset1
operator|!=
literal|null
operator|)
operator|||
operator|(
name|charset2
operator|!=
literal|null
operator|)
condition|)
block|{
if|if
condition|(
name|charset1
operator|==
literal|null
condition|)
block|{
name|charset
operator|=
name|charset2
expr_stmt|;
name|collation
operator|=
name|collation2
expr_stmt|;
block|}
if|else if
condition|(
name|charset2
operator|==
literal|null
condition|)
block|{
name|charset
operator|=
name|charset1
expr_stmt|;
name|collation
operator|=
name|collation1
expr_stmt|;
block|}
if|else if
condition|(
name|charset1
operator|.
name|equals
argument_list|(
name|charset2
argument_list|)
condition|)
block|{
name|charset
operator|=
name|charset1
expr_stmt|;
name|collation
operator|=
name|collation1
expr_stmt|;
block|}
if|else if
condition|(
name|charset1
operator|.
name|contains
argument_list|(
name|charset2
argument_list|)
condition|)
block|{
name|charset
operator|=
name|charset1
expr_stmt|;
name|collation
operator|=
name|collation1
expr_stmt|;
block|}
else|else
block|{
name|charset
operator|=
name|charset2
expr_stmt|;
name|collation
operator|=
name|collation2
expr_stmt|;
block|}
block|}
if|if
condition|(
name|charset
operator|!=
literal|null
condition|)
block|{
name|resultType
operator|=
name|createTypeWithCharsetAndCollation
argument_list|(
name|resultType
argument_list|,
name|charset
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
comment|// TODO: come up with a cleaner way to support
comment|// interval + datetime = datetime
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|>
operator|(
name|i
operator|+
literal|1
operator|)
condition|)
block|{
name|RelDataType
name|type1
init|=
name|types
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|type1
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|type1
expr_stmt|;
return|return
name|createTypeWithNullability
argument_list|(
name|resultType
argument_list|,
name|nullCount
operator|>
literal|0
operator|||
name|nullableCount
operator|>
literal|0
argument_list|)
return|;
block|}
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|typeName
operator|.
name|allowsPrec
argument_list|()
operator|&&
operator|!
name|resultTypeName
operator|.
name|allowsPrec
argument_list|()
condition|)
block|{
comment|// use the bigger primitive
if|if
condition|(
name|type
operator|.
name|getPrecision
argument_list|()
operator|>
name|resultType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
name|resultType
operator|=
name|type
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Let the result type have precision (p), scale (s)
comment|// and number of whole digits (d) as follows: d =
comment|// max(p1 - s1, p2 - s2) s<= max(s1, s2) p = s + d
name|int
name|p1
init|=
name|resultType
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|type
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|int
name|s1
init|=
name|resultType
operator|.
name|getScale
argument_list|()
decl_stmt|;
name|int
name|s2
init|=
name|type
operator|.
name|getScale
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxPrecision
init|=
name|typeSystem
operator|.
name|getMaxNumericPrecision
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxScale
init|=
name|typeSystem
operator|.
name|getMaxNumericScale
argument_list|()
decl_stmt|;
name|int
name|dout
init|=
name|Math
operator|.
name|max
argument_list|(
name|p1
operator|-
name|s1
argument_list|,
name|p2
operator|-
name|s2
argument_list|)
decl_stmt|;
name|dout
operator|=
name|Math
operator|.
name|min
argument_list|(
name|dout
argument_list|,
name|maxPrecision
argument_list|)
expr_stmt|;
name|int
name|scale
init|=
name|Math
operator|.
name|max
argument_list|(
name|s1
argument_list|,
name|s2
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
name|maxPrecision
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
name|maxScale
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
name|precision
operator|<=
name|maxPrecision
assert|;
assert|assert
name|precision
operator|>
literal|0
operator|||
operator|(
name|resultType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|DECIMAL
operator|&&
name|precision
operator|==
literal|0
operator|&&
name|scale
operator|==
literal|0
operator|)
assert|;
name|resultType
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
block|}
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isApproximateNumeric
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
comment|// already approximate; promote to double just in case
comment|// TODO:  only promote when required
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// Only promote to double for decimal types
name|resultType
operator|=
name|createDoublePrecisionType
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isApproximateNumeric
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isApproximateNumeric
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|getPrecision
argument_list|()
operator|>
name|resultType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
name|resultType
operator|=
name|type
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|createDoublePrecisionType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|resultType
operator|=
name|type
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// TODO: come up with a cleaner way to support
comment|// interval + datetime = datetime
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|>
operator|(
name|i
operator|+
literal|1
operator|)
condition|)
block|{
name|RelDataType
name|type1
init|=
name|types
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|type1
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|type1
expr_stmt|;
return|return
name|createTypeWithNullability
argument_list|(
name|resultType
argument_list|,
name|nullCount
operator|>
literal|0
operator|||
name|nullableCount
operator|>
literal|0
argument_list|)
return|;
block|}
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|resultType
argument_list|)
condition|)
block|{
comment|// TODO jvs 4-June-2005:  This shouldn't be necessary;
comment|// move logic into IntervalSqlType.combine
name|Object
name|type1
init|=
name|resultType
decl_stmt|;
name|resultType
operator|=
operator|(
operator|(
name|IntervalSqlType
operator|)
name|resultType
operator|)
operator|.
name|combine
argument_list|(
name|this
argument_list|,
operator|(
name|IntervalSqlType
operator|)
name|type
argument_list|)
expr_stmt|;
name|resultType
operator|=
operator|(
operator|(
name|IntervalSqlType
operator|)
name|resultType
operator|)
operator|.
name|combine
argument_list|(
name|this
argument_list|,
operator|(
name|IntervalSqlType
operator|)
name|type1
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// TODO: come up with a cleaner way to support
comment|// datetime +/- interval (or integer) = datetime
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|>
operator|(
name|i
operator|+
literal|1
operator|)
condition|)
block|{
name|RelDataType
name|type1
init|=
name|types
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|type1
argument_list|)
operator|||
name|SqlTypeUtil
operator|.
name|isIntType
argument_list|(
name|type1
argument_list|)
condition|)
block|{
name|resultType
operator|=
name|type
expr_stmt|;
return|return
name|createTypeWithNullability
argument_list|(
name|resultType
argument_list|,
name|nullCount
operator|>
literal|0
operator|||
name|nullableCount
operator|>
literal|0
argument_list|)
return|;
block|}
block|}
block|}
else|else
block|{
comment|// TODO:  datetime precision details; for now we let
comment|// leastRestrictiveByCast handle it
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|resultType
operator|!=
literal|null
operator|&&
name|nullableCount
operator|>
literal|0
condition|)
block|{
name|resultType
operator|=
name|createTypeWithNullability
argument_list|(
name|resultType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|resultType
return|;
block|}
specifier|private
name|RelDataType
name|createDoublePrecisionType
parameter_list|()
block|{
return|return
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|copyMultisetType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|MultisetSqlType
name|mt
init|=
operator|(
name|MultisetSqlType
operator|)
name|type
decl_stmt|;
name|RelDataType
name|elementType
init|=
name|copyType
argument_list|(
name|mt
operator|.
name|getComponentType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|MultisetSqlType
argument_list|(
name|elementType
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|copyIntervalType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
operator|new
name|IntervalSqlType
argument_list|(
name|typeSystem
argument_list|,
name|type
operator|.
name|getIntervalQualifier
argument_list|()
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|copyObjectType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
return|return
operator|new
name|ObjectSqlType
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|type
operator|.
name|getSqlIdentifier
argument_list|()
argument_list|,
name|nullable
argument_list|,
name|type
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|type
operator|.
name|getComparability
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|copyArrayType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|ArraySqlType
name|at
init|=
operator|(
name|ArraySqlType
operator|)
name|type
decl_stmt|;
name|RelDataType
name|elementType
init|=
name|copyType
argument_list|(
name|at
operator|.
name|getComponentType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArraySqlType
argument_list|(
name|elementType
argument_list|,
name|nullable
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|copyMapType
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|MapSqlType
name|mt
init|=
operator|(
name|MapSqlType
operator|)
name|type
decl_stmt|;
name|RelDataType
name|keyType
init|=
name|copyType
argument_list|(
name|mt
operator|.
name|getKeyType
argument_list|()
argument_list|)
decl_stmt|;
name|RelDataType
name|valueType
init|=
name|copyType
argument_list|(
name|mt
operator|.
name|getValueType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|MapSqlType
argument_list|(
name|keyType
argument_list|,
name|valueType
argument_list|,
name|nullable
argument_list|)
return|;
block|}
comment|// override RelDataTypeFactoryImpl
specifier|protected
name|RelDataType
name|canonize
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|type
operator|=
name|super
operator|.
name|canonize
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|type
operator|instanceof
name|ObjectSqlType
operator|)
condition|)
block|{
return|return
name|type
return|;
block|}
name|ObjectSqlType
name|objectType
init|=
operator|(
name|ObjectSqlType
operator|)
name|type
decl_stmt|;
if|if
condition|(
operator|!
name|objectType
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|objectType
operator|.
name|setFamily
argument_list|(
name|objectType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|objectType
operator|.
name|setFamily
argument_list|(
operator|(
name|RelDataTypeFamily
operator|)
name|createTypeWithNullability
argument_list|(
name|objectType
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
comment|/** The unknown type. Similar to the NULL type, but is only equal to    * itself. */
specifier|private
specifier|static
class|class
name|UnknownSqlType
extends|extends
name|BasicSqlType
block|{
name|UnknownSqlType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
operator|.
name|getTypeSystem
argument_list|()
argument_list|,
name|SqlTypeName
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|"UNKNOWN"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

