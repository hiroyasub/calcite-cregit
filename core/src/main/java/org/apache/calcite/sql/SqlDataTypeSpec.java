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
name|util
operator|.
name|SqlVisitor
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
name|SqlMonotonicity
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
name|SqlValidator
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
name|SqlValidatorScope
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
name|Litmus
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
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Represents a SQL data type specification in a parse tree.  *  *<p>A<code>SqlDataTypeSpec</code> is immutable; once created, you cannot  * change any of the fields.</p>  *  *<p>We support the following data type expressions:  *  *<ul>  *<li>Complex data type expression like:  *<blockquote><code>ROW(<br>  *     foo NUMBER(5, 2) NOT NULL,<br>  *       rec ROW(b BOOLEAN, i MyUDT NOT NULL))</code></blockquote>  *   Internally we use {@link SqlRowTypeNameSpec} to specify row data type name.  *</li>  *<li>Simple data type expression like CHAR, VARCHAR and DOUBLE  *   with optional precision and scale;  *   Internally we use {@link SqlBasicTypeNameSpec} to specify basic sql data type name.  *</li>  *<li>Collection data type expression like:  *<blockquote><code>  *     INT ARRAY;  *     VARCHAR(20) MULTISET;  *     INT ARRAY MULTISET;</code></blockquote>  *   Internally we use {@link SqlCollectionTypeNameSpec} to specify collection data type name.  *</li>  *<li>User defined data type expression like `My_UDT`;  *   Internally we use {@link SqlUserDefinedTypeNameSpec} to specify user defined data type name.  *</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|SqlDataTypeSpec
extends|extends
name|SqlNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlTypeNameSpec
name|typeNameSpec
decl_stmt|;
specifier|private
specifier|final
name|TimeZone
name|timeZone
decl_stmt|;
comment|/** Whether data type allows nulls.    *    *<p>Nullable is nullable! Null means "not specified". E.g.    * {@code CAST(x AS INTEGER)} preserves the same nullability as {@code x}.    */
specifier|private
specifier|final
name|Boolean
name|nullable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a type specification representing a type.    *    * @param typeNameSpec The type name can be basic sql type, row type,    *                     collections type and user defined type    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
specifier|final
name|SqlTypeNameSpec
name|typeNameSpec
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|typeNameSpec
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a type specification representing a type, with time zone specified.    *    * @param typeNameSpec The type name can be basic sql type, row type,    *                     collections type and user defined type    * @param timeZone     Specified time zone    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
specifier|final
name|SqlTypeNameSpec
name|typeNameSpec
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|typeNameSpec
argument_list|,
name|timeZone
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a type specification representing a type, with time zone,    * nullability and base type name specified.    *    * @param typeNameSpec The type name can be basic sql type, row type,    *                     collections type and user defined type    * @param timeZone     Specified time zone    * @param nullable     The nullability    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
name|SqlTypeNameSpec
name|typeNameSpec
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|,
name|Boolean
name|nullable
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeNameSpec
operator|=
name|typeNameSpec
expr_stmt|;
name|this
operator|.
name|timeZone
operator|=
name|timeZone
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|typeNameSpec
argument_list|,
name|timeZone
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|CONSTANT
return|;
block|}
specifier|public
name|SqlIdentifier
name|getCollectionsTypeName
parameter_list|()
block|{
if|if
condition|(
name|typeNameSpec
operator|instanceof
name|SqlCollectionTypeNameSpec
condition|)
block|{
return|return
name|typeNameSpec
operator|.
name|getTypeName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|SqlIdentifier
name|getTypeName
parameter_list|()
block|{
return|return
name|typeNameSpec
operator|.
name|getTypeName
argument_list|()
return|;
block|}
specifier|public
name|SqlTypeNameSpec
name|getTypeNameSpec
parameter_list|()
block|{
return|return
name|typeNameSpec
return|;
block|}
specifier|public
name|TimeZone
name|getTimeZone
parameter_list|()
block|{
return|return
name|timeZone
return|;
block|}
specifier|public
name|Boolean
name|getNullable
parameter_list|()
block|{
return|return
name|nullable
return|;
block|}
comment|/** Returns a copy of this data type specification with a given    * nullability. */
specifier|public
name|SqlDataTypeSpec
name|withNullable
parameter_list|(
name|Boolean
name|nullable
parameter_list|)
block|{
return|return
name|withNullable
argument_list|(
name|nullable
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|/** Returns a copy of this data type specification with a given    * nullability, extending the parser position. */
specifier|public
name|SqlDataTypeSpec
name|withNullable
parameter_list|(
name|Boolean
name|nullable
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
specifier|final
name|SqlParserPos
name|newPos
init|=
name|pos
operator|==
name|SqlParserPos
operator|.
name|ZERO
condition|?
name|this
operator|.
name|pos
else|:
name|this
operator|.
name|pos
operator|.
name|plus
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|nullable
argument_list|,
name|this
operator|.
name|nullable
argument_list|)
operator|&&
name|newPos
operator|.
name|equals
argument_list|(
name|this
operator|.
name|pos
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|typeNameSpec
argument_list|,
name|timeZone
argument_list|,
name|nullable
argument_list|,
name|newPos
argument_list|)
return|;
block|}
comment|/**    * Returns a new SqlDataTypeSpec corresponding to the component type if the    * type spec is a collections type spec.<br>    * Collection types are<code>ARRAY</code> and<code>MULTISET</code>.    */
specifier|public
name|SqlDataTypeSpec
name|getComponentTypeSpec
parameter_list|()
block|{
assert|assert
name|typeNameSpec
operator|instanceof
name|SqlCollectionTypeNameSpec
assert|;
name|SqlTypeNameSpec
name|elementTypeName
init|=
operator|(
operator|(
name|SqlCollectionTypeNameSpec
operator|)
name|typeNameSpec
operator|)
operator|.
name|getElementTypeName
argument_list|()
decl_stmt|;
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|elementTypeName
argument_list|,
name|timeZone
argument_list|,
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|typeNameSpec
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateDataType
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlDataTypeSpec
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
name|SqlDataTypeSpec
name|that
init|=
operator|(
name|SqlDataTypeSpec
operator|)
name|node
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|timeZone
argument_list|,
name|that
operator|.
name|timeZone
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|typeNameSpec
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|typeNameSpec
argument_list|,
name|litmus
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|null
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
comment|/**    * Converts this type specification to a {@link RelDataType}.    *    *<p>Throws an error if the type is not found.    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|)
block|{
return|return
name|deriveType
argument_list|(
name|validator
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Converts this type specification to a {@link RelDataType}.    *    *<p>Throws an error if the type is not found.    *    * @param nullable Whether the type is nullable if the type specification    *                 does not explicitly state    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|RelDataType
name|type
decl_stmt|;
name|type
operator|=
name|typeNameSpec
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|)
expr_stmt|;
comment|// Fix-up the nullability, default is false.
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|type
operator|=
name|fixUpNullability
argument_list|(
name|typeFactory
argument_list|,
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
comment|//~ Tools ------------------------------------------------------------------
comment|/**    * Fix up the nullability of the {@code type}.    *    * @param typeFactory Type factory    * @param type        The type to coerce nullability    * @param nullable    Default nullability to use if this type specification does not    *                    specify nullability    * @return Type with specified nullability or the default(false)    */
specifier|private
name|RelDataType
name|fixUpNullability
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|nullable
operator|!=
literal|null
condition|)
block|{
name|nullable
operator|=
name|this
operator|.
name|nullable
expr_stmt|;
block|}
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
block|}
end_class

end_unit

