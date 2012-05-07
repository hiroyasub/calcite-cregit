begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * BasicSqlType represents a standard atomic SQL type (excluding interval  * types).  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|BasicSqlType
extends|extends
name|AbstractSqlType
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|int
name|SCALE_NOT_SPECIFIED
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_NOT_SPECIFIED
init|=
operator|-
literal|1
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|int
name|precision
decl_stmt|;
specifier|private
name|int
name|scale
decl_stmt|;
specifier|private
name|SqlCollation
name|collation
decl_stmt|;
specifier|private
name|SerializableCharset
name|wrappedCharset
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs a type with no parameters. This should only be called from a      * factory method.      *      * @param typeName Type name      *      * @pre typeName.allowsNoPrecNoScale(false,false)      */
specifier|public
name|BasicSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
block|{
name|super
argument_list|(
name|typeName
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"typeName.allowsPrecScale(false,false), typeName="
operator|+
name|typeName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|PRECISION_NOT_SPECIFIED
expr_stmt|;
name|this
operator|.
name|scale
operator|=
name|SCALE_NOT_SPECIFIED
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a type with precision/length but no scale.      *      * @param typeName Type name      *      * @pre typeName.allowsPrecNoScale(true,false)      */
specifier|public
name|BasicSqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|super
argument_list|(
name|typeName
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|"typeName.allowsPrecScale(true,false)"
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
name|SCALE_NOT_SPECIFIED
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a type with precision/length and scale.      *      * @param typeName Type name      *      * @pre typeName.allowsPrecScale(true,true)      */
specifier|public
name|BasicSqlType
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
name|super
argument_list|(
name|typeName
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|typeName
operator|.
name|allowsPrecScale
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|"typeName.allowsPrecScale(true,true)"
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
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Constructs a type with nullablity      */
name|BasicSqlType
name|createWithNullability
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
name|BasicSqlType
name|ret
decl_stmt|;
try|try
block|{
name|ret
operator|=
operator|(
name|BasicSqlType
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ret
operator|.
name|isNullable
operator|=
name|nullable
expr_stmt|;
name|ret
operator|.
name|computeDigest
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**      * Constructs a type with charset and collation      *      * @pre SqlTypeUtil.inCharFamily(this)      */
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
name|Util
operator|.
name|pre
argument_list|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|this
argument_list|)
argument_list|,
literal|"Not an chartype"
argument_list|)
expr_stmt|;
name|BasicSqlType
name|ret
decl_stmt|;
try|try
block|{
name|ret
operator|=
operator|(
name|BasicSqlType
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ret
operator|.
name|wrappedCharset
operator|=
name|SerializableCharset
operator|.
name|forCharset
argument_list|(
name|charset
argument_list|)
expr_stmt|;
name|ret
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
name|ret
operator|.
name|computeDigest
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|//implement RelDataType
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
switch|switch
condition|(
name|typeName
condition|)
block|{
case|case
name|BOOLEAN
case|:
return|return
literal|1
return|;
case|case
name|TINYINT
case|:
return|return
literal|3
return|;
case|case
name|SMALLINT
case|:
return|return
literal|5
return|;
case|case
name|INTEGER
case|:
return|return
literal|10
return|;
case|case
name|BIGINT
case|:
return|return
literal|19
return|;
case|case
name|DECIMAL
case|:
return|return
name|SqlTypeName
operator|.
name|MAX_NUMERIC_PRECISION
return|;
case|case
name|REAL
case|:
return|return
literal|7
return|;
case|case
name|FLOAT
case|:
case|case
name|DOUBLE
case|:
return|return
literal|15
return|;
case|case
name|TIME
case|:
return|return
literal|0
return|;
comment|// SQL99 part 2 section 6.1 syntax rule 30
case|case
name|TIMESTAMP
case|:
comment|// farrago supports only 0 (see
comment|// SqlTypeName.getDefaultPrecision), but it should be 6
comment|// (microseconds) per SQL99 part 2 section 6.1 syntax rule 30.
return|return
literal|0
return|;
case|case
name|DATE
case|:
return|return
literal|0
return|;
case|case
name|CHAR
case|:
case|case
name|VARCHAR
case|:
case|case
name|BINARY
case|:
case|case
name|VARBINARY
case|:
return|return
literal|1
return|;
comment|// SQL2003 part 2 section 6.1 syntax rule 5
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"type "
operator|+
name|typeName
operator|+
literal|" does not have a precision"
argument_list|)
throw|;
block|}
block|}
return|return
name|precision
return|;
block|}
comment|// implement RelDataType
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
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"type "
operator|+
name|typeName
operator|+
literal|" does not have a scale"
argument_list|)
throw|;
block|}
block|}
return|return
name|scale
return|;
block|}
comment|// implement RelDataType
specifier|public
name|Charset
name|getCharset
parameter_list|()
throws|throws
name|RuntimeException
block|{
return|return
operator|(
name|wrappedCharset
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|wrappedCharset
operator|.
name|getCharset
argument_list|()
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
name|collation
return|;
block|}
comment|// implement RelDataTypeImpl
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
operator|(
name|precision
operator|!=
name|PRECISION_NOT_SPECIFIED
operator|)
decl_stmt|;
name|boolean
name|printScale
init|=
operator|(
name|scale
operator|!=
name|SCALE_NOT_SPECIFIED
operator|)
decl_stmt|;
comment|// for the digest, print the precision when defaulted,
comment|// since (for instance) TIME is equivalent to TIME(0).
if|if
condition|(
name|withDetail
condition|)
block|{
comment|// -1 means there is no default value for precision
if|if
condition|(
name|typeName
operator|.
name|getDefaultPrecision
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|printPrecision
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|typeName
operator|.
name|getDefaultScale
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|printScale
operator|=
literal|true
expr_stmt|;
block|}
block|}
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
comment|/**      * Returns a value which is a limit for this type.      *      *<p>For example,      *      *<table border="1">      *<tr>      *<th>Datatype</th>      *<th>sign</th>      *<th>limit</th>      *<th>beyond</th>      *<th>precision</th>      *<th>scale</th>      *<th>Returns</th>      *</tr>      *<tr>      *<td>Integer</th>      *<td>true</td>      *<td>true</td>      *<td>false</td>      *<td>-1</td>      *<td>-1</td>      *<td>2147483647 (2 ^ 31 -1 = MAXINT)</td>      *</tr>      *<tr>      *<td>Integer</th>      *<td>true</td>      *<td>true</td>      *<td>true</td>      *<td>-1</td>      *<td>-1</td>      *<td>2147483648 (2 ^ 31 = MAXINT + 1)</td>      *</tr>      *<tr>      *<td>Integer</th>      *<td>false</td>      *<td>true</td>      *<td>false</td>      *<td>-1</td>      *<td>-1</td>      *<td>-2147483648 (-2 ^ 31 = MININT)</td>      *</tr>      *<tr>      *<td>Boolean</th>      *<td>true</td>      *<td>true</td>      *<td>false</td>      *<td>-1</td>      *<td>-1</td>      *<td>TRUE</td>      *</tr>      *<tr>      *<td>Varchar</th>      *<td>true</td>      *<td>true</td>      *<td>false</td>      *<td>10</td>      *<td>-1</td>      *<td>'ZZZZZZZZZZ'</td>      *</tr>      *</table>      *      * @param sign If true, returns upper limit, otherwise lower limit      * @param limit If true, returns value at or near to overflow; otherwise      * value at or near to underflow      * @param beyond If true, returns the value just beyond the limit, otherwise      * the value at the limit      *      * @return Limit value      */
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

begin_comment
comment|// End BasicSqlType.java
end_comment

end_unit

