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
name|enumerable
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|ParameterExpression
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
name|RelCollation
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
name|RelFieldCollation
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
name|util
operator|.
name|Pair
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Physical type of a row.  *  *<p>Consists of the SQL row type (returned by {@link #getRowType()}), the Java  * type of the row (returned by {@link #getJavaRowType()}), and methods to  * generate expressions to access fields, generate records, and so forth.  * Together, the records encapsulate how the logical type maps onto the physical  * type.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|PhysType
block|{
comment|/** Returns the Java type (often a Class) that represents a row. For    * example, in one row format, always returns {@code Object[].class}. */
name|Type
name|getJavaRowType
parameter_list|()
function_decl|;
comment|/**    * Returns the Java class that is used to store the field with the given    * ordinal.    *    *<p>For instance, when the java row type is {@code Object[]}, the java    * field type is {@code Object} even if the field is not nullable.</p> */
name|Type
name|getJavaFieldType
parameter_list|(
name|int
name|field
parameter_list|)
function_decl|;
comment|/** Returns the physical type of a field. */
name|PhysType
name|field
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/** Returns the physical type of a given field's component type. */
name|PhysType
name|component
parameter_list|(
name|int
name|field
parameter_list|)
function_decl|;
comment|/** Returns the SQL row type. */
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/** Returns the Java class of the field with the given ordinal. */
name|Class
name|fieldClass
parameter_list|(
name|int
name|field
parameter_list|)
function_decl|;
comment|/** Returns whether a given field allows null values. */
name|boolean
name|fieldNullable
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/** Generates a reference to a given field in an expression.    *    *<p>For example given {@code expression=employee} and {@code field=2},    * generates</p>    *    *<pre>{@code employee.deptno}</pre>    *    * @param expression Expression    * @param field Ordinal of field    * @return Expression to access the field of the expression    */
name|Expression
name|fieldReference
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|)
function_decl|;
comment|/** Generates a reference to a given field in an expression.    *    *<p>This method optimizes for the target storage type (i.e. avoids    * casts).</p>    *    *<p>For example given {@code expression=employee} and {@code field=2},    * generates</p>    *    *<pre>{@code employee.deptno}</pre>    *    * @param expression Expression    * @param field Ordinal of field    * @param storageType optional hint for storage class    * @return Expression to access the field of the expression    */
name|Expression
name|fieldReference
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|,
name|Type
name|storageType
parameter_list|)
function_decl|;
comment|/** Generates an accessor function for a given list of fields.  The resulting    * object is a {@link List} (implementing {@link Object#hashCode()} and    * {@link Object#equals(Object)} per that interface) and also implements    * {@link Comparable}.    *    *<p>For example:</p>    *    *<pre>{@code    * new Function1<Employee, Object[]> {    *    public Object[] apply(Employee v1) {    *        return FlatLists.of(v1.<fieldN>, v1.<fieldM>);    *    }    * }    * }</pre>    */
name|Expression
name|generateAccessor
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
function_decl|;
comment|/** Generates a selector for the given fields from an expression, with the    * default row format. */
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
function_decl|;
comment|/** Generates a lambda expression that is a selector for the given fields from    * an expression. */
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|JavaRowFormat
name|targetFormat
parameter_list|)
function_decl|;
comment|/** Generates a lambda expression that is a selector for the given fields from    * an expression.    *    *<p>{@code usedFields} must be a subset of {@code fields}.    * For each field, there is a corresponding indicator field.    * If a field is used, its value is assigned and its indicator is left    * {@code false}.    * If a field is not used, its value is not assigned and its indicator is    * set to {@code true};    * This will become a value of 1 when {@code GROUPING(field)} is called. */
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|usedFields
parameter_list|,
name|JavaRowFormat
name|targetFormat
parameter_list|)
function_decl|;
comment|/** Generates a selector for the given fields from an expression. */
name|Expression
name|selector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|,
name|JavaRowFormat
name|targetFormat
parameter_list|)
function_decl|;
comment|/** Projects a given collection of fields from this input record, into    * a particular preferred output format. The output format is optimized    * if there are 0 or 1 fields. */
name|PhysType
name|project
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
function_decl|;
comment|/** Projects a given collection of fields from this input record, optionally    * with indicator fields, into a particular preferred output format.    *    *<p>The output format is optimized if there are 0 or 1 fields    * and indicators are disabled. */
name|PhysType
name|project
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
function_decl|;
comment|/** Returns a lambda to create a collation key and a comparator. The    * comparator is sometimes null. */
name|Pair
argument_list|<
name|Expression
argument_list|,
name|Expression
argument_list|>
name|generateCollationKey
parameter_list|(
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|collations
parameter_list|)
function_decl|;
comment|/** Returns a comparator. Unlike the comparator returned by    * {@link #generateCollationKey(java.util.List)}, this comparator acts on the    * whole element. */
name|Expression
name|generateComparator
parameter_list|(
name|RelCollation
name|collation
parameter_list|)
function_decl|;
comment|/** Returns a expression that yields a comparer, or null if this type    * is comparable. */
name|Expression
name|comparer
parameter_list|()
function_decl|;
comment|/** Generates an expression that creates a record for a row, initializing    * its fields with the given expressions. There must be one expression per    * field.    *    * @param expressions Expression to initialize each field    * @return Expression to create a row    */
name|Expression
name|record
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
function_decl|;
comment|/** Returns the format. */
name|JavaRowFormat
name|getFormat
parameter_list|()
function_decl|;
name|List
argument_list|<
name|Expression
argument_list|>
name|accessors
parameter_list|(
name|Expression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|)
function_decl|;
comment|/** Returns a copy of this type that allows nulls if {@code nullable} is    * true. */
name|PhysType
name|makeNullable
parameter_list|(
name|boolean
name|nullable
parameter_list|)
function_decl|;
comment|/** Converts an enumerable of this physical type to an enumerable that uses a    * given physical type for its rows. */
name|Expression
name|convertTo
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|PhysType
name|targetPhysType
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End PhysType.java
end_comment

end_unit

