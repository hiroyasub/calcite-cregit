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
name|io
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
name|parser
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
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
decl_stmt|;
specifier|protected
name|String
name|digest
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RelDataTypeImpl.    *    * @param fieldList List of fields    */
specifier|protected
name|RelDataTypeImpl
parameter_list|(
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
comment|/**    * Default constructor, to allow derived classes such as {@link    * BasicSqlType} to be {@link Serializable}.    *    *<p>(The serialization specification says that a class can be serializable    * even if its base class is not serializable, provided that the base class    * has a public or protected zero-args constructor.)    */
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
comment|// implement RelDataType
specifier|public
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
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
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|field
return|;
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
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getFieldOrdinal
parameter_list|(
name|String
name|fieldName
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
name|fieldList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fieldList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|// implement RelDataType
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldList
parameter_list|()
block|{
assert|assert
name|isStruct
argument_list|()
assert|;
return|return
name|fieldList
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|left
argument_list|(
name|fieldList
argument_list|)
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
assert|assert
name|isStruct
argument_list|()
operator|:
name|this
assert|;
return|return
name|fieldList
operator|.
name|size
argument_list|()
return|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataType
name|getComponentType
parameter_list|()
block|{
comment|// this is not a collection type
return|return
literal|null
return|;
block|}
specifier|public
name|RelDataType
name|getKeyType
parameter_list|()
block|{
comment|// this is not a map type
return|return
literal|null
return|;
block|}
specifier|public
name|RelDataType
name|getValueType
parameter_list|()
block|{
comment|// this is not a map type
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
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
comment|// implement RelDataType
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|RelDataTypeImpl
condition|)
block|{
specifier|final
name|RelDataTypeImpl
name|that
init|=
operator|(
name|RelDataTypeImpl
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|digest
operator|.
name|equals
argument_list|(
name|that
operator|.
name|digest
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|digest
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|// implement RelDataType
specifier|public
name|String
name|getFullTypeString
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
comment|// implement RelDataType
specifier|public
name|boolean
name|isNullable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|// implement RelDataType
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
throws|throws
name|RuntimeException
block|{
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
specifier|public
name|SqlIntervalQualifier
name|getIntervalQualifier
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|PRECISION_NOT_SPECIFIED
return|;
block|}
comment|// implement RelDataType
specifier|public
name|int
name|getScale
parameter_list|()
block|{
return|return
name|SCALE_NOT_SPECIFIED
return|;
block|}
comment|// implement RelDataType
specifier|public
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// implement RelDataType
specifier|public
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
comment|// implement RelDataType
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
comment|/**    * Generates a string representation of this type.    *    * @param sb         StringBuffer into which to generate the string    * @param withDetail when true, all detail information needed to compute a    *                   unique digest (and return from getFullTypeString) should    *                   be included;    */
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
specifier|protected
name|void
name|computeDigest
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
literal|" NOT NULL"
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
comment|// implement RelDataType
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
comment|// implement RelDataType
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
operator|(
name|containsType
argument_list|(
name|type1
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|containsType
argument_list|(
name|type2
argument_list|)
operator|)
assert|;
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|// implement RelDataType
specifier|public
name|RelDataTypeComparability
name|getComparability
parameter_list|()
block|{
return|return
name|RelDataTypeComparability
operator|.
name|All
return|;
block|}
comment|/**    * Returns an implementation of    * {@link RelProtoDataType}    * that copies a given type using th given type factory.    */
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
return|return
operator|new
name|RelProtoDataType
argument_list|()
block|{
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataTypeFactory
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|copyType
argument_list|(
name|protoType
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelDataTypeImpl.java
end_comment

end_unit

