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
name|util
operator|.
name|SerializableCharset
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
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * BasicSqlType represents a standard atomic SQL type (excluding interval  * types).  *  *<p>Instances of this class are immutable.  */
end_comment

begin_class
specifier|public
class|class
name|BasicSqlType
extends|extends
name|AbstractSqlType
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
specifier|private
specifier|final
name|int
name|scale
decl_stmt|;
specifier|private
specifier|final
name|RelDataTypeSystem
name|typeSystem
decl_stmt|;
specifier|private
specifier|final
name|SqlCollation
name|collation
decl_stmt|;
specifier|private
specifier|final
name|SerializableCharset
name|wrappedCharset
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a type with no parameters. This should only be called from a    * factory method.    *    * @param typeSystem Type system    * @param typeName Type name    */
specifier|public
name|BasicSqlType
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|this
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|,
literal|false
argument_list|,
name|PRECISION_NOT_SPECIFIED
argument_list|,
name|SCALE_NOT_SPECIFIED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPrecScale
argument_list|(
name|typeName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Throws if {@code typeName} does not allow the given combination of    * precision and scale. */
specifier|protected
specifier|static
name|void
name|checkPrecScale
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|precisionSpecified
parameter_list|,
name|boolean
name|scaleSpecified
parameter_list|)
block|{
if|if
condition|(
operator|!
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
name|precisionSpecified
argument_list|,
name|scaleSpecified
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"typeName.allowsPrecScale("
operator|+
name|precisionSpecified
operator|+
literal|", "
operator|+
name|scaleSpecified
operator|+
literal|"): "
operator|+
name|typeName
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructs a type with precision/length but no scale.    *    * @param typeSystem Type system    * @param typeName Type name    * @param precision Precision (called length for some types)    */
specifier|public
name|BasicSqlType
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|this
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|,
literal|false
argument_list|,
name|precision
argument_list|,
name|SCALE_NOT_SPECIFIED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPrecScale
argument_list|(
name|typeName
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a type with precision/length and scale.    *    * @param typeSystem Type system    * @param typeName Type name    * @param precision Precision (called length for some types)    * @param scale Scale    */
specifier|public
name|BasicSqlType
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
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
name|this
argument_list|(
name|typeSystem
argument_list|,
name|typeName
argument_list|,
literal|false
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkPrecScale
argument_list|(
name|typeName
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Internal constructor. */
specifier|private
name|BasicSqlType
parameter_list|(
name|RelDataTypeSystem
name|typeSystem
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|boolean
name|nullable
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|SqlCollation
name|collation
parameter_list|,
name|SerializableCharset
name|wrappedCharset
parameter_list|)
block|{
name|super
argument_list|(
name|typeName
argument_list|,
name|nullable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeSystem
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeSystem
argument_list|)
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
name|collation
operator|=
name|collation
expr_stmt|;
name|this
operator|.
name|wrappedCharset
operator|=
name|wrappedCharset
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Constructs a type with nullablity.    */
name|BasicSqlType
name|createWithNullability
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
if|if
condition|(
name|nullable
operator|==
name|this
operator|.
name|isNullable
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|BasicSqlType
argument_list|(
name|this
operator|.
name|typeSystem
argument_list|,
name|this
operator|.
name|typeName
argument_list|,
name|nullable
argument_list|,
name|this
operator|.
name|precision
argument_list|,
name|this
operator|.
name|scale
argument_list|,
name|this
operator|.
name|collation
argument_list|,
name|this
operator|.
name|wrappedCharset
argument_list|)
return|;
block|}
comment|/**    * Constructs a type with charset and collation.    *    *<p>This must be a character type.    */
name|BasicSqlType
name|createWithCharsetAndCollation
parameter_list|(
name|Charset
name|charset
parameter_list|,
name|SqlCollation
name|collation
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|BasicSqlType
argument_list|(
name|this
operator|.
name|typeSystem
argument_list|,
name|this
operator|.
name|typeName
argument_list|,
name|this
operator|.
name|isNullable
argument_list|,
name|this
operator|.
name|precision
argument_list|,
name|this
operator|.
name|scale
argument_list|,
name|collation
argument_list|,
name|SerializableCharset
operator|.
name|forCharset
argument_list|(
name|charset
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
if|if
condition|(
name|precision
operator|==
name|PRECISION_NOT_SPECIFIED
condition|)
block|{
return|return
name|typeSystem
operator|.
name|getDefaultPrecision
argument_list|(
name|typeName
argument_list|)
return|;
block|}
return|return
name|precision
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getScale
parameter_list|()
block|{
if|if
condition|(
name|scale
operator|==
name|SCALE_NOT_SPECIFIED
condition|)
block|{
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|TINYINT
case|:
case|case
name|SMALLINT
case|:
case|case
name|INTEGER
case|:
case|case
name|BIGINT
case|:
case|case
name|DECIMAL
case|:
return|return
literal|0
return|;
default|default:
comment|// fall through
block|}
block|}
return|return
name|scale
return|;
block|}
annotation|@
name|Override
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|wrappedCharset
operator|==
literal|null
condition|?
literal|null
else|:
name|wrappedCharset
operator|.
name|getCharset
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
comment|// implement RelDataTypeImpl
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
comment|// Called to make the digest, which equals() compares;
comment|// so equivalent data types must produce identical type strings.
name|sb
operator|.
name|append
argument_list|(
name|typeName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|printPrecision
init|=
name|precision
operator|!=
name|PRECISION_NOT_SPECIFIED
decl_stmt|;
name|boolean
name|printScale
init|=
name|scale
operator|!=
name|SCALE_NOT_SPECIFIED
decl_stmt|;
if|if
condition|(
name|printPrecision
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|printScale
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|withDetail
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|wrappedCharset
operator|!=
literal|null
operator|&&
operator|!
name|SqlCollation
operator|.
name|IMPLICIT
operator|.
name|getCharset
argument_list|()
operator|.
name|equals
argument_list|(
name|wrappedCharset
operator|.
name|getCharset
argument_list|()
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" CHARACTER SET \""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|wrappedCharset
operator|.
name|getCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collation
operator|!=
literal|null
operator|&&
name|collation
operator|!=
name|SqlCollation
operator|.
name|IMPLICIT
operator|&&
name|collation
operator|!=
name|SqlCollation
operator|.
name|COERCIBLE
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" COLLATE \""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|collation
operator|.
name|getCollationName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a value which is a limit for this type.    *    *<p>For example,    *    *<table border="1">    *<caption>Limits</caption>    *<tr>    *<th>Datatype</th>    *<th>sign</th>    *<th>limit</th>    *<th>beyond</th>    *<th>precision</th>    *<th>scale</th>    *<th>Returns</th>    *</tr>    *<tr>    *<td>Integer</td>    *<td>true</td>    *<td>true</td>    *<td>false</td>    *<td>-1</td>    *<td>-1</td>    *<td>2147483647 (2 ^ 31 -1 = MAXINT)</td>    *</tr>    *<tr>    *<td>Integer</td>    *<td>true</td>    *<td>true</td>    *<td>true</td>    *<td>-1</td>    *<td>-1</td>    *<td>2147483648 (2 ^ 31 = MAXINT + 1)</td>    *</tr>    *<tr>    *<td>Integer</td>    *<td>false</td>    *<td>true</td>    *<td>false</td>    *<td>-1</td>    *<td>-1</td>    *<td>-2147483648 (-2 ^ 31 = MININT)</td>    *</tr>    *<tr>    *<td>Boolean</td>    *<td>true</td>    *<td>true</td>    *<td>false</td>    *<td>-1</td>    *<td>-1</td>    *<td>TRUE</td>    *</tr>    *<tr>    *<td>Varchar</td>    *<td>true</td>    *<td>true</td>    *<td>false</td>    *<td>10</td>    *<td>-1</td>    *<td>'ZZZZZZZZZZ'</td>    *</tr>    *</table>    *    * @param sign   If true, returns upper limit, otherwise lower limit    * @param limit  If true, returns value at or near to overflow; otherwise    *               value at or near to underflow    * @param beyond If true, returns the value just beyond the limit, otherwise    *               the value at the limit    * @return Limit value    */
specifier|public
name|Object
name|getLimit
parameter_list|(
name|boolean
name|sign
parameter_list|,
name|SqlTypeName
operator|.
name|Limit
name|limit
parameter_list|,
name|boolean
name|beyond
parameter_list|)
block|{
name|int
name|precision
init|=
name|typeName
operator|.
name|allowsPrec
argument_list|()
condition|?
name|this
operator|.
name|getPrecision
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
name|int
name|scale
init|=
name|typeName
operator|.
name|allowsScale
argument_list|()
condition|?
name|this
operator|.
name|getScale
argument_list|()
else|:
operator|-
literal|1
decl_stmt|;
return|return
name|typeName
operator|.
name|getLimit
argument_list|(
name|sign
argument_list|,
name|limit
argument_list|,
name|beyond
argument_list|,
name|precision
argument_list|,
name|scale
argument_list|)
return|;
block|}
block|}
end_class

end_unit

