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
name|avatica
operator|.
name|util
operator|.
name|TimeUnit
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|Map
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

begin_comment
comment|/**  * RelDataTypeFactory is a factory for datatype descriptors. It defines methods  * for instantiating and combining SQL, Java, and collection types. The factory  * also provides methods for return type inference for arithmetic in cases where  * SQL 2003 is implementation defined or impractical.  *  *<p>This interface is an example of the  * {@link org.apache.calcite.util.Glossary#ABSTRACT_FACTORY_PATTERN abstract factory pattern}.  * Any implementation of<code>RelDataTypeFactory</code> must ensure that type  * objects are canonical: two types are equal if and only if they are  * represented by the same Java object. This reduces memory consumption and  * comparison cost.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the type system.    *    * @return Type system    */
name|RelDataTypeSystem
name|getTypeSystem
parameter_list|()
function_decl|;
comment|/**    * Creates a type that corresponds to a Java class.    *    * @param clazz the Java class used to define the type    * @return canonical Java type descriptor    */
name|RelDataType
name|createJavaType
parameter_list|(
name|Class
name|clazz
parameter_list|)
function_decl|;
comment|/**    * Creates a cartesian product type.    *    * @return canonical join type descriptor    * @param types array of types to be joined    */
name|RelDataType
name|createJoinType
parameter_list|(
name|RelDataType
modifier|...
name|types
parameter_list|)
function_decl|;
comment|/**    * Creates a type that represents a structured collection of fields, given    * lists of the names and types of the fields.    *    * @param  kind         Name resolution policy    * @param typeList      types of the fields    * @param fieldNameList names of the fields    * @return canonical struct type descriptor    */
name|RelDataType
name|createStructType
parameter_list|(
name|StructKind
name|kind
parameter_list|,
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
comment|/** Creates a type that represents a structured collection of fields.    * Shorthand for<code>createStructType(StructKind.FULLY_QUALIFIED, typeList,    * fieldNameList)</code>. */
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
comment|/**    * Creates a type that represents a structured collection of fields,    * obtaining the field information via a callback.    *    * @param fieldInfo callback for field information    * @return canonical struct type descriptor    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|RelDataType
name|createStructType
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
comment|/**    * Creates a type that represents a structured collection of fieldList,    * obtaining the field information from a list of (name, type) pairs.    *    * @param fieldList List of (name, type) pairs    * @return canonical struct type descriptor    */
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
comment|/**    * Creates an array type. Arrays are ordered collections of elements.    *    * @param elementType    type of the elements of the array    * @param maxCardinality maximum array size, or -1 for unlimited    * @return canonical array type descriptor    */
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
comment|/**    * Creates a map type. Maps are unordered collections of key/value pairs.    *    * @param keyType   type of the keys of the map    * @param valueType type of the values of the map    * @return canonical map type descriptor    */
name|RelDataType
name|createMapType
parameter_list|(
name|RelDataType
name|keyType
parameter_list|,
name|RelDataType
name|valueType
parameter_list|)
function_decl|;
comment|/**    * Creates a multiset type. Multisets are unordered collections of elements.    *    * @param elementType    type of the elements of the multiset    * @param maxCardinality maximum collection size, or -1 for unlimited    * @return canonical multiset type descriptor    */
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
comment|/**    * Duplicates a type, making a deep copy. Normally, this is a no-op, since    * canonical type objects are returned. However, it is useful when copying a    * type from one factory to another.    *    * @param type input type    * @return output type, a new object equivalent to input type    */
name|RelDataType
name|copyType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**    * Creates a type that is the same as another type but with possibly    * different nullability. The output type may be identical to the input    * type. For type systems without a concept of nullability, the return value    * is always the same as the input.    *    * @param type     input type    * @param nullable true to request a nullable type; false to request a NOT    *                 NULL type    * @return output type, same as input type except with specified nullability    * @throws NullPointerException if type is null    */
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
comment|/**    * Creates a type that is the same as another type but with possibly    * different charset or collation. For types without a concept of charset or    * collation this function must throw an error.    *    * @param type      input type    * @param charset   charset to assign    * @param collation collation to assign    * @return output type, same as input type except with specified charset and    * collation    */
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
comment|/** Returns the default {@link Charset} (valid if this is a string type). */
name|Charset
name|getDefaultCharset
parameter_list|()
function_decl|;
comment|/**    * Returns the most general of a set of types (that is, one type to which    * they can all be cast), or null if conversion is not possible. The result    * may be a new type that is less restrictive than any of the input types,    * e.g.<code>leastRestrictive(INT, NUMERIC(3, 2))</code> could be    * {@code NUMERIC(12, 2)}.    *    * @param types input types to be combined using union (not null, not empty)    * @return canonical union type descriptor    */
name|RelDataType
name|leastRestrictive
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
function_decl|;
comment|/**    * Creates a SQL type with no precision or scale.    *    * @param typeName Name of the type, for example {@link SqlTypeName#BOOLEAN},    *   never null    * @return canonical type descriptor    */
name|RelDataType
name|createSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Creates a SQL type that represents the "unknown" type.    * It is only equal to itself, and is distinct from the NULL type.     * @return unknown type    */
name|RelDataType
name|createUnknownType
parameter_list|()
function_decl|;
comment|/**    * Creates a SQL type with length (precision) but no scale.    *    * @param typeName  Name of the type, for example {@link SqlTypeName#VARCHAR}.    *                  Never null.    * @param precision Maximum length of the value (non-numeric types) or the    *                  precision of the value (numeric/datetime types).    *                  Must be non-negative or    *                  {@link RelDataType#PRECISION_NOT_SPECIFIED}.    * @return canonical type descriptor    */
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
comment|/**    * Creates a SQL type with precision and scale.    *    * @param typeName  Name of the type, for example {@link SqlTypeName#DECIMAL}.    *                  Never null.    * @param precision Precision of the value.    *                  Must be non-negative or    *                  {@link RelDataType#PRECISION_NOT_SPECIFIED}.    * @param scale     scale of the values, i.e. the number of decimal places to    *                  shift the value. For example, a NUMBER(10,3) value of    *                  "123.45" is represented "123450" (that is, multiplied by    *                  10^3). A negative scale<em>is</em> valid.    * @return canonical type descriptor    */
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
comment|/**    * Creates a SQL interval type.    *    * @param intervalQualifier contains information if it is a year-month or a    *                          day-time interval along with precision information    * @return canonical type descriptor    */
name|RelDataType
name|createSqlIntervalType
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
function_decl|;
comment|/**    * Infers the return type of a decimal multiplication. Decimal    * multiplication involves at least one decimal operand and requires both    * operands to have exact numeric types.    *    * @param type1 type of the first operand    * @param type2 type of the second operand    * @return the result type for a decimal multiplication, or null if decimal    * multiplication should not be applied to the operands.    * @deprecated Use    * {@link RelDataTypeSystem#deriveDecimalMultiplyType(RelDataTypeFactory, RelDataType, RelDataType)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
comment|/**    * Returns whether a decimal multiplication should be implemented by casting    * arguments to double values.    *    *<p>Pre-condition:<code>createDecimalProduct(type1, type2) != null</code>    *    * @deprecated Use    * {@link RelDataTypeSystem#shouldUseDoubleMultiplication(RelDataTypeFactory, RelDataType, RelDataType)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
comment|/**    * Infers the return type of a decimal division. Decimal division involves    * at least one decimal operand and requires both operands to have exact    * numeric types.    *    * @param type1 type of the first operand    * @param type2 type of the second operand    * @return the result type for a decimal division, or null if decimal    * division should not be applied to the operands.    *    * @deprecated Use    * {@link RelDataTypeSystem#deriveDecimalDivideType(RelDataTypeFactory, RelDataType, RelDataType)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
comment|/**    * Create a decimal type equivalent to the numeric {@code type},    * this is related to specific system implementation,    * you can override this logic if it is required.    *    * @param type the numeric type to create decimal type with    * @return decimal equivalence of the numeric type.    */
name|RelDataType
name|decimalOf
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**    * Creates a    * {@link org.apache.calcite.rel.type.RelDataTypeFactory.FieldInfoBuilder}.    * But since {@code FieldInfoBuilder} is deprecated, we recommend that you use    * its base class {@link Builder}, which is not deprecated.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
name|FieldInfoBuilder
name|builder
parameter_list|()
function_decl|;
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**    * Callback that provides enough information to create fields.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
interface|interface
name|FieldInfo
block|{
comment|/**      * Returns the number of fields.      *      * @return number of fields      */
name|int
name|getFieldCount
parameter_list|()
function_decl|;
comment|/**      * Returns the name of a given field.      *      * @param index Ordinal of field      * @return Name of given field      */
name|String
name|getFieldName
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * Returns the type of a given field.      *      * @param index Ordinal of field      * @return Type of given field      */
name|RelDataType
name|getFieldType
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link FieldInfo} that provides a fluid API to build    * a list of fields.    */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
class|class
name|FieldInfoBuilder
extends|extends
name|Builder
implements|implements
name|FieldInfo
block|{
specifier|public
name|FieldInfoBuilder
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|typeName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
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
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|TimeUnit
name|startUnit
parameter_list|,
name|int
name|startPrecision
parameter_list|,
name|TimeUnit
name|endUnit
parameter_list|,
name|int
name|fractionalSecondPrecision
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|startUnit
argument_list|,
name|startPrecision
argument_list|,
name|endUnit
argument_list|,
name|fractionalSecondPrecision
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|nullable
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|nullable
argument_list|(
name|nullable
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|add
parameter_list|(
name|RelDataTypeField
name|field
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|add
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|addAll
parameter_list|(
name|Iterable
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
name|fields
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|addAll
argument_list|(
name|fields
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|kind
parameter_list|(
name|StructKind
name|kind
parameter_list|)
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|kind
argument_list|(
name|kind
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FieldInfoBuilder
name|uniquify
parameter_list|()
block|{
return|return
operator|(
name|FieldInfoBuilder
operator|)
name|super
operator|.
name|uniquify
argument_list|()
return|;
block|}
block|}
comment|/** Fluid API to build a list of fields. */
class|class
name|Builder
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|StructKind
name|kind
init|=
name|StructKind
operator|.
name|FULLY_QUALIFIED
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
name|boolean
name|nullableRecord
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a Builder with the given type factory.      */
specifier|public
name|Builder
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the number of fields.      *      * @return number of fields      */
specifier|public
name|int
name|getFieldCount
parameter_list|()
block|{
return|return
name|names
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Returns the name of a given field.      *      * @param index Ordinal of field      * @return Name of given field      */
specifier|public
name|String
name|getFieldName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|names
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Returns the type of a given field.      *      * @param index Ordinal of field      * @return Type of given field      */
specifier|public
name|RelDataType
name|getFieldType
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
return|;
block|}
comment|/**      * Adds a field with given name and type.      */
specifier|public
name|Builder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field with a type created using      * {@link org.apache.calcite.rel.type.RelDataTypeFactory#createSqlType(org.apache.calcite.sql.type.SqlTypeName)}.      */
specifier|public
name|Builder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field with a type created using      * {@link org.apache.calcite.rel.type.RelDataTypeFactory#createSqlType(org.apache.calcite.sql.type.SqlTypeName, int)}.      */
specifier|public
name|Builder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|add
argument_list|(
name|name
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field with a type created using      * {@link org.apache.calcite.rel.type.RelDataTypeFactory#createSqlType(org.apache.calcite.sql.type.SqlTypeName, int, int)}.      */
specifier|public
name|Builder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
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
name|add
argument_list|(
name|name
argument_list|,
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
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field with an interval type.      */
specifier|public
name|Builder
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|TimeUnit
name|startUnit
parameter_list|,
name|int
name|startPrecision
parameter_list|,
name|TimeUnit
name|endUnit
parameter_list|,
name|int
name|fractionalSecondPrecision
parameter_list|)
block|{
specifier|final
name|SqlIntervalQualifier
name|q
init|=
operator|new
name|SqlIntervalQualifier
argument_list|(
name|startUnit
argument_list|,
name|startPrecision
argument_list|,
name|endUnit
argument_list|,
name|fractionalSecondPrecision
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|name
argument_list|,
name|typeFactory
operator|.
name|createSqlIntervalType
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Changes the nullability of the last field added.      *      * @throws java.lang.IndexOutOfBoundsException if no fields have been      *                                             added      */
specifier|public
name|Builder
name|nullable
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
name|RelDataType
name|lastType
init|=
name|types
operator|.
name|get
argument_list|(
name|types
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastType
operator|.
name|isNullable
argument_list|()
operator|!=
name|nullable
condition|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|lastType
argument_list|,
name|nullable
argument_list|)
decl_stmt|;
name|types
operator|.
name|set
argument_list|(
name|types
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds a field. Field's ordinal is ignored.      */
specifier|public
name|Builder
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
comment|/**      * Adds all fields in a collection.      */
specifier|public
name|Builder
name|addAll
parameter_list|(
name|Iterable
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
name|fields
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|field
range|:
name|fields
control|)
block|{
name|add
argument_list|(
name|field
operator|.
name|getKey
argument_list|()
argument_list|,
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|Builder
name|kind
parameter_list|(
name|StructKind
name|kind
parameter_list|)
block|{
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Sets whether the record type will be nullable. */
specifier|public
name|Builder
name|nullableRecord
parameter_list|(
name|boolean
name|nullableRecord
parameter_list|)
block|{
name|this
operator|.
name|nullableRecord
operator|=
name|nullableRecord
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Makes sure that field names are unique.      */
specifier|public
name|Builder
name|uniquify
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|uniqueNames
init|=
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|names
argument_list|,
name|typeFactory
operator|.
name|getTypeSystem
argument_list|()
operator|.
name|isSchemaCaseSensitive
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniqueNames
operator|!=
name|names
condition|)
block|{
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
name|names
operator|.
name|addAll
argument_list|(
name|uniqueNames
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Creates a struct type with the current contents of this builder.      */
specifier|public
name|RelDataType
name|build
parameter_list|()
block|{
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|kind
argument_list|,
name|types
argument_list|,
name|names
argument_list|)
argument_list|,
name|nullableRecord
argument_list|)
return|;
block|}
comment|/** Creates a dynamic struct type with the current contents of this      * builder. */
specifier|public
name|RelDataType
name|buildDynamic
parameter_list|()
block|{
specifier|final
name|RelDataType
name|dynamicType
init|=
operator|new
name|DynamicRecordTypeImpl
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|build
argument_list|()
decl_stmt|;
name|dynamicType
operator|.
name|getFieldList
argument_list|()
operator|.
name|addAll
argument_list|(
name|type
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dynamicType
return|;
block|}
comment|/** Returns whether a field exists with the given name. */
specifier|public
name|boolean
name|nameExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

