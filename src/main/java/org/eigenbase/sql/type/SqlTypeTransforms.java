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
name|sql
operator|.
name|type
package|;
end_package

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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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

begin_comment
comment|/**  * SqlTypeTransforms defines a number of reusable instances of {@link  * SqlTypeTransform}.  *  *<p>NOTE: avoid anonymous inner classes here except for unique,  * non-generalizable strategies; anything else belongs in a reusable top-level  * class. If you find yourself copying and pasting an existing strategy's  * anonymous inner class, you're making a mistake.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlTypeTransforms
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * Parameter type-inference transform strategy where a derived type is      * transformed into the same type but nullable if any of a calls operands is      * nullable      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|toNullable
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
return|return
name|SqlTypeUtil
operator|.
name|makeNullableIfOperandsAre
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
argument_list|,
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|typeToTransform
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Parameter type-inference transform strategy where a derived type is      * transformed into the same type but not nullable.      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|toNotNullable
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|typeToTransform
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Parameter type-inference transform strategy where a derived type is      * transformed into the same type with nulls allowed.      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|forceNullable
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|typeToTransform
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Type-inference strategy whereby the result type of a call is VARYING the      * type given. The length returned is the same as length of the first      * argument. Return type will have same nullablilty as input type      * nullablility. First Arg must be of string type.      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|toVarying
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
switch|switch
condition|(
name|typeToTransform
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|VARCHAR
case|:
case|case
name|VARBINARY
case|:
return|return
name|typeToTransform
return|;
block|}
name|SqlTypeName
name|retTypeName
init|=
name|toVar
argument_list|(
name|typeToTransform
argument_list|)
decl_stmt|;
name|RelDataType
name|ret
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|retTypeName
argument_list|,
name|typeToTransform
operator|.
name|getPrecision
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|inCharFamily
argument_list|(
name|typeToTransform
argument_list|)
condition|)
block|{
name|ret
operator|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithCharsetAndCollation
argument_list|(
name|ret
argument_list|,
name|typeToTransform
operator|.
name|getCharset
argument_list|()
argument_list|,
name|typeToTransform
operator|.
name|getCollation
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|ret
argument_list|,
name|typeToTransform
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|SqlTypeName
name|toVar
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|SqlTypeName
name|sqlTypeName
init|=
name|type
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|CHAR
case|:
return|return
name|SqlTypeName
operator|.
name|VARCHAR
return|;
case|case
name|BINARY
case|:
return|return
name|SqlTypeName
operator|.
name|VARBINARY
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|sqlTypeName
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
comment|/**      * Parameter type-inference transform strategy where a derived type must be      * a multiset type and the returned type is the multiset's element type.      *      * @see MultisetSqlType#getComponentType      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|toMultisetElementType
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
return|return
name|typeToTransform
operator|.
name|getComponentType
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Parameter type-inference transform strategy where a derived type must be      * a struct type with precisely one field and the returned type is the type      * of that field.      */
specifier|public
specifier|static
specifier|final
name|SqlTypeTransform
name|onlyColumn
init|=
operator|new
name|SqlTypeTransform
argument_list|()
block|{
specifier|public
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|typeToTransform
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
assert|assert
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

begin_comment
comment|// End SqlTypeTransforms.java
end_comment

end_unit

