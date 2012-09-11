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
comment|/**  * RelDataTypeFactory is a factory for datatype descriptors. It defines methods  * for instantiating and combining SQL, Java, and collection types. The factory  * also provides methods for return type inference for arithmetic in cases where  * SQL 2003 is implementation defined or impractical.  *  *<p>This interface is an example of the {@link  * org.eigenbase.util.Glossary#AbstractFactoryPattern abstract factory pattern}.  * Any implementation of<code>RelDataTypeFactory</code> must ensure that type  * objects are canonical: two types are equal if and only if they are  * represented by the same Java object. This reduces memory consumption and  * comparison cost.  *  * @author jhyde  * @version $Id$  * @since May 29, 2003  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a type which corresponds to a Java class.      *      * @param clazz the Java class used to define the type      *      * @return canonical Java type descriptor      */
name|RelDataType
name|createJavaType
parameter_list|(
name|Class
name|clazz
parameter_list|)
function_decl|;
comment|/**      * Creates a cartesian product type.      *      * @return canonical join type descriptor      *      * @pre types array of types to be joined      * @pre types != null      * @pre types.length>= 1      */
specifier|public
name|RelDataType
name|createJoinType
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|)
function_decl|;
comment|/**      * Creates a type which represents a structured collection of fields.      *      * @param types types of the fields      * @param fieldNames names of the fields      *      * @return canonical struct type descriptor      *      * @pre types.length == fieldNames.length      * @post return != null      */
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
function_decl|;
comment|/**      * Creates a type which represents a structured collection of fields, given      * lists of the names and types of the fields.      *      * @param typeList types of the fields      * @param fieldNameList names of the fields      *      * @return canonical struct type descriptor      *      * @pre typeList.size() == fieldNameList.size()      * @post return != null      */
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
function_decl|;
comment|/**      * Creates a type which represents a structured collection of fields,      * obtaining the field information via a callback.      *      * @param fieldInfo callback for field information      *      * @return canonical struct type descriptor      */
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
comment|/**      * Creates a type which represents a structured collection of fieldList,      * obtaining the field information from a list of (name, type) pairs.      *      * @param fieldList List of (name, type) pairs      *      * @return canonical struct type descriptor      */
specifier|public
name|RelDataType
name|createStructType
parameter_list|(
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
function_decl|;
comment|/**      * Creates an array type. Arrays are ordered collections of elements.      *      * @param elementType type of the elements of the array      * @param maxCardinality maximum array size, or -1 for unlimited      *      * @return canonical array type descriptor      */
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
function_decl|;
comment|/**      * Creates a multiset type. Multisets are unordered collections of elements.      *      * @param elementType type of the elements of the multiset      * @param maxCardinality maximum collection size, or -1 for unlimited      *      * @return canonical multiset type descriptor      */
specifier|public
name|RelDataType
name|createMultisetType
parameter_list|(
name|RelDataType
name|elementType
parameter_list|,
name|long
name|maxCardinality
parameter_list|)
function_decl|;
comment|/**      * Duplicates a type, making a deep copy. Normally, this is a no-op, since      * canonical type objects are returned. However, it is useful when copying a      * type from one factory to another.      *      * @param type input type      *      * @return output type, a new object equivalent to input type      */
specifier|public
name|RelDataType
name|copyType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**      * Creates a type which is the same as another type but with possibly      * different nullability. The output type may be identical to the input      * type. For type systems without a concept of nullability, the return value      * is always the same as the input.      *      * @param type input type      * @param nullable true to request a nullable type; false to request a NOT      * NULL type      *      * @return output type, same as input type except with specified nullability      */
specifier|public
name|RelDataType
name|createTypeWithNullability
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
function_decl|;
comment|/**      * Creates a Type which is the same as another type but with possibily      * different charset or collation. For types without a concept of charset or      * collation this function must throw an error.      *      * @param type input type      * @param charset charset to assign      * @param collation collation to assign      *      * @return output type, same as input type except with specified charset and      * collation      *      * @pre SqlTypeUtil.inCharFamily(type)      */
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
function_decl|;
comment|/**      * @return the default {@link Charset} for string types      */
specifier|public
name|Charset
name|getDefaultCharset
parameter_list|()
function_decl|;
comment|/**      * Returns the most general of a set of types (that is, one type to which      * they can all be cast), or null if conversion is not possible. The result      * may be a new type which is less restrictive than any of the input types,      * e.g. leastRestrictive(INT, NUMERIC(3,2)) could be NUMERIC(12,2).      *      * @param types input types to be unioned      *      * @return canonical union type descriptor      *      * @pre types != null      * @pre types.length>= 1      */
specifier|public
name|RelDataType
name|leastRestrictive
parameter_list|(
name|RelDataType
index|[]
name|types
parameter_list|)
function_decl|;
comment|/**      * Creates a SQL type with no precision or scale.      *      * @param typeName Name of the type, for example {@link      * SqlTypeName#BOOLEAN}.      *      * @return canonical type descriptor      *      * @pre typeName != null      * @post return != null      */
specifier|public
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**      * Creates a SQL type with length (precision) but no scale.      *      * @param typeName Name of the type, for example {@link      * org.eigenbase.sql.type.SqlTypeName#VARCHAR}.      * @param precision maximum length of the value (non-numeric types) or the      * precision of the value (numeric/datetime types) requires both operands to      * have exact numeric types.      *      * @return canonical type descriptor      *      * @pre typeName != null      * @pre length>= 0      * @post return != null      */
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
function_decl|;
comment|/**      * Creates a SQL type with precision and scale.      *      * @param typeName Name of the type, for example {@link      * org.eigenbase.sql.type.SqlTypeName#DECIMAL}.      * @param precision precision of the value      * @param scale scale of the values, i.e. the number of decimal places to      * shift the value. For example, a NUMBER(10,3) value of "123.45" is      * represented "123450" (that is, multiplied by 10^3). A negative scale<em>      * is</em> valid.      *      * @return canonical type descriptor      *      * @pre typeName != null      * @pre length>= 0      * @post return != null      */
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
function_decl|;
comment|/**      * Creates a SQL interval type.      *      * @param intervalQualifier contains information if it is a year-month or a      * day-time interval along with precision information      *      * @return canonical type descriptor      */
specifier|public
name|RelDataType
name|createSqlIntervalType
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
function_decl|;
comment|/**      * Infers the return type of a decimal multiplication. Decimal      * multiplication involves at least one decimal operand and requires both      * operands to have exact numeric types.      *      * @param type1 type of the first operand      * @param type2 type of the second operand      *      * @return the result type for a decimal multiplication, or null if decimal      * multiplication should not be applied to the operands.      */
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
function_decl|;
comment|/**      * @return whether a decimal multiplication should be implemented by casting      * arguments to double values.      *      * @pre createDecimalProduct(type1, type2) != null      */
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
function_decl|;
comment|/**      * Infers the return type of a decimal division. Decimal division involves      * at least one decimal operand and requires both operands to have exact      * numeric types.      *      * @param type1 type of the first operand      * @param type2 type of the second operand      *      * @return the result type for a decimal division, or null if decimal      * division should not be applied to the operands.      */
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
function_decl|;
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**      * Callback which provides enough information to create fields.      */
specifier|public
interface|interface
name|FieldInfo
block|{
comment|/**          * Returns the number of fields.          *          * @return number of fields          */
specifier|public
name|int
name|getFieldCount
parameter_list|()
function_decl|;
comment|/**          * Returns the name of a given field.          *          * @param index Ordinal of field          * @return Name of given field          */
specifier|public
name|String
name|getFieldName
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**          * Returns the type of a given field.          *          * @param index Ordinal of field          * @return Type of given field          */
specifier|public
name|RelDataType
name|getFieldType
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
block|}
comment|/**      * Simple implementation of {@link FieldInfo}, based on a list of fields.      */
specifier|public
specifier|static
class|class
name|ListFieldInfo
implements|implements
name|FieldInfo
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|RelDataTypeField
argument_list|>
name|fieldList
decl_stmt|;
comment|/**          * Creates a ListFieldInfo.          *          * @param fieldList List of fields          */
specifier|public
name|ListFieldInfo
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
name|this
operator|.
name|fieldList
operator|=
name|fieldList
expr_stmt|;
block|}
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
return|return
name|fieldList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
comment|/**      * Implementation of {@link FieldInfo} that provides a fluid API to build      * a list of fields.      */
specifier|public
specifier|static
class|class
name|FieldInfoBuilder
implements|implements
name|FieldInfo
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataTypeField
argument_list|>
argument_list|()
decl_stmt|;
comment|/**          * Creates a FieldInfoBuilder with one field.          *          * @param name Field name          * @param type Field type          * @return A FieldInfoBuilder          */
specifier|public
specifier|static
name|FieldInfoBuilder
name|of
parameter_list|(
name|String
name|name
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
operator|new
name|FieldInfoBuilder
argument_list|()
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/**          * Creates a FieldInfoBuilder with the given fields.          *          * @param fields Field          * @return A FieldInfoBuilder          */
specifier|public
specifier|static
name|FieldInfoBuilder
name|of
parameter_list|(
name|Iterable
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
return|return
operator|new
name|FieldInfoBuilder
argument_list|()
operator|.
name|addAll
argument_list|(
name|fields
argument_list|)
return|;
block|}
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|fields
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
name|fields
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
return|return
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
comment|/** Adds a field with given name and type. */
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
name|name
argument_list|,
name|fields
operator|.
name|size
argument_list|()
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a field. Field's ordinal is ignored. */
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|RelDataTypeField
name|field
parameter_list|)
block|{
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a field. */
specifier|public
name|FieldInfoBuilder
name|addAll
parameter_list|(
name|Iterable
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fields
control|)
block|{
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End RelDataTypeFactory.java
end_comment

end_unit

