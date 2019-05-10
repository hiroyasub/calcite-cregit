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
name|type
operator|.
name|SqlTypeUtil
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Represents a SQL data type specification in a parse tree.  *  *<p>A<code>SqlDataTypeSpec</code> is immutable; once created, you cannot  * change any of the fields.</p>  *  *<p>todo: This should really be a subtype of {@link SqlCall}.</p>  *  *<p>In its full glory, we will have to support complex type expressions  * like:</p>  *  *<blockquote><code>ROW(<br>  *   NUMBER(5, 2) NOT NULL AS foo,<br>  *   ROW(BOOLEAN AS b, MyUDT NOT NULL AS i) AS rec)</code></blockquote>  *  *<p>Currently it only supports simple datatypes like CHAR, VARCHAR and DOUBLE,  * with optional precision and scale.</p>  */
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
name|SqlIdentifier
name|collectionsTypeName
decl_stmt|;
specifier|private
specifier|final
name|SqlIdentifier
name|typeName
decl_stmt|;
specifier|private
specifier|final
name|SqlIdentifier
name|baseTypeName
decl_stmt|;
specifier|private
specifier|final
name|int
name|scale
decl_stmt|;
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
specifier|private
specifier|final
name|String
name|charSetName
decl_stmt|;
specifier|private
specifier|final
name|TimeZone
name|timeZone
decl_stmt|;
comment|/** Whether data type is allows nulls.    *    *<p>Nullable is nullable! Null means "not specified". E.g.    * {@code CAST(x AS INTEGER)} preserves has the same nullability as {@code x}.    */
specifier|private
name|Boolean
name|nullable
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a type specification representing a regular, non-collection type.    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
specifier|final
name|SqlIdentifier
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|String
name|charSetName
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
literal|null
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
argument_list|,
name|timeZone
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a type specification representing a collection type.    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
name|SqlIdentifier
name|collectionsTypeName
parameter_list|,
name|SqlIdentifier
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|String
name|charSetName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|collectionsTypeName
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a type specification that has no base type.    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
name|SqlIdentifier
name|collectionsTypeName
parameter_list|,
name|SqlIdentifier
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|String
name|charSetName
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
name|this
argument_list|(
name|collectionsTypeName
argument_list|,
name|typeName
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
argument_list|,
name|timeZone
argument_list|,
name|nullable
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a type specification.    */
specifier|public
name|SqlDataTypeSpec
parameter_list|(
name|SqlIdentifier
name|collectionsTypeName
parameter_list|,
name|SqlIdentifier
name|typeName
parameter_list|,
name|SqlIdentifier
name|baseTypeName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|String
name|charSetName
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
name|collectionsTypeName
operator|=
name|collectionsTypeName
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|baseTypeName
operator|=
name|baseTypeName
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|scale
operator|=
name|scale
expr_stmt|;
name|this
operator|.
name|charSetName
operator|=
name|charSetName
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
operator|(
name|collectionsTypeName
operator|!=
literal|null
operator|)
condition|?
operator|new
name|SqlDataTypeSpec
argument_list|(
name|collectionsTypeName
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
argument_list|,
name|pos
argument_list|)
else|:
operator|new
name|SqlDataTypeSpec
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
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
return|return
name|collectionsTypeName
return|;
block|}
specifier|public
name|SqlIdentifier
name|getTypeName
parameter_list|()
block|{
return|return
name|typeName
return|;
block|}
specifier|public
name|int
name|getScale
parameter_list|()
block|{
return|return
name|scale
return|;
block|}
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|precision
return|;
block|}
specifier|public
name|String
name|getCharSetName
parameter_list|()
block|{
return|return
name|charSetName
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
name|collectionsTypeName
argument_list|,
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
argument_list|,
name|timeZone
argument_list|,
name|nullable
argument_list|,
name|getParserPosition
argument_list|()
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
name|getCollectionsTypeName
argument_list|()
operator|!=
literal|null
assert|;
return|return
operator|new
name|SqlDataTypeSpec
argument_list|(
name|typeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
name|charSetName
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
name|String
name|name
init|=
name|typeName
operator|.
name|getSimple
argument_list|()
decl_stmt|;
if|if
condition|(
name|SqlTypeName
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|SqlTypeName
name|sqlTypeName
init|=
name|SqlTypeName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// we have a built-in data type
name|writer
operator|.
name|keyword
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|sqlTypeName
operator|.
name|allowsPrec
argument_list|()
operator|&&
operator|(
name|precision
operator|>=
literal|0
operator|)
condition|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|FUN_CALL
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|precision
argument_list|)
expr_stmt|;
if|if
condition|(
name|sqlTypeName
operator|.
name|allowsScale
argument_list|()
operator|&&
operator|(
name|scale
operator|>=
literal|0
operator|)
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|scale
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|charSetName
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"CHARACTER SET"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|identifier
argument_list|(
name|charSetName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collectionsTypeName
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
name|collectionsTypeName
operator|.
name|getSimple
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// We're generating a type for an alien system. For example,
comment|// UNSIGNED is a built-in type in MySQL.
comment|// (Need a more elegant way than '_' of flagging this.)
name|writer
operator|.
name|keyword
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// else we have a user defined type
name|typeName
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
name|SqlNode
operator|.
name|equalDeep
argument_list|(
name|this
operator|.
name|collectionsTypeName
argument_list|,
name|that
operator|.
name|collectionsTypeName
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
if|if
condition|(
operator|!
name|this
operator|.
name|typeName
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|typeName
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
if|if
condition|(
name|this
operator|.
name|precision
operator|!=
name|that
operator|.
name|precision
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
name|this
operator|.
name|scale
operator|!=
name|that
operator|.
name|scale
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
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|charSetName
argument_list|,
name|that
operator|.
name|charSetName
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
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
comment|/**    * Throws an error if the type is not found.    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|)
block|{
name|RelDataType
name|type
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|typeName
operator|.
name|isSimple
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|collectionsTypeName
condition|)
block|{
specifier|final
name|String
name|collectionName
init|=
name|collectionsTypeName
operator|.
name|getSimple
argument_list|()
decl_stmt|;
if|if
condition|(
name|SqlTypeName
operator|.
name|get
argument_list|(
name|collectionName
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|this
argument_list|,
name|RESOURCE
operator|.
name|unknownDatatypeName
argument_list|(
name|collectionName
argument_list|)
argument_list|)
throw|;
block|}
block|}
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
name|deriveType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|typeName
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
comment|/**    * Does not throw an error if the type is not built-in.    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|deriveType
argument_list|(
name|typeFactory
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Converts this type specification to a {@link RelDataType}.    *    *<p>Does not throw an error if the type is not built-in.    *    * @param nullable Whether the type is nullable if the type specification    *                 does not explicitly state    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
if|if
condition|(
operator|!
name|typeName
operator|.
name|isSimple
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|name
init|=
name|typeName
operator|.
name|getSimple
argument_list|()
decl_stmt|;
specifier|final
name|SqlTypeName
name|sqlTypeName
init|=
name|SqlTypeName
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sqlTypeName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// NOTE jvs 15-Jan-2009:  earlier validation is supposed to
comment|// have caught these, which is why it's OK for them
comment|// to be assertions rather than user-level exceptions.
name|RelDataType
name|type
decl_stmt|;
if|if
condition|(
operator|(
name|precision
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|scale
operator|>=
literal|0
operator|)
condition|)
block|{
assert|assert
name|sqlTypeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
assert|;
name|type
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|precision
operator|>=
literal|0
condition|)
block|{
assert|assert
name|sqlTypeName
operator|.
name|allowsPrecNoScale
argument_list|()
assert|;
name|type
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|sqlTypeName
operator|.
name|allowsNoPrecNoScale
argument_list|()
assert|;
name|type
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// Applying Syntax rule 10 from SQL:99 spec section 6.22 "If TD is a
comment|// fixed-length, variable-length or large object character string,
comment|// then the collating sequence of the result of the<cast
comment|// specification> is the default collating sequence for the
comment|// character repertoire of TD and the result of the<cast
comment|// specification> has the Coercible coercibility characteristic."
name|SqlCollation
name|collation
init|=
name|SqlCollation
operator|.
name|COERCIBLE
decl_stmt|;
name|Charset
name|charset
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|charSetName
condition|)
block|{
name|charset
operator|=
name|typeFactory
operator|.
name|getDefaultCharset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|javaCharSetName
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|SqlUtil
operator|.
name|translateCharacterSetName
argument_list|(
name|charSetName
argument_list|)
argument_list|,
name|charSetName
argument_list|)
decl_stmt|;
name|charset
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|javaCharSetName
argument_list|)
expr_stmt|;
block|}
name|type
operator|=
name|typeFactory
operator|.
name|createTypeWithCharsetAndCollation
argument_list|(
name|type
argument_list|,
name|charset
argument_list|,
name|collation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|collectionsTypeName
condition|)
block|{
specifier|final
name|String
name|collectionName
init|=
name|collectionsTypeName
operator|.
name|getSimple
argument_list|()
decl_stmt|;
specifier|final
name|SqlTypeName
name|collectionsSqlTypeName
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|SqlTypeName
operator|.
name|get
argument_list|(
name|collectionName
argument_list|)
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|collectionsSqlTypeName
condition|)
block|{
case|case
name|MULTISET
case|:
name|type
operator|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|collectionsSqlTypeName
argument_list|)
throw|;
block|}
block|}
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
name|type
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
name|nullable
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlDataTypeSpec.java
end_comment

end_unit

