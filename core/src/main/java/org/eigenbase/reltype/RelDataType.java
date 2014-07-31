begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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

begin_comment
comment|/**  * RelDataType represents the type of a scalar expression or entire row returned  * from a relational expression.  *  *<p>This is a somewhat "fat" interface which unions the attributes of many  * different type classes into one. Inelegant, but since our type system was  * defined before the advent of Java generics, it avoids a lot of typecasting.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataType
comment|/*extends Type*/
block|{
name|int
name|SCALE_NOT_SPECIFIED
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
name|int
name|PRECISION_NOT_SPECIFIED
init|=
operator|-
literal|1
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Queries whether this is a structured type.    *    * @return whether this type has fields; examples include rows and    * user-defined structured types in SQL, and classes in Java    */
name|boolean
name|isStruct
parameter_list|()
function_decl|;
comment|// NOTE jvs 17-Dec-2004:  once we move to Java generics, getFieldList()
comment|// will be declared to return a read-only List<RelDataTypeField>,
comment|// and getFields() will be eliminated.  Currently,
comment|// anyone can mutate a type by poking into the array returned
comment|// by getFields!
comment|/**    * Gets the fields in a struct type. The field count is equal to the size of    * the returned list.    *    * @return read-only list of fields    */
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getFieldList
parameter_list|()
function_decl|;
comment|/**    * Returns the names of the fields in a struct type. The field count is    * equal to the size of the returned list.    *    * @return read-only list of field names    */
name|List
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
function_decl|;
comment|/**    * Returns the number of fields in a struct type.    *    *<p>This method is equivalent to<code>{@link #getFieldList}    * ().size()</code>.    */
name|int
name|getFieldCount
parameter_list|()
function_decl|;
comment|/**    * Looks up a field by name.    *    *<p>NOTE: Be careful choosing the value of {@code caseSensitive}:</p>    *<ul>    *<li>If the field name was supplied by an end-user (e.g. as a column alias    * in SQL), use your session's case-sensitivity setting.</li>    *<li>Only hard-code {@code true} if you are sure that the field name is    * internally generated.</li>    *<li>Hard-coding {@code false} is almost certainly wrong.</li>    *</ul>    *    * @param fieldName name of field to find    * @param caseSensitive Whether case-sensitive    * @return named field, or null if not found    */
name|RelDataTypeField
name|getField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
function_decl|;
comment|/**    * Queries whether this type allows null values.    *    * @return whether type allows null values    */
name|boolean
name|isNullable
parameter_list|()
function_decl|;
comment|/**    * Gets the component type if this type is a collection, otherwise null.    *    * @return canonical type descriptor for components    */
name|RelDataType
name|getComponentType
parameter_list|()
function_decl|;
comment|/**    * Gets the key type if this type is a map, otherwise null.    *    * @return canonical type descriptor for key    */
name|RelDataType
name|getKeyType
parameter_list|()
function_decl|;
comment|/**    * Gets the value type if this type is a map, otherwise null.    *    * @return canonical type descriptor for value    */
name|RelDataType
name|getValueType
parameter_list|()
function_decl|;
comment|/**    * Gets this type's character set, or null if this type cannot carry a    * character set or has no character set defined.    *    * @return charset of type    */
name|Charset
name|getCharset
parameter_list|()
function_decl|;
comment|/**    * Gets this type's collation, or null if this type cannot carry a collation    * or has no collation defined.    *    * @return collation of type    */
name|SqlCollation
name|getCollation
parameter_list|()
function_decl|;
comment|/**    * Gets this type's interval qualifier, or null if this is not an interval    * type.    *    * @return interval qualifier    */
name|SqlIntervalQualifier
name|getIntervalQualifier
parameter_list|()
function_decl|;
comment|/**    * Gets the JDBC-defined precision for values of this type. Note that this    * is not always the same as the user-specified precision. For example, the    * type INTEGER has no user-specified precision, but this method returns 10    * for an INTEGER type.    *    *<p>Returns {@link #PRECISION_NOT_SPECIFIED} (-1) if precision is not    * applicable for this type.</p>    *    * @return number of decimal digits for exact numeric types; number of    * decimal digits in mantissa for approximate numeric types; number of    * decimal digits for fractional seconds of datetime types; length in    * characters for character types; length in bytes for binary types; length    * in bits for bit types; 1 for BOOLEAN; -1 if precision is not valid for    * this type    */
name|int
name|getPrecision
parameter_list|()
function_decl|;
comment|/**    * Gets the scale of this type. Returns {@link #SCALE_NOT_SPECIFIED} (-1) if    * scale is not valid for this type.    *    * @return number of digits of scale    */
name|int
name|getScale
parameter_list|()
function_decl|;
comment|/**    * Gets the {@link SqlTypeName} of this type.    *    * @return SqlTypeName, or null if this is not an SQL predefined type    */
name|SqlTypeName
name|getSqlTypeName
parameter_list|()
function_decl|;
comment|/**    * Gets the {@link SqlIdentifier} associated with this type. For a    * predefined type, this is a simple identifier based on {@link    * #getSqlTypeName}. For a user-defined type, this is a compound identifier    * which uniquely names the type.    *    * @return SqlIdentifier, or null if this is not an SQL type    */
name|SqlIdentifier
name|getSqlIdentifier
parameter_list|()
function_decl|;
comment|/**    * Gets a string representation of this type without detail such as    * character set and nullability.    *    * @return abbreviated type string    */
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**    * Gets a string representation of this type with full detail such as    * character set and nullability. The string must serve as a "digest" for    * this type, meaning two types can be considered identical iff their    * digests are equal.    *    * @return full type string    */
name|String
name|getFullTypeString
parameter_list|()
function_decl|;
comment|/**    * Gets a canonical object representing the family of this type. Two values    * can be compared if and only if their types are in the same family.    *    * @return canonical object representing type family    */
name|RelDataTypeFamily
name|getFamily
parameter_list|()
function_decl|;
comment|/**    * @return precedence list for this type    */
name|RelDataTypePrecedenceList
name|getPrecedenceList
parameter_list|()
function_decl|;
comment|/**    * @return the category of comparison operators which make sense when    * applied to values of this type    */
name|RelDataTypeComparability
name|getComparability
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelDataType.java
end_comment

end_unit

