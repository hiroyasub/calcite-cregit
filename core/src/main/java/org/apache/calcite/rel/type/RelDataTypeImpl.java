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
name|rel
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
name|SqlIdentifier
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|BasicSqlType
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
name|calcite
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Iterables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|UnknownInitialization
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * RelDataTypeImpl is an abstract base for implementations of  * {@link RelDataType}.  *  *<p>Identity is based upon the {@link #digest} field, which each derived class  * should set during construction.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelDataTypeImpl
implements|implements
name|RelDataType
implements|,
name|RelDataTypeFamily
block|{
comment|/**    * Suffix for the digests of non-nullable types.    */
specifier|public
specifier|static
specifier|final
name|String
name|NON_NULLABLE_SUFFIX
init|=
literal|" NOT NULL"
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
decl_stmt|;
specifier|protected
annotation|@
name|Nullable
name|String
name|digest
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RelDataTypeImpl.    *    * @param fieldList List of fields    */
specifier|protected
name|RelDataTypeImpl
parameter_list|(
annotation|@
name|Nullable
name|List
argument_list|<
name|?
extends|extends
name|RelDataTypeField
argument_list|>
name|fieldList
parameter_list|)
block|{
if|if
condition|(
name|fieldList
operator|!=
literal|null
condition|)
block|{
comment|// Create a defensive copy of the list.
name|this
operator|.
name|fieldList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fieldList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|fieldList
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Default constructor, to allow derived classes such as    * {@link BasicSqlType} to be {@link Serializable}.    *    *<p>(The serialization specification says that a class can be serializable    * even if its base class is not serializable, provided that the base class    * has a public or protected zero-args constructor.)    */
specifier|protected
name|RelDataTypeImpl
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|elideRecord
parameter_list|)
block|{
if|if
condition|(
name|fieldList
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to access field "
operator|+
name|fieldName
operator|+
literal|" in a type with no fields: "
operator|+
name|this
argument_list|)
throw|;
block|}
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fieldList
control|)
block|{
if|if
condition|(
name|Util
operator|.
name|matches
argument_list|(
name|caseSensitive
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
if|if
condition|(
name|elideRecord
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Slot
argument_list|>
name|slots
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|getFieldRecurse
argument_list|(
name|slots
argument_list|,
name|this
argument_list|,
literal|0
argument_list|,
name|fieldName
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
name|loop
label|:
for|for
control|(
name|Slot
name|slot
range|:
name|slots
control|)
block|{
switch|switch
condition|(
name|slot
operator|.
name|count
condition|)
block|{
case|case
literal|0
case|:
break|break;
comment|// no match at this depth; try deeper
case|case
literal|1
case|:
return|return
name|slot
operator|.
name|field
return|;
default|default:
break|break
name|loop
break|;
comment|// duplicate fields at this depth; abandon search
block|}
block|}
block|}
comment|// Extra field
if|if
condition|(
name|fieldList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|RelDataTypeField
name|lastField
init|=
name|Iterables
operator|.
name|getLast
argument_list|(
name|fieldList
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"_extra"
argument_list|)
condition|)
block|{
return|return
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|fieldName
argument_list|,
operator|-
literal|1
argument_list|,
name|lastField
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// a dynamic * field will match any field name.
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fieldList
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isDynamicStar
argument_list|()
condition|)
block|{
comment|// the requested field could be in the unresolved star
return|return
name|field
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|void
name|getFieldRecurse
parameter_list|(
name|List
argument_list|<
name|Slot
argument_list|>
name|slots
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|int
name|depth
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
while|while
condition|(
name|slots
operator|.
name|size
argument_list|()
operator|<=
name|depth
condition|)
block|{
name|slots
operator|.
name|add
argument_list|(
operator|new
name|Slot
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Slot
name|slot
init|=
name|slots
operator|.
name|get
argument_list|(
name|depth
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|type
operator|.
name|getFieldList
argument_list|()
control|)
block|{
if|if
condition|(
name|Util
operator|.
name|matches
argument_list|(
name|caseSensitive
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldName
argument_list|)
condition|)
block|{
name|slot
operator|.
name|count
operator|++
expr_stmt|;
name|slot
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
block|}
comment|// No point looking to depth + 1 if there is a hit at depth.
if|if
condition|(
name|slot
operator|.
name|count
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|type
operator|.
name|getFieldList
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|getFieldRecurse
argument_list|(
name|slots
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|depth
operator|+
literal|1
argument_list|,
name|fieldName
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldList
parameter_list|()
block|{
assert|assert
name|fieldList
operator|!=
literal|null
operator|:
literal|"fieldList must not be null, type = "
operator|+
name|this
assert|;
return|return
name|fieldList
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
assert|assert
name|fieldList
operator|!=
literal|null
operator|:
literal|"fieldList must not be null, type = "
operator|+
name|this
assert|;
return|return
name|Pair
operator|.
name|left
argument_list|(
name|fieldList
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
assert|assert
name|fieldList
operator|!=
literal|null
operator|:
literal|"fieldList must not be null, type = "
operator|+
name|this
assert|;
return|return
name|fieldList
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StructKind
name|getStructKind
parameter_list|()
block|{
return|return
name|isStruct
argument_list|()
condition|?
name|StructKind
operator|.
name|FULLY_QUALIFIED
else|:
name|StructKind
operator|.
name|NONE
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataType
name|getComponentType
parameter_list|()
block|{
comment|// this is not a collection type
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataType
name|getKeyType
parameter_list|()
block|{
comment|// this is not a map type
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataType
name|getValueType
parameter_list|()
block|{
comment|// this is not a map type
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStruct
parameter_list|()
block|{
return|return
name|fieldList
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|RelDataTypeImpl
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|digest
argument_list|,
operator|(
operator|(
name|RelDataTypeImpl
operator|)
name|obj
operator|)
operator|.
name|digest
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|digest
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFullTypeString
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|digest
argument_list|,
literal|"digest"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isNullable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlCollation
name|getCollation
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlIntervalQualifier
name|getIntervalQualifier
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|PRECISION_NOT_SPECIFIED
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getScale
parameter_list|()
block|{
return|return
name|SCALE_NOT_SPECIFIED
return|;
block|}
comment|/**    * Gets the {@link SqlTypeName} of this type.    * Sub-classes must override the method to ensure the resulting value is non-nullable.    *    * @return SqlTypeName, never null    */
annotation|@
name|Override
specifier|public
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
block|{
comment|// The implementations must provide non-null value, however, we keep this for compatibility
return|return
name|castNonNull
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlIdentifier
name|getSqlIdentifier
parameter_list|()
block|{
name|SqlTypeName
name|typeName
init|=
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
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|typeName
operator|.
name|name
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypeFamily
name|getFamily
parameter_list|()
block|{
comment|// by default, put each type into its own family
return|return
name|this
return|;
block|}
comment|/**    * Generates a string representation of this type.    *    * @param sb         StringBuilder into which to generate the string    * @param withDetail when true, all detail information needed to compute a    *                   unique digest (and return from getFullTypeString) should    *                   be included;    */
specifier|protected
specifier|abstract
name|void
name|generateTypeString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|boolean
name|withDetail
parameter_list|)
function_decl|;
comment|/**    * Computes the digest field. This should be called in every non-abstract    * subclass constructor once the type is fully defined.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"method.invocation.invalid"
argument_list|)
specifier|protected
name|void
name|computeDigest
parameter_list|(
annotation|@
name|UnknownInitialization
name|RelDataTypeImpl
name|this
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|generateTypeString
argument_list|(
name|sb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isNullable
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|NON_NULLABLE_SUFFIX
argument_list|)
expr_stmt|;
block|}
name|digest
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|generateTypeString
argument_list|(
name|sb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypePrecedenceList
name|getPrecedenceList
parameter_list|()
block|{
comment|// by default, make each type have a precedence list containing
comment|// only other types in the same family
return|return
operator|new
name|RelDataTypePrecedenceList
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|containsType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|getFamily
argument_list|()
operator|==
name|type
operator|.
name|getFamily
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTypePrecedence
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
assert|assert
name|containsType
argument_list|(
name|type1
argument_list|)
assert|;
assert|assert
name|containsType
argument_list|(
name|type2
argument_list|)
assert|;
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataTypeComparability
name|getComparability
parameter_list|()
block|{
return|return
name|RelDataTypeComparability
operator|.
name|ALL
return|;
block|}
comment|/**    * Returns an implementation of    * {@link RelProtoDataType}    * that copies a given type using the given type factory.    */
specifier|public
specifier|static
name|RelProtoDataType
name|proto
parameter_list|(
specifier|final
name|RelDataType
name|protoType
parameter_list|)
block|{
assert|assert
name|protoType
operator|!=
literal|null
assert|;
return|return
name|typeFactory
lambda|->
name|typeFactory
operator|.
name|copyType
argument_list|(
name|protoType
argument_list|)
return|;
block|}
comment|/** Returns a {@link org.apache.calcite.rel.type.RelProtoDataType}    * that will create a type {@code typeName}.    *    *<p>For example, {@code proto(SqlTypeName.DATE), false}    * will create {@code DATE NOT NULL}.</p>    *    * @param typeName Type name    * @param nullable Whether nullable    * @return Proto data type    */
specifier|public
specifier|static
name|RelProtoDataType
name|proto
parameter_list|(
specifier|final
name|SqlTypeName
name|typeName
parameter_list|,
specifier|final
name|boolean
name|nullable
parameter_list|)
block|{
assert|assert
name|typeName
operator|!=
literal|null
assert|;
return|return
name|typeFactory
lambda|->
block|{
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|;
block|}
comment|/** Returns a {@link org.apache.calcite.rel.type.RelProtoDataType}    * that will create a type {@code typeName(precision)}.    *    *<p>For example, {@code proto(SqlTypeName.VARCHAR, 100, false)}    * will create {@code VARCHAR(100) NOT NULL}.</p>    *    * @param typeName Type name    * @param precision Precision    * @param nullable Whether nullable    * @return Proto data type    */
specifier|public
specifier|static
name|RelProtoDataType
name|proto
parameter_list|(
specifier|final
name|SqlTypeName
name|typeName
parameter_list|,
specifier|final
name|int
name|precision
parameter_list|,
specifier|final
name|boolean
name|nullable
parameter_list|)
block|{
assert|assert
name|typeName
operator|!=
literal|null
assert|;
return|return
name|typeFactory
lambda|->
block|{
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|;
block|}
comment|/** Returns a {@link org.apache.calcite.rel.type.RelProtoDataType}    * that will create a type {@code typeName(precision, scale)}.    *    *<p>For example, {@code proto(SqlTypeName.DECIMAL, 7, 2, false)}    * will create {@code DECIMAL(7, 2) NOT NULL}.</p>    *    * @param typeName Type name    * @param precision Precision    * @param scale Scale    * @param nullable Whether nullable    * @return Proto data type    */
specifier|public
specifier|static
name|RelProtoDataType
name|proto
parameter_list|(
specifier|final
name|SqlTypeName
name|typeName
parameter_list|,
specifier|final
name|int
name|precision
parameter_list|,
specifier|final
name|int
name|scale
parameter_list|,
specifier|final
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|typeFactory
lambda|->
block|{
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
return|;
block|}
return|;
block|}
comment|/**    * Returns the "extra" field in a row type whose presence signals that    * fields will come into existence just by asking for them.    *    * @param rowType Row type    * @return The "extra" field, or null    */
specifier|public
specifier|static
annotation|@
name|Nullable
name|RelDataTypeField
name|extra
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
comment|// Even in a case-insensitive connection, the name must be precisely
comment|// "_extra".
return|return
name|rowType
operator|.
name|getField
argument_list|(
literal|"_extra"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDynamicStruct
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Work space for {@link RelDataTypeImpl#getFieldRecurse}. */
specifier|private
specifier|static
class|class
name|Slot
block|{
name|int
name|count
decl_stmt|;
annotation|@
name|Nullable
name|RelDataTypeField
name|field
decl_stmt|;
block|}
block|}
end_class

end_unit

